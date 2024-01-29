package engineAnswers;

public class EntityCountDTO {
    private final String name;
    private final int populationAtStart;
    private final int populationAtEnd;

    public EntityCountDTO(String name, int populationAtStart, int populationAtEnd) {
        this.name = name;
        this.populationAtStart = populationAtStart;
        this.populationAtEnd = populationAtEnd;
    }

    public String getName() {
        return name;
    }

    public int getPopulationAtStart() {
        return populationAtStart;
    }

    public int getPopulationAtEnd() {
        return populationAtEnd;
    }

    @Override
    public String toString() {
        return  "name: '" + name + '\'' +
                ", population at start: " + populationAtStart +
                ", population at end: " + populationAtEnd;
    }
}
