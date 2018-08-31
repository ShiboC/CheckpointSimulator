
import java.util.ArrayList;

public abstract class CheckpointStrategy {
    private int interval = 0;

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public abstract CheckpointStatus getCheckpointStatus(int superstep, ArrayList<Integer> checkpointCost, int lastCheckpoint, int[] computeTime);

}
