package engineAnswers;

import java.util.List;

public class EntityDTO {
    private final String Name;
    private final int population;
    private final List <PropertyDTO> properties;

    public EntityDTO(String name, int population, List<PropertyDTO> properties) {
        Name = name;
        this.population = population;
        this.properties = properties;
    }

    public String getName() {
        return Name;
    }

    public int getPopulation() {
        return population;
    }

    public List<PropertyDTO> getProperties() {
        return properties;
    }
}
