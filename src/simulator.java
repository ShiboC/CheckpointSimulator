public class simulator {
    public int DYNAMIC_CHECKPOINTING=1;
    public int STATIC_CHECKPOINTING=2;
    public int NORMAL_DISTRIBUTION=1;
    public int UNIFORM_DISTRIBUTION=2;

    public static void main(String[] args) {
        printResult();
        System.out.println(getNumberInNormalDistribution(4,1));
    }
    public int[] generateComputeTime(int distribution,int size,int mean,int std_dev){
        int[] computeTime=new int[size];
        if(distribution==UNIFORM_DISTRIBUTION){
            for(int i=0;i<size;i++){
                computeTime[i]=mean;
            }
            return computeTime;
        }
        if(distribution==NORMAL_DISTRIBUTION){
            for(int i=0;i<size;i++){

            }
//            double fx=1/(Math.sqrt(2*Math.PI)*std_dev)* Math.pow(Math.E,(-1*()));
        }
        return null;
    }
    public static double getNumberInNormalDistribution(int mean,int std_dev){
        return mean+(randomNormalDistribution()*std_dev);
    }

    public static double randomNormalDistribution(){
        double u=0.0, v=0.0, w=0.0, c=0.0;
        do{
            //获得两个（-1,1）的独立随机变量
            u=Math.random()*2-1.0;
            v=Math.random()*2-1.0;
            w=u*u+v*v;
        }while(w==0.0||w>=1.0);
        //这里就是 Box-Muller转换
        c=Math.sqrt((-2*Math.log(w))/w);
        //返回2个标准正态分布的随机数，封装进一个数组返回
        //当然，因为这个函数运行较快，也可以扔掉一个
        //return [u*c,v*c];
        return u*c;
    }
    public static void printResult(){
        long[] computeTime=new long[]{1,1,1,1,1};
        int checkpointTime=2;
        long time=0;
        for(int i=0;i<5;i++){
            long endCheckpoint=time+checkpointTime;
            System.out.println("superstep:"+(i+1)+"; checkpoint start:"+time+"; end:"+endCheckpoint+"; duration:"+checkpointTime);
            long endCompute=endCheckpoint+computeTime[i];
            System.out.println("superstep:"+(i+1)+"; compute    start:"+endCheckpoint+"; end:"+endCompute+"; duration:"+computeTime[i]);
            time=endCompute;
        }
    }
}
