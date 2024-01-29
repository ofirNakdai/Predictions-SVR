package exceptions.xml;

public class NotExistingEntityException extends RuntimeException{
    String entityName;
    String actionName;

    public NotExistingEntityException(String entityName, String actionName)
    {
        super("The entity: " + entityName + " that referenced in: " + actionName + " does not exist!");
        this.entityName = entityName;
        this.actionName = actionName;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }
}
