import java.util.HashSet;

public class Simulator {
    public int lastCheckpoint = -1;
    public int restartedSuperstep = -1;
    public int supersteps = 0; //total supersteps
    // public int checkpointCost=0;
    public CheckpointStrategy checkpointStrategy;


    public Simulator() {
    }

    public Simulator(int supersteps, CheckpointStrategy checkpointStrategy) {
        this.supersteps = supersteps;
        this.checkpointStrategy = checkpointStrategy;
    }

    public static void main(String[] args) {
        CheckpointStrategy checkpointStrategy = new CheckpointStrategy(0, false);
        Simulator simulator = new Simulator(10, checkpointStrategy);
        //  simulator.checkpointCost=2;
        int[] computeTime1 = simulator.generateComputeTime(5);
        for (int i = 0; i < computeTime1.length; i++) {
            System.out.println(computeTime1[i]);
        }
        int[] computeTime2 = simulator.generateComputeTime(10, 1, 1000, 3);
        for (int i = 0; i < computeTime2.length; i++) {
            System.out.println(computeTime2[i]);
        }
        HashSet<Integer> failureSteps = new HashSet<Integer>();
        failureSteps.add(3);
        failureSteps.add(7);


    }

    /**
     * get checkpoint status for this superstep, under static interval checkpoint strategy
     *
     * @param superstep
     * @param checkpointStrategy
     * @return checkpoint status
     */
    public CheckpointStatus getCheckpointStatus(int superstep, CheckpointStrategy checkpointStrategy) {
        //no checkpoint
        if (checkpointStrategy.isDynamic() == false && checkpointStrategy.getInterval() == 0) {
            return CheckpointStatus.NONE;
        }
        //static interval checkpoint
        if (checkpointStrategy.isDynamic() == false && checkpointStrategy.getInterval() > 0) {
            long firstCheckpoint = 0;
            if (restartedSuperstep != -1) {
                firstCheckpoint = restartedSuperstep + checkpointStrategy.getInterval();
            }
            if (superstep < firstCheckpoint) {
                return CheckpointStatus.NONE;
            }
            if (((superstep - firstCheckpoint) % checkpointStrategy.getInterval()) == 0) {
                return CheckpointStatus.CHECKPOINT;

            }
            return CheckpointStatus.NONE;
        }
        return CheckpointStatus.NONE;
    }

    /**
     * generate computetime with uniform distribution
     *
     * @param value value for each elements
     * @return an array of computetime
     */
    public int[] generateComputeTime(int value) {
        int size = this.supersteps;
        int[] computeTime = new int[size];

        for (int i = 0; i < size; i++) {
            computeTime[i] = value;
        }

        return computeTime;
    }

    /**
     * generate computetime with normal distribution
     *
     * @param mean       mean value of the normal distribution
     * @param std_dev    standard deviation of the normal distribution
     * @param multiplier multiply the result of probability density
     * @param minvalue   default minvalue of the distribution
     * @return an array of computetime
     */
    public int[] generateComputeTime(int mean, int std_dev, int multiplier, int minvalue) {
        int size = this.supersteps;
        int[] computeTime = new int[size];

        for (int i = 0; i < size; i++) {
            double fx = 1 / (Math.sqrt(2 * Math.PI) * std_dev) * Math.pow(Math.E, (-0.5 * Math.pow(i - mean, 2) / Math.pow(std_dev, 2)));
            computeTime[i] = (int) Math.round(fx * multiplier);
            if (computeTime[i] < minvalue) {
                computeTime[i] = minvalue;
            }
        }
        return computeTime;
    }


    public void printResult(int[] computeTime, int checkpointTime, HashSet<Integer> failureSteps) {
        int superstep = 0;
        long time = 0;

        do {
            CheckpointStatus checkpointStatus = this.getCheckpointStatus(superstep, this.checkpointStrategy);
            long endCheckpoint = time;
            if (checkpointStatus == CheckpointStatus.CHECKPOINT) {
                endCheckpoint = time + checkpointTime;
                System.out.println("superstep:" + (superstep + 1) + "; checkpoint start:" + time + "; end:" + endCheckpoint + "; duration:" + checkpointTime);
                this.lastCheckpoint = superstep;
            }
            if (failureSteps.contains(superstep)) {
                System.out.println("kill at superstep:" + superstep);
                if (checkpointStrategy.getInterval() == 0 && checkpointStrategy.isDynamic() == false) {
                    break;
                } else {
                    System.out.println("restart from superstep:" + this.lastCheckpoint);
                    failureSteps.remove(superstep);
                    superstep=lastCheckpoint;
                    continue;
                }
            }else{
                long endCompute = endCheckpoint + computeTime[superstep];
                System.out.println("superstep:" + superstep + "; compute start:" + endCheckpoint + "; end:" + endCompute + "; duration:" + endCompute);
                time = endCompute;
            }
            superstep++;
        } while (superstep <= this.supersteps);

    }


}
