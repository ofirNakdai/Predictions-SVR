package engineAnswers;

public class ActionDTO {
    private final String type;
    private final String mainEntityName;
    private final String secondaryEntityName;

    public ActionDTO(String type, String mainEntityName, String secondaryEntityName) {
        this.type = type;
        this.mainEntityName = mainEntityName;
        this.secondaryEntityName = secondaryEntityName;
    }

    public String getMainEntityName() {
        return mainEntityName;
    }

    public String getSecondaryEntityName() {
        return secondaryEntityName;
    }

    public String getType() {
        return type;
    }
}
