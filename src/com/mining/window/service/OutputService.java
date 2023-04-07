package com.mining.window.service;

import com.component.data.table.ETable;
import com.mining.entity.ProcessResult;
import com.mining.entity.config.ProjectConfigure;
import com.mining.manage.ProcessExecuteEngine;
import com.mining.window.KGWindow;
import com.mining.window.OutputWindow;
import com.mining.window.dialog.CompileErrorDialog;
import org.jdesktop.swingx.JXSearchField;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class OutputService extends AbstractService {
    //当前项目的路径和项目名称
    private String path;
    //当前项目的配置文件，包括主类、java可执行文件位置等。
    private ProjectConfigure configure;


    //使用工具
    private String tool;

    //重做管理器，用于编辑框支持撤销和重做操作的
    private UndoManager undoManager;
    //用于记录当前项目是否处于运行状态
    private boolean isProjectRunning = false;

    /**
     * 设定当前项目的名称和路径
     * @param path 路径
     */
    public void setPath(String path){
        this.path = path.replace("\\", "/");
    }

    /**
     * 获取当前项目的配置
     * @return 项目配置
     *
     */
    public ProjectConfigure getConfigure() {
        return configure;
    }

    public String getTool() {
        return tool;
    }

    public void setTool(String tool) {
        this.tool = tool;
    }


    /**
     * 加载项目配置文件
     */
    public void loadProjectConfig(){
        File file = new File(path+"/.idea");
        if(file.exists()) {
            try (ObjectInputStream stream = new ObjectInputStream(Files.newInputStream(file.toPath()))){
                configure = (ProjectConfigure) stream.readObject();
            }catch (Exception e){
                e.printStackTrace();
            }
        } else {
            this.updateAndSaveConfigure(new ProjectConfigure("", "java"));
        }
    }

    /**
     * 更新并保存新的设置
     * @param configure 新的设置
     */
    public void updateAndSaveConfigure(ProjectConfigure configure){
        JButton button = this.getComponent("main.button.run");
        this.configure = configure;
        try (ObjectOutputStream stream = new ObjectOutputStream(Files.newOutputStream(Paths.get(path+"/.idea")))){
            stream.writeObject(configure);
            stream.flush();
            if(button != null) {
                if(configure.getMainClass().isEmpty()) {
                    button.setEnabled(false);
                    button.setToolTipText("请先完成项目运行配置！");
                } else {
                    button.setEnabled(true);
                    button.setToolTipText("点击编译运行项目");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }




    /**
     * 参数按钮的行为，点击后文本区变成参数区
     */
    public void argButtonAction(){
        // KGWindow window = (KGWindow) this.getWindow();
        JTree tree=this.getComponent("main.tree.files");
        tree.clearSelection();
        JSplitPane panel = this.getComponent("main.panel.content");
        JSplitPane paneltop= (JSplitPane) panel.getTopComponent();
        JPanel panelright= (JPanel) paneltop.getRightComponent();
        CardLayout cardLayout= (CardLayout)panelright.getLayout();

        cardLayout.show(panelright,"arg1");


    }

    /**
     * 构建按钮的行为，很明显，直接构建就完事了
     */
    public void buildButtonAction(){
        OutputWindow window = (OutputWindow) this.getWindow();
        ProcessResult result = ProcessExecuteEngine.buildProject(path);
        if(result.getExitCode() == 0) {
            JOptionPane.showMessageDialog(window, "编译成功！");
        } else {
            CompileErrorDialog dialog = new CompileErrorDialog(window, result.getOutput());
            dialog.openDialog();
        }

    }

    public void setSearch(ETable table){
        JXSearchField jxSearchField=this.getComponent("out.search");
        JButton button=this.getComponent("out.search.button");


        button.addActionListener(e -> {
            TableModel model = table.getModel();
            for (int i = 0; i < model.getRowCount(); i++) {
                String value0 = (String) model.getValueAt(i, 0);

                String value = (String) model.getValueAt(i, 1);
                String value2 = (String) model.getValueAt(i, 3);
                if(value0.contains(jxSearchField.getText())||value.contains(jxSearchField.getText())||value2.contains(jxSearchField.getText())&&jxSearchField!=null){
                    //table.setRowSelectionInterval(i, i);
                    //return;
                    table.addRowSelectionInterval(table.convertRowIndexToView(i),table.convertRowIndexToView(i));
                }
            }
             //table.clearSelection();

        });

    }

    public void setSearch2(ETable table2){
        JXSearchField jxSearchField2=this.getComponent("out.search2");
        JButton button2=this.getComponent("out.search.button2");

        button2.addActionListener(e -> {
            TableModel model = table2.getModel();
            for (int i = 0; i < model.getRowCount(); i++) {
                String value = (String) model.getValueAt(i, 0);
                String value2 = (String) model.getValueAt(i, 2);
                if(value.contains(jxSearchField2.getText())||value2.contains(jxSearchField2.getText())&&jxSearchField2!=null){
                    //table.setRowSelectionInterval(i, i);
                    //return;
                    table2.addRowSelectionInterval(table2.convertRowIndexToView(i),table2.convertRowIndexToView(i));
                }
            }
             //table2.clearSelection();
        });
    }


    /**
todo 目前是单一图  后面可改成自定义选择参数展示

     */
    public void setChart(ETable table){

        JButton chartbutton=this.getComponent("out.button.graph");

        chartbutton.addActionListener(e -> {

            JScrollPane jScrollPane=this.getComponent("rule.table");
            JViewport viewport = jScrollPane.getViewport();
            ETable etable = (ETable)viewport.getView();



            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            int []select=etable.getSelectedRows();


            //System.out.printf(String.valueOf(select.length));
            DefaultTableModel model= (DefaultTableModel) etable.getModel();


            int i;
            if(tool.equals("amie")) {
                for (i = 0; i < select.length; i++) {

                    dataset.addValue(Double.parseDouble((String) model.getValueAt(table.convertRowIndexToModel(select[i]), 4)),
                            "headCoverage", (String) model.getValueAt(table.convertRowIndexToModel(select[i]), 0));
                    dataset.addValue(Double.parseDouble((String) model.getValueAt(table.convertRowIndexToModel(select[i]), 5)),
                            "stdConfidence", (String) model.getValueAt(table.convertRowIndexToModel(select[i]), 0));
                    dataset.addValue(Double.parseDouble((String) model.getValueAt(table.convertRowIndexToModel(select[i]), 6)),
                            "PCAConfidence", (String) model.getValueAt(table.convertRowIndexToModel(select[i]), 0));

                }

                // 创建JFreeChart对象
                JFreeChart chart = ChartFactory.createBarChart3D(
                        "RuleStastic", // 图标题
                        "RuleId",
                        "Score",
                        dataset,
                        PlotOrientation.VERTICAL,
                        true, true, false);

                //利用awt进行显示
                ChartFrame chartFrame = new ChartFrame("", chart);
                chartFrame.pack();
                chartFrame.setLocation(this.getWindow().getLocation());
                chartFrame.setVisible(true);

            }
            else if (tool.equals("anyburl")){

                for (i = 0; i < select.length; i++) {


                    dataset.addValue(Double.parseDouble((String) model.getValueAt(table.convertRowIndexToModel(select[i]), 6)),
                            "Confidence", (String) model.getValueAt(table.convertRowIndexToModel(select[i]), 0));

                }

                // 创建JFreeChart对象
                JFreeChart chart = ChartFactory.createBarChart3D(
                        "RuleStastic", // 图标题
                        "RuleId",
                        "Score",
                        dataset,
                        PlotOrientation.VERTICAL,
                        true, true, false);

                //利用awt进行显示
                ChartFrame chartFrame = new ChartFrame("", chart);
                chartFrame.pack();
                chartFrame.setLocation(this.getWindow().getLocation());
                chartFrame.setVisible(true);


            }



        });


    }



    /**
     * 配置文件树的右键弹出窗口
     * @return MouseAdapter
     */
    public MouseAdapter fileTreeRightClick(){
        JTree fileTree = this.getComponent("main.tree.files");
        JPopupMenu treePopupMenu = this.getComponent("main.popup.tree");
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3)
                    treePopupMenu.show(fileTree, e.getX(), e.getY());
            }
        };
    }



    private void deleteFile(String name){
        if(name == null) return;


        File file = new File(path+"/"+name);
        if(file.exists() && file.delete()) {
            JOptionPane.showMessageDialog(this.getWindow(), "文件删除成功！");
        }else {
            JOptionPane.showMessageDialog(this.getWindow(), "文件删除失败，文件不存在？");
        }
        KGWindow window = (KGWindow) this.getWindow();
        window.refreshFileTree();
    }

    /**
     * 创建源文件，并生成默认代码
     * @param name 名称
     */
    private void createFile(String name){
        //MainWindow window = (MainWindow) this.getWindow();
        KGWindow window = (KGWindow) this.getWindow();
        if(name == null) return;
//        String[] split = name.split("\\.");
//        String className = split[split.length - 1];
//        String packageName = name.substring(0, name.length() - className.length() - 1);

        try {
            File dir = new File(path);
            if(!dir.exists() && !dir.mkdirs()) {
                JOptionPane.showMessageDialog(window, "无法创建文件夹！");
                return;
            }
            File file = new File(path+"/"+name);
            if(file.exists() || !file.createNewFile()) {
                JOptionPane.showMessageDialog(window, "无法创建，此文件已存在！");
                return;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
