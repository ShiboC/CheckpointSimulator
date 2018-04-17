import simple.IterationUnit;

import java.util.ArrayList;

public class test {
    public static void main(String[] args) {
//        for(int i=0;i<30;i++){
//            System.out.println(poissonDistributionDensity(i,2));
//        }
        int[] a=new int[10];
        System.out.println(a.length);
        System.out.println(a[0]);
        ArrayList<IterationUnit> list=new ArrayList<IterationUnit>();
        IterationUnit unit=new IterationUnit();
        unit.setAttepmt(4);

        list.add(unit);
        System.out.println(list.get(0).getAttepmt());

        unit.setAttepmt(5);
        unit.setSuperstep(6);
        System.out.println(list.get(0).getAttepmt());

        System.out.println(list.get(0).getSuperstep());

        ArrayList<Double> listd=expntl(0.2,100); //lamda =5
        System.out.println(listd.toString());
    }
    public static ArrayList<Double> expntl(double e, int row) {
        double t, temp;
        ArrayList<Double> a = new ArrayList<Double>();
        for (int i = 0; i < row; i++) {
            t =  Math.random();
            temp = (-e * Math.log(t));//                x = -(1 / lamda) * Math.log(z);

            a.add(temp);
        }
        return a;
    }
    public static double poissonDistributionDensity(int k, int lambda){
        int kProd=1;
        for(int i=1;i<=k;i++){
            kProd*=i;
        }
        return Math.pow(lambda,k)*Math.pow(Math.E,-lambda)/kProd;
    }
}
