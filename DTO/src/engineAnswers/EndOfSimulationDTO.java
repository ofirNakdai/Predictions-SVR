package engineAnswers;

public class EndOfSimulationDTO {
    final int simulationID;
    final String reasonOfTermination;

    public EndOfSimulationDTO(int simulationID, String reasonOfTermination) {
        this.simulationID = simulationID;
        this.reasonOfTermination = reasonOfTermination;
    }

    public int getSimulationID() {
        return simulationID;
    }

    public String getReasonOfTermination() {
        return reasonOfTermination;
    }
}
