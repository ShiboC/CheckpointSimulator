package simple;

import java.util.ArrayList;
import java.util.HashSet;

public class Simulator {
    private int lastCheckpoint = -1;
    private int restartedSuperstep = -1;
    private int supersteps = 0; //total supersteps
    private int recoveryOverhead = 0;
    private int checkpointCost = 0;
    private CheckpointStrategy checkpointStrategy;
//    private ArrayList<simple.IterationUnit> iterationUnits =new ArrayList<simple.IterationUnit>(); //each iteration's running time result

    public Simulator() {
    }

    public Simulator(int supersteps, CheckpointStrategy checkpointStrategy) {
        this.supersteps = supersteps;
        this.checkpointStrategy = checkpointStrategy;
    }

    public static void main(String[] args) {
        CheckpointStrategy checkpointStrategy = new CheckpointStrategy(1, false);
        Simulator simulator = new Simulator(10, checkpointStrategy);
        simulator.recoveryOverhead = 2;
        simulator.checkpointCost = 5;
        int[] computeTime1 = simulator.generateComputeTime(2);
        int[] computeTime2 = simulator.generateComputeTime(3, 3, 100, 3);

        HashSet<Integer> failureSteps = new HashSet<Integer>();
        failureSteps.add(3);
        failureSteps.add(7);
        HashSet<Integer> backup = new HashSet<Integer>();
        //failureSteps add by poisson distribution
        int lamda = 1;
        for (int i = 1; i <= simulator.supersteps; i++) {
            double rate = poissonDistributionDensity(i, 1);
            if (Math.random() <= rate) {
                failureSteps.add(i);
            }
        }

        System.out.println("failureSteps:" + failureSteps.toString());
        backup = (HashSet<Integer>) failureSteps.clone();
        simulator.printResult(computeTime1, failureSteps);
        simulator.restartedSuperstep = -1;
        simulator.lastCheckpoint = -1;
//        System.out.println(backup);
        ArrayList<IterationUnit> iterationUnits = simulator.generateResult(computeTime1, backup);
        for (int i = 0; i < iterationUnits.size(); i++) {
            System.out.println(iterationUnits.get(i));
        }
    }

    /**
     * get checkpoint status for this superstep, under static interval checkpoint strategy
     *
     * @param superstep
     * @param checkpointStrategy
     * @return checkpoint status
     */
    public CheckpointStatus getCheckpointStatus(int superstep, CheckpointStrategy checkpointStrategy, int[] computeTime) {
        //no checkpoint
        if (checkpointStrategy.isDynamic() == false && checkpointStrategy.getInterval() == 0) {
            return CheckpointStatus.NONE;
        }
        //static interval checkpoint
        if (checkpointStrategy.isDynamic() == false) {
            long firstCheckpoint = 0;
            if (restartedSuperstep != -1) {
                firstCheckpoint = restartedSuperstep + checkpointStrategy.getInterval();
            }
            if (superstep < firstCheckpoint) {
                return CheckpointStatus.NONE;
            }
            if (((superstep - firstCheckpoint) % checkpointStrategy.getInterval()) == 0 && lastCheckpoint != superstep) {
                return CheckpointStatus.CHECKPOINT;

            }
            return CheckpointStatus.NONE;
        }
        //dynamic checkpoint
        if (checkpointStrategy.isDynamic() == true) {
            if (lastCheckpoint == -1) {
                return CheckpointStatus.CHECKPOINT;
            }
            int recoveryCost = recoveryOverhead;

            for (int i = lastCheckpoint; i < superstep; i++) {
                recoveryCost += computeTime[i];
            }
            if (checkpointCost <= recoveryCost && (superstep - lastCheckpoint >= checkpointStrategy.getInterval())) {
                return CheckpointStatus.CHECKPOINT;
            }
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
        int size = this.supersteps + 1;
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
    public int[] generateComputeTime(double mean, double std_dev, double multiplier, int minvalue) {
        int size = this.supersteps;
        int[] computeTime = new int[size + 1];

        for (int i = 0; i < size; i++) {
            double fx = 1 / (Math.sqrt(2 * Math.PI) * std_dev) * Math.pow(Math.E, (-0.5 * Math.pow(i - mean, 2) / Math.pow(std_dev, 2)));
            computeTime[i] = (int) Math.round(fx * multiplier);
            if (computeTime[i] < minvalue) {
                computeTime[i] = minvalue;
            }
        }
        return computeTime;
    }

    public static double poissonDistributionDensity(int k, int lambda) {
        int kProd = 1;
        for (int i = 1; i <= k; i++) {
            kProd *= i;
        }
        return Math.pow(lambda, k) * Math.pow(Math.E, -lambda) / kProd;
    }

    public ArrayList<IterationUnit> generateResult(int[] computeTime, HashSet<Integer> failureSteps) {
        ArrayList<IterationUnit> iterationUnits = new ArrayList<>();
        int superstep = 0;
        long time = 0;
        do {
//            System.out.println(superstep);
            IterationUnit iterationUnit = new IterationUnit();
            if (iterationUnits.size() != 0) {
                if (!iterationUnits.get(iterationUnits.size() - 1).isRestarted() || iterationUnits.get(iterationUnits.size() - 1).getComputeEnd() != 0) {//if it is not the a just restarted superstep
                    iterationUnit.setAttepmt(iterationUnits.get(iterationUnits.size() - 1).getAttepmt());// attempt equals to the last one
                    iterationUnit.setSuperstep(superstep);
                    iterationUnits.add(iterationUnit);
//                    System.out.println("not just restarted. add new");
                }
            } else {
                iterationUnits.add(iterationUnit);
//                System.out.println("size 0 add new");
            }


            CheckpointStatus checkpointStatus = this.getCheckpointStatus(superstep, this.checkpointStrategy, computeTime);
            if (checkpointStatus == CheckpointStatus.CHECKPOINT) {
                iterationUnit.setCheckpointStart(time);
                time += checkpointCost;
                iterationUnit.setCheckpointEnd(time);
                this.lastCheckpoint = superstep;
//                System.out.println("do checkpoint");
            }
            if (failureSteps.contains(superstep)) {
//                System.out.println("do kill");
                iterationUnit.setKilled(true);
//                System.out.println(iterationUnit);
                if (checkpointStrategy.getInterval() == 0 && checkpointStrategy.isDynamic() == false) {//no checkpoint. job stopped
//                    System.out.println("ck none. jump");
                    break;
                } else {//restart
//                    System.out.println("restarted");
                    IterationUnit iterationUnit2 = new IterationUnit();
                    iterationUnit2.setAttepmt(iterationUnits.get(iterationUnits.size() - 1).getAttepmt() + 1);//attempt incremented
                    iterationUnit2.setRestarted(true);
                    iterationUnit2.setSuperstep(this.lastCheckpoint);
                    iterationUnit2.setRecoveryOverheadStart(time);
                    time += recoveryOverhead;
                    iterationUnit2.setRecoveryOverheadEnd(time);
                    iterationUnits.add(iterationUnit2);
                    failureSteps.remove(superstep);
                    superstep = lastCheckpoint;
//                    System.out.println(iterationUnit2);
                    continue;
                }
            } else {
                iterationUnits.get(iterationUnits.size() - 1).setComputeStart(time);
                time += computeTime[superstep];
                iterationUnits.get(iterationUnits.size() - 1).setComputeEnd(time);
//                System.out.println("compute");
//                System.out.println(iterationUnits.get(iterationUnits.size()-1));
            }
            superstep++;
        } while (superstep <= this.supersteps);
        return iterationUnits;

    }

    public void printResult(int[] computeTime, HashSet<Integer> failureSteps) {
        int superstep = 0;
        long time = 0;

        do {
            CheckpointStatus checkpointStatus = this.getCheckpointStatus(superstep, this.checkpointStrategy, computeTime);
            long endCheckpoint = time;
            if (checkpointStatus == CheckpointStatus.CHECKPOINT) {
                endCheckpoint = time + checkpointCost;
                System.out.println("superstep:" + superstep + "; checkpoint start:" + time + "; end:" + endCheckpoint +
                        "; duration:" + checkpointCost);
                this.lastCheckpoint = superstep;
                time += checkpointCost;
            }
            if (failureSteps.contains(superstep)) {
                System.out.println("kill at superstep:" + superstep);
                if (checkpointStrategy.getInterval() == 0 && checkpointStrategy.isDynamic() == false) {
                    break;
                } else {
                    System.out.println("restart from superstep:" + this.lastCheckpoint);
                    System.out.println("recovery overhead start:" + time + "; end:" + (time + recoveryOverhead) +
                            "; duration:" + recoveryOverhead);
                    time += recoveryOverhead;
                    failureSteps.remove(superstep);
                    superstep = lastCheckpoint;
                    continue;
                }
            } else {
                long endCompute = endCheckpoint + computeTime[superstep];
                System.out.println("superstep:" + superstep + "; compute start:" + endCheckpoint + "; end:" +
                        endCompute + "; duration:" + (endCompute - endCheckpoint));
                time = endCompute;
            }
            superstep++;
        } while (superstep <= this.supersteps);

    }

    /*
     * e为期望值，row为需要生成的随机数的个数
     */
    public ArrayList<Float> expntl(float e, int row) {
        float t, temp;
        ArrayList<Float> a = new ArrayList<Float>();
        for (int i = 0; i < row; i++) {
            t = (float) Math.random();
            temp = (float) (-e * Math.log(t));//                x = -(1 / lamda) * Math.log(z);

            a.add(temp);
        }
        return a;
    }




}
