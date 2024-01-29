package engine.world;

import engine.action.api.Action;
import engine.context.ContextImpl;
import engine.entity.EntityInstance;
import engine.entity.manager.api.EntityInstanceManager;
import engine.entity.manager.impl.EntityInstanceManagerImpl;
import engine.environment.active.ActiveEnvironmentVariables;
import engine.environment.active.ActiveEnvironmentVariablesImpl;
//import engine.environment.manager.EnvironmentVariablesDefManager;
import engine.property.PropertyDefinition;
import engine.property.api.PropertyInstance;
import engine.rule.Rule;
import engine.world.factory.SecondaryEntityDetails;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WorldInstance implements Serializable, Runnable {
    private String dateOfRun = "";
    private int currentNumberOfTicks = 0;
    private Instant startTime;
    private long runningTime = 0;
    private final List<Rule> rules = new ArrayList<>();
    EntityInstanceManager entityInstanceManager;
    ActiveEnvironmentVariables activeEnvironmentVariables;
    private Integer maxNumberOfTicks = null;
    private Long secondsToTerminate = null;
    private boolean isTerminationByUser = false;
    private Instant endTime;
    private SimulationStatus status;
    private final Object statusLock = new Object();

    private TerminationReason terminationReason = null;

    private int simulationID;
    private String errorMassage = null;


    public WorldInstance(WorldDefinition worldDef, int id) {
        this.secondsToTerminate = worldDef.getSecondsToTerminate();
        this.maxNumberOfTicks = worldDef.getMaxNumberOfTicks();
        this.rules.addAll(worldDef.getRules());
        this.isTerminationByUser = worldDef.isTerminationByUser();
        this.simulationID = id;
        this.status = SimulationStatus.CREATED;
    }
    public SimulationStatus getStatus(){
        return this.status;
    }

    public boolean isTerminateByUser(){
        return isTerminationByUser;
    }
    public EntityInstanceManager getEntityInstanceManager() {
        return entityInstanceManager;
    }

    public Collection<PropertyInstance> getActiveEnvironmentVariables() {
        return activeEnvironmentVariables.getEvnVariables();
    }

    public Instant getStartTime() {
        return startTime;
    }

    public int getCurrentNumberOfTicks() {
        return currentNumberOfTicks;
    }

    public void runInitIteration(WorldDefinition simulationDef){//Tick0
        currentNumberOfTicks = 0;

        entityInstanceManager = new EntityInstanceManagerImpl(simulationDef.getEntitiesDefinitions());
        entityInstanceManager.createEntitiesInstancesAndLocate(simulationDef.getNumOfRowsInGrid(), simulationDef.getNumOfColsInGrid());

        activeEnvironmentVariables = new ActiveEnvironmentVariablesImpl();
        for(PropertyDefinition envVarDef : simulationDef.getEnvironmentVariablesDefinitions())
        {
            activeEnvironmentVariables.createEvnVariableFromDef(envVarDef);
        }

        entityInstanceManager.updateEntitiesPopByTicks(currentNumberOfTicks);
    }

    public void pauseSimulation(){
        synchronized (statusLock) {
            if (this.status == SimulationStatus.RUNNING) {
//                System.out.println("Thread: "+Thread.currentThread()+" is pausing the simulation");
                this.status = SimulationStatus.PAUSED;
                runningTime += Duration.between(startTime , Instant.now()).getSeconds();
            }
        }

    }

    public void resumeSimulation(){
        synchronized (statusLock) {
            if (this.status == SimulationStatus.PAUSED) {
//                System.out.println("Thread: "+Thread.currentThread()+" is resuming the simulation");

                this.status = SimulationStatus.RUNNING;
                startTime = Instant.now();
            }
        }
    }

    public TerminationReason runMainLoopEx2(){ //TICK 1 and up...;
        boolean isTerminated = false;
        boolean isPaused = false;


        synchronized (this){
            if(this.status != SimulationStatus.TERMINATED)
                this.status = SimulationStatus.RUNNING;
        }
        this.startTime = Instant.now();

        while (!isTerminated)
        {
            isPaused = checkIfPaused();
            isTerminated = isTermination();
            if(!isPaused && !isTerminated){
                currentNumberOfTicks++;
                entityInstanceManager.makeMoveToAllEntities();

                if(currentNumberOfTicks < 100) {
                    entityInstanceManager.updateEntitiesPopByTicks(currentNumberOfTicks);
                }
                else if(currentNumberOfTicks % 100 == 0)
                    entityInstanceManager.updateEntitiesPopByTicks(currentNumberOfTicks);


                List<Action> actionList = rules.stream()
                        .filter(rule -> rule.isActive(this.currentNumberOfTicks))
                        .flatMap(rule -> rule.getActions().stream())
                        .collect(Collectors.toList());


                Stream<EntityInstance> allEntitiesInstances = entityInstanceManager.getAllEntitiesInstances();
                try {
                    allEntitiesInstances.forEach(entityInstance -> {
                        String entityName = entityInstance.getName();
                        actionList.stream()
                                .filter(action -> action.getMainEntityName().equals(entityName))
                                .forEach(action -> {
                                    SecondaryEntityDetails secondaryEntityDetails = action.getSecondaryEntityDetails();
                                    if (secondaryEntityDetails == null) { //no secondary entity
                                        action.Run(new ContextImpl(entityInstance, null, this.entityInstanceManager, this.activeEnvironmentVariables, this.currentNumberOfTicks));
                                    } else {
                                        List<EntityInstance> secondaryEntities = entityInstanceManager.getInstancesListByName(secondaryEntityDetails.getName());

                                        if (secondaryEntityDetails.getMaxCount() == null) { // count = "all"
                                            if (secondaryEntityDetails.getCondition() == null) { // no condition
                                                //use all secondary list
                                                secondaryEntities.forEach(secondaryEntityInstance -> action.Run(new ContextImpl(entityInstance, secondaryEntityInstance, this.entityInstanceManager, this.activeEnvironmentVariables, this.currentNumberOfTicks)));
                                            } else { // there is condition
                                                //use all secondary list after filtered by condition
                                                secondaryEntities
                                                        .stream()
                                                        .filter(secondaryEntityInstance -> secondaryEntityDetails.getCondition().evaluateCondition(new ContextImpl(secondaryEntityInstance, null, this.entityInstanceManager, this.activeEnvironmentVariables, this.currentNumberOfTicks))) //SENT THE CURRENT ENTITY(SECONDARY) AS MAIN, AND NULL AS SECONDARY
                                                        .forEach(secondaryEntityInstance -> action.Run(new ContextImpl(entityInstance, secondaryEntityInstance, this.entityInstanceManager, this.activeEnvironmentVariables, this.currentNumberOfTicks)));
                                            }
                                        } else { // count is a number
                                            if (secondaryEntityDetails.getCondition() == null) { // no condition
                                                //use random up to MaxCount secondary entities
                                                Random random = new Random();
                                                int maxCount = secondaryEntityDetails.getMaxCount();
                                                int secEntSize = secondaryEntities.size();

                                                for (int i = 0; i < maxCount && i < secEntSize; i++) {
                                                    EntityInstance randChosenSecEnt = secondaryEntities.get(random.nextInt(secEntSize));
                                                    action.Run(new ContextImpl(entityInstance, randChosenSecEnt, this.entityInstanceManager, this.activeEnvironmentVariables, this.currentNumberOfTicks));
                                                }

                                            } else {// there is condition
                                                //use random up to MaxCount secondary entities filtered by condition
                                                List<EntityInstance> secondaryEntitiesAfterCondition = secondaryEntities
                                                        .stream()
                                                        .filter(secondaryEntity -> secondaryEntityDetails.getCondition().evaluateCondition(new ContextImpl(secondaryEntity, null, this.entityInstanceManager, this.activeEnvironmentVariables, this.currentNumberOfTicks))) //SENT THE CURRENT ENTITY(SECONDARY) AS MAIN, AND NULL AS SECONDARY
                                                        .collect(Collectors.toList());

                                                Random random = new Random();
                                                int maxCount = secondaryEntityDetails.getMaxCount();
                                                int secEntSize = secondaryEntitiesAfterCondition.size(); //NEED TO CHOOSE MINIMUM FROM THEM OF FROM ALL?

                                                for (int i = 0; i < maxCount && i < secEntSize; i++) {
                                                    EntityInstance randChosenSecEnt = secondaryEntitiesAfterCondition.get(random.nextInt(secEntSize));

                                                    action.Run(new ContextImpl(entityInstance, randChosenSecEnt, this.entityInstanceManager, this.activeEnvironmentVariables, this.currentNumberOfTicks));
                                                }
                                            }
                                        }

                                    }

                                });
                    });

                    this.entityInstanceManager.killEntities();
                    this.entityInstanceManager.createScratchEntities();
                    this.entityInstanceManager.createDerivedEntities();

                } catch (Exception e) {
                    synchronized (statusLock) {
                        this.status = SimulationStatus.TERMINATED;
                        this.terminationReason = TerminationReason.ERROR;
                    }
                    this.errorMassage = e.getMessage();
                }
            } // if not paused
        }// if not terminate

        synchronized (this){
            if(this.status == SimulationStatus.RUNNING)
                this.runningTime += Duration.between(startTime , Instant.now()).getSeconds();
            this.status = SimulationStatus.TERMINATED;
        }

        if(this.maxNumberOfTicks != null && currentNumberOfTicks >= this.maxNumberOfTicks)
        {
//            System.out.println("Simulation ended by thread: " + Thread.currentThread().getId());
            this.terminationReason = TerminationReason.MAXTICKSREACHED;
            return TerminationReason.MAXTICKSREACHED;
        }
        else if(this.secondsToTerminate != null && runningTime >= secondsToTerminate)
        {
//            System.out.println("Simulation ended by thread: " + Thread.currentThread().getId());
            this.endTime = Instant.now();
            this.terminationReason = TerminationReason.SECONDSREACHED;
            return TerminationReason.SECONDSREACHED;
        }
        else if(errorMassage != null){
            this.terminationReason = TerminationReason.ERROR;
            return TerminationReason.ERROR;
        }
        else
        {
//            System.out.println("Simulation ended by thread: " + Thread.currentThread().getId());
            this.terminationReason = TerminationReason.ENDEDBYUSER;
            return TerminationReason.ENDEDBYUSER;
        }
    }

    public String getTerminationReason(){
        if(this.terminationReason == null)
            return null;
        return this.terminationReason.toString();
    }
    private boolean checkIfPaused(){
        synchronized (statusLock) {
            return this.status == SimulationStatus.PAUSED;
        }
    }
    private boolean isTermination(){

        long timeSimulationRunning = getRunningTime();

        synchronized (this) {
            if (this.status == SimulationStatus.TERMINATED)
                return true;
        }

        if(this.isTerminationByUser)
            return false;

        if (this.secondsToTerminate != null && this.maxNumberOfTicks != null)
        {
            return (timeSimulationRunning >= this.secondsToTerminate) ||
                    (this.currentNumberOfTicks >= this.maxNumberOfTicks);

        }
        else if (this.secondsToTerminate != null)
        {
            return (timeSimulationRunning >= this.secondsToTerminate);
        }
        else // this.maxNumberOfTicks != null
        {
            return (this.currentNumberOfTicks >= this.maxNumberOfTicks);
        }

    }

    public long getRunningTime() {

        synchronized (statusLock) {
            if (this.status == SimulationStatus.RUNNING)
                return runningTime + Duration.between(startTime, Instant.now()).getSeconds();
            else
                return runningTime;
        }
    }

    public String getDateOfRun() {
        return dateOfRun;
    }

    public void setDateOfRun(String dateOfRun) {
        this.dateOfRun = dateOfRun;
    }

    public void terminateSimulation(){
        synchronized (statusLock) {
            if(this.status != SimulationStatus.TERMINATED) {
                if(this.status == SimulationStatus.RUNNING){
                    this.runningTime += Duration.between(startTime, Instant.now()).getSeconds();
                }
//                System.out.println("Thread: " + Thread.currentThread().getName() + " is terminating the simulation");
                this.status = SimulationStatus.TERMINATED;
            }
        }
    }

    public Integer getMaxNumberOfTicks() {
        return maxNumberOfTicks;
    }

    public Long getSecondsToTerminate() {
        return secondsToTerminate;
    }

    @Override
    public void run() {
        runMainLoopEx2();
    }

    public String getErrorMassage() {
        return errorMassage;
    }
}
