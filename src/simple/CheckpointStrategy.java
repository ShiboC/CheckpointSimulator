package simple;

public class CheckpointStrategy {
    private int interval;
    private boolean dynamic; //if is false, then it is static checkpoint strategy

    public CheckpointStrategy(int interval, boolean dynamic) {
        this.interval = interval;
        this.dynamic = dynamic;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }
}
