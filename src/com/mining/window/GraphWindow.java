package com.mining.window;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class GraphWindow extends JFrame {
    private mxGraphComponent graphComponent;

    public GraphWindow( String path) {
        super( "图谱预览");


        mxGraph graph = new mxGraph();

        Object parent = graph.getDefaultParent();
        Random r = new Random();
        int cnt=0;

        graph.getModel().beginUpdate();
        Map<String,Object> map=new HashMap<String,Object>();
        try
        {
            List<String> allLines = Files.readAllLines(Paths.get(path));

            Object v1 = null;
            Object v2 = null;
            for (String line : allLines) {
                cnt++;
                if (cnt>10)
                    break;
                String[] arr = line.split("\\s+");

                if(!map.containsKey(arr[0])){
                    v1 = graph.insertVertex(parent, null, arr[0],r.nextInt(400), r.nextInt(320), 80,
                            30,"fillColor=#87CEFA;strokeColor=white;fontStyle=1;rounded=1;fontSize=12;");
                    map.put(arr[0],v1);
                }else
                    v1=map.get(arr[0]);

                if(!map.containsKey(arr[2])){
                    v2 = graph.insertVertex(parent, null, arr[2],r.nextInt(400), r.nextInt(320), 80,
                            30,"fillColor=#87CEFA;strokeColor=white;fontStyle=1;rounded=1;fontSize=12;");
                    map.put(arr[2],v2);

                }else
                    v2=map.get(arr[2]);

                graph.insertEdge(parent, null, arr[1], v1, v2);

            }


//            Object v2 = graph.insertVertex(parent, null, "World!", 240, 150,
//                    80, 30);
//            graph.insertEdge(parent, null, "Edge", v1, v2);
        } catch (IOException e) {
            e.printStackTrace();
        } finally
        {
            graph.getModel().endUpdate();
        }


        graphComponent = new mxGraphComponent(graph);

        graph.setCellsEditable(false);

        JMenu jMenu=new JMenu("预览实体数");
        JMenuItem jMenuItem=new JMenuItem("5");
        jMenuItem.addActionListener(e->{
            this.initgraph(5);
            repaint();
            validate();
            invalidate();
            validate();
        });
        JMenuItem jMenuItem2=new JMenuItem("10");
        jMenuItem2.addActionListener(e->{
            this.initgraph(10);
            repaint();
            validate();
            invalidate();
            validate();
        });
        JMenuItem jMenuItem3=new JMenuItem("20");
        jMenuItem3.addActionListener(e->{
            this.initgraph(20);
            repaint();
            validate();
            invalidate();
            validate();
        });
        jMenu.add(jMenuItem);
        jMenu.add(jMenuItem2);
        jMenu.add(jMenuItem3);

        setJMenuBar(new JMenuBar());
        getJMenuBar().add(jMenu);


        getContentPane().add(graphComponent);
    }



    private void initgraph(int i){
        getContentPane().remove(graphComponent);

        mxGraph graph = new mxGraph();
        Object parent = graph.getDefaultParent();
        Random r = new Random();
        int cnt=0;
        graph.getModel().beginUpdate();
        Map<String,Object> map=new HashMap<String,Object>();
        try
        {
            List<String> allLines = Files.readAllLines(Paths.get("D:\\test\\yago2core.10kseedsSample.compressed.notypes.tsv"));

            Object v1 = null;
            Object v2 = null;
            for (String line : allLines) {
                cnt++;
                if (cnt>i)
                    break;
                String[] arr = line.split("\\s+");

                if(i!=20){
                    if(!map.containsKey(arr[0])){
                        v1 = graph.insertVertex(parent, null, arr[0],r.nextInt(400), r.nextInt(320), 80,
                                30);
                        map.put(arr[0],v1);
                    }else
                        v1=map.get(arr[0]);

                    if(!map.containsKey(arr[2])){
                        v2 = graph.insertVertex(parent, null, arr[2],r.nextInt(400), r.nextInt(320), 80,
                                30);
                        map.put(arr[2],v2);
                    }else
                        v2=map.get(arr[2]);
                }
                else{

                    if(!map.containsKey(arr[0])){
                        v1 = graph.insertVertex(parent, null, arr[0],r.nextInt(800), r.nextInt(600), 80,
                                30);
                        map.put(arr[0],v1);
                    }else
                        v1=map.get(arr[0]);

                    if(!map.containsKey(arr[2])){
                        v2 = graph.insertVertex(parent, null, arr[2],r.nextInt(800), r.nextInt(600), 80,
                                30);
                        map.put(arr[2],v2);
                    }else
                        v2=map.get(arr[2]);

                }

                graph.insertEdge(parent, null, arr[1], v1, v2);

            }

//            Object v1 = graph.insertVertex(parent, null, "Hello",20, 20, 80,
//                    30);
//            Object v2 = graph.insertVertex(parent, null, "World!", 240, 150,
//                    80, 30);
//            graph.insertEdge(parent, null, "Edge", v1, v2);
        } catch (IOException e) {
            e.printStackTrace();
        } finally
        {
            graph.getModel().endUpdate();

        }

        graphComponent = new mxGraphComponent(graph);

        Object[] cells=  graph.getChildCells(parent,true,false);
        Map<Object , String> newMap = map.entrySet().stream().collect(Collectors.toMap(entry -> entry.getValue(), entry -> entry.getKey()));

        for (int j=0;j<cells.length;j++){
            System.out.printf(newMap.get(cells[j]));

        }
        graph.setCellStyle("rounded=1;",cells);

        getContentPane().add(graphComponent);

    }



}
