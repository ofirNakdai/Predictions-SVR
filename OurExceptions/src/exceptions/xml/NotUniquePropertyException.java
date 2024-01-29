package exceptions.xml;

public class NotUniquePropertyException extends RuntimeException{
    public NotUniquePropertyException(String propertyName, String entityName)
    {
        super("A property named " + propertyName + " is already exists in the entity: " + entityName + ". each property in an entity must have a unique name!");
    }
}
