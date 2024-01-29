package engineAnswers;

public class ActiveEnvVarDTO {
    final String name;
    final String value;

    public ActiveEnvVarDTO(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
