
import java.util.ArrayList;

public class Simulator implements Cloneable {
    private int lastCheckpoint = -1;
    private int supersteps = 0;
    private int recoveryOverhead = 0;
    private int[] checkpointCost;
    private int[] computeCost;
    private int[] failureSteps; //only need to set up one of "failureSteps" and "failureTimeInterval"
    private int[] failureTimeInterval;
    private CheckpointStrategy checkpointStrategy;
    private boolean enableFailatCheckpointRecovery = false;

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

        int[] computeTimeList = {10577, 1640, 1504, 1554, 1596, 1626, 1465, 1486, 1665, 2074, 1886, 1943, 2690, 4253, 6163, 7967, 9426, 9405, 8493, 6865, 5502, 4485, 3686, 2955, 2637, 2483, 2049, 1949, 1940, 1884, 1801, 1745, 1643, 1696, 1737, 1605, 1640, 1700, 1584, 1604, 1606, 1642, 1776, 1584, 1584, 1733, 1706, 1626};
        simulator.computeCost = computeTimeList;

        int[] checkpointTimeList = {11611, 7710, 8122, 9157, 6366, 9188, 8474, 6847, 8971, 8570, 8505, 7563, 7571, 6634, 7548, 9486, 7410, 6493, 7103, 8425, 7529, 9562, 8125, 8780, 7725, 8038, 8343, 8576, 8672, 6270, 10851, 7417, 7355, 8330, 7763, 6738, 9006, 8582, 7763, 7009, 9089, 8224, 6692, 8923, 7565, 6613, 7331, 7343};
        for (int i = 0; i < checkpointTimeList.length; i++) {
            checkpointTimeList[i] = checkpointTimeList[i] * 2;
        }
        simulator.checkpointCost = checkpointTimeList;
        //        simulator.failureInterval = DataGenerator.modifyData(DataGenerator.getExponentialDistributionData(60, 1), 150000, 1);

        simulator.failureTimeInterval = new int[]{60804, 9685, 76920, 69694, 14921, 29196, 537193, 14152, 204939, 72032, 16294, 74558, 95703, 25252, 39922, 26797, 35302, 42113, 43567, 97429, 18819, 47220, 52544, 287595, 95423, 117126, 3599, 32458, 32151, 16405, 39122, 39728, 1061, 62019, 2728, 13036, 72998, 123684, 42994, 281383, 223137, 6404, 126798, 33734, 7907, 21732, 7437, 44183, 80781, 32769, 47861, 97947, 43420, 69249, 24389, 105930, 61214, 89972, 79674, 31317};
        simulator.failureSteps = new int[]{7, 19, 31, 43};

        System.out.println("dynamic checkpointing result:attempt;checkpoint;iteration");
        //result can be generated two ways, one is by predefined superstep, one is by predefined failure interval.
        simulator.checkpointStrategy = CheckpointStrategyFactory.getClass(DynamicCheckpoint.class);
        for (int i = 1; i <= 10; i++) {
            simulator.checkpointStrategy.setInterval(i);
            ArrayList<IterationUnit> iterationUnits = simulator.generateResultByTime();
//            ArrayList<IterationUnit> iterationUnits = simulator.generateResultByStep();

            //get total number of checkpoints
            int checkpointCounter = 0;
            for (int j = 0; j < iterationUnits.size(); j++) {
                if (iterationUnits.get(j).getCheckpointEnd() != 0) {
                    checkpointCounter++;
                }
            }
            System.out.println(
                    iterationUnits.get(iterationUnits.size() - 1).getComputeEnd() +
                            ":" + iterationUnits.get(iterationUnits.size() - 1).getAttepmt() +
                            ":" + checkpointCounter +
                            ":" + iterationUnits.size());
        }

        System.out.println("static checkpointing result:attempt:checkpoint:iteration:endtime");
        simulator.checkpointStrategy = CheckpointStrategyFactory.getClass(StaticCheckpoint.class);
        for (int i = 1; i <= 10; i++) {
            simulator.checkpointStrategy.setInterval(i);
            ArrayList<IterationUnit> iterationUnits = simulator.generateResultByTime();
            //get total number of checkpoints
            int checkpointCounter = 0;
            for (int j = 0; j < iterationUnits.size(); j++) {
                if (iterationUnits.get(j).getCheckpointEnd() != 0) {
                    checkpointCounter++;
                }
            }
            //or export the results in CSV
//            CSVUtils.exportCsv("result.csv",iterationUnits);
            System.out.println(iterationUnits.get(iterationUnits.size() - 1).getAttepmt() +
                    ":" + checkpointCounter +
                    ":" + iterationUnits.size() +
                    ":" + iterationUnits.get(iterationUnits.size() - 1).getComputeEnd());
        }
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
            CheckpointStatus checkpointStatus = this.checkpointStrategy.getCheckpointStatus(superstep, checkpointCostActual, this.lastCheckpoint, this.computeCost);
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
            if (failureSteps != null && failureSteps.length > failureCounter && failureSteps[failureCounter] == superstep) {
                iterationUnit.setKillTime(time);
                if (failureCounter < this.supersteps - 1) {
                    failureCounter++;
                }
                if (this.checkpointStrategy.getClass() == NoCheckpoint.class) {
                    iterationUnit.setComputeEnd(time);
                    return iterationUnits;
                }
                superstep = (lastCheckpoint == -1) ? 0 : lastCheckpoint;
                continue;
            }
            time += computeCost[superstep];
            iterationUnit.setComputeEnd(time);
            superstep++;
        } while (superstep <= this.supersteps);
        //reset simulator status: lastCheckpoint,
        this.lastCheckpoint = -1;
        return iterationUnits;

    }

    public ArrayList<IterationUnit> generateResultByTime() {
        ArrayList<IterationUnit> iterationUnits = new ArrayList<>();
        int superstep = 0;
        long time = 0;
        long nextFailtime = Long.MAX_VALUE;
        int checkpointCounter = 0;
        int failureCounter = 0;
        ArrayList<Integer> checkpointCostActual = new ArrayList<Integer>();
        if (failureTimeInterval != null) {
            nextFailtime = failureTimeInterval[0];
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
                    if (this.enableFailatCheckpointRecovery) {
                        if (failureTimeInterval != null && failureCounter < failureTimeInterval.length && nextFailtime <= time + computeCost[superstep]) {
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
                    nextFailtime = time + failureTimeInterval[failureCounter];
                }
            } else { //the loop is just started, the iterationUnits is still empty
                iterationUnits.add(iterationUnit);
            }
            //do checkpoint
            CheckpointStatus checkpointStatus = this.checkpointStrategy.getCheckpointStatus(superstep, checkpointCostActual, this.lastCheckpoint, this.computeCost);
            if (checkpointStatus == CheckpointStatus.CHECKPOINT) {
                iterationUnit.setCheckpointStart(time);
                // usually not enable kill at recovery period
                if (this.enableFailatCheckpointRecovery) {
                    if (failureTimeInterval != null && failureCounter < failureTimeInterval.length && nextFailtime <= time + computeCost[superstep]) {
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

            if (failureTimeInterval != null && failureCounter < failureTimeInterval.length && nextFailtime <= time + computeCost[superstep]) {

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
            time += computeCost[superstep];

            iterationUnit.setComputeEnd(time);

            superstep++;
        } while (superstep <= this.supersteps);
        //reset simulator status: lastCheckpoint,
        this.lastCheckpoint = -1;

        return iterationUnits;

    }


}
