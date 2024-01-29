package ex2;

import java.util.Map;

public class EntitiesPopulationDTO {

    private Map<String, Map<Integer,Integer>> entitiesPopByTicks;

    public EntitiesPopulationDTO(Map<String, Map<Integer, Integer>> entitiesPopByTicks) {
        this.entitiesPopByTicks = entitiesPopByTicks;
    }

    public Map<String, Map<Integer, Integer>> getEntitiesPopByTicks() {
        return entitiesPopByTicks;
    }
}
