package engine.system;

import engine.action.api.Action;
import engine.action.impl.*;
import engine.action.impl.condition.impl.MultipleCondition;
import engine.action.impl.condition.impl.SingleCondition;
import engine.action.impl.replace.Replace;
import engine.entity.EntityDefinition;
import engine.entity.EntityInstance;
import engine.property.PropertyDefinition;
import engine.property.api.PropertyInstance;
import engine.rule.Rule;
import engine.world.WorldDefinition;
import engine.world.WorldInstance;
import engine.world.factory.WorldDefFactory;
import engine.world.factory.WorldDefFactoryImpl;
import engineAnswers.*;
import ex2.EntitiesPopulationDTO;
import ex2.ThreadpoolDTO;
import ex2.actions.*;
import ex2.runningSimulationDTO;
import jaxb.generated2.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

public class SystemEngineImpl implements SystemEngine, Serializable {
    private final static String JAXB_XML_GAME_PACKAGE_NAME = "jaxb.generated2";
    private WorldInstance simulation = null;
    private WorldDefinition simulationDef = null;
    private final WorldDefFactory worldDefFactory = new WorldDefFactoryImpl();
    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy | HH.mm.ss");
    int currentSimulationID = 0;
    private final Map<Integer, WorldInstance> id2pastSimulation = new HashMap<>();
    private boolean isThereLoadedSimulation = false;

    private int numOfThreads = 1;
    ThreadPoolExecutor threadExecutor;
    private Map<Integer, Future<?>> runningSimulations;



    @Override
    public void loadSimulation(String filePath) throws Exception {
        final WorldDefinition currentlyWorkingWorldDef = this.simulationDef;

        if (!filePath.endsWith(".xml")){
            throw new Exception("Invalid file format! must be a .xml file");
        }
        try {
            InputStream inputStream = new FileInputStream(new File(filePath));
            PRDWorld generatedWorld = deserializeFrom(inputStream);

            simulationDef = new WorldDefinition();
            worldDefFactory.insertDataToWorldDefinition(this.simulationDef, generatedWorld);

            this.numOfThreads = generatedWorld.getPRDThreadCount();
            threadExecutor = (ThreadPoolExecutor)Executors.newFixedThreadPool(this.numOfThreads);


            isThereLoadedSimulation = true;
        } catch (Exception e) {
            if (currentlyWorkingWorldDef != null)
            {
                this.simulationDef = currentlyWorkingWorldDef;
            }
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public SimulationDetailsDTO getSimulationDetails(){
        SimulationDetailsDTO simulationDetails;
        List<EntityDTO> entitiesDetails = new ArrayList<>();
        List<RuleDTO> rulesDetails = new ArrayList<>();

        for (EntityDefinition entityDefinition: simulationDef.getEntitiesDefinitions()) {
            List<PropertyDTO> propertiesDetails = new ArrayList<>();
            for (PropertyDefinition propertyDefinition: entityDefinition.getName2propertyDef().values()) {
                addPropertyToDtoList(propertiesDetails, propertyDefinition);
            }
            entitiesDetails.add(new EntityDTO(entityDefinition.getName(), entityDefinition.getPopulation(), propertiesDetails));
        }

        List<PropertyDTO> envVarsDetails = getEnvVarsDefinitionDto();

        for (Rule rule: simulationDef.getRules()) {
            List<ActionDTO> actionsDetails = new ArrayList<>();
            for (Action action: rule.getActions()){
                String secondaryEntityName = action.getSecondaryEntityDetails() == null ? null : action.getSecondaryEntityDetails().getName();
                String actionType = action.getActionType().toString();

                switch (action.getActionType()) {
                    case INCREASE:
                        Increase increaseAction = (Increase) action;
                        actionsDetails.add(new IncreaseActionDTO(actionType, increaseAction.getMainEntityName(), secondaryEntityName, increaseAction.getPropertyName(), increaseAction.getByExpression()));
                        break;
                    case DECREASE:
                        Decrease decreaseAction = (Decrease) action;
                        actionsDetails.add(new IncreaseActionDTO(actionType, decreaseAction.getMainEntityName(), secondaryEntityName, decreaseAction.getPropertyName(), decreaseAction.getByExpression()));
                        break;
                    case CALCULATION:
                        Calculation calculationAction = (Calculation) action;
                        actionsDetails.add(new CalculationActionDTO(actionType, calculationAction.getMainEntityName(),secondaryEntityName, calculationAction.getCalcType().toString(), calculationAction.getPropertyName(), calculationAction.getArg1Expression(),calculationAction.getArg2Expression()));
                        break;
                    case CONDITION:
                        if (action instanceof SingleCondition)
                        {
                            SingleCondition singleConditionAction = (SingleCondition) action;
                            actionsDetails.add(new SingleConditionDTO(actionType, singleConditionAction.getMainEntityName(), secondaryEntityName, singleConditionAction.getFirstArgExpression(), singleConditionAction.getOperator().toString(), singleConditionAction.getSecondArgExpression(), singleConditionAction.getNumOfThenActions(), singleConditionAction.getNumOfElseActions()));
                        } else{ // (action instanceof MultipleCondition)
                            MultipleCondition multipleConditionAction = (MultipleCondition) action;
                            actionsDetails.add(new MultipleConditionDTO(actionType, multipleConditionAction.getMainEntityName(), secondaryEntityName, multipleConditionAction.getLogicalOperator().toString(),multipleConditionAction.getNumOfConditions(), multipleConditionAction.getNumOfThenActions(), multipleConditionAction.getNumOfElseActions()));
                        }
                        break;
                    case SET:
                        SetAction setAction = (SetAction) action;
                        actionsDetails.add(new SetActionDTO(actionType, setAction.getMainEntityName(), secondaryEntityName, setAction.getPropertyName(), setAction.getValueExpression()));
                        break;
                    case KILL:
                        actionsDetails.add(new ActionDTO(actionType, action.getMainEntityName(), secondaryEntityName));
                        break;
                    case REPLACE:
                        Replace replaceAction = (Replace) action;
                        actionsDetails.add(new ReplaceActionDTO(actionType, replaceAction.getMainEntityName(), secondaryEntityName, replaceAction.getEntityToCreateName(), replaceAction.getCreationMode().toString()));
                        break;
                    case PROXIMITY:
                        Proximity proximityAction = (Proximity) action;
                        actionsDetails.add(new ProximityActionDTO(actionType, proximityAction.getMainEntityName(), secondaryEntityName, proximityAction.getTargetEntityName(), proximityAction.getOfExpression(), proximityAction.getNumOfThenActions()));
                        break;
                }
            }
            rulesDetails.add(new RuleDTO(rule.getName(), rule.getTicksForActivations(), rule.getProbForActivations(), actionsDetails));
        }

        simulationDetails = new SimulationDetailsDTO(entitiesDetails, envVarsDetails, rulesDetails, simulationDef.getMaxNumberOfTicks(), simulationDef.getSecondsToTerminate(), simulationDef.isTerminationByUser(), simulationDef.getNumOfRowsInGrid(), simulationDef.getNumOfColsInGrid());
        return simulationDetails;
    }

    private void addPropertyToDtoList(List<PropertyDTO> propertiesDtoList, PropertyDefinition propertyDefinition) {
        if (propertyDefinition.getValueRange() != null) {
            propertiesDtoList.add( new PropertyDTO(propertyDefinition.getName(), propertyDefinition.getType().toString(), propertyDefinition.getValueRange().getFrom(),propertyDefinition.getValueRange().getTo(), propertyDefinition.isInitializedRandomly(), propertyDefinition.getInitValue()));
        }
        else{
            propertiesDtoList.add(new PropertyDTO(propertyDefinition.getName(), propertyDefinition.getType().toString(), null,null, propertyDefinition.isInitializedRandomly(), propertyDefinition.getInitValue()));
        }
    }
    private static PRDWorld deserializeFrom(InputStream in) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(JAXB_XML_GAME_PACKAGE_NAME);
        Unmarshaller u = jc.createUnmarshaller();
        return (PRDWorld) u.unmarshal(in);
    }

    private WorldInstance createNewSimulation(){
        WorldInstance resSimulation = new WorldInstance(this.simulationDef, currentSimulationID);
        resSimulation.runInitIteration(this.simulationDef);
        String dateOfRun = simpleDateFormat.format(new Date());
        resSimulation.setDateOfRun(dateOfRun);

        return resSimulation;
    }
    @Override
    public void updateEntityDefPopulation(EntityDTO newEntityDTO) {
        this.simulationDef.getEntityDefinitionByName(newEntityDTO.getName()).setPopulation(newEntityDTO.getPopulation());
    }
    @Override
    public void setAllPopulationToZero() {
        simulationDef.getEntitiesDefinitions().forEach(entity -> entity.setPopulation(0));
    }
    @Override
    public Collection<EntityDTO> getEntitiesListDTO() {
        List<EntityDTO> entitiesDetails = new ArrayList<>();

        for (EntityDefinition entityDefinition: simulationDef.getEntitiesDefinitions()) {
            List<PropertyDTO> propertiesDetails = new ArrayList<>();
            for (PropertyDefinition propertyDefinition: entityDefinition.getName2propertyDef().values()) {
                addPropertyToDtoList(propertiesDetails, propertyDefinition);
            }
            entitiesDetails.add(new EntityDTO(entityDefinition.getName(), entityDefinition.getPopulation(), propertiesDetails));
        }

        return entitiesDetails;
    }

    @Override
    public void clearPastSimulations()
    {
        id2pastSimulation.values().forEach(WorldInstance::terminateSimulation);
        threadExecutor.shutdownNow();
//        threadExecutor = (ThreadPoolExecutor)Executors.newFixedThreadPool(this.numOfThreads);

//        threadExecutor.shutdown();
//        try {
//            if (!threadExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
//                // If not terminated within 10 seconds, force shutdown
//                threadExecutor.shutdownNow();
//            }
//        } catch (InterruptedException e) {
//            // Handle InterruptedException
//            Thread.currentThread().interrupt();
//        }
        threadExecutor = (ThreadPoolExecutor)Executors.newFixedThreadPool(this.numOfThreads);

        id2pastSimulation.clear();
        currentSimulationID = 0;
    }

    @Override
    public pastSimulationDTO runSimulation() {
        WorldInstance currSimulation = this.createNewSimulation();
        id2pastSimulation.put(currentSimulationID, currSimulation);

        threadExecutor.execute(currSimulation);
        currentSimulationID++;

        return new pastSimulationDTO(currSimulation.getDateOfRun(), currentSimulationID - 1);
    }

    @Override
    public List<ActiveEnvVarDTO> getActiveEnvVarsDto(int simulationID) {
        List<ActiveEnvVarDTO> activeEnvVarDtos = new ArrayList<>();

        for (PropertyInstance activeEnvVar : id2pastSimulation.get(simulationID).getActiveEnvironmentVariables()) {
            activeEnvVarDtos.add(new ActiveEnvVarDTO(activeEnvVar.getName(), activeEnvVar.getValue()));
        }
        return activeEnvVarDtos;
    }

    @Override
    public List<PropertyDTO> getEnvVarsDefinitionDto() {
        List<PropertyDTO> environmentVariablesDetails = new ArrayList<>();
        for (PropertyDefinition environmentVariableDefinition: simulationDef.getEnvironmentVariablesDefinitions()) {
            addPropertyToDtoList(environmentVariablesDetails, environmentVariableDefinition);
        }
        return environmentVariablesDetails;
    }

    @Override
    public void setEnvVarDefFromDto(PropertyDTO envVarDto) {
        simulationDef.getEnvironmentVariableDefByName(envVarDto.getName()).setInitializedRandomly(envVarDto.isInitialisedRandomly());

        if(!(envVarDto.isInitialisedRandomly()))
        {
            simulationDef.getEnvironmentVariableDefByName(envVarDto.getName()).setInitValue(envVarDto.getInitValue());
        }
    }

    @Override
    public List<pastSimulationDTO> getPastSimulationsDetails(){
        List <pastSimulationDTO> pastSimulations = new ArrayList<>();
        for(Map.Entry<Integer, WorldInstance> entry : id2pastSimulation.entrySet()){
            pastSimulations.add(new pastSimulationDTO(entry.getValue().getDateOfRun(), entry.getKey().intValue()));
        }

        pastSimulations.sort(Comparator.comparing(pastSimulationDTO::getDateOfRun));
        return pastSimulations;
    }

    @Override
    public Boolean isThereLoadedSimulation() {
        return this.isThereLoadedSimulation;
    }


    // ~~~~~~~~~~~~~~~~~~~~~~ Terminated Simulations Details ~~~~~~~~~~~~~~~~~~~~~~~~~~
    @Override
    public HistogramDTO getHistogramOfPropertyInTerminatedSimulation(int simulationID, String entityName, String propertyName){

        if(!this.id2pastSimulation.containsKey(simulationID))
            throw new RuntimeException("Trying to view simulation with ID:" + simulationID + ", this ID does not exist in the system!");
        long valueCounter = 0;
        WorldInstance desiredSimulation = this.id2pastSimulation.get(simulationID);
        List<EntityInstance> pastSimulationEntities = desiredSimulation.getEntityInstanceManager().getInstancesListByName(entityName);
        Map<String, Long> histogram = new TreeMap<>();

        for(EntityInstance entity : pastSimulationEntities) {
            valueCounter = 0L;
            if (histogram.containsKey(entity.getPropertyByName(propertyName).getValue())) {
                valueCounter = histogram.get(entity.getPropertyByName(propertyName).getValue());
            }
            histogram.put(entity.getPropertyByName(propertyName).getValue(), valueCounter + 1   );
        }
        return new HistogramDTO(simulationID, entityName, propertyName, histogram);
    }
    @Override
    public PropertyConsistencyDTO getConsistencyOfPropertyInTerminatedSimulation(int simulationID, String entityName, String propertyName){
        int lastTick = id2pastSimulation.get(simulationID).getCurrentNumberOfTicks();
        Double consistency = id2pastSimulation.get(simulationID).getEntityInstanceManager().getAvgOfUnmodifiedTicksOfProperty(entityName,propertyName,lastTick);

        return new PropertyConsistencyDTO(entityName, propertyName, consistency);
    }
    @Override
    public AveragePropertyValueDTO getAverageOfPropertyInTerminatedSimulation(int simulationID, String entityName, String propertyName){
        Double avg = id2pastSimulation.get(simulationID).getEntityInstanceManager().getAverageValueOfProperty(entityName,propertyName);

        return new AveragePropertyValueDTO(entityName, propertyName, avg);
    }
    // ~~~~~~~~~~~~~~~~~~~~~~ ^ Terminated Simulations Details ^ ~~~~~~~~~~~~~~~~~~~~~~~~~~


    @Override
    public List<EntityCountDTO> getPastSimulationEntityCount(pastSimulationDTO desiredPastSimulation) {

        List<EntityCountDTO> entityCountDtos = new ArrayList<>();
        WorldInstance pastSimulation = this.id2pastSimulation.get(desiredPastSimulation.getId());

        for (EntityDefinition entityDefinition : simulationDef.getEntitiesDefinitions()) {

            int startCount = pastSimulation.getEntityInstanceManager().getEntityDefByName(entityDefinition.getName()).getPopulation();
            int endCount = pastSimulation.getEntityInstanceManager().getInstancesListByName(entityDefinition.getName()).size();
            entityCountDtos.add(new EntityCountDTO(entityDefinition.getName(),startCount, endCount));

        }
        return entityCountDtos;
    }

    @Override
    public List<EntityDTO> getPastSimulationEntitiesDTO(pastSimulationDTO desiredPastSimulation) {
        List<EntityDTO> entitiesDetails = new ArrayList<>();

        for (EntityDefinition entityDefinition: simulationDef.getEntitiesDefinitions()) {
            List<PropertyDTO> propertiesDetails = new ArrayList<>();
            for (PropertyDefinition propertyDefinition: entityDefinition.getName2propertyDef().values()) {
                addPropertyToDtoList(propertiesDetails, propertyDefinition);
            }
            entitiesDetails.add(new EntityDTO(entityDefinition.getName(), entityDefinition.getPopulation(), propertiesDetails));
        }
        return entitiesDetails;
    }

    @Override
    public void pauseSimulation(int simulationID){
        WorldInstance simulationToPause = id2pastSimulation.get(simulationID);
        simulationToPause.pauseSimulation();
    }
    @Override
    public void resumeSimulation(int simulationID){
        WorldInstance simulationToResume = id2pastSimulation.get(simulationID);
        simulationToResume.resumeSimulation();
//        threadExecutor.notifyAll();
    }
    @Override
    public void stopSimulation(int simulationID){
        WorldInstance simulationToStop = id2pastSimulation.get(simulationID);
        simulationToStop.terminateSimulation();
    }

    @Override
    public ThreadpoolDTO getThreadpoolData() {
        return new ThreadpoolDTO(threadExecutor.getQueue().size(), threadExecutor.getActiveCount(), threadExecutor.getCompletedTaskCount());
    }

    @Override
    public int getGridSize() {
        return simulationDef.getNumOfColsInGrid() * simulationDef.getNumOfRowsInGrid();
    }

    @Override
    public EntitiesPopulationDTO getEntitiesPopByTicks(int simulationID) {
        Map<String, Map<Integer, Integer>> originalMap = id2pastSimulation.get(simulationID).getEntityInstanceManager().getEntitiesPopByTicks();
        Map<String, Map<Integer,Integer>> entitiesPopByTicks = new HashMap<>();

        for (Map.Entry<String, Map<Integer, Integer>> entry : originalMap.entrySet()) {
            String key = entry.getKey();
            Map<Integer, Integer> innerMap = entry.getValue();
            Map<Integer, Integer> copiedInnerMap = new HashMap<>(innerMap); // Create a copy of the inner map
            entitiesPopByTicks.put(key, copiedInnerMap);
        }

        return new EntitiesPopulationDTO(entitiesPopByTicks);
    }


    //////////////////////////////////////PULLING DATA:
    @Override
    public runningSimulationDTO pullData(int simulationID){
        WorldInstance wantedSimulation = id2pastSimulation.get(simulationID);
        int currentNumberOfTicks = wantedSimulation.getCurrentNumberOfTicks();
//        long timeRunning = wantedSimulation.getStartTime() == null ? 0 : Duration.between(wantedSimulation.getStartTime(), Instant.now()).getSeconds();

        long timeRunning = wantedSimulation.getRunningTime();

        List<EntityCountDTO> entityCountDtos = new ArrayList<>();
        for (EntityDefinition entityDefinition : simulationDef.getEntitiesDefinitions()) {

            int startCount = wantedSimulation.getEntityInstanceManager().getEntityDefByName(entityDefinition.getName()).getPopulation();
            int endCount = wantedSimulation.getEntityInstanceManager().getInstancesListByName(entityDefinition.getName()).size(); //MIGHT BE PROBLEM DURING SIMULATION
            entityCountDtos.add(new EntityCountDTO(entityDefinition.getName(),startCount, endCount));
        }
                
        boolean endSimulationByUser = wantedSimulation.isTerminateByUser();
        String terminationReason = wantedSimulation.getTerminationReason();
        String errorMassage = wantedSimulation.getErrorMassage();
        return new runningSimulationDTO(currentNumberOfTicks,
                                        wantedSimulation.getMaxNumberOfTicks(),
                                        timeRunning,
                                        wantedSimulation.getSecondsToTerminate(),
                                        wantedSimulation.getStatus().toString(),
                                        endSimulationByUser,
                                        entityCountDtos,
                                        terminationReason,
                                        errorMassage);
    }

//    public void pauseSimulation(int simulationID){
//        WorldInstance wantedSimulation = id2pastSimulation.get(simulationID);
//        wantedSimulation.setState(PAUSED);
//    }

//    public void resumeSimulation(int simulationID){
//        WorldInstance wantedSimulation = id2pastSimulation.get(simulationID);
//        wantedSimulation.setState(RUNNING);
//    }

//    public void stopSimulation(int simulationID){
//        WorldInstance wantedSimulation = id2pastSimulation.get(simulationID);
////        wantedSimulation.endSimualation()
////                free the thread
////                wantedSimulation.setState(ENDED);
//    }

}
