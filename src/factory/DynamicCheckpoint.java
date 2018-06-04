package factory;

import java.util.ArrayList;

public class DynamicCheckpoint extends CheckpointStrategy {
    @Override
    public CheckpointStatus getCheckpointStatus(int superstep, ArrayList<Integer> checkpointCost, int lastCheckpoint, int[] computeTime) {
//        System.out.println("supersteps:"+superstep+",last checkpoint:"+lastCheckpoint);
        if (lastCheckpoint == -1) {
            return CheckpointStatus.CHECKPOINT;
        }
        int recoveryCost = 0;

        for (int i = lastCheckpoint; i < superstep; i++) {
            recoveryCost += computeTime[i];
        }
        double sumCheckpointCost = 0;
        for (int i = 0; i < checkpointCost.size(); i++) {
            sumCheckpointCost += checkpointCost.get(i);
        }
        double avgCheckpointCost = sumCheckpointCost / checkpointCost.size();
//        System.out.println("avgckcost:"+avgCheckpointCost);
        if (avgCheckpointCost <= recoveryCost && (superstep - lastCheckpoint >= this.getInterval())) {
            return CheckpointStatus.CHECKPOINT;
        }
        return CheckpointStatus.NONE;
    }
}
