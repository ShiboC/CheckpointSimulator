package simple;

//granularity: attempt+superstep
public class IterationUnit {
    private int attepmt=0; //job attempt, increment when a job is restarted
    private int superstep=0;
    private boolean restarted=false; //this superstep is restarted or not
    private boolean killed=false; //this superstep is killed or not
    private long checkpointStart=0;
    private long checkpointEnd=0;
    private long computeStart=0;
    private long computeEnd=0;
    private long recoveryOverheadStart=0;
    private long recoveryOverheadEnd=0;

    @Override
    public String toString() {
        return "simple.IterationUnit{" +
                "attepmt=" + attepmt +
                ", superstep=" + superstep +
                ", restarted=" + restarted +
                ", killed=" + killed +
                ", checkpointStart=" + checkpointStart +
                ", checkpointEnd=" + checkpointEnd +
                ", computeStart=" + computeStart +
                ", computeEnd=" + computeEnd +
                ", recoveryOverheadStart=" + recoveryOverheadStart +
                ", recoveryOverheadEnd=" + recoveryOverheadEnd +
                '}';
    }

    public int getAttepmt() {
        return attepmt;
    }

    public void setAttepmt(int attepmt) {
        this.attepmt = attepmt;
    }

    public int getSuperstep() {
        return superstep;
    }

    public void setSuperstep(int superstep) {
        this.superstep = superstep;
    }

    public boolean isRestarted() {
        return restarted;
    }

    public void setRestarted(boolean restarted) {
        this.restarted = restarted;
    }

    public boolean isKilled() {
        return killed;
    }

    public void setKilled(boolean killed) {
        this.killed = killed;
    }

    public long getCheckpointStart() {
        return checkpointStart;
    }

    public void setCheckpointStart(long checkpointStart) {
        this.checkpointStart = checkpointStart;
    }

    public long getCheckpointEnd() {
        return checkpointEnd;
    }

    public void setCheckpointEnd(long checkpointEnd) {
        this.checkpointEnd = checkpointEnd;
    }

    public long getComputeStart() {
        return computeStart;
    }

    public void setComputeStart(long computeStart) {
        this.computeStart = computeStart;
    }

    public long getComputeEnd() {
        return computeEnd;
    }

    public void setComputeEnd(long computeEnd) {
        this.computeEnd = computeEnd;
    }

    public long getRecoveryOverheadStart() {
        return recoveryOverheadStart;
    }

    public void setRecoveryOverheadStart(long recoveryOverheadStart) {
        this.recoveryOverheadStart = recoveryOverheadStart;
    }

    public long getRecoveryOverheadEnd() {
        return recoveryOverheadEnd;
    }

    public void setRecoveryOverheadEnd(long recoveryOverheadEnd) {
        this.recoveryOverheadEnd = recoveryOverheadEnd;
    }
}
