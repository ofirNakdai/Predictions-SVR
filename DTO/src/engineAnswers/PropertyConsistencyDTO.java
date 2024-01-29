package engineAnswers;

public class PropertyConsistencyDTO {
    String entityName;
    String propertyName;
    Double value;

    public PropertyConsistencyDTO(String entityName, String propertyName, Double value) {
        this.entityName = entityName;
        this.propertyName = propertyName;
        this.value = value;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Double getValue() {
        return value;
    }
}
