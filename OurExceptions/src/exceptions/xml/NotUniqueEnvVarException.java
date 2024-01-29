package exceptions.xml;

public class NotUniqueEnvVarException extends RuntimeException{
    public NotUniqueEnvVarException(String envVarName)
    {
        super("En environment variable with the name: " + envVarName + " is already exists." + System.lineSeparator() + "Each environment variable must have a unique name!");
    }
}
