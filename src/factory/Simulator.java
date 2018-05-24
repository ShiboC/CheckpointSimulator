package factory;


import java.util.ArrayList;

public class Simulator implements Cloneable {
    private int lastCheckpoint = -1;
    //    private int restartedSuperstep = -1;
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

    public Simulator(int supersteps, int recoveryOverhead, CheckpointStrategy checkpointStrategy) {
        this.supersteps = supersteps;
        this.checkpointStrategy = checkpointStrategy;
        this.recoveryOverhead = recoveryOverhead;
    }

    public static void main(String[] args) throws Exception {
        String resultPath = "result.csv";
        // set up checkpoint strategy.
        CheckpointStrategy checkpointStrategy = CheckpointStrategyFactory.getClass(DynamicCheckpoint.class);
        checkpointStrategy.setInterval(1);
        // configure simulator
        Simulator simulator = new Simulator(47, 1, checkpointStrategy);

        simulator.recoveryOverhead = 60000;
        // pre-generate checkpointCost Pool, actual checkpoint number is less, will be added during running.
//        simulator.checkpointCost = DataGenerator.getSameData(simulator.supersteps + 2, 10);
//        simulator.checkpointCost = DataGenerator.modifyData(DataGenerator.getNormalDistributionData(simulator.supersteps + 1, 10, 1), 1, 0);
//        simulator.computeTime = DataGenerator.modifyData(DataGenerator.getNormalDistributionDensity(
//                simulator.supersteps + 1, 16, 4), 100000, 1500);
//        simulator.computeTime = DataGenerator.getSameData(simulator.supersteps + 1, 2);
        //1500,1500,1500,1500,1500,1500,1500,1500,1500,2157,3238,4566,6049,7528,8802,9667,9974,9667,8802,7528,6049,4566,3238,2157,1500,1500,1500,1500,1500,1500,1500,1500,1500,1500,1500,1500,1500,1500,1500,1500,1500,1500,1500,1500,1500,1500,1500,1500,
        int[] computeTimeList = {10577, 1640, 1504, 1554, 1596, 1626, 1465, 1486, 1665, 2074, 1886, 1943, 2690, 4253, 6163, 7967, 9426, 9405, 8493, 6865, 5502, 4485, 3686, 2955, 2637, 2483, 2049, 1949, 1940, 1884, 1801, 1745, 1643, 1696, 1737, 1605, 1640, 1700, 1584, 1604, 1606, 1642, 1776, 1584, 1584, 1733, 1706, 1626};
        simulator.computeTime = computeTimeList;
        System.out.println("computeTime:");
        for (int ft : computeTimeList) {
            System.out.print(ft + ",");
        }
        System.out.println();
        int[] checkpointTimeList = {11611, 7710, 8122, 9157, 6366, 9188, 8474, 6847, 8971, 8570, 8505, 7563, 7571, 6634, 7548, 9486, 7410, 6493, 7103, 8425, 7529, 9562, 8125, 8780, 7725, 8038, 8343, 8576, 8672, 6270, 10851, 7417, 7355, 8330, 7763, 6738, 9006, 8582, 7763, 7009, 9089, 8224, 6692, 8923, 7565, 6613, 7331, 7343};
        simulator.checkpointCost = checkpointTimeList;
        System.out.println("checkpointTime:");
        for (int ft : checkpointTimeList) {
            System.out.print(ft + ",");
        }
        System.out.println();

        //set up failure step up, failure time interval follows exponential distribution.
        // assume the whole running time without failure is 1, lambda is the expected failure numbers.
//        simulator.failureSteps = simulator.generateFailureSteps(3);
//        System.out.println("failureSteps:" + simulator.failureSteps.toString());

        simulator.failureInterval = DataGenerator.modifyData(DataGenerator.getExponentialDistributionData(60, 1), 150000, 1);
//        simulator.failureInterval = simulator.generateFailureTime(1, 180000, 1, 30);
//        simulator.failureInterval=new int[]{5868,97953,111678,55550,8543,130566,169785,45938,170421,190684,106013,245436,164373,36042,4851,209137,686104,319519,46483,29196,25042,179031,33037,186239,86569,147925,99686,107060,98496,87773};
        System.out.println("failureInterval:");
        for (int ft : simulator.failureInterval) {
            System.out.print(ft + ",");
        }
        System.out.println();

//        System.out.println("checkpoint cost:");
//        for (int ft : simulator.checkpointCost) {
//            System.out.print(ft + ",");
//        }
//        System.out.println();
//7 19 31 43
//        simulator.failureSteps = new int[]{7, 19, 31, 43};
//        System.out.println(simulator.failureSteps);
        simulator.checkpointStrategy = CheckpointStrategyFactory.getClass(DynamicCheckpoint.class);
        for (int i = 1; i <= 10; i++) {
//            ArrayList<IterationUnit> iterationUnits = simulator.generateResultByTime(simulator.failureInterval);
//        ArrayList<IterationUnit> iterationUnits = simulator.generateResultByStep(new HashSet<Integer>());
            simulator.checkpointStrategy.setInterval(i);
//            ArrayList<IterationUnit> iterationUnits = simulator.generateResultByStep();
            ArrayList<IterationUnit> iterationUnits = simulator.generateResultByTime();

//            for (int j = 0; j < iterationUnits.size(); j++) {
//                System.out.println(iterationUnits.get(j));
//            }
            System.out.println("interval:" + i + ",attempt:" + iterationUnits.get(iterationUnits.size() - 1).getAttepmt() + ",totaltime:" + iterationUnits.get(iterationUnits.size() - 1).getComputeEnd());
        }
        System.out.println("static");
        simulator.checkpointStrategy = CheckpointStrategyFactory.getClass(StaticCheckpoint.class);
        for (int i = 1; i <= 10; i++) {
//            ArrayList<IterationUnit> iterationUnits = simulator.generateResultByTime(simulator.failureInterval);
//        ArrayList<IterationUnit> iterationUnits = simulator.generateResultByStep(new HashSet<Integer>());
            simulator.checkpointStrategy.setInterval(i);
//            ArrayList<IterationUnit> iterationUnits = simulator.generateResultByStep();
            ArrayList<IterationUnit> iterationUnits = simulator.generateResultByTime();

//            for (int j = 0; j < iterationUnits.size(); j++) {
//                System.out.println(iterationUnits.get(j));
//            }
            System.out.println("interval:" + i + ",attempt:" + iterationUnits.get(iterationUnits.size() - 1).getAttepmt() + ",totaltime:" + iterationUnits.get(iterationUnits.size() - 1).getComputeEnd());
        }
//        ArrayList<IterationUnit> iterationUnits1 = simulator1.generateResultByTime(simulator1.failureInterval);
//        CSVUtils.exportCsv(resultPath, iterationUnits);
//        ArrayList<IterationUnit> iterationUnits = simulator.generateResultByStep();
//        ArrayList<IterationUnit> iterationUnits = simulator.generateResultByTime();
////
//        simulator.checkpointStrategy.setInterval(1);
//        System.out.println(simulator.checkpointStrategy.getClass().getName());
//        for (int j = 0; j < iterationUnits.size(); j++) {
//            System.out.println(iterationUnits.get(j));
//        }

//        for (int i = 0; i < iterationUnits1.size(); i++) {
//            System.out.println(iterationUnits1.get(i));
//        }
    }

    // failure steps' time interval follows exponential distribution
//    public HashSet<Integer> generateFailureSteps(int lambda) throws CloneNotSupportedException {
//        HashSet<Integer> failureSteps = new HashSet<Integer>();
//        Simulator stemp = (Simulator) this.clone();
//        ArrayList<IterationUnit> iterationUnitsWithoutFailure = stemp.generateResultByStep(failureSteps);
//        long totalTime = iterationUnitsWithoutFailure.get(iterationUnitsWithoutFailure.size() - 1).getComputeEnd();
////        System.out.println(totalTime);
//        double[] failureIntervalList = DataGenerator.getExponentialDistributionData(stemp.supersteps, lambda);
//        double timer = failureIntervalList[0] * totalTime;
//
//        int failureCounter = 0;
//        for (IterationUnit i : iterationUnitsWithoutFailure) {
////            System.out.println("timer:"+timer);
//            if (i.getCheckpointStart() <= timer && i.getComputeEnd() >= timer) {
//                failureSteps.add(i.getSuperstep());
//                failureCounter++;
//                timer += failureIntervalList[failureCounter] * totalTime;
//            }
//        }
//        return failureSteps;
//
//    }

//    public int[] generateFailureTime(int lambda, int period, int min, int size) throws CloneNotSupportedException {
////
////
//////        System.out.println(totalTime);
//        int[] failureIntervalList = new int[]{5868,97953,111678,55550,8543,130566,169785,45938,170421,190684,106013,245436,164373,36042,4851,209137,686104,319519,46483,29196,25042,179031,33037,186239,86569,147925,99686,107060,98496,87773};
//        int[] failureInterval = new int[size];
//        failureInterval[0]=failureIntervalList[0];
//        for (int i = 1; i < size; i++) {
//            failureInterval[i] = failureInterval[i - 1] + failureIntervalList[i];
////            System.out.println(failureInterval[i]);
//        }
//        return failureInterval;
////
//    }

    //only fail at compute part.
    public ArrayList<IterationUnit> generateResultByStep() {
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

                    time += recoveryOverhead;
                    iterationUnit.setRecoveryOverheadEnd(time);
                }
            } else {
                iterationUnits.add(iterationUnit);
//                System.out.println("size 0 add new");
            }
            //do checkpoint
            CheckpointStatus checkpointStatus = this.checkpointStrategy.getCheckpointStatus(superstep, checkpointCostActual, this.recoveryOverhead, this.lastCheckpoint, this.computeTime);
            if (checkpointStatus == CheckpointStatus.CHECKPOINT) {
                iterationUnit.setCheckpointStart(time);
//

                time += checkpointCost[checkpointCounter];
                checkpointCostActual.add(checkpointCost[checkpointCounter]);
                checkpointCounter++;
                iterationUnit.setCheckpointEnd(time);
                this.lastCheckpoint = superstep;
//                System.out.println("do checkpoint");
            }
            //do compute

            iterationUnit.setComputeStart(time);
            if (failureSteps.length != 0 && failureSteps.length > failureCounter && failureSteps[failureCounter] == superstep) {
                iterationUnit.setKillTime(time);
                if (failureCounter < this.supersteps - 1) {
                    failureCounter++;
                }

                if (this.checkpointStrategy.getClass() == NoCheckpoint.class) {
                    return iterationUnits;
                }
                superstep = (lastCheckpoint == -1) ? 0 : lastCheckpoint;
//                failureSteps.remove(superstep);
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

    public ArrayList<IterationUnit> generateResultByTime() {
        ArrayList<IterationUnit> iterationUnits = new ArrayList<>();
        int superstep = 0;
        long time = 0;
        long nextFailtime = Long.MAX_VALUE;

        int checkpointCounter = 0;
        int failureCounter = 0;
        ArrayList<Integer> checkpointCostActual = new ArrayList<Integer>();
        if(failureInterval.length>0){
            nextFailtime=failureInterval[0];
        }
        do {
//            System.out.println(superstep);
            IterationUnit iterationUnit = new IterationUnit();

            //start, set up attempt and superstep, check if it's a restarted.
            if (iterationUnits.size() != 0) {
                if (iterationUnits.get(iterationUnits.size() - 1).getKillTime() == 0) {//if it is not the a just restarted superstep
                    iterationUnit.setAttepmt(failureCounter);// attempt equals to the last one, which is also the same as failure Counter.
                    iterationUnit.setSuperstep(superstep);
                    iterationUnits.add(iterationUnit);
//                    System.out.println("not just restarted. add new");
                } else { //a restart,need to add recoveryoverhead and deal with failure during recovery.
                    iterationUnit.setAttepmt(failureCounter);// attempt equals to the last one
                    iterationUnit.setSuperstep(superstep);
                    iterationUnits.add(iterationUnit);
                    iterationUnit.setRecoveryOverheadStart(time);
                    // do not enable kill at recovery period
//                    if (failureCounter < failureInterval.length && failureInterval[failureCounter] + lastFailtime >= time && failureInterval[failureCounter] + lastFailtime <= time + recoveryOverhead) {//fail during recovery
//                        time = failureInterval[failureCounter] + lastFailtime;
//                        iterationUnit.setKillTime(time);
//                        lastFailtime = time;
//                        if (failureCounter < this.supersteps - 1) {
//                            failureCounter++;
//                        }
//                        if (this.checkpointStrategy.getClass() == NoCheckpoint.class) {
//                            return iterationUnits;
//                        }
////                        System.out.println("fas;s:"+lastCheckpoint+","+superstep);
//                        superstep = (lastCheckpoint == -1) ? 0 : lastCheckpoint;
//
//
//                        continue;
//                    }
                    time += recoveryOverhead;
                    iterationUnit.setRecoveryOverheadEnd(time);
                    nextFailtime=time+failureInterval[failureCounter];
                }
            } else {
                iterationUnits.add(iterationUnit);
//                System.out.println("size 0 add new");
            }
            //do checkpoint
            CheckpointStatus checkpointStatus = this.checkpointStrategy.getCheckpointStatus(superstep, checkpointCostActual, this.recoveryOverhead, this.lastCheckpoint, this.computeTime);
            if (checkpointStatus == CheckpointStatus.CHECKPOINT) {
                iterationUnit.setCheckpointStart(time);
//                System.out.println("failtime:"+(failureInterval[failureCounter] + lastFailtime));
                //do not enable kill at checkpoint period
//                if (failureCounter < failureInterval.length && failureInterval[failureCounter] + lastFailtime >= time && failureInterval[failureCounter] + lastFailtime <= time + checkpointCost[checkpointCounter]) {
//                    time = failureInterval[failureCounter] + lastFailtime;
//                    iterationUnit.setKillTime(time);
//                    lastFailtime = time;
//                    if (failureCounter < this.supersteps - 1) {
//                        failureCounter++;
//                    }
//
//                    if (this.checkpointStrategy.getClass() == NoCheckpoint.class) {
//                        return iterationUnits;
//                    }
//                    superstep = (lastCheckpoint == -1) ? 0 : lastCheckpoint;
//
//                    continue;
//                }
                time += checkpointCost[checkpointCounter];
                checkpointCostActual.add(checkpointCost[checkpointCounter]);
                checkpointCounter++;
                iterationUnit.setCheckpointEnd(time);
                this.lastCheckpoint = superstep;
//                System.out.println("do checkpoint");
            }
            //do compute

            iterationUnit.setComputeStart(time);
//            if (failureCounter < failureInterval.length && failureInterval[failureCounter] + lastFailtime >= time && failureInterval[failureCounter] + lastFailtime <= time + computeTime[superstep]) {
            if (failureCounter < failureInterval.length && nextFailtime <= time + computeTime[superstep]) {

//                time = failureInterval[failureCounter] + lastFailtime;
                time=Math.max(nextFailtime,time);
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
