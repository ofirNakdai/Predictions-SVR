package engine.property.api;

import engine.property.PropertyType;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public abstract class PropertyInstance implements Serializable {
    protected String value;
    private final String name;
    private final PropertyType type;

    private Map<Integer, String> tick2value = new TreeMap<>();
//    private Integer lastTickModified = 0;


    public PropertyInstance(String name, PropertyType type) {
        this.name = name;
        this.type = type;
    }

    public PropertyType getType(){
        return this.type;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public int getLastTickModified() {
        Optional<Integer> lastModifiedTick = tick2value.keySet().stream().max(Integer::compare);
        return lastModifiedTick.get();
    }

    protected void setLastTickModified(Integer currTick) {
        if(currTick != null){
            this.tick2value.put(currTick, this.value);
        }
    }

    public double getAvgUnmodifiedTicks(int lastTick){
        int sum = 0;
        List<Integer> ticksCollection = tick2value.keySet().stream().sorted().collect(Collectors.toList());

        int totalIdleTime = 0;
        int previousModificationTime = 0;


        for (int modificationTime : ticksCollection) {
            int timeGap = Math.abs(modificationTime - previousModificationTime);
            totalIdleTime += timeGap;
            previousModificationTime = modificationTime;
        }

        // Calculate idle time from the last modification to the end of the simulation.
        int finalIdleTime = Math.abs(lastTick - previousModificationTime);
        totalIdleTime += finalIdleTime;

        // Calculate the average idle time.
        int numIntervals = ticksCollection.size() + 1;
        double averageIdleTime = (double) totalIdleTime / numIntervals;

        return averageIdleTime;

    }
}
