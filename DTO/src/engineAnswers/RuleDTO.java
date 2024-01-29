package engineAnswers;

import java.util.List;

public class RuleDTO {
    private final String name;
    private final int ticksForActivation;
    private final double probabilityForActivation;
    private final List<ActionDTO> actions;

    public RuleDTO(String name, int ticksForActivation, double probabilityForActivation, List<ActionDTO> actions) {
        this.name = name;
        this.ticksForActivation = ticksForActivation;
        this.probabilityForActivation = probabilityForActivation;
        this.actions = actions;
    }

    public String getName() {
        return name;
    }

    public int getTicksForActivation() {
        return ticksForActivation;
    }

    public double getProbabilityForActivation() {
        return probabilityForActivation;
    }

    public List<ActionDTO> getActions() {
        return actions;
    }
}
