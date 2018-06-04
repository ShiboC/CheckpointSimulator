package factory;


import java.util.ArrayList;

public class Simulator implements Cloneable {
    private int lastCheckpoint = -1;
    private int supersteps = 0; //total supersteps
    private int recoveryOverhead = 0;
    private int[] checkpointCost;
    private int[] computeTime;
    private int[] failureSteps;
    private int[] failureInterval;
    private CheckpointStrategy checkpointStrategy;

    public Simulator() {
    }

    ;

    public Simulator(int supersteps, int recoveryOverhead) {
        this.supersteps = supersteps;
        this.checkpointStrategy = new NoCheckpoint();
        this.recoveryOverhead = recoveryOverhead;
    }

    public Simulator(int supersteps, int recoveryOverhead, CheckpointStrategy checkpointStrategy) {
        this.supersteps = supersteps;
        this.checkpointStrategy = checkpointStrategy;
        this.recoveryOverhead = recoveryOverhead;
    }

    public static void main(String[] args) throws Exception {
        // set up checkpoint strategy.
        CheckpointStrategy checkpointStrategy = CheckpointStrategyFactory.getClass(DynamicCheckpoint.class);
        checkpointStrategy.setInterval(1);
        // configure simulator
        Simulator simulator = new Simulator(47, 1, checkpointStrategy);
        simulator.recoveryOverhead = 60000;
        // pre-generate checkpointCost Pool, actual checkpoint number during runtime is less
//        simulator.checkpointCost = DataGenerator.getSameData(simulator.supersteps + 2, 10);
//        simulator.checkpointCost = DataGenerator.modifyData(DataGenerator.getNormalDistributionData(simulator.supersteps + 1, 10, 1), 1, 0);
//        simulator.computeTime = DataGenerator.modifyData(DataGenerator.getNormalDistributionDensity(
//                simulator.supersteps + 1, 16, 4), 100000, 1500);
//        simulator.computeTime = DataGenerator.getSameData(simulator.supersteps + 1, 2);
        int[] computeTimeList = {10577, 1640, 1504, 1554, 1596, 1626, 1465, 1486, 1665, 2074, 1886, 1943, 2690, 4253, 6163, 7967, 9426, 9405, 8493, 6865, 5502, 4485, 3686, 2955, 2637, 2483, 2049, 1949, 1940, 1884, 1801, 1745, 1643, 1696, 1737, 1605, 1640, 1700, 1584, 1604, 1606, 1642, 1776, 1584, 1584, 1733, 1706, 1626};
        simulator.computeTime = computeTimeList;
//        simulator.computeTime = DataGenerator.getSameData(simulator.supersteps + 1, 4000);
        int[] checkpointTimeList = {11611, 7710, 8122, 9157, 6366, 9188, 8474, 6847, 8971, 8570, 8505, 7563, 7571, 6634, 7548, 9486, 7410, 6493, 7103, 8425, 7529, 9562, 8125, 8780, 7725, 8038, 8343, 8576, 8672, 6270, 10851, 7417, 7355, 8330, 7763, 6738, 9006, 8582, 7763, 7009, 9089, 8224, 6692, 8923, 7565, 6613, 7331, 7343};
//        for(int i=0;i<checkpointTimeList.length;i++){
//            checkpointTimeList[i]=checkpointTimeList[i]*2;
//        }
        simulator.checkpointCost = checkpointTimeList;


        //set up failure step up, failure time interval follows exponential distribution.
        // assume the whole running time without failure is 1, lambda is the expected failure numbers.
//        simulator.failureSteps = simulator.generateFailureSteps(3);
//        System.out.println("failureSteps:" + simulator.failureSteps.toString());

//        simulator.failureInterval = DataGenerator.modifyData(DataGenerator.getExponentialDistributionData(60, 1), 150000, 1);
        simulator.failureInterval=new int[]{112027,86757,827906,147203,563252,188700,167606,5829,62180,6231,86711,158742,11460,16595,135668,444881,660217,172675,6986,104897,265963,306913,37972,343993,32120,56839,23819,5767,356511,102260,287978,8332,255854,103459,92284,12013,113651,6071,201003,306263,380574,172054,244695,66961,617154,54190,570438,39913,648543,223070,233269,60147,19629,41364,239512,37431,42694,602392,383993,60530

        };
//        System.out.println("failureInterval:");
//        for (int ft : simulator.failureInterval) {
//            System.out.print(ft + ",");
//        }
//        System.out.println();
//        simulator.failureSteps = new int[]{7,19,31,43};
        System.out.println("dynamic checkpointing result");
        //result can be generated two ways, one is by predefined superstep, one is by predefined failure interval.
        simulator.checkpointStrategy = CheckpointStrategyFactory.getClass(DynamicCheckpoint.class);
        for (int i = 1; i <= 5; i++) {
            simulator.checkpointStrategy.setInterval(i);
//            ArrayList<IterationUnit> iterationUnits = simulator.generateResultByStep();
            ArrayList<IterationUnit> iterationUnits = simulator.generateResultByTime(false);
            //get total number of checkpoints
            int checkpointCounter = 0;
            for (int j = 0; j < iterationUnits.size(); j++) {
                if (iterationUnits.get(j).getCheckpointEnd() != 0) {
                    checkpointCounter++;
                }
            }
//            System.out.println("interval:" + i + ",failureCounter:" + iterationUnits.get(iterationUnits.size() - 1).getAttepmt() +
//                    ",CheckpointCounter:" + checkpointCounter +
//                    ",IterationCounter:" + iterationUnits.size() +
//                    ",totaltime:" + iterationUnits.get(iterationUnits.size() - 1).getComputeEnd());
            System.out.println(
                    iterationUnits.get(iterationUnits.size() - 1).getComputeEnd()+
                    ":" + iterationUnits.get(iterationUnits.size() - 1).getAttepmt() +
                    ":" + checkpointCounter +
                    ":" + iterationUnits.size());
        }

        System.out.println("static checkpointing result");
        simulator.checkpointStrategy = CheckpointStrategyFactory.getClass(StaticCheckpoint.class);
        for (int i = 1; i <= 5; i++) {
            simulator.checkpointStrategy.setInterval(i);
//            ArrayList<IterationUnit> iterationUnits = simulator.generateResultByStep();
            ArrayList<IterationUnit> iterationUnits = simulator.generateResultByTime(false);
            //get total number of checkpoints
            int checkpointCounter = 0;
            for (int j = 0; j < iterationUnits.size(); j++) {
                if (iterationUnits.get(j).getCheckpointEnd() != 0) {
                    checkpointCounter++;
                }
            }
//            System.out.println("interval:" + i + ",failureCounter:" + iterationUnits.get(iterationUnits.size() - 1).getAttepmt() +
//                    ",CheckpointCounter:" + checkpointCounter +
//                    ",IterationCounter:" + iterationUnits.size() +
//                    ",totaltime:" + iterationUnits.get(iterationUnits.size() - 1).getComputeEnd());
            System.out.println( iterationUnits.get(iterationUnits.size() - 1).getAttepmt() +
                    ":" + checkpointCounter +
                    ":" + iterationUnits.size() +
                    ":" + iterationUnits.get(iterationUnits.size() - 1).getComputeEnd());
        }

        //output detailed result of iterations to CSV
//        simulator.checkpointStrategy=CheckpointStrategyFactory.getClass(NoCheckpoint.class);
//        ArrayList<IterationUnit> iterationUnits = simulator.generateResultByStep();
//        ArrayList<IterationUnit> iterationUnits = simulator.generateResultByTime();
//        CSVUtils.exportCsv(resultPath, iterationUnits1);


        //output detailed result of iterations to console
//        for (int i = 0; i < iterationUnits.size(); i++) {
//            System.out.println(iterationUnits.get(i));
//        }
    }


    public ArrayList<IterationUnit> generateResultByStep() {//only fail at compute part.
        ArrayList<IterationUnit> iterationUnits = new ArrayList<>();
        int superstep = 0;
        long time = 0;
        int checkpointCounter = 0;
        int failureCounter = 0;
        ArrayList<Integer> checkpointCostActual = new ArrayList<Integer>();
        do {
            IterationUnit iterationUnit = new IterationUnit();

            //start, set up attempt and superstep, check if it's a restarted.
            if (iterationUnits.size() != 0) {//if the loop is not first started, the iterationUnits is not empty
                if (iterationUnits.get(iterationUnits.size() - 1).getKillTime() == 0) {//if it is not the a just restarted superstep
                    iterationUnit.setAttepmt(failureCounter);// attempt equals to the last one
                    iterationUnit.setSuperstep(superstep);
                    iterationUnits.add(iterationUnit);
                } else { //a restart
                    iterationUnit.setAttepmt(failureCounter);// attempt equals to the last one
                    iterationUnit.setSuperstep(superstep);
                    iterationUnits.add(iterationUnit);
                    iterationUnit.setRecoveryOverheadStart(time);
                    time += recoveryOverhead;
                    iterationUnit.setRecoveryOverheadEnd(time);
                }
            } else {//the loop is just started, the iterationUnits is still empty
                iterationUnits.add(iterationUnit);
            }
            //do checkpoint
            CheckpointStatus checkpointStatus = this.checkpointStrategy.getCheckpointStatus(superstep, checkpointCostActual, this.lastCheckpoint, this.computeTime);
            if (checkpointStatus == CheckpointStatus.CHECKPOINT) {
                iterationUnit.setCheckpointStart(time);
                time += checkpointCost[checkpointCounter];
                checkpointCostActual.add(checkpointCost[checkpointCounter]);
                checkpointCounter++;
                iterationUnit.setCheckpointEnd(time);
                this.lastCheckpoint = superstep;
            }
            //do compute
            iterationUnit.setComputeStart(time);
            if (failureSteps!=null&& failureSteps.length > failureCounter && failureSteps[failureCounter] == superstep) {
                iterationUnit.setKillTime(time);
                if (failureCounter < this.supersteps - 1) {
                    failureCounter++;
                }
                if (this.checkpointStrategy.getClass() == NoCheckpoint.class) {
                    return iterationUnits;
                }
                superstep = (lastCheckpoint == -1) ? 0 : lastCheckpoint;
                continue;
            }
            time += computeTime[superstep];
            iterationUnit.setComputeEnd(time);
            superstep++;
        } while (superstep <= this.supersteps);
        //reset simulator status: lastCheckpoint,
        this.lastCheckpoint = -1;
        return iterationUnits;

    }

    public ArrayList<IterationUnit> generateResultByTime(boolean enableKillatCheckpointRecovery) {
        ArrayList<IterationUnit> iterationUnits = new ArrayList<>();
        int superstep = 0;
        long time = 0;
        long nextFailtime = Long.MAX_VALUE;
        int checkpointCounter = 0;
        int failureCounter = 0;
        ArrayList<Integer> checkpointCostActual = new ArrayList<Integer>();
        if (failureInterval!=null) {
            nextFailtime = failureInterval[0];
        }
        do {
            IterationUnit iterationUnit = new IterationUnit();
            //start, set up attempt and superstep, check if it's a restarted.
            if (iterationUnits.size() != 0) {//if the loop is not first started, the iterationUnits is not empty
                if (iterationUnits.get(iterationUnits.size() - 1).getKillTime() == 0) {//if it is not the a just restarted superstep
                    iterationUnit.setAttepmt(failureCounter);// attempt equals to the last one, which is also the same as failure Counter.
                    iterationUnit.setSuperstep(superstep);
                    iterationUnits.add(iterationUnit);
                } else { //a restart,need to add recoveryoverhead and deal with failure during recovery.
                    iterationUnit.setAttepmt(failureCounter);// attempt equals to the last one
                    iterationUnit.setSuperstep(superstep);
                    iterationUnits.add(iterationUnit);
                    iterationUnit.setRecoveryOverheadStart(time);
                    // usually not enable kill at recovery period
                    if (enableKillatCheckpointRecovery) {
                        if (failureInterval!=null&&failureCounter < failureInterval.length && nextFailtime <= time + computeTime[superstep]) {
                            time = Math.max(nextFailtime, time);
                            iterationUnit.setKillTime(time);
                            if (failureCounter < this.supersteps - 1) {
                                failureCounter++;
                            }
                            if (this.checkpointStrategy.getClass() == NoCheckpoint.class) {
                                return iterationUnits;
                            }
                            superstep = (lastCheckpoint == -1) ? 0 : lastCheckpoint;
                            continue;
                        }
                    }
                    time += recoveryOverhead;
                    iterationUnit.setRecoveryOverheadEnd(time);
                    //after recovery, set up next fail time.
                    nextFailtime = time + failureInterval[failureCounter];
                }
            } else { //the loop is just started, the iterationUnits is still empty
                iterationUnits.add(iterationUnit);
            }
            //do checkpoint
            CheckpointStatus checkpointStatus = this.checkpointStrategy.getCheckpointStatus(superstep, checkpointCostActual,  this.lastCheckpoint, this.computeTime);
            if (checkpointStatus == CheckpointStatus.CHECKPOINT) {
                iterationUnit.setCheckpointStart(time);
                // usually not enable kill at recovery period
                if (enableKillatCheckpointRecovery) {
                    if (failureInterval!=null&&failureCounter < failureInterval.length && nextFailtime <= time + computeTime[superstep]) {
                        time = Math.max(nextFailtime, time);
                        iterationUnit.setKillTime(time);
                        if (failureCounter < this.supersteps - 1) {
                            failureCounter++;
                        }
                        if (this.checkpointStrategy.getClass() == NoCheckpoint.class) {
                            return iterationUnits;
                        }
                        superstep = (lastCheckpoint == -1) ? 0 : lastCheckpoint;
                        continue;
                    }
                }

                time += checkpointCost[checkpointCounter];
                checkpointCostActual.add(checkpointCost[checkpointCounter]);
                checkpointCounter++;
                iterationUnit.setCheckpointEnd(time);
                this.lastCheckpoint = superstep;
            }
            //do compute

            iterationUnit.setComputeStart(time);

            if (failureInterval!=null&&failureCounter < failureInterval.length && nextFailtime <= time + computeTime[superstep]) {

                time = Math.max(nextFailtime, time);
                iterationUnit.setKillTime(time);
                if (failureCounter < this.supersteps - 1) {
                    failureCounter++;
                }

                if (this.checkpointStrategy.getClass() == NoCheckpoint.class) {
                    return iterationUnits;
                }
                superstep = (lastCheckpoint == -1) ? 0 : lastCheckpoint;

                continue;
            }
            time += computeTime[superstep];

            iterationUnit.setComputeEnd(time);

            superstep++;
        } while (superstep <= this.supersteps);
        //reset simulator status: lastCheckpoint,
        this.lastCheckpoint = -1;

        return iterationUnits;

    }


}
