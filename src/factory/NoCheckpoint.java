package factory;

import java.util.ArrayList;

public class NoCheckpoint extends CheckpointStrategy {



    @Override
    public CheckpointStatus getCheckpointStatus(int superstep, ArrayList<Integer> checkpointCost, int recoveryOverhead, int lastCheckpoint,  int[] computeTime) {
        return CheckpointStatus.NONE;
    }
}
