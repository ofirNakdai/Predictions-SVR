package engine.entity.manager.impl;

import engine.entity.EntityDefinition;
import engine.entity.EntityInstance;
import engine.entity.manager.api.EntityInstanceManager;
import engine.property.PropertyType;
import engine.property.api.PropertyInstance;
import engine.property.impl.BooleanProperty;
import engine.property.impl.FloatProperty;
import engine.property.impl.StringProperty;
import engine.world.grid.manager.api.GridManagerAPI;
import engine.world.grid.manager.impl.GridManagerImpl;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class EntityInstanceManagerImpl implements EntityInstanceManager, Serializable {
    private int count;
    private final Map<String, List<EntityInstance>> name2EntInstancesList;
    private final Map<String, EntityDefinition> name2EntitiesDef = new HashMap<>();
    private final GridManagerAPI gridManager;
    private final Set<EntityInstance> EntitiesToKill;
    private final Map<EntityInstance, String> EntityInstance2DerivedName;
    private final Set<String> EntitiesToCreate;

    //for entities chart in termination details:
    private final Map<String, Map<Integer,Integer>> entitiesPopByTicks;

    public EntityInstanceManagerImpl(Collection<EntityDefinition> entityDefinitionCollection) {
        count = 0;
        name2EntInstancesList = new HashMap<>();
        EntitiesToKill = new HashSet<>();
        entityDefinitionCollection.forEach(entityDefinition -> this.name2EntitiesDef.put(entityDefinition.getName(), entityDefinition.clone()));
        EntityInstance2DerivedName = new HashMap<>();
        EntitiesToCreate = new HashSet<>();
        gridManager = new GridManagerImpl();

        entitiesPopByTicks = new HashMap<>();
        name2EntitiesDef.keySet().forEach(key -> entitiesPopByTicks.put(key, new HashMap<>()));
    }

    @Override
    public Stream<EntityInstance> getAllEntitiesInstances()
    {
        return name2EntInstancesList.values().stream().flatMap(List::stream); //.flatMap(List::stream).collect(Collectors.toList());
    }

    @Override
    public void createEntitiesInstancesAndLocate(int rowSize, int colSize) {
        for(EntityDefinition entityDefinition: this.name2EntitiesDef.values()){
            name2EntInstancesList.put(entityDefinition.getName(),new ArrayList<>());

            for (int i = 0; i < entityDefinition.getPopulation(); i++) {
                EntityInstance newEntityInstance = new EntityInstance(entityDefinition, count);
                count++;

                name2EntInstancesList.get(entityDefinition.getName()).add(newEntityInstance);
            }
        }

        gridManager.setEmptyGrid(rowSize, colSize);
        gridManager.initEntitiesRandomly(this.name2EntInstancesList);
    }

    @Override
    public void killEntity(EntityInstance entityInstance) {
        EntitiesToKill.add(entityInstance);
    }

    @Override
    public void killEntities()
    {
        for (EntityInstance entityInstance : EntitiesToKill) {
            gridManager.clearLocation(entityInstance.getGridLocation());
//            entityInstance.getGridLocation(null); not needed because removed?
            name2EntInstancesList.get(entityInstance.getName()).remove(entityInstance);
        }
        EntitiesToKill.clear();
    }

    @Override
    public List<EntityInstance> getInstancesListByName(String entityName) {
        return name2EntInstancesList.get(entityName);
    }


    @Override
    public EntityDefinition getEntityDefByName(String entityName) {
        return name2EntitiesDef.get(entityName);
    }



    ///////FOR REPLACE ACTION:

    @Override
    public void createScratchEntity(String entityName) {
        EntitiesToCreate.add(entityName);
    }
    @Override
    public void createScratchEntities() {
        for (String entityName : EntitiesToCreate) {
            EntityInstance newEntity = new EntityInstance(name2EntitiesDef.get(entityName), count);
            count++;

            gridManager.initEntityLocationRandomly(newEntity); //Enter the entity that created in a random location(?)

            name2EntInstancesList.get(entityName).add(newEntity);

        }
        EntitiesToCreate.clear();
    }
    @Override
    public void createDerivedEntity(EntityInstance entityInstance, String EntityToCreate){
        EntityInstance2DerivedName.put(entityInstance, EntityToCreate);
    }
    @Override
    public void createDerivedEntities()
    {
        for (Map.Entry<EntityInstance, String> entry : EntityInstance2DerivedName.entrySet()) {
            createDerivedEntityInstance(entry.getKey(), entry.getValue());

            //KILL THE MAIN ENTITY:
            name2EntInstancesList.get(entry.getKey().getName()).remove(entry.getKey());
        }

        EntityInstance2DerivedName.clear();
    }
    private void createDerivedEntityInstance(EntityInstance entityInstance, String derivedEntityName){
        EntityInstance derivedEntity = new EntityInstance(name2EntitiesDef.get(derivedEntityName), entityInstance.getId());
        //count++; setted the Id of the replaced one

        //SET SAME LOCATION:
        gridManager.replaceEntitiesInLocation(entityInstance, derivedEntity, entityInstance.getGridLocation());

        //SET SAME PROPERTIES WHERE EQUAL:
        for (PropertyInstance propertyInstance: entityInstance.getProperties()) {
            String propertyName = propertyInstance.getName();
            PropertyType propertyType = propertyInstance.getType();

            PropertyInstance propertyAtDerived = derivedEntity.getPropertyByName(propertyName);
            if (propertyAtDerived != null && propertyAtDerived.getType() == propertyType){
                switch (propertyType) {
                    case BOOLEAN:
                        if (propertyAtDerived instanceof BooleanProperty) {
                            ((BooleanProperty) propertyAtDerived).setValue( Boolean.valueOf(propertyInstance.getValue()) , null);
                        }
                        break;
                    case FLOAT:
                        if (propertyAtDerived instanceof FloatProperty) {
                            ((FloatProperty) propertyAtDerived).setValue(Float.parseFloat(propertyInstance.getValue()), null);
                        }
                        break;
                    case STRING:
                        if (propertyAtDerived instanceof StringProperty) {
                            ((StringProperty) propertyAtDerived).setValue(propertyInstance.getValue(), null);
                        }
                        break;
                }
            }
        }

        name2EntInstancesList.get(derivedEntityName).add(derivedEntity);
    }


    @Override
    public void makeMoveToAllEntities() {
        this.gridManager.makeRandomMoveToAllEntities(this.name2EntInstancesList);
    }


    /////FOR PROXIMITY:
    @Override
    public boolean isEnt1NearEnt2(EntityInstance entityInstance1, EntityInstance entityInstance2, int depth){
        return gridManager.isEnt1NearEnt2(entityInstance1, entityInstance2, depth);
    }


    // ~~~~~~~~~~~~~~~ Terminated Simulations Extra Details ~~~~~~~~~~~~~
    @Override
    public double getAvgOfUnmodifiedTicksOfProperty(String entityName, String propertyName, int lastTick) {
        AtomicReference<Double> totalAvg = new AtomicReference<>((double) 0);

        if(!name2EntInstancesList.containsKey(entityName))
            throw new IllegalArgumentException("Entity: "+entityName+" does not exists!");
        List<EntityInstance> entityInstances = name2EntInstancesList.get(entityName);
        entityInstances.forEach(entityInstance -> {
            totalAvg.updateAndGet(v -> v + entityInstance.getPropertyByName(propertyName).getAvgUnmodifiedTicks(lastTick));
        });

        if(entityInstances.isEmpty())
            throw new IllegalArgumentException("Entity: " + entityName + " has 0 instances");

        return totalAvg.get() / entityInstances.size();

    }
    @Override
    public double getAverageValueOfProperty(String entityName, String propertyName){
        if(!(name2EntitiesDef.get(entityName).getName2propertyDef().get(propertyName).getType() == PropertyType.FLOAT))
            throw new IllegalArgumentException("Trying to get average of a non-numerical property: " + propertyName + " of entity: " + entityName);


        int size = name2EntInstancesList.get(entityName).size();
        double total = 0;

        if(size == 0)
            throw new IllegalArgumentException("Trying to get average of property with no living entities: " + propertyName + " of entity: " + entityName);

        for(EntityInstance entity:name2EntInstancesList.get(entityName)) {
            total += Double.parseDouble(entity.getPropertyByName(propertyName).getValue());
        }

        return total / size;
    }


    //for entities chart in termination details:
    //ALL TICKS:
    @Override
    public void updateEntitiesPopByTicks(int currentTick){
        for (Map.Entry<String, List<EntityInstance>> entry : name2EntInstancesList.entrySet()) {
            this.entitiesPopByTicks.get(entry.getKey()).put(currentTick, entry.getValue().size());
        }
    }

    //LAST 10K TICKS:
//    @Override
//    public void updateEntitiesPopByTicks(int currentTick){
//        for (Map.Entry<String, List<EntityInstance>> entry : name2EntInstancesList.entrySet()) {
//            if(currentTick >= 1000){
//                this.entitiesPopByTicks.get(entry.getKey()).remove(currentTick - 10000);
//            }
//            this.entitiesPopByTicks.get(entry.getKey()).put(currentTick, entry.getValue().size());
//        }
//    }
    @Override
    public Map<String, Map<Integer, Integer>> getEntitiesPopByTicks() {
        return entitiesPopByTicks;
    }
}
