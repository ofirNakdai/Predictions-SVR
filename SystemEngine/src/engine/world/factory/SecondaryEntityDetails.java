package engine.world.factory;

import engine.action.impl.condition.api.Condition;

import java.util.ArrayList;
import java.util.List;

public class SecondaryEntityDetails {
    private String name;
    private Integer maxCount; //NULL IF "ALL"
    private Condition condition;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(Integer maxCount) {
        this.maxCount = maxCount;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }
}
