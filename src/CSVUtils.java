

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CSVUtils {
    public static void main(String[] args) {
        List<String> a = new ArrayList<>();
        a.add("a");
        a.add("b");
        a.add("c");
        List<IterationUnit> b = new ArrayList<IterationUnit>();
        b.add(new IterationUnit());
        b.add(new IterationUnit());
        b.add(new IterationUnit());

        exportCsv("b.csv", b);
    }

    /**
     * export
     *
     * @return
     */
    public static boolean exportCsv(String filePath, List<IterationUnit> dataList) {
        boolean isSucess = false;
        File file = new File(filePath);
        FileOutputStream out = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;
        try {
            out = new FileOutputStream(file);
            osw = new OutputStreamWriter(out);
            bw = new BufferedWriter(osw);
            bw.append("attepmt,superstep,killTime,checkpointStart,checkpointEnd,checkpointDuration" +
                    ",computeStart,computeEnd,computeDuration,recoveryOverheadStart,recoveryOverheadEnd.recoveryOverheadDuration\r");
            if (dataList != null && !dataList.isEmpty()) {
                for (IterationUnit data : dataList) {
                    bw.append(data.toCsvString()).append("\r");
                }
            }
            isSucess = true;
        } catch (Exception e) {
            isSucess = false;
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                    bw = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (osw != null) {
                try {
                    osw.close();
                    osw = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                    out = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return isSucess;
    }

    /**
     * import
     */
    public static List<String> importCsv(File file) {
        List<String> dataList = new ArrayList<String>();

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line = "";
            while ((line = br.readLine()) != null) {
                dataList.add(line);
            }
        } catch (Exception e) {
        } finally {
            if (br != null) {
                try {
                    br.close();
                    br = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return dataList;
    }
}