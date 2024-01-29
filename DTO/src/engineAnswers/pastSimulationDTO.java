package engineAnswers;

public class pastSimulationDTO {
    private final String dateOfRun;
    private final int id;

    public pastSimulationDTO(String dateOfRun, int id) {
        this.dateOfRun = dateOfRun;
        this.id = id;
    }

    public String getDateOfRun() {
        return dateOfRun;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return dateOfRun + "    ID: " + id;
    }
}
