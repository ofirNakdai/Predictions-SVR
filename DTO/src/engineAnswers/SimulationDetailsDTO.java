package engineAnswers;

import java.util.List;

public class SimulationDetailsDTO {
    private final List<EntityDTO> entities;
    private final List<PropertyDTO> environmentVariables;
    private final List<RuleDTO> rules;
    private final Integer maxNumberOfTicks;
    private final Long secondsToTerminate;
    private final boolean isTerminatedByUser;
    private final int rowsInGrid;
    private final int colsInGrid;


    public SimulationDetailsDTO(List<EntityDTO> entities, List<PropertyDTO> environmentVariables, List<RuleDTO> rules, Integer maxNumberOfTicks, Long secondsToTerminate, boolean isTerminatedByUser, int rowsInGrid, int colsInGrid) {
        this.entities = entities;
        this.environmentVariables = environmentVariables;
        this.rules = rules;
        this.maxNumberOfTicks = maxNumberOfTicks;
        this.secondsToTerminate = secondsToTerminate;
        this.isTerminatedByUser = isTerminatedByUser;
        this.rowsInGrid = rowsInGrid;
        this.colsInGrid = colsInGrid;
    }

    public List<EntityDTO> getEntities() {
        return entities;
    }

    public List<PropertyDTO> getEnvironmentVariables() {
        return environmentVariables;
    }

    public List<RuleDTO> getRules() {
        return rules;
    }

    public Integer getMaxNumberOfTicks() {
        return maxNumberOfTicks;
    }

    public Long getSecondsToTerminate() {
        return secondsToTerminate;
    }

    public boolean isTerminatedByUser() {
        return isTerminatedByUser;
    }

    public int getRowsInGrid() {
        return rowsInGrid;
    }

    public int getColsInGrid() {
        return colsInGrid;
    }
}
