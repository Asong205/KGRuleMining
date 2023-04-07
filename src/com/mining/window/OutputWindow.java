package com.mining.window;

import com.component.basic.color.ColorUtil;
import com.component.basic.layout.CenterLayout;
import com.component.data.table.ETable;
import com.component.data.table.renderer.ETableCellRenderer;
import com.component.navigation.steps.StepsComponent;
import com.component.svg.icon.regular.XCircleSvg;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.mining.manage.OutputManager;
import com.mining.manage.ProcessExecuteEngine;
import com.mining.window.dialog.DataSaveDialog;
import com.mining.window.dialog.FileSaveDialog;
import com.mining.window.dialog.HelpDialog;
import com.mining.window.enums.CloseAction;
import com.mining.window.service.OutputService;
import org.jdesktop.swingx.JXSearchField;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

public class OutputWindow extends AbstractWindow <OutputService>{


    private final String path;
    private final String name;

    private String tool;
    private ETable datatable;
    private ETable ruletable;

    public OutputWindow(String name, String path) {
        super("项目："+name, new Dimension(1000, 600), true, OutputService.class);
        //设定路径和项目名称，然后开始配置窗口内容
        this.path = path;
        this.name = name;
        //窗口关闭不能直接退出程序，因为要回到欢迎界面
        this.setDefaultCloseAction(CloseAction.DISPOSE);
        //为业务层设定当前项目的路径
        service.setPath(path);
        //然后是加载当前项目的配置，项目的配置不同会影响组件的某些显示状态
        service.loadProjectConfig();
        //最后再初始化窗口内容
        this.initWindowContent();
    }

    public OutputWindow(String name, String path ,String tool) {
        super("项目："+name, new Dimension(1000, 600), true, OutputService.class);
        //设定路径和项目名称，然后开始配置窗口内容
        this.path = path;
        this.name = name;
        this.tool = tool;
        //窗口关闭不能直接退出程序，因为要回到欢迎界面
        this.setDefaultCloseAction(CloseAction.DISPOSE);
        //为业务层设定当前项目的路径
        service.setPath(path);
        service.setTool(tool);
        //然后是加载当前项目的配置，项目的配置不同会影响组件的某些显示状态
        service.loadProjectConfig();
        //最后再初始化窗口内容
        this.initWindowContent();
    }

    @Override
    protected void initWindowContent() {
        //代码编辑主界面包括最上面的一排工具栏

        this.addComponent("main.panel.ruletools", new JPanel(), BorderLayout.NORTH, this::initControlTools);


        //以及左边的文件树区域和中间的代码编辑区域，还有最下面的控制台区域
        this.addComponent("main.panel.table", new JSplitPane(), BorderLayout.CENTER, panel -> {
            //这里先分出最下方控制台和中心区域两个部分，所以先纵向分割一下
            panel.setOrientation(JSplitPane.VERTICAL_SPLIT);

            //首先配置最下方的控制台区域
            panel.setBottomComponent(this.createConsole());
            panel.setDividerLocation(380);   //下面的分割条默认在 y = 400 位置上

            //这一块是中心表格区域   AMIE
            if (tool.equals("amie")) {

                OutputManager outputManager = null;
                try {
                    outputManager = new OutputManager(path,tool);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                java.util.List<java.util.List<String>> list = outputManager.getRule();

                String[][] res = new String[list.size()][]; // 存放转换结果的 二维数组
                for (int i = 0; i < res.length; i++) { // 转换方法
                    res[i] = list.get(i).toArray(new String[list.get(i).size()]);
                }


                String[] s2 = {"id", "body", "    ", "head", "Head Coverage", "stdConfidence", "PCAConfidence", "PositiveExamples"};
                DefaultTableModel tableModel = new DefaultTableModel(res, s2);


                ETable table = new ETable(tableModel);
                TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(tableModel);
                sorter.setComparator(0, new Comparator()
                {
                    public int compare(Object arg0, Object arg1) {
                        try {
                            Integer a = Integer.parseInt(arg0.toString());
                            Integer b = Integer.parseInt(arg1.toString());
                            return (int) (a - b);
                        } catch (NumberFormatException e) {
                            return 0;
                        }
                    }
                });
                sorter.setComparator(7, new Comparator()
                {
                    public int compare(Object arg0, Object arg1) {
                        try {
                            Integer a = Integer.parseInt(arg0.toString());
                            Integer b = Integer.parseInt(arg1.toString());
                            return (int) (a - b);
                        } catch (NumberFormatException e) {
                            return 0;
                        }
                    }
                });

                table.setRowSorter(sorter);

                TableColumn column0 = table.getColumn(0);
                column0.setMaxWidth(40);

                TableColumn column = table.getColumn(1);
                column.setMinWidth(100);

                TableColumn column2 = table.getColumn(2);
                column2.setMaxWidth(40);
                TableColumn column3 = table.getColumn(4);
                column3.setMaxWidth(120);
                TableColumn column4 = table.getColumn(5);
                column4.setMaxWidth(120);
                TableColumn column5 = table.getColumn(6);
                column5.setMaxWidth(120);
                TableColumn column6 = table.getColumn(7);
                column6.setMaxWidth(120);

                table.setRowHeight(30);//设置行高


                table.setFont(new Font("微软雅黑", Font.PLAIN, 10));
                ETableCellRenderer renderer = table.getCellRenderer();

                renderer.setContainBorder(true);
                renderer.setContainStripe(true);
                JScrollPane jScrollPane =new JScrollPane(table);
                datatable=table;
                this.mapComponent("rule.table",jScrollPane);




                //中间区域

                JTabbedPane tb = new JTabbedPane();

                tb.add("规则数据", jScrollPane);

                JTextArea logArea = new JTextArea();

                logArea.setEditable(false);
                logArea.setFont(new Font("微软雅黑",Font.PLAIN, 14));

                try {
                    logArea.setText(outputManager.getOutput().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                JScrollPane logpane =new JScrollPane(logArea);



                tb.add("输出日志", logpane);

                service.setSearch(table);
                service.setChart(table);


                //******************rule 1.4


                java.util.List<java.util.List<String>> list2 = outputManager.getRules();
                JTabbedPane tb2 = new JTabbedPane();
                String[][] res2 = new String[list2.size()][]; // 存放转换结果的 二维数组
                for(int i=0; i<res2.length; i++){ // 转换方法
                    res2[i] = list2.get(i).toArray(new String[list2.get(i).size()]);
                }



                String[] s22 = {"body","    ", "head"};
                DefaultTableModel tableModel2 = new DefaultTableModel(res2, s22);



                ETable table2 = new ETable(tableModel2);
                TableColumn column22 =table2.getColumn(0);
                column22.setMinWidth(100);

                TableColumn column222 =table2.getColumn(1);
                column222.setMaxWidth(40);
//            TableColumn column32 =table2.getColumn(3);
//            column32.setMaxWidth(100);
//            TableColumn column42 =table2.getColumn(4);
//            column4.setMaxWidth(100);
//            TableColumn column52 =table2.getColumn(5);
//            column52.setMaxWidth(100);
//            TableColumn column62 =table2.getColumn(6);
//            column62.setMaxWidth(100);

                table2.setRowHeight(30);//设置行高

                //table.setRowMargin(10);//设置行间距

                //table.setFont(new Font("微软雅黑", Font.BOLD,18));//设置字体
                //table.setIntercellSpacing(new Dimension(4 ,4));

                table2.setFont(new Font("微软雅黑",Font.PLAIN, 10));
                ETableCellRenderer renderer2 = table2.getCellRenderer();

                renderer2.setContainBorder(true);
                renderer2.setContainStripe(true);
                //设置居中
                renderer2.setHorizontalAlignment(JLabel.CENTER);


                JScrollPane jScrollPane2 =new JScrollPane(table2);

                ruletable=table2;
                tb2.add("规则管理",jScrollPane2);

                service.setSearch2(table2);

                CardLayout cardLayout = new CardLayout();
                JPanel jPanel =new JPanel();
                jPanel.setLayout(cardLayout);
                jPanel.add(tb,"rule3");
                jPanel.add(tb2,"rule4");
                this.mapComponent("main.rule",jPanel);
                cardLayout.show(jPanel,"rule3");

                panel.setTopComponent(jPanel);

            }
            //*********************************************anyburl************
            else
                if (tool.equals("anyburl")){

                    OutputManager outputManager = null;
                    try {
                        outputManager = new OutputManager(path,tool);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    java.util.List<java.util.List<String>> list = outputManager.getRule2();

                    String[][] res = new String[list.size()][]; // 存放转换结果的 二维数组
                    for (int i = 0; i < res.length; i++) { // 转换方法
                        res[i] = list.get(i).toArray(new String[list.get(i).size()]);
                    }





                    String[] s2 = {"id", "body", "    ", "head", "BodySize", "SupportDegree", "StdConfidence"};
                    DefaultTableModel tableModel = new DefaultTableModel(res, s2);


                    ETable table = new ETable(tableModel);

                    for (int i = 0; i < res.length; i++) { // 转换方法
                         tableModel.setValueAt(Integer.parseInt(res[i][4]),i,4);
                    }


                    TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(tableModel);
                    sorter.setComparator(0, new Comparator()
                    {
                        public int compare(Object arg0, Object arg1) {
                            try {
                                Integer a = Integer.parseInt(arg0.toString());
                                Integer b = Integer.parseInt(arg1.toString());
                                return (int) (a - b);
                            } catch (NumberFormatException e) {
                                return 0;
                            }
                        }
                    });
                    sorter.setComparator(4, new Comparator()
                    {
                        public int compare(Object arg0, Object arg1) {
                            try {
                                Integer a = Integer.parseInt(arg0.toString());
                                Integer b = Integer.parseInt(arg1.toString());
                                return (int) (a - b);
                            } catch (NumberFormatException e) {
                                return 0;
                            }
                        }
                    });
                    sorter.setComparator(5, new Comparator()
                    {
                        public int compare(Object arg0, Object arg1) {
                            try {
                                Integer a = Integer.parseInt(arg0.toString());
                                Integer b = Integer.parseInt(arg1.toString());
                                return (int) (a - b);
                            } catch (NumberFormatException e) {
                                return 0;
                            }
                        }
                    });


                    table.setRowSorter(sorter);
                    TableColumn column0 = table.getColumn(0);
                    column0.setMaxWidth(40);

                    TableColumn column = table.getColumn(1);
                    column.setMinWidth(100);

                    TableColumn column2 = table.getColumn(2);
                    column2.setMaxWidth(40);
                    TableColumn column3 = table.getColumn(4);
                    column3.setMaxWidth(100);
                    TableColumn column4 = table.getColumn(5);
                    column4.setMaxWidth(100);
                    TableColumn column5 = table.getColumn(6);
                    column5.setMaxWidth(100);


                    table.setRowHeight(30);//设置行高


                    table.setFont(new Font("微软雅黑", Font.PLAIN, 10));
                    ETableCellRenderer renderer = table.getCellRenderer();

                    renderer.setContainBorder(true);
                    renderer.setContainStripe(true);
                    JScrollPane jScrollPane =new JScrollPane(table);
                    datatable=table;
                    this.mapComponent("rule.table",jScrollPane);




                    //中间区域

                    JTabbedPane tb = new JTabbedPane();

                    tb.add("规则数据", jScrollPane);

                    JTextArea logArea = new JTextArea();

                    logArea.setEditable(false);
                    logArea.setFont(new Font("微软雅黑",Font.PLAIN, 14));

                    try {
                        logArea.setText(outputManager.getOutput2().toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    JScrollPane logpane =new JScrollPane(logArea);



                    tb.add("输出日志", logpane);

                    service.setSearch(table);

                    service.setChart(table);


                    //******************rule 1.4


                    java.util.List<java.util.List<String>> list2 = outputManager.getRules2();
                    JTabbedPane tb2 = new JTabbedPane();
                    String[][] res2 = new String[list2.size()][]; // 存放转换结果的 二维数组
                    for(int i=0; i<res2.length; i++){ // 转换方法
                        res2[i] = list2.get(i).toArray(new String[list2.get(i).size()]);
                    }



                    String[] s22 = {"body","    ", "head"};
                    DefaultTableModel tableModel2 = new DefaultTableModel(res2, s22);


                    ETable table2 = new ETable(tableModel2);
                    TableColumn column22 =table2.getColumn(0);
                    column22.setMinWidth(100);

                    TableColumn column222 =table2.getColumn(1);
                    column222.setMaxWidth(40);
//            TableColumn column32 =table2.getColumn(3);
//            column32.setMaxWidth(100);
//            TableColumn column42 =table2.getColumn(4);
//            column4.setMaxWidth(100);
//            TableColumn column52 =table2.getColumn(5);
//            column52.setMaxWidth(100);
//            TableColumn column62 =table2.getColumn(6);
//            column62.setMaxWidth(100);

                    table2.setRowHeight(30);//设置行高

                    //table.setRowMargin(10);//设置行间距

                    //table.setFont(new Font("微软雅黑", Font.BOLD,18));//设置字体
                    //table.setIntercellSpacing(new Dimension(4 ,4));

                    table2.setFont(new Font("微软雅黑",Font.PLAIN, 10));
                    ETableCellRenderer renderer2 = table2.getCellRenderer();

                    renderer2.setContainBorder(true);
                    renderer2.setContainStripe(true);
                    //设置居中
                    renderer2.setHorizontalAlignment(JLabel.CENTER);


                    JScrollPane jScrollPane2 =new JScrollPane(table2);

                    ruletable=table2;
                    tb2.add("规则管理",jScrollPane2);

                    service.setSearch2(table2);

                    CardLayout cardLayout = new CardLayout();
                    JPanel jPanel =new JPanel();
                    jPanel.setLayout(cardLayout);
                    jPanel.add(tb,"rule3");
                    jPanel.add(tb2,"rule4");
                    this.mapComponent("main.rule",jPanel);
                    cardLayout.show(jPanel,"rule3");

                    panel.setTopComponent(jPanel);


                }



        });
    }

    /**
     * 对最上面一排工具栏包括里面的各个按钮进行初始化。
     * @param jpanel 工具栏面板
     */

    private void initControlTools(JPanel jpanel){

        //ui1.3
        CardLayout cardLayout=new CardLayout();

//		给主要显示面板添加布局方式
        jpanel.setLayout(cardLayout);

        JPanel panel=new JPanel();
        //ui1.4
        JPanel panel2=new JPanel();

        //************************1.3

        //这里采用流式布局，直接让按钮居右按顺序放置
        panel.setPreferredSize(new Dimension(0, 40));
        FlowLayout layout = new FlowLayout();
        layout.setAlignment(FlowLayout.RIGHT);
        layout.setHgap(10);

        panel.setLayout(layout);


        this.addComponent(panel, "out.button.help", new JButton("?"), button -> {
//            button.setBackground(null);
//            button.setBorder(new RoundBorder(-1));

            button.setPreferredSize(new Dimension(30, 30));
            button.addActionListener(e -> {
                // service.settingButtonAction()
                HelpDialog helpDialog =new HelpDialog(this,"out");
                helpDialog.openDialog();
            });
        });

        this.addComponent(panel,"out.search",new JXSearchField(),jxSearchField1 -> {
            jxSearchField1.setPreferredSize(new Dimension(200,30));

//            jxSearchField1.setBorder(new RoundBorder(-1));
//            //消除矩形背景
//            jxSearchField1.setBackground(null);

        });

        this.addComponent(panel,"out.search.button",new JButton("搜索"), button -> {
//            button.setBackground(null);
//            button.setBorder(new RoundBorder(-1));

            button.setPreferredSize(new Dimension(60, 35));


        });

        this.addComponent(panel, "out.button.graph", new JButton("生成图表"), button -> {
            button.setPreferredSize(new Dimension(90, 35));
//            button.setBorder(new RoundBorder(-1));
//
//            //消除矩形背景
//            button.setBackground(null);
            button.addActionListener(e ->{

                //service.buildButtonAction()
                    }
            );
        });


        this.addComponent(panel, "out.button.out", new JButton("数据导出"), button -> {
            button.setPreferredSize(new Dimension(90, 35));
            button.addActionListener(e -> {
                DataSaveDialog fileDialog=new DataSaveDialog(this);
                fileDialog.openDialog();

            });
        });


        this.addComponent(panel, "out.button.next", new JButton("下一步"), button -> {
            button.setPreferredSize(new Dimension(80, 35));
            button.addActionListener(e -> {
                // service.settingButtonAction()
                JPanel jp=this.getComponent("main.panel.ruletools");
                CardLayout cardLayout1= (CardLayout) jp.getLayout();
                cardLayout1.show(jp,"1.4");
                StepsComponent c=this.getComponent("main.step");
                c.addStep();

                JPanel rulejp=this.getComponent("main.rule");
                CardLayout cardLayout2= (CardLayout) rulejp.getLayout();
                cardLayout2.show(rulejp,"rule4");


            });
        });
        this.addComponent(panel, "out.button.stat", new JButton("返回"), button -> {
            button.setPreferredSize(new Dimension(70, 35));
            button.addActionListener(e -> {
                this.closeWindow();
            });
        });


        //******************1.4

        //这里采用流式布局，直接让按钮居右按顺序放置
        panel2.setPreferredSize(new Dimension(0, 40));
        FlowLayout layout2 = new FlowLayout();
        layout.setAlignment(FlowLayout.RIGHT);
        layout.setHgap(10);

        panel2.setLayout(layout);

        this.addComponent(panel2, "out2.button.help", new JButton("?"), button -> {
//            button.setBackground(null);
//            button.setBorder(new RoundBorder(-1));

            button.setPreferredSize(new Dimension(30, 30));
            button.addActionListener(e -> {
                // service.settingButtonAction()
                HelpDialog helpDialog =new HelpDialog(this,"out2");
                helpDialog.openDialog();
            });
        });

        this.addComponent(panel2,"out.search2",new JXSearchField(),jxSearchField1 -> {
            jxSearchField1.setPreferredSize(new Dimension(200,30));

//            jxSearchField1.setBorder(new RoundBorder(-1));
//
//            //消除矩形背景
//            jxSearchField1.setBackground(null);


        });

        this.addComponent(panel2,"out.search.button2",new JButton("搜索"), button -> {
            //button.setBackground(null);
            //button.setBorder(new RoundBorder(-1));

            button.setPreferredSize(new Dimension(60, 35));


        });

        this.addComponent(panel2, "out2.button.file", new JButton("文件导出"), button -> {
            button.setPreferredSize(new Dimension(90, 35));
//            button.setBorder(new RoundBorder(-1));
//
//            //消除矩形背景
//            button.setBackground(null);

            button.addActionListener(e ->{
                FileSaveDialog fileDialog=new FileSaveDialog(this);
                fileDialog.openDialog();

                        //service.buildButtonAction()
                    }
            );
        });

        this.addComponent(panel2, "out2.button.rule", new JButton("新增规则"), button -> {
            button.setPreferredSize(new Dimension(90, 35));
            button.addActionListener(e -> {
                DefaultTableModel defaultTableModel= (DefaultTableModel) ruletable.getModel();

                defaultTableModel.addRow(new Object[]{"body"," => ","head"});
            });
        });
        this.addComponent(panel2, "out2.button.delete", new JButton("删除所选"), button -> {
            button.setPreferredSize(new Dimension(90, 35));
            button.addActionListener(e -> {
                DefaultTableModel defaultTableModel= (DefaultTableModel) ruletable.getModel();
                int[] selectedRow = ruletable.getSelectedRows();
                for (int i=0;i<selectedRow.length;i++){
                    if(selectedRow[i]!=-1)
                        defaultTableModel.removeRow(selectedRow[i]);
                }


            });
        });

        this.addComponent(panel2, "out2.button.up", new JButton("上一步"), button -> {
            button.setPreferredSize(new Dimension(80, 35));
            button.addActionListener(e -> {
                JPanel jp=this.getComponent("main.panel.ruletools");
                CardLayout cardLayout1= (CardLayout) jp.getLayout();
                cardLayout1.show(jp,"1.3");
                StepsComponent c=this.getComponent("main.step");
                c.reduceStep();

                JPanel rulejp=this.getComponent("main.rule");
                CardLayout cardLayout2= (CardLayout) rulejp.getLayout();
                cardLayout2.show(rulejp,"rule3");

            });
        });



        //*****************
        jpanel.add(panel,"1.3");
        jpanel.add(panel2,"1.4");

        cardLayout.show(jpanel,"1.3");



    }

    public ETable getDatatable(){
        return datatable;
    }
    public ETable getRuletable(){
        return ruletable;
    }
    public String getPath() {
        return path;
    }

    /**
     * 创建底部控制台板块，用于展示控制台输出信息
     * @return 底部板块
     */
    private JPanel createConsole(){
        //步骤

        JPanel stepPanel=new JPanel();
        //stepPanel.setLayout(new FlowLayout());
        //stepPanel.setLayout(new FlowLayout());
        stepPanel.setLayout(new CenterLayout());

        StepsComponent c = new StepsComponent(Arrays.asList(" ", " ", " ", " "),
                XCircleSvg.class,
                Arrays.asList("项目配置", "参数调试", "输出控制", "规则管理"),
                2, ColorUtil.PRIMARY, 120, true);
//        c.setAchievedColor(ColorUtil.BORDER_LEVEL2);
//        c.setCurrentColor(ColorUtil.PRIMARY);

        this.mapComponent("main.step",c);


        stepPanel.add(c);

//        stepPanel.add(up);
//        stepPanel.add(down);

        return stepPanel;
    }
    public String getTool() {
        return tool;
    }



    @Override
    protected boolean onClose() {
        //关闭之前如果还有运行的项目没有结束，一定要结束掉
        ProcessExecuteEngine.stopProcess();


        return true;
    }



    public static void main(String[] args) throws UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(new FlatIntelliJLaf());
        OutputWindow window = new OutputWindow("KG rule mining","d:/test","anyburl");
        window.openWindow();
    }
}
