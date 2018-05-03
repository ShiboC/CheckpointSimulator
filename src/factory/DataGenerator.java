package factory;

import java.util.ArrayList;
import java.util.Random;

public class DataGenerator {
    public static int[] getSameData(int size, int value) {
        int[] dataList = new int[size];

        for (int i = 0; i < size; i++) {
            dataList[i] = value;
        }

        return dataList;
    }

    public static double[] getNormalDistributionDensity(int size, double mean, double std_dev) {

        double[] dataList = new double[size];

        for (int i = 0; i < size; i++) {
            dataList[i] = 1 / (Math.sqrt(2 * Math.PI) * std_dev) * Math.pow(Math.E, (-0.5 * Math.pow(i - mean, 2) / Math.pow(std_dev, 2)));
        }
        return dataList;
    }

    public static double[] getNormalDistributionData(int size, double mean, double std_dev) {

        double[] dataList = new double[size];
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            dataList[i] = std_dev * random.nextGaussian() + mean;
        }
        return dataList;
    }

//    public static double getPoissonDistributionDensity(int k, int lambda) {
//        int kProd = 1;
//        for (int i = 1; i <= k; i++) {
//            kProd *= i;
//        }
//        return Math.pow(lambda, k) * Math.pow(Math.E, -lambda) / kProd;
//    }
    //expected value is 1/lambda in a period of "unit" time
    public static double[] getExponentialDistributionData(int size,double lambda) {
        double[] dataList = new double[size];
        for (int i = 0; i < size; i++) {
            dataList[i] = -1 / lambda * Math.log(Math.random());//                x = -(1 / lamda) * Math.log(z);
        }
        return dataList;
    }


    //multiply the datalist and make sure all data are not smaller than minValue
    public static int[] modifyData(double[] computeTime, int multiplier, int minValue) {
        int[] dataList = new int[computeTime.length];
        for (int i = 0; i < computeTime.length; i++) {
            dataList[i] =(int)Math.round(computeTime[i]*multiplier);
            if(dataList[i]<minValue)
                dataList[i]=minValue;
        }
        return dataList;
    }
}
