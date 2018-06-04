//dealing giraph logs, not related to simulator

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class logTool {
    public List<File> outFiles=new ArrayList<File>();

    private  List<File> getFileList(String fileDir) {
        List<File> fileList = new ArrayList<File>();
        File file = new File(fileDir);
        File[] files = file.listFiles();// 获取目录下的所有文件或文件夹
        if (files == null) {// 如果目录为空，直接退出
            System.out.println("null");
            return null;
        }
        // 遍历，目录下的所有文件
        for (File f : files) {
            if (f.isFile() && f.getName().equals("stdout")) {
                fileList.add(f);
                this.outFiles.add(f);
            } else if (f.isDirectory()) {
//                System.out.println(f.getAbsolutePath());
                getFileList(f.getAbsolutePath());
            }
        }
        for (File f1 : fileList) {
//            System.out.println(f1.getAbsolutePath());
//            System.out.println(f1.getName());

        }
//        System.out.println("return fileList");
        return fileList;
    }

    private static void getLogInfo(String filePath) throws Exception {
        File file = new File(filePath);

        BufferedReader bw = new BufferedReader(new FileReader(file));
        String line = null;
        long start = 0;
        long end = 0;
        int checkpointCounter=0;
        int computationCounter=0;
        String firstLine = bw.readLine();
        while ((line = bw.readLine()) != null) {
            if (line.contains("coordination of superstep,0")) {
                start = Long.parseLong(line.split(",")[3]);
            }
            if (line.contains("coordination of superstep,47")) {
                end = Long.parseLong(line.split(",")[3]);
            }
            if (line.contains("checkpointStart/End")){
                checkpointCounter++;
            }
            if (line.contains("computeStart/End")){
                computationCounter++;
            }


        }
        if(start>0 &&end>0) {
            System.out.print(filePath+","+firstLine);

            System.out.println(",start(0)," + start + ",end(47)," + end + ",duration," + (end - start)+",checkpointCounter,"+checkpointCounter+",computationCounter,"+computationCounter);
        }
        bw.close();

    }

    public static void main(String[] args) throws Exception {
        logTool logTool=new logTool();
        logTool.getFileList("E:\\11-TUB\\Thesis\\0603");
//        System.out.println(logTool.outFiles);
//        getLogInfo("E:\\11-TUB\\Thesis\\新建文件夹\\application_1526231213015_0002\\container_1526231213015_0002_01_000002\\stdout");
        for (File f:logTool.outFiles) {
//            System.out.println(f.getAbsolutePath());
            getLogInfo(f.getAbsolutePath());
        }
    }

}


