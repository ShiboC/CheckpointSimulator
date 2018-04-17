package factory;

import java.util.ArrayList;

public class DynamicCheckpoint extends CheckpointStrategy {
    @Override
    public CheckpointStatus getCheckpointStatus(int superstep, ArrayList<Integer> checkpointCost, int recoveryOverhead, int lastCheckpoint, int restartedSuperstep, int[] computeTime) {
        if (lastCheckpoint == -1) {
            return CheckpointStatus.CHECKPOINT;
        }
        int recoveryCost = recoveryOverhead;

        for (int i = lastCheckpoint; i < superstep; i++) {
            recoveryCost += computeTime[i];
        }
        double sumCheckpointCost=0;
        for(int i=0;i<checkpointCost.size();i++){
            sumCheckpointCost+=checkpointCost.get(i);
        }
        double avgCheckpointCost=sumCheckpointCost/checkpointCost.size();
        if (avgCheckpointCost <= recoveryCost && (superstep - lastCheckpoint >= this.getInterval())) {
            return CheckpointStatus.CHECKPOINT;
        }
        return CheckpointStatus.NONE;
    }
}
