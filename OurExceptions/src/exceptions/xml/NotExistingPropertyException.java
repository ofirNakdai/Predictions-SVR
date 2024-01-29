package exceptions.xml;

public class NotExistingPropertyException extends RuntimeException{
    public NotExistingPropertyException(String propertyName, String actionName, String entityName)
    {
        super("The property: " + propertyName + " that referenced in action: " + actionName + ", does not exist in the entity: " + entityName);
    }
}
