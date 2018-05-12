package factory;

import java.util.ArrayList;

public class StaticCheckpoint extends CheckpointStrategy {


    @Override
    public CheckpointStatus getCheckpointStatus(int superstep, ArrayList<Integer> checkpointCost, int recoveryOverhead, int lastCheckpoint, int[] computeTime) {
        long firstCheckpoint = 0;
        if (lastCheckpoint != -1) {
            firstCheckpoint = lastCheckpoint + this.getInterval();
        }
        if (superstep < firstCheckpoint) {
            return CheckpointStatus.NONE;
        }
        if (((superstep - firstCheckpoint) % this.getInterval()) == 0 && lastCheckpoint != superstep) {
            return CheckpointStatus.CHECKPOINT;

        }
        return CheckpointStatus.NONE;
    }
}
