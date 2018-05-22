import factory.DataGenerator;
import factory.Simulator;
import simple.IterationUnit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class test {
    public static void main(String[] args) throws Exception {
//        for(int i=0;i<30;i++){
//            System.out.println(poissonDistributionDensity(i,2));
//        }
        Simulator s=new Simulator();
//        s.generateFailureTime(1,1,1,30);
        for(int i=0;i<4;i++) {
            System.out.println(Math.round(Math.random() * 47));
        }
        //7,19,31,43
        //43
        //7
        //31
        HashSet<Long> test;
        System.out.println("start");
        int[] test1 = DataGenerator.modifyData(DataGenerator.getExponentialDistributionData(100, 1), 180, 1);
        for (int i = 0; i < test1.length; i++) {
            System.out.println(test1[i]);
        }
        System.out.println("end");
        int[] a = new int[10];
        System.out.println(a.length);
        System.out.println(a[0]);
        ArrayList<IterationUnit> list = new ArrayList<IterationUnit>();
        IterationUnit unit = new IterationUnit();
        unit.setAttepmt(4);

        list.add(unit);
        System.out.println(list.get(0).getAttepmt());

        unit.setAttepmt(5);
        unit.setSuperstep(6);
        System.out.println(list.get(0).getAttepmt());

        System.out.println(list.get(0).getSuperstep());

        ArrayList<Double> listd = expntl(0.2, 100); //lamda =5
        System.out.println(listd.toString());
    }

    public static double RandExp(double const_a)//此处的const_a是指数分布的那个参数λ

    {


        //此处属于技巧，利用GUID产生随机数的种子，模拟真正的随机
        Random rand = new Random();
        double p;
        double temp;

        temp = 1 / const_a;

        double randres;
        //while((randres = -temp * Math.Log(temp * p, Math.E)) > 0);
        //while ((p = rand.NextDouble()) > const_a) ;
        while (true) //用于产生随机的密度，保证比参数λ小
        {
            p = rand.nextDouble();
            if (p < const_a)
                break;
        }
        randres = -temp * Math.log(temp * p);
        return randres;
    }

    public static double[] getExponentialDistributionData(int size, double lambda) {
        double[] dataList = new double[size];
        for (int i = 0; i < size; i++) {
            dataList[i] = -1 / lambda * Math.log(Math.random());//                x = -(1 / lamda) * Math.log(z);
        }
        return dataList;
    }
    public static ArrayList<Double> expntl(double e, int row) {
        double t, temp;
        ArrayList<Double> a = new ArrayList<Double>();
        for (int i = 0; i < row; i++) {
            t = Math.random();
            temp = (-e * Math.log(t));//                x = -(1 / lamda) * Math.log(z);

            a.add(temp);
        }
        return a;
    }

    public static double poissonDistributionDensity(int k, int lambda) {
        int kProd = 1;
        for (int i = 1; i <= k; i++) {
            kProd *= i;
        }
        return Math.pow(lambda, k) * Math.pow(Math.E, -lambda) / kProd;
    }
}
