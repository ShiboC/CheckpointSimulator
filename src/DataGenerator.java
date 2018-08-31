import java.util.Random;

public class DataGenerator {
    //Return a list of same data simply
    public static int[] getSameData(int size, int value) {
        int[] dataList = new int[size];

        for (int i = 0; i < size; i++) {
            dataList[i] = value;
        }

        return dataList;
    }
    // Return a list of data follows normal distribution density function.
    public static double[] getNormalDistributionDensity(int size, double mean, double std_dev) {

        double[] dataList = new double[size];

        for (int i = 0; i < size; i++) {
            dataList[i] = 1 / (Math.sqrt(2 * Math.PI) * std_dev) * Math.pow(Math.E, (-0.5 * Math.pow(i - mean, 2) / Math.pow(std_dev, 2)));
        }
        return dataList;
    }
    //Return a list of data follows normal distribution. Note that data from this function follows normal distribution, different from the last one which follows distribution density curve
    public static double[] getNormalDistributionData(int size, double mean, double std_dev) {

        double[] dataList = new double[size];
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            dataList[i] = std_dev * random.nextGaussian() + mean;
        }
        return dataList;
    }


    //Return a list of data follows exponential distribution, expected value is 1/lambda in a period of "unit" time
    public static double[] getExponentialDistributionData(int size, double lambda) {
        double[] dataList = new double[size];
        for (int i = 0; i < size; i++) {
            dataList[i] = -1 / lambda * Math.log(Math.random());
        }
        return dataList;
    }


    //multiply the datalist and make sure all data are not smaller than minValue
    public static int[] modifyData(double[] dataArray, int multiplier, int minValue) {
        int[] dataList = new int[dataArray.length];
        for (int i = 0; i < dataArray.length; i++) {
            dataList[i] = (int) Math.round(dataArray[i] * multiplier);
            if (dataList[i] < minValue)
                dataList[i] = minValue;
        }
        return dataList;
    }
}
