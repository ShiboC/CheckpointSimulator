package factory;

import java.util.ArrayList;

public class StaticCheckpoint extends CheckpointStrategy {


    @Override
    public CheckpointStatus getCheckpointStatus(int superstep, ArrayList<Integer> checkpointCost, int recoveryOverhead, int lastCheckpoint, int restartedSuperstep, int[] computeTime) {
        long firstCheckpoint = 0;
        if (restartedSuperstep != -1) {
            firstCheckpoint = restartedSuperstep + this.getInterval();
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
