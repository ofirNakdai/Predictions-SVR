package engine.world.factory;

import engine.world.WorldDefinition;
import jaxb.generated2.PRDWorld;

public interface WorldDefFactory {

    void insertDataToWorldDefinition(WorldDefinition simulationDef, PRDWorld generatedWorld);


}
