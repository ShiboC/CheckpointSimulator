package factory;


import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;

public class Simulator {
    private int lastCheckpoint = -1;
    private int restartedSuperstep = -1;
    private int supersteps = 0; //total supersteps
    private int recoveryOverhead = 0;
    private int[] checkpointCost;
    private int[] computeTime;
    private HashSet<Integer> failureSteps=new HashSet<Integer>();
    private CheckpointStrategy checkpointStrategy;
//    private ArrayList<simple.IterationUnit> iterationUnits =new ArrayList<simple.IterationUnit>(); //each iteration's running time result

    public Simulator() {
    }

    public Simulator(int supersteps, int recoveryOverhead, CheckpointStrategy checkpointStrategy) {
        this.supersteps = supersteps;
        this.checkpointStrategy = checkpointStrategy;
        this.recoveryOverhead = recoveryOverhead;
    }

    public static void main(String[] args) {
        // set up checkpoint strategy.
        CheckpointStrategy checkpointStrategy = CheckpointStrategyFactory.getClass(StaticCheckpoint.class);
        checkpointStrategy.setInterval(2);
        // configure simulator
        Simulator simulator = new Simulator(10, 1, checkpointStrategy);
        simulator.recoveryOverhead = 2;
        // pre-generate checkpointCost Pool, actual checkpoint number is less, will be added during running.
        simulator.checkpointCost =DataGenerator.getSameData(simulator.supersteps+1, 5);
        simulator.computeTime = DataGenerator.modifyData(DataGenerator.getNormalDistributionDensity(
                simulator.supersteps+1, 3, 3), 100, 3);
        simulator.computeTime = DataGenerator.getSameData(simulator.supersteps+1, 2);


        //set up failure step up, failure time interval follows exponential distribution.
        // assume the whole running time without failure is 1, lambda is the expected failure numbers.
        simulator.failureSteps=simulator.generateFailureSteps(3);
        System.out.println("failureSteps:" + simulator.failureSteps.toString());
//        simulator.printResult(simulator.computeTime, simulator.failureSteps);
//        System.out.println(backup);
        ArrayList<IterationUnit> iterationUnits = simulator.generateResult( simulator.failureSteps);
        for (int i = 0; i < iterationUnits.size(); i++) {
            System.out.println(iterationUnits.get(i));
        }
    }
    // failure steps' time interval follows exponential distribution
    public HashSet<Integer> generateFailureSteps(int lambda){
        HashSet<Integer> failureSteps=new HashSet<Integer>();
        ArrayList<IterationUnit> iterationUnitsWithoutFailure=this.generateResult(failureSteps);
        long totalTime=iterationUnitsWithoutFailure.get(iterationUnitsWithoutFailure.size()-1).getComputeEnd();
//        System.out.println(totalTime);
        double[] failureIntervalList=DataGenerator.getExponentialDistributionData(this.supersteps,lambda);
        double timer=failureIntervalList[0]*totalTime;

        int failureCounter=0;
        for(IterationUnit i:iterationUnitsWithoutFailure){
//            System.out.println("timer:"+timer);
            if(i.getCheckpointStart()<=timer&&i.getComputeEnd()>=timer){
                failureSteps.add(i.getSuperstep());
                failureCounter++;
                timer+=failureIntervalList[failureCounter]*totalTime;
            }
        }
        return failureSteps;

    }
    public ArrayList<IterationUnit> generateResult( HashSet<Integer> failureSteps) {
        ArrayList<IterationUnit> iterationUnits = new ArrayList<>();
        int superstep = 0;
        long time = 0;
        int checkpointCounter=0;
        ArrayList<Integer> checkpointCostActual=new ArrayList<Integer>();
        do {
//            System.out.println(superstep);
            IterationUnit iterationUnit = new IterationUnit();
            if (iterationUnits.size() != 0) {
                if (iterationUnits.get(iterationUnits.size() - 1).getRestartTime()==0 || iterationUnits.get(iterationUnits.size() - 1).getComputeEnd() != 0) {//if it is not the a just restarted superstep
                    iterationUnit.setAttepmt(iterationUnits.get(iterationUnits.size() - 1).getAttepmt());// attempt equals to the last one
                    iterationUnit.setSuperstep(superstep);
                    iterationUnits.add(iterationUnit);
//                    System.out.println("not just restarted. add new");
                }
            } else {
                iterationUnits.add(iterationUnit);
//                System.out.println("size 0 add new");
            }
            CheckpointStatus checkpointStatus = this.checkpointStrategy.getCheckpointStatus(superstep,checkpointCostActual,this.recoveryOverhead,this.lastCheckpoint,this.restartedSuperstep,this.computeTime);
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
                if (checkpointStrategy.getInterval() == 0 && this.lastCheckpoint==-1) {//no checkpoint. job stopped
//                    System.out.println("ck none. jump");
                    break;
                } else {//restart
//                    System.out.println("restarted");
                    IterationUnit iterationUnit2 = new IterationUnit();
                    iterationUnit2.setAttepmt(iterationUnits.get(iterationUnits.size() - 1).getAttepmt() + 1);//attempt incremented
                    iterationUnit2.setRestartTime(time);
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
        int checkpointCounter=0;
        ArrayList<Integer> checkpointCostActual=new ArrayList<Integer>();

        do {
            CheckpointStatus checkpointStatus = this.checkpointStrategy.getCheckpointStatus(superstep,checkpointCostActual,this.recoveryOverhead,this.lastCheckpoint,this.restartedSuperstep,this.computeTime);
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
                if (checkpointStrategy.getInterval() == 0 &&  this.lastCheckpoint==-1) {
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
