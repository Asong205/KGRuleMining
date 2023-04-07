package com.mining.manage;

import com.mining.entity.RuleEntity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class OutputManager {

    String path;
    String tool;




    //anyburl  看log得文件time值
    String time;
    String logpath;


//    public OutputManager(String path) {
//        this.path=path+"/output/output.txt";
//    }
    public OutputManager(String path ,String tool) throws IOException {
        this.tool=tool;
        if (tool.equals("amie"))
            this.path=path+"/output/output.txt";
        else if (tool.equals("anyburl")){
            logpath=path+"/output/output_log";
            StringBuffer sb=new StringBuffer();
            List<String> allLines = Files.readAllLines(Paths.get(path+"/output/output_log"));
                for (String line : allLines) {
                    if (line.contains("BATCH_TIME = ")){
                        sb.append(line.substring(13));
                    }
                }
            this.path=path+"/output/output-"+sb;
        }

    }

    //
    public StringBuffer getOutput() throws IOException {

        StringBuffer sb=new StringBuffer();

        List<String> allLines = Files.readAllLines(Paths.get(path));

        byte[] bytes = Files.readAllBytes(Paths.get(path));

        String content = new String(bytes, StandardCharsets.UTF_8);
        if (content.contains("=>")){
            for (String  line : allLines) {
                if (line.contains("  Starting ")){
                    sb.append("数据集：\n");
                    sb.append(line.substring(11)+"\n");
                }else
                if (line.contains("Loaded ")){
                    sb.append("\n实例数量：\n");
                    sb.append(line.substring(7,line.indexOf("facts"))+"\n");
                }else
                if (line.contains("Filtering on PCA confidence with minimum threshold")){
                    sb.append("\nPCA置信度阈值：\n");
                    sb.append(line.substring(51)+"\n");
                }else
                if (line.contains("Starting the mining phase... Using")){
                    sb.append("\n线程数：\n");
                    sb.append(line.substring(35,line.indexOf("threads"))+"\n");
                }else
                if (line.contains("Total time")){
                    sb.append("\n运行时长：\n");
                    sb.append(line.substring(11)+"\n");
                }else
                if (line.contains("rules mined.")){
                    sb.append("\n规则总数量：\n");
                    sb.append(line.substring(0,line.indexOf("rules mined."))+"\n");
                }



            }

        }else{

            //报错情况 直接输出日志

            for (String line : allLines) {
                sb.append(line+"\n");
            }
        }
        return sb;
    };


    //todo anyburl日志格式 目前是_log文件内容
    public StringBuffer getOutput2() throws IOException {

        StringBuffer sb = new StringBuffer();

        List<String> allLines = Files.readAllLines(Paths.get(logpath));

        for (String line : allLines) {
            sb.append(line+"\n");
        }

        return sb;
    }

    //获取全部数据  amie
    public List<List<String>> getRule(){

            Integer cnt = 1;

            List<List<String>> List = new ArrayList<>();
            try {
                List<String> allLines = Files.readAllLines(Paths.get(path));
                for (String line : allLines) {
                    if (line.contains("=>")) {
                        //String [] arr = line.split("\\s+");
                        String[] arr = line.split("=>");
                        String[] arr2 = arr[1].split("\\s+");
                        String body = arr2[0] + arr2[1] + arr2[2] + arr2[3];
                        //System.out.println(body);
                        java.util.List l = new RuleEntity(arr[0], body, arr2[4], arr2[5], arr2[6], arr2[7]).getList();
                        l.add(0, cnt.toString());
                        cnt++;

                        List.add(l);
                    }
                    //sb.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return List;

    };

    public List<List<String>> getRule2(){

        Integer cnt = 1;

        List<List<String>> List = new ArrayList<>();
        try {
            List<String> allLines = Files.readAllLines(Paths.get(path));
            for (String line : allLines) {

                    //String [] arr = line.split("\\s+");
                    String[] arr = line.split("<=");
                    String[] arr2 = arr[0].split("\\s+");
                    String body = arr[1];
                    //System.out.println(body);
                    String head=arr2[3];


                    java.util.List l = new RuleEntity(head, body, arr2[0], arr2[1], arr2[2]).getList2();
                    l.add(0, cnt.toString());

                    cnt++;

                    List.add(l);

                //sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return List;

    };



    //获取规则
    public List<List<String>> getRules(){

        List<List<String>> List = new ArrayList<>();
        try {
            List<String> allLines = Files.readAllLines(Paths.get(path));
            for (String line : allLines) {
                if (line.contains("=>") )
                {
                    //String [] arr = line.split("\\s+");
                    String [] arr = line.split("=>");
                    String [] arr2 =arr[1].split("\\s+");
                    String body = arr2[0]+arr2[1]+arr2[2]+arr2[3];
                    //System.out.println(body);
                    List.add(new RuleEntity(arr[0],body,arr2[4],arr2[5],arr2[6],arr2[7]).getList());
                }
                //sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return List;
    };


    //获取规则
    public List<List<String>> getRules2(){

        List<List<String>> List = new ArrayList<>();
        try {
            List<String> allLines = Files.readAllLines(Paths.get(path));
            for (String line : allLines) {

                    //String [] arr = line.split("\\s+");
                    String[] arr = line.split("<=");
                    String[] arr2 = arr[0].split("\\s+");
                    String body = arr[1];
                    //System.out.println(body);
                    String head=arr2[3];

                    List.add(new RuleEntity(head, body, arr2[0], arr2[1], arr2[2]).getList2());
                }
                //sb.append(line);

            //System.out.printf(List.get(0).get(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return List;
    };



//    public static void main(String[] args) {
//        String line="s a    b";
//        String [] arr = line.split("\\s+");
//        System.out.printf(arr[1]);
//
//    }

}
