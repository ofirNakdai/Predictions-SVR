package ex2;

import engineAnswers.EntityCountDTO;

import java.util.List;
import java.util.Map;

public class runningSimulationDTO {
    private int currentTick;
    private Integer totalTicks;
    private long currentSeconds;
    private Long totalSeconds;
    private String status;
    private boolean isTerminateByUser;
    private List<EntityCountDTO> entityCountDTOS;

    private String terminationReason = null;
    private String errorMassage;

    public runningSimulationDTO(int currentTick, Integer totalTicks, long currentSeconds, Long totalSeconds, String status, boolean isTerminateByUser, List<EntityCountDTO> entityCountDTOS, String terminationReason, String errorMassage) {
        this.currentTick = currentTick;
        this.totalTicks = totalTicks;
        this.currentSeconds = currentSeconds;
        this.totalSeconds = totalSeconds;
        this.status = status;
        this.isTerminateByUser = isTerminateByUser;
        this.entityCountDTOS = entityCountDTOS;
        this.terminationReason = terminationReason;
        this.errorMassage = errorMassage;
    }

    public String getErrorMassage() {
        return errorMassage;
    }

    public String getTerminationReason() {
        return terminationReason;
    }

    public boolean isTerminateByUser() {
        return isTerminateByUser;
    }

    public int getCurrentTick() {
        return currentTick;
    }

    public long getCurrentSeconds() {
        return currentSeconds;
    }

    public List<EntityCountDTO> getEntityCountDTOS() {
        return entityCountDTOS;
    }

    public String getStatus() {
        return status;
    }

    public Integer getTotalTicks() {
        return totalTicks;
    }

    public Long getTotalSeconds() {
        return totalSeconds;
    }
}
