package ex2.actions;

import engineAnswers.ActionDTO;

public class ReplaceActionDTO extends ActionDTO {
    private final String entityToCreateName;
    private final String creationMode;

    public ReplaceActionDTO(String type, String mainEntityName, String secondaryEntityName, String entityToCreateName, String creationMode) {
        super(type, mainEntityName, secondaryEntityName);
        this.entityToCreateName = entityToCreateName;
        this.creationMode = creationMode;
    }

    public String getEntityToCreateName() {
        return entityToCreateName;
    }

    public String getCreationMode() {
        return creationMode;
    }
}
