package factory;


import java.util.ArrayList;
import java.util.HashSet;

public class Simulator implements Cloneable {
    private int lastCheckpoint = -1;
    private int restartedSuperstep = -1;
    private int supersteps = 0; //total supersteps
    private int recoveryOverhead = 0;
    private int[] checkpointCost;
    private int[] computeTime;
    private HashSet<Integer> failureSteps = new HashSet<Integer>();
    private int[] failureTime;
    private CheckpointStrategy checkpointStrategy;

    //    private ArrayList<simple.IterationUnit> iterationUnits =new ArrayList<simple.IterationUnit>(); //each iteration's running time result
    public Simulator() {
    }

    ;

    public Simulator(int supersteps, int recoveryOverhead, CheckpointStrategy checkpointStrategy) {
        this.supersteps = supersteps;
        this.checkpointStrategy = checkpointStrategy;
        this.recoveryOverhead = recoveryOverhead;
    }

    public static void main(String[] args) throws Exception {
        // set up checkpoint strategy.
        CheckpointStrategy checkpointStrategy = CheckpointStrategyFactory.getClass(NoCheckpoint.class);
        //   checkpointStrategy.setInterval(2);
        // configure simulator
        Simulator simulator = new Simulator(10, 1, checkpointStrategy);

        simulator.recoveryOverhead = 2;
        // pre-generate checkpointCost Pool, actual checkpoint number is less, will be added during running.
        simulator.checkpointCost = DataGenerator.getSameData(simulator.supersteps + 1, 5);
        simulator.computeTime = DataGenerator.modifyData(DataGenerator.getNormalDistributionDensity(
                simulator.supersteps + 1, 3, 3), 100, 3);
        simulator.computeTime = DataGenerator.getSameData(simulator.supersteps + 1, 2);

        Simulator a = (Simulator) simulator.clone();
        //set up failure step up, failure time interval follows exponential distribution.
        // assume the whole running time without failure is 1, lambda is the expected failure numbers.
        simulator.failureSteps = simulator.generateFailureSteps(3);
        System.out.println("failureSteps:" + simulator.failureSteps.toString());

        simulator.failureTime = simulator.generateFailureTime(3);
        System.out.println("failureTime:");
        for (int ft : simulator.failureTime) {
            System.out.print(ft + ",");
        }
        System.out.println();
//        simulator.printResult(simulator.computeTime, simulator.failureSteps);

//        ArrayList<IterationUnit> iterationUnits = simulator.generateResultByStep(simulator.failureSteps);
        ArrayList<IterationUnit> iterationUnits = simulator.generateResultByTime(simulator.failureTime);

        for (int i = 0; i < iterationUnits.size(); i++) {
            System.out.println(iterationUnits.get(i));
        }
    }

    // failure steps' time interval follows exponential distribution
    public HashSet<Integer> generateFailureSteps(int lambda) throws CloneNotSupportedException {
        HashSet<Integer> failureSteps = new HashSet<Integer>();
        Simulator stemp = (Simulator) this.clone();
        ArrayList<IterationUnit> iterationUnitsWithoutFailure = stemp.generateResultByStep(failureSteps);
        long totalTime = iterationUnitsWithoutFailure.get(iterationUnitsWithoutFailure.size() - 1).getComputeEnd();
//        System.out.println(totalTime);
        double[] failureIntervalList = DataGenerator.getExponentialDistributionData(stemp.supersteps, lambda);
        double timer = failureIntervalList[0] * totalTime;

        int failureCounter = 0;
        for (IterationUnit i : iterationUnitsWithoutFailure) {
//            System.out.println("timer:"+timer);
            if (i.getCheckpointStart() <= timer && i.getComputeEnd() >= timer) {
                failureSteps.add(i.getSuperstep());
                failureCounter++;
                timer += failureIntervalList[failureCounter] * totalTime;
            }
        }
        return failureSteps;

    }

    public int[] generateFailureTime(int lambda) throws CloneNotSupportedException {

        Simulator stemp = (Simulator) this.clone();
        ArrayList<IterationUnit> iterationUnitsWithoutFailure = stemp.generateResultByStep(new HashSet<Integer>());
        long totalTime = iterationUnitsWithoutFailure.get(iterationUnitsWithoutFailure.size() - 1).getComputeEnd();
//        System.out.println(totalTime);
        double[] failureIntervalList = DataGenerator.getExponentialDistributionData(stemp.supersteps, lambda);
        int[] failureTime = new int[stemp.supersteps];
        failureTime[0] = (int) Math.round(failureIntervalList[0] * totalTime);
        for (int i = 1; i < stemp.supersteps; i++) {
            failureTime[i] = failureTime[i - 1] + (int) Math.round(failureIntervalList[i] * totalTime);
        }
        return failureTime;

    }

    public ArrayList<IterationUnit> generateResultByStep(HashSet<Integer> failureSteps) {
        ArrayList<IterationUnit> iterationUnits = new ArrayList<>();
        int superstep = 0;
        long time = 0;
        int checkpointCounter = 0;
        ArrayList<Integer> checkpointCostActual = new ArrayList<Integer>();
        do {
//            System.out.println(superstep);
            IterationUnit iterationUnit = new IterationUnit();
            if (iterationUnits.size() != 0) {
                if (iterationUnits.get(iterationUnits.size() - 1).getRecoveryOverheadStart() == 0 || iterationUnits.get(iterationUnits.size() - 1).getComputeEnd() != 0) {//if it is not the a just restarted superstep
                    iterationUnit.setAttepmt(iterationUnits.get(iterationUnits.size() - 1).getAttepmt());// attempt equals to the last one
                    iterationUnit.setSuperstep(superstep);
                    iterationUnits.add(iterationUnit);
//                    System.out.println("not just restarted. add new");
                }
            } else {
                iterationUnits.add(iterationUnit);
//                System.out.println("size 0 add new");
            }
            CheckpointStatus checkpointStatus = this.checkpointStrategy.getCheckpointStatus(superstep, checkpointCostActual, this.recoveryOverhead, this.lastCheckpoint, this.restartedSuperstep, this.computeTime);
            if (checkpointStatus == CheckpointStatus.CHECKPOINT) {
                iterationUnit.setCheckpointStart(time);
                time += checkpointCost[checkpointCounter];
                checkpointCostActual.add(checkpointCost[checkpointCounter]);
                checkpointCounter++;
                iterationUnit.setCheckpointEnd(time);
                this.lastCheckpoint = superstep;
//                System.out.println("do checkpoint");
            }
            if (failureSteps.contains(superstep)) {
//                System.out.println("do kill");
                iterationUnit.setKillTime(time);
//                System.out.println(iterationUnit);
                if (checkpointStrategy.getInterval() == 0 && this.lastCheckpoint == -1) {//no checkpoint. job stopped
//                    System.out.println("ck none. jump");
                    return iterationUnits;
                } else {//restart
//                    System.out.println("restarted");
                    IterationUnit iterationUnit2 = new IterationUnit();
                    iterationUnit2.setAttepmt(iterationUnits.get(iterationUnits.size() - 1).getAttepmt() + 1);//attempt incremented
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
//                System.out.println("superstep:"+superstep);
                time += computeTime[superstep];
                iterationUnits.get(iterationUnits.size() - 1).setComputeEnd(time);
//                System.out.println("compute");
//                System.out.println(iterationUnits.get(iterationUnits.size()-1));
            }
            superstep++;
        } while (superstep <= this.supersteps);
        return iterationUnits;

    }

    public ArrayList<IterationUnit> generateResultByTime(int[] failureTime) {
        ArrayList<IterationUnit> iterationUnits = new ArrayList<>();
        int superstep = 0;
        long time = 0;
        int checkpointCounter = 0;
        int failureCounter = 0;
        ArrayList<Integer> checkpointCostActual = new ArrayList<Integer>();
        do {
//            System.out.println(superstep);
            IterationUnit iterationUnit = new IterationUnit();

            //start, set up attempt and superstep, check if it's a restarted.
            if (iterationUnits.size() != 0) {
                if (iterationUnits.get(iterationUnits.size() - 1).getKillTime() == 0) {//if it is not the a just restarted superstep
                    iterationUnit.setAttepmt(failureCounter);// attempt equals to the last one
                    iterationUnit.setSuperstep(superstep);
                    iterationUnits.add(iterationUnit);
//                    System.out.println("not just restarted. add new");
                } else { //a restart
                    iterationUnit.setAttepmt(failureCounter);// attempt equals to the last one
                    iterationUnit.setSuperstep(superstep);
                    iterationUnits.add(iterationUnit);
                    iterationUnit.setRecoveryOverheadStart(time);
                    if (failureTime[failureCounter] >= time && failureTime[failureCounter] <= time + recoveryOverhead) {
                        iterationUnit.setKillTime(failureTime[failureCounter]);
                        time = failureTime[failureCounter];

                        failureCounter++;
                        if (this.checkpointStrategy.getClass()==NoCheckpoint.class) {
                            return iterationUnits;
                        }
                        superstep = lastCheckpoint;
                        continue;
                    }
                    time += recoveryOverhead;
                    iterationUnit.setRecoveryOverheadEnd(time);
                }
            } else {
                iterationUnits.add(iterationUnit);
//                System.out.println("size 0 add new");
            }
            //do checkpoint
            CheckpointStatus checkpointStatus = this.checkpointStrategy.getCheckpointStatus(superstep, checkpointCostActual, this.recoveryOverhead, this.lastCheckpoint, this.restartedSuperstep, this.computeTime);
            if (checkpointStatus == CheckpointStatus.CHECKPOINT) {
                iterationUnit.setCheckpointStart(time);
                if (failureTime[failureCounter] >= time && failureTime[failureCounter] <= time + checkpointCost[checkpointCounter]) {
                    iterationUnit.setKillTime(failureTime[failureCounter]);
                    time = failureTime[failureCounter];

                    failureCounter++;

                    if (this.checkpointStrategy.getClass()==NoCheckpoint.class) {
                        return iterationUnits;
                    }
                    superstep = lastCheckpoint;
                    continue;
                }
                time += checkpointCost[checkpointCounter];
                checkpointCostActual.add(checkpointCost[checkpointCounter]);
                checkpointCounter++;
                iterationUnit.setCheckpointEnd(time);
                this.lastCheckpoint = superstep;
//                System.out.println("do checkpoint");
            }
            //do compute

            iterationUnit.setComputeStart(time);
            if (failureTime[failureCounter] >= time && failureTime[failureCounter] <= time + computeTime[superstep]) {
                iterationUnit.setKillTime(failureTime[failureCounter]);
                time = failureTime[failureCounter];

                failureCounter++;

                if (this.checkpointStrategy.getClass()==NoCheckpoint.class) {
                    return iterationUnits;
                }
                superstep = lastCheckpoint;
                continue;
            }
            time += computeTime[superstep];

            iterationUnit.setComputeEnd(time);

            superstep++;
        } while (superstep <= this.supersteps);
        return iterationUnits;

    }

    public void printResult(int[] computeTime, HashSet<Integer> failureSteps) {
        int superstep = 0;
        long time = 0;
        int checkpointCounter = 0;
        ArrayList<Integer> checkpointCostActual = new ArrayList<Integer>();

        do {
            CheckpointStatus checkpointStatus = this.checkpointStrategy.getCheckpointStatus(superstep, checkpointCostActual, this.recoveryOverhead, this.lastCheckpoint, this.restartedSuperstep, this.computeTime);
            long endCheckpoint = time;
            if (checkpointStatus == CheckpointStatus.CHECKPOINT) {
                endCheckpoint = time + checkpointCost[checkpointCounter];
                System.out.println("superstep:" + superstep + "; checkpoint start:" + time + "; end:" + endCheckpoint +
                        "; duration:" + checkpointCost[checkpointCounter]);
                this.lastCheckpoint = superstep;
                time += checkpointCost[checkpointCounter];
                checkpointCostActual.add(checkpointCost[checkpointCounter]);
                checkpointCounter++;
            }
            if (failureSteps.contains(superstep)) {
                System.out.println("kill at superstep:" + superstep);
                if (checkpointStrategy.getInterval() == 0 && this.lastCheckpoint == -1) {
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


}
