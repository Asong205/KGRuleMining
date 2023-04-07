package com.mining.window;

import com.component.basic.color.ColorUtil;
import com.component.basic.layout.CenterLayout;
import com.component.navigation.steps.StepsComponent;
import com.component.notice.loading.LoadingLabel2;
import com.component.notice.notification.NotificationComponent;
import com.component.radiance.common.api.icon.RadianceIcon;
import com.component.svg.icon.regular.XCircleSvg;
import com.component.util.SwingTestUtil;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.mining.manage.ProcessExecuteEngine;
import com.mining.manage.ProjectManager;
import com.mining.window.component.SwitchButton;
import com.mining.window.dialog.CompileErrorDialog;
import com.mining.window.dialog.HelpDialog;
import com.mining.window.enums.CloseAction;
import com.mining.window.layout.XCardLayout;
import com.mining.window.service.MainService;
import com.mxgraph.swing.util.mxSwingConstants;
import com.mxgraph.util.mxConstants;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

import static com.component.notice.notification.NotificationFactory.openNotification;

public class KGWindow extends AbstractWindow <MainService>{

    private final String path;
    private final String name;
    private JTextField cmdField;

    private String cmd;

    private String tool="amie";

    private DefaultMutableTreeNode root;

    public KGWindow(String name, String path) {
        super("项目："+name, new Dimension(1000, 600), true, MainService.class);
        //设定路径和项目名称，然后开始配置窗口内容
        this.path = path;
        this.name = name;
        //窗口关闭不能直接退出程序，因 为要回到欢迎界面
        this.setDefaultCloseAction(CloseAction.DISPOSE);
        //为业务层设定当前项目的路径
        service.setPath(path);
        //然后是加载当前项目的配置，项目的配置不同会影响组件的某些显示状态
        service.loadProjectConfig();
        //最后再初始化窗口内容
        this.initWindowContent();
    }



    @Override
    protected void initWindowContent() {
        //
        //编辑主界面包括最上面的一排工具栏
        this.addComponent("main.panel.tools", new JPanel(), BorderLayout.NORTH, this::initControlTools);

        //以及左边的文件树区域和中间的编辑区域，还有最下面的控制台区域
        this.addComponent("main.panel.content", new JSplitPane(), BorderLayout.CENTER, panel -> {
            //这里先分出最下方控制台和中心区域两个部分，所以先纵向分割一下
            panel.setOrientation(JSplitPane.VERTICAL_SPLIT);

            //首先配置最下方的控制台区域


            JPanel jPanel =this.createConsole();
            this.mapComponent("console.panel", jPanel);

            panel.setBottomComponent(jPanel);

            //切换显示
            CardLayout cardLayout0= (CardLayout)jPanel.getLayout();
            cardLayout0.show(jPanel,"step");

            panel.setDividerLocation(380);   //下面的分割条默认在 y = 400 位置上

            //这一块是中心区域，中心区域包含左侧文件树和右侧代码编辑界面
            JSplitPane centerPanel = new JSplitPane();

            centerPanel.setLeftComponent(this.createLeftPanel());
            //文本 参数 区

            JPanel cpanel =this.createRightPanel();

            centerPanel.setRightComponent(cpanel);

            XCardLayout cardLayout= (XCardLayout)cpanel.getLayout();
            cardLayout.show(cpanel,"arg1");

            centerPanel.setDividerLocation(200);   //中间的分割条默认在 x = 200 位置上
            panel.setTopComponent(centerPanel);
        });
    }

    /**
     * 对最上面一排工具栏包括里面的各个按钮进行初始化。
     * @param panel 工具栏面板
     */
    private void initControlTools(JPanel panel){
        //这里采用流式布局，直接让按钮居右按顺序放置
        panel.setPreferredSize(new Dimension(800, 65));

        //panel.setBounds(0,50,800,65);
//        FlowLayout layout = new FlowLayout();
//        layout.setAlignment(FlowLayout.CENTER);
        panel.setLayout(null);



        this.addComponent(panel,"main.text",cmdField = new JTextField(50), field -> {
            field.setBounds(180, 10, 450, 35);
            field.setVisible(false);
//            field.addKeyListener(new KeyAdapter() {
//                @Override
//                public void keyReleased(KeyEvent e) {
//                    //onKeyPress();
//                }
//            });

        });
        SwingTestUtil.setDefaultTimingSource();




        //测试用例
        cmdField.setText("java -jar files/amie-milestone-intKB.jar D:\\下载\\allData\\kbs\\yago2\\yago2core.10kseedsSample.compressed.notypes.tsv > D:/test/file.txt");

        //第一个按钮是运行/停止按钮，这个按钮有两种状态，如果主类已经配置，那么就可以运行，否则就不能运行


        this.addComponent(panel, "main.button.run", new JButton("运行"), button -> {

            //button.setPreferredSize(new Dimension(60, 25));
            button.setBounds(750,10,60,35);
//            if(service.getConfigure().getMainClass().isEmpty()) {  //判断主类是否已经配置
//                button.setEnabled(false);
//                button.setToolTipText("请先完成项目运行配置！");
//            } else
            {
                button.setEnabled(true);
                button.setToolTipText("点击运行项目");
            }
            button.addActionListener(e -> {

                // synchronized (this) {
                    try {
                        button.setEnabled(false);

                        if(tool.equals("amie"))
                        service.runButtonAction2(cmdField.getText());
                        else{
                            service.runButtonAction3(cmdField.getText());
                        }

                    } catch (InterruptedException | IOException interruptedException) {
                        interruptedException.printStackTrace();
                    }
               // }


                refreshFileTree();

            });
        });

//        this.addComponent(panel, "main.button.fresh", new JButton("更新"), button -> {
//            button.setToolTipText("点击更新并保存参数配置");
//            button.setPreferredSize(new Dimension(60, 25));
//            button.addActionListener(e -> service.freshButtonAction());
//        });
//        this.addComponent(panel, "main.button.setting", new JButton("console"), button -> {
//            button.setToolTipText("点击更新并保存参数配置");
//            button.setPreferredSize(new Dimension(80, 25));
//            button.addActionListener(e -> service.freshButtonAction());
//        });
        // 参数按钮逻辑修改
        this.addComponent(panel, "main.button.arg", new JButton("参数"), button -> {
            //button.setPreferredSize(new Dimension(60, 25));
            button.setBounds(820,10,60,35);
            //button.addActionListener(e -> service.settingButtonAction());
            button.addActionListener(e -> service.argButtonAction());
        });

        this.addComponent(panel,"main.load",new LoadingLabel2(ColorUtil.PRIMARY, 4), c -> {
            c.setVisible(false);
            c.setBounds(910,0,60,60);

        });

        this.addComponent(panel,"main.menu",new JMenuBar(), jMenuBar -> {
            //loc

            jMenuBar.setBounds(10,10,80,35);
            // 自定义JToolBar UI的border/
            //jMenuBar


            //jMenuBar.setPreferredSize(new Dimension(120,25));
            //jMenuBar.setPreferredSize(new Dimension(80,35));
            //UIManager.put("PopupMenu.border", BorderFactory.createLineBorder(Color.black, 1));
            JMenu settingjMenu =new JMenu("设置");

            JMenu helpjMenu =new JMenu("帮助");

            JMenuItem jMenuItem21=new JMenuItem("关于规则提取");//菜单头下面的子菜单

            JMenuItem jMenuItem22=new JMenuItem("关于本系统");


            helpjMenu.add(jMenuItem21);//将子菜单加入到菜单头里面去

            helpjMenu.add(jMenuItem22);

            JMenuItem finditem=new JMenuItem("查找(F)");//菜单头下面的子菜单

            jMenuItem21.addActionListener(e -> {
                HelpDialog helpDialog=new HelpDialog(this,"1.2.1");
                helpDialog.openDialog();
            });

            jMenuItem22.addActionListener(e -> {
                HelpDialog helpDialog=new HelpDialog(this,"1.2.2");
                helpDialog.openDialog();
            });

            finditem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    Find();
                }
            });
            finditem.setMnemonic('F');
            finditem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_MASK));

            JMenuItem consoleItem=new JMenuItem("调出控制台");
            this.mapComponent("consoleitem",consoleItem);



            JMenuItem stepItem=new JMenuItem("当前步骤");

            this.mapComponent("stepitem",stepItem);



            JMenuItem jMenuItem3=new JMenuItem("提取工具切换");

            jMenuItem3.addActionListener(e -> {
                tool = service.argChangeButtonAction();


            });


            JMenuItem projitem=new JMenuItem("切换项目");
            projitem.addActionListener(e -> {
                try {
                    ProjectManager.loadProjects();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                } catch (ClassNotFoundException classNotFoundException) {
                    classNotFoundException.printStackTrace();
                }

                this.closeWindow();
                KGWelcomeWindow startWindow = new KGWelcomeWindow();
                //tartWindow.setDefaultCloseAction(CloseAction.DISPOSE);
                startWindow.openWindow();

            });

            JMenuItem cmditem=new JMenuItem("调出命令行");
            cmditem.addActionListener(e -> {

                if(cmdField.isVisible()){
                cmdField.setVisible(false);
                }
                else{
                    cmdField.setVisible(true);
                }
                }


            );

            settingjMenu.add(finditem);//将子菜单加入到菜单头里面去
            // todo 目前控制台不可用
//            settingjMenu.add(consoleItem);
//            settingjMenu.add(stepItem);
           settingjMenu.add(jMenuItem3);
            settingjMenu.add(projitem);
            settingjMenu.add(cmditem);

            jMenuBar.add(settingjMenu);
            jMenuBar.add(helpjMenu);

            //setJMenuBar(jMenuBar);

        });

    }

    /**
     * 创建左侧文件树板块，用于展示整个项目的文件列表
     * @return 文件树板块
     */
    private JScrollPane createLeftPanel(){
        //首先配置文件树
        root = new DefaultMutableTreeNode(new MainWindow.NodeData(path, name));
        buildTreeNode(root);
        JTree fileTree = new JTree(root);
        this.mapComponent("main.tree.files", fileTree);
        fileTree.addTreeSelectionListener(e -> {
            TreePath treePath = e.getPath();
            StringBuilder filePath = new StringBuilder(this.path);
            for (int i = 1; i < treePath.getPathCount(); i++)
                filePath.append("/").append(treePath.getPathComponent(i));
            this.service.switchEditFile(filePath.toString());
        });
        //接着是右键文件树的弹出菜单，对文件进行各种操作，包括创建新的源文件和删除源文件
        JPopupMenu treePopupMenu = new JPopupMenu();
        this.mapComponent("main.popup.tree", treePopupMenu);
        this.add(treePopupMenu);
        JMenuItem createItem = new JMenuItem("创建源文件");
        createItem.addActionListener(e -> service.createNewFile());
        JMenuItem deleteItem = new JMenuItem("删除");
        deleteItem.addActionListener(e -> service.deleteProjectFile());
        JMenuItem freshItem = new JMenuItem("刷新");
        freshItem.addActionListener(e ->  {
            refreshFileTree();
        }
        );
        treePopupMenu.add(createItem);
        treePopupMenu.add(deleteItem);
        treePopupMenu.add(freshItem);
        fileTree.addMouseListener(service.fileTreeRightClick());
        //文件树构造完成后，直接放进滚动面板返回就行了
        return new JScrollPane(fileTree);
    }

    /**
     * 创建右侧编辑板块，用于对项目代码进行编辑操作
     * @return 编辑板块
     */
    private JPanel createRightPanel(){

        JTextArea editArea = new JTextArea();
        this.mapComponent("main.textarea.edit", editArea);
        //快速配置编辑文本域的各项功能
        this.service.setupEditArea();
        //编辑界面的字体采用FiraCode，好看不止一点半点
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, new File("files/FiraCode-Medium.ttf"));
            editArea.setFont(font.deriveFont(13.0F));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //默认情况下无法进行编辑，必须选中文件之后才可以
        editArea.setEditable(false);
        JPanel panel = new JPanel();


        XCardLayout cardLayout=new XCardLayout();

//		给主要显示面板添加布局方式
        panel.setLayout(cardLayout);
        JScrollPane textpanel= new JScrollPane(editArea);
        JPanel argpanel1= new JPanel();
        argpanel1.setLayout(null);

        //******amie+*****
        //路径
        JLabel pathlabel=new JLabel("*数据集：");
        pathlabel.setBounds(30,30,80, 30);
        JTextField pathField = new JTextField(10);
        pathField.setBounds(100,30,200, 30);

        this.mapComponent("main.arg.path", pathField);
        JButton pathbutton =new JButton("...");
        pathbutton.setBounds(310,30,40,30);
         pathbutton.addActionListener(e -> {
                 service.pathButtonAction();

         });

        //分隔符
        JLabel delmlabel=new JLabel("分隔符：");
        delmlabel.setBounds(30,70,80, 30);
        JTextField delmField = new JTextField(10);
        delmField.setBounds(100,70,100, 30);

        this.mapComponent("main.arg.delm", delmField);

        //置信度
        JLabel conflabel=new JLabel("置信度阈值：");
        conflabel.setBounds(30,110,80, 30);
        JTextField confField = new JTextField(10);
        confField.setBounds(100,110,100, 30);

        this.mapComponent("main.arg.conf", confField);


        //头覆盖率
        JLabel headlabel=new JLabel("头覆盖率：");
        headlabel.setBounds(30,150,80, 30);
        JTextField headField = new JTextField(10);
        headField.setBounds(100,150,100, 30);

        this.mapComponent("main.arg.head", headField);

        //pca置信度
        JLabel pcalabel=new JLabel("pca置信度：");
        pcalabel.setBounds(30,190,80, 30);
        JTextField pcaField = new JTextField(10);
        pcaField.setBounds(100,190,100, 30);

        this.mapComponent("main.arg.pca", pcaField);

        //迭代次数
        JLabel recurslabel=new JLabel("递归层数：");
        recurslabel.setBounds(30,230,80, 30);
        JTextField recursField = new JTextField(10);
        recursField.setBounds(100,230,100, 30);

        this.mapComponent("main.arg.recurs", recursField);

        //线程数
        JLabel threadlabel=new JLabel("线程数：");
        threadlabel.setBounds(30,270,80, 30);
        JTextField threadField = new JTextField(10);
        threadField.setBounds(100,270,100, 30);

        this.mapComponent("main.arg.thread", threadField);



        //限定规则体
        JLabel bodylabel=new JLabel("限定规则体：");
        bodylabel.setBounds(400,70,80, 30);
        JTextField bodyField = new JTextField(20);
        bodyField.setBounds(470,70,200, 30);

        this.mapComponent("main.arg.body", bodyField);


        //限定规则头

        JLabel rheadlabel=new JLabel("限定规则头：");
        rheadlabel.setBounds(400,110,80, 30);
        JTextField rheadField = new JTextField(20);
        rheadField.setBounds(470,110,200, 30);

        this.mapComponent("main.arg.rhead", rheadField);

        //排除规则体

        JLabel bodylabel2=new JLabel("排除规则体：");
        bodylabel2.setBounds(400,150,80, 30);
        JTextField bodyField2 = new JTextField(20);
        bodyField2.setBounds(470,150,200, 30);

        this.mapComponent("main.arg.body2", bodyField2);

        //排除规则头

        JLabel rheadlabel2=new JLabel("排除规则头：");
        rheadlabel2.setBounds(400,190,80, 30);
        JTextField rheadField2 = new JTextField(20);
        rheadField2.setBounds(470,190,200, 30);

        this.mapComponent("main.arg.rhead2", rheadField2);

        this.addComponent(argpanel1, "main.disable", new JLabel("忽略必定成立规则:"),lable->{
            lable.setBounds(400,240,120,30);
        });

        this.addComponent(argpanel1, "main.button.disable", new SwitchButton(false), button -> {
            button.setBounds(520,245,50,30);

            button.setPreferredSize(new Dimension(50, 20));
            button.addActionListener(e -> {
                // service.settingButtonAction()
            });
        });

        this.addComponent(argpanel1, "main.button.graph", new JButton("图谱预览"), button -> {
//            button.setBackground(null);
//            button.setBorder(new RoundBorder(-1));
            button.setBounds(400,30,90,30);

            button.setPreferredSize(new Dimension(90, 30));
            button.addActionListener(e -> {
                if (pathField.getText().isEmpty())
                    JOptionPane.showMessageDialog(this, "请先选择数据集");
                else{
                    GraphWindow graphWindow =new GraphWindow(pathField.getText());
                    graphWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    mxSwingConstants.SHADOW_COLOR = Color.LIGHT_GRAY;
                    mxConstants.W3C_SHADOWCOLOR = "#D3D3D3";
                    graphWindow.setSize(500, 400);
                    graphWindow.setLocation(this.getLocation());
                    graphWindow.setVisible(true);

                }

                // service.settingButtonAction()
            });
        });


        this.addComponent(argpanel1, "main.button.help", new JButton("?"), button -> {
//            button.setBackground(null);
//            button.setBorder(new RoundBorder(-1));
            button.setBounds(400,300,30,30);

            button.setPreferredSize(new Dimension(30, 30));
            button.addActionListener(e -> {

                HelpDialog helpDialog=new HelpDialog(this,"arg");
                helpDialog.openDialog();
                // service.settingButtonAction()
            });
        });

        this.addComponent(argpanel1, "main.button.update", new JButton("更新"), button -> {
//            button.setBackground(null);
//            button.setBorder(new RoundBorder(-1));
            button.setBounds(500,300,70,30);
            button.setEnabled(true);

            button.setPreferredSize(new Dimension(30, 30));
            button.addActionListener(e -> {

                cmd="java -jar files/amie-milestone-intKB.jar "+pathField.getText();

                //conf head pca recur thread body rhead body2 rhead2
                SwitchButton disable=this.getComponent("main.button.disable");

                if (disable.isOpen())
                    cmd=cmd.concat(" -dpr");
                if(!delmField.getText().trim().equals(""))
                    cmd=cmd.concat(" -d "+delmField.getText());
                if(!confField.getText().trim().equals(""))
                    cmd=cmd.concat(" -minc "+confField.getText());
                if(!headField.getText().trim().equals(""))
                    cmd=cmd.concat(" -minhc "+headField.getText());
                if(!pcaField.getText().trim().equals(""))
                    cmd=cmd.concat(" -minpca "+pcaField.getText());
                if(!recursField.getText().trim().equals(""))
                    cmd=cmd.concat(" -rl  "+recursField.getText());
                if(!threadField.getText().trim().equals(""))
                    cmd=cmd.concat(" -nc  "+threadField.getText());
                if(!bodyField.getText().trim().equals(""))
                    cmd=cmd.concat(" -btr  "+bodyField.getText());
                if(!rheadField.getText().trim().equals(""))
                    cmd=cmd.concat(" -htr  "+rheadField.getText());
                if(!bodyField2.getText().trim().equals(""))
                    cmd=cmd.concat(" -bexr  "+bodyField2.getText());
                if(!rheadField2.getText().trim().equals(""))
                    cmd=cmd.concat(" -hexr  "+rheadField2.getText());


                //final
                File dir = new File(path + "/" +"output");

                if(dir.exists() || dir.mkdirs()) {}

                cmd=cmd.concat(" > "+path+"/output/output.txt");

                cmdField.setText(cmd);

                JPanel toolpanel=this.getComponent("main.panel.tools");

                RadianceIcon icon = com.component.svg.icon.fill.XCircleSvg.of(16, 16);
                icon.setColorFilter(color -> ColorUtil.SUCCESS);
                NotificationComponent c = openNotification(toolpanel, "提示", icon, new JLabel("更新成功              "),
                        true, true, SwingConstants.NORTH_EAST, 0, 4500);


            });
        });

        this.addComponent(argpanel1, "main.button.next", new JButton("下一步"), button -> {
//            button.setBackground(null);
//            button.setBorder(new RoundBorder(-1));
            button.setBounds(600,300,80,30);
            button.setEnabled(false);

            button.setPreferredSize(new Dimension(30, 30));
            button.addActionListener(e -> {
                // service.settingButtonAction()
                OutputWindow outputWindow =new OutputWindow(name,path,tool);
                outputWindow.openWindow();

            });
        });



        //放入面板
        argpanel1.add(pathlabel);
        argpanel1.add(pathField);
        argpanel1.add(pathbutton);
        argpanel1.add(delmlabel);
        argpanel1.add(delmField);
        argpanel1.add(conflabel);
        argpanel1.add(confField);
        argpanel1.add(headlabel);
        argpanel1.add(headField);
        argpanel1.add(pcalabel);
        argpanel1.add(pcaField);
        argpanel1.add(recurslabel);
        argpanel1.add(recursField);
        argpanel1.add(threadlabel);
        argpanel1.add(threadField);
        argpanel1.add(bodylabel);
        argpanel1.add(bodyField);
        argpanel1.add(rheadlabel);
        argpanel1.add(rheadField);
        argpanel1.add(bodyField2);
        argpanel1.add(bodylabel2);
        argpanel1.add(rheadlabel2);
        argpanel1.add(rheadField2);

       //首先添加最左侧的标签
        argpanel1.setBorder(new TitledBorder("amie参数设置"));


        panel.add(textpanel,"text");
        textpanel.setName("text");
        panel.add(argpanel1,"arg1");
        argpanel1.setName("arg1");



    //    *******************************************************anyburl************************************



        JPanel argpanel2= new JPanel();
        argpanel2.setLayout(null);

        JLabel pathlabel2=new JLabel("*数据集：");
        pathlabel2.setBounds(30,60,80, 30);
        JTextField pathField2 = new JTextField(10);
        pathField2.setBounds(100,60,200, 30);

        this.mapComponent("main.arg.path2", pathField2);
        JButton pathbutton2 =new JButton("...");
        pathbutton2.setBounds(310,60,40,30);
        pathbutton2.addActionListener(e -> {

            try {
                service.pathButtonAction2();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

        });

        //分隔符 支持度
        JLabel suplabel2=new JLabel("支持度：");
        suplabel2.setBounds(30,110,80, 30);
        JTextField supField2 = new JTextField(10);
        supField2.setBounds(100,110,100, 30);

        this.mapComponent("main.arg.sup2", supField2);

        //置信度
        JLabel conflabel2=new JLabel("置信度阈值：");
        conflabel2.setBounds(30,160,80, 30);
        JTextField confField2 = new JTextField(10);
        confField2.setBounds(100,160,100, 30);

        this.mapComponent("main.arg.conf2", confField2);


//        //头覆盖率
//        JLabel headlabel2=new JLabel("头覆盖率：");
//        headlabel2.setBounds(30,150,80, 30);
//        JTextField headField2 = new JTextField(10);
//        headField2.setBounds(100,150,100, 30);
//
//        this.mapComponent("main.arg.head2", headField2);
//
//        //pca置信度
//        JLabel pcalabel2=new JLabel("pca置信度：");
//        pcalabel2.setBounds(30,190,80, 30);
//        JTextField pcaField2 = new JTextField(10);
//        pcaField2.setBounds(100,190,100, 30);
//
//        this.mapComponent("main.arg.pca2", pcaField2);

        //迭代时间/s
        // todo 判断必填项是否填写
        JLabel batchlabel2=new JLabel("*迭代时间：");
        batchlabel2.setBounds(30,210,80, 30);
        JTextField batchField2 = new JTextField(10);
        batchField2.setBounds(100,210,100, 30);

        this.mapComponent("main.arg.batch2", batchField2);

        //线程数
        JLabel threadlabel2=new JLabel("线程数：");
        threadlabel2.setBounds(30,260,80, 30);
        JTextField threadField2 = new JTextField(10);
        threadField2.setBounds(100,260,100, 30);

        this.mapComponent("main.arg.thread2", threadField2);

//

//        JLabel bodylabel22=new JLabel("限定规则体：");
//        bodylabel22.setBounds(400,30,80, 30);
//        JTextField bodyField22 = new JTextField(20);
//        bodyField22.setBounds(470,30,200, 30);
//
//        this.mapComponent("main.arg.body2", bodyField22);
        this.addComponent(argpanel2, "main.button.graph2", new JButton("图谱预览"), button -> {
//            button.setBackground(null);
//            button.setBorder(new RoundBorder(-1));
            button.setBounds(400,60,90,30);

            button.setPreferredSize(new Dimension(90, 30));
            button.addActionListener(e -> {
                if (pathField.getText().isEmpty())
                    JOptionPane.showMessageDialog(this, "请先选择数据集");
                else{
                    GraphWindow graphWindow =new GraphWindow(pathField.getText());
                    graphWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    mxSwingConstants.SHADOW_COLOR = Color.LIGHT_GRAY;
                        mxConstants.W3C_SHADOWCOLOR = "#D3D3D3";
                    graphWindow.setSize(500, 400);
                    graphWindow.setLocation(this.getLocation());
                    graphWindow.setVisible(true);

                }

                // service.settingButtonAction()
            });
        });

        //限定规则头  逗号间隔

        JLabel rheadlabel22=new JLabel("限定规则头：");
        rheadlabel22.setBounds(400,110,80, 30);
        JTextField rheadField22 = new JTextField(20);
        rheadField22.setBounds(470,110,200, 30);

        this.mapComponent("main.arg.rhead2", rheadField22);

//        //排除规则体
//
//        JLabel bodylabel222=new JLabel("排除规则体：");
//        bodylabel222.setBounds(400,110,80, 30);
//        JTextField bodyField222 = new JTextField(20);
//        bodyField222.setBounds(470,110,200, 30);
//
//        this.mapComponent("main.arg.body22", bodyField222);

        //排除规则头

        JLabel rheadlabel222=new JLabel("排除规则头：");
        rheadlabel222.setBounds(400,160,80, 30);
        JTextField rheadField222 = new JTextField(20);
        rheadField222.setBounds(470,160,200, 30);

        this.mapComponent("main.arg.rhead22", rheadField222);

        this.addComponent(argpanel2, "main.disable2", new JLabel("提取含实例的规则:"),lable->{
            lable.setBounds(400,215,120,30);
        });

        this.addComponent(argpanel2, "main.button.disable2", new SwitchButton(false), button -> {
            button.setBounds(520,220,50,30);

            button.setPreferredSize(new Dimension(50, 20));
            button.addActionListener(e -> {
                // service.settingButtonAction()
            });
        });



        this.addComponent(argpanel2, "main.button.help2", new JButton("?"), button -> {
//            button.setBackground(null);
//            button.setBorder(new RoundBorder(-1));
            button.setBounds(400,270,30,30);

            button.setPreferredSize(new Dimension(30, 30));
            button.addActionListener(e -> {
                // service.settingButtonAction()
                HelpDialog helpDialog=new HelpDialog(this,"arg2");
                helpDialog.openDialog();
            });
        });


        this.addComponent(argpanel2, "main.button.update2", new JButton("更新"), button -> {
//            button.setBackground(null);
//            button.setBorder(new RoundBorder(-1));
            button.setBounds(500,270,70,30);
            button.setEnabled(true);

            button.setPreferredSize(new Dimension(30, 30));
            button.addActionListener(e -> {
                //参数更新 anyburl
                // java -Xmx12G -cp AnyBURL-22.jar de.unima.ki.anyburl.LearnReinforced config-learn.properties

                cmd="java -Xmx12G -cp files/AnyBURL-22.jar de.unima.ki.anyburl.LearnReinforced "+path+"/conf/config-learn.properties";


                File dir = new File(path + "/" +"output");

                File dir2 = new File(path + "/" +"conf");

                File conf = new File(path + "/" +"conf/config-learn.properties");

                if(dir.exists() || dir.mkdirs()) {}
                if(dir2.exists() || dir2.mkdirs()) {}

                SwitchButton disable=this.getComponent("main.button.disable2");

                try {
                    if(conf.exists() || conf.createNewFile()) {}

                    String confstr="PATH_OUTPUT   = "+path+"/output/output\n";

                    FileWriter fileWritter = new FileWriter(conf, false);
                    //conf head pca recur thread body rhead body2 rhead2

                    if(!pathField2.getText().trim().equals(""))
                        confstr=confstr.concat("PATH_TRAINING = "+pathField2.getText()+"\n");
                    if(!supField2.getText().trim().equals(""))
                        confstr=confstr.concat("THRESHOLD_CORRECT_PREDICTIONS = "+supField2.getText()+"\n");
                    if(!confField2.getText().trim().equals(""))
                        confstr=confstr.concat("THRESHOLD_CONFIDENCE = "+confField2.getText()+"\n");
//                    if(!headField2.getText().trim().equals(""))
//                        cmd=cmd.concat(" -minhc "+headField2.getText());
//                    if(!pcaField2.getText().trim().equals(""))
//                        cmd=cmd.concat(" -minpca "+pcaField2.getText());

                    if(!batchField2.getText().trim().equals("")){
                        confstr=confstr.concat("BATCH_TIME = "+batchField2.getText()+"\n");
                        confstr=confstr.concat("SNAPSHOTS_AT = "+batchField2.getText()+"\n");
                    }
                    if(!threadField2.getText().trim().equals(""))
                        confstr=confstr.concat("WORKER_THREADS = "+threadField2.getText()+"\n");
    //                if(!bodyField22.getText().trim().equals(""))
    //                    cmd=cmd.concat(" -btr  "+bodyField22.getText());
                    //限定规则头
                    if(!rheadField22.getText().trim().equals(""))
                        confstr=confstr.concat("SINGLE_RELATIONS = "+rheadField22.getText()+"\n");
    //                if(!bodyField222.getText().trim().equals(""))
    //                    cmd=cmd.concat(" -bexr  "+bodyField222.getText());
                    if(!rheadField222.getText().trim().equals(""))
                        confstr=confstr.concat("FORBIDDEN_RELATIONS = "+rheadField222.getText()+"\n");

                    if (disable.isOpen())
                        confstr=confstr.concat("CONSTANTS_OFF = true"+"\n");
                    else
                        confstr=confstr.concat("CONSTANTS_OFF = false"+"\n");


                    fileWritter.write(confstr);
                    fileWritter.close();

                } catch (IOException ioException) {
                    CompileErrorDialog compileErrorDialog=new CompileErrorDialog(this,ioException.getMessage());
                }


                cmdField.setText(cmd);

                JPanel toolpanel=this.getComponent("main.panel.tools");

                RadianceIcon icon = com.component.svg.icon.fill.XCircleSvg.of(16, 16);
                icon.setColorFilter(color -> ColorUtil.SUCCESS);
                NotificationComponent c = openNotification(toolpanel, "提示", icon, new JLabel("更新成功              "),
                        true, true, SwingConstants.NORTH_EAST, 0, 4500);



            });
        });

        this.addComponent(argpanel2, "main.button.next2", new JButton("下一步"), button -> {
//            button.setBackground(null);
//            button.setBorder(new RoundBorder(-1));
            button.setBounds(600,270,80,30);
            button.setEnabled(false);

            button.setPreferredSize(new Dimension(30, 30));
            button.addActionListener(e -> {
                // service.settingButtonAction()
                OutputWindow outputWindow =new OutputWindow(name,path,tool);
                outputWindow.openWindow();

            });
        });


        //放入面板
        argpanel2.add(pathlabel2);
        argpanel2.add(pathField2);
        argpanel2.add(pathbutton2);
        argpanel2.add(suplabel2);
        argpanel2.add(supField2);
        argpanel2.add(conflabel2);
        argpanel2.add(confField2);
//        argpanel2.add(headlabel2);
//        argpanel2.add(headField2);
//        argpanel2.add(pcalabel2);
//        argpanel2.add(pcaField2);
        argpanel2.add(batchlabel2);
        argpanel2.add(batchField2);
        argpanel2.add(threadlabel2);
        argpanel2.add(threadField2);
//        argpanel2.add(bodylabel22);
//        argpanel2.add(bodyField22);
        argpanel2.add(rheadlabel22);
        argpanel2.add(rheadField22);
//        argpanel2.add(bodyField222);
//        argpanel2.add(bodylabel222);
        argpanel2.add(rheadlabel222);
        argpanel2.add(rheadField222);

        //首先添加最左侧的标签
        argpanel1.setBorder(new TitledBorder("AMIE-参数设置"));

        argpanel1.setLayout(null);

        panel.add(argpanel2,"arg2");
        argpanel2.setBorder(new TitledBorder("ANYBurl-参数设置"));
        argpanel2.setName("arg2");


        return panel;
    }



    /**
     * 创建底部控制台板块，用于展示控制台输出信息
     * @return 底部板块
     *
     */
    private JPanel createConsole(){
        JPanel jp =new JPanel();
        CardLayout cardLayout=new CardLayout();

//		给主要显示面板添加布局方式
        jp.setLayout(cardLayout);

        JScrollPane jScrollPane =new JScrollPane();
        JTextArea consoleArea = new JTextArea("控制台中尚未启动任何进程");
        this.mapComponent("main.textarea.console", consoleArea);
        //下方

        consoleArea.setEditable(false);
        consoleArea.addKeyListener(service.inputRedirect());
        jScrollPane.add(consoleArea);

        //步骤

        JPanel stepPanel=new JPanel();
        //stepPanel.setLayout(new FlowLayout());
        stepPanel.setLayout(new CenterLayout());

        StepsComponent c = new StepsComponent(Arrays.asList(" ", " ", " ", " "),
                XCircleSvg.class,
                Arrays.asList("项目配置", "参数调试", "输出控制", "规则管理"),
                1, ColorUtil.PRIMARY, 120, true);


        stepPanel.add(c);


        jp.add(jScrollPane,"console");
        this.mapComponent("main.textarea.step", stepPanel);
        //this.mapComponent("main.textarea.step", stepPanel);
        jp.add(stepPanel,"step");

        JMenuItem consoleItem=this.getComponent("consoleitem");
        JMenuItem stepItem=this.getComponent("stepitem");

        //切换控制台
        consoleItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                CardLayout cardLayout0= (CardLayout)jp.getLayout();
                cardLayout0.show(jp,"console");
            }
        });

        //
        stepItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                CardLayout cardLayout0= (CardLayout)jp.getLayout();
                cardLayout0.show(jp,"step");
            }
        });


        return jp;

    }

    /**
     * 快速刷新文件树，构建JTree结点并重新绘制
     */
    public void refreshFileTree(){
        buildTreeNode(root);
        SwingUtilities.updateComponentTreeUI(this);
    }

    /**
     * 构建JTree结点，采用BFS算法完成
     */
    private void buildTreeNode(DefaultMutableTreeNode root){
        root.removeAllChildren();   //先清理掉
        //BFS算法列出所有结点并构建成树
        Queue<DefaultMutableTreeNode> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            DefaultMutableTreeNode node = queue.poll();
            MainWindow.NodeData data = (MainWindow.NodeData) node.getUserObject();
            for (File file : Objects.requireNonNull(data.getFile().listFiles())) {
                if (file.getName().charAt(0) == '.') continue;   //隐藏文件不需要显示出来
                DefaultMutableTreeNode child = new DefaultMutableTreeNode(new MainWindow.NodeData(file.getAbsolutePath(), file.getName()));
                node.add(child);
                if(file.isDirectory()) queue.offer(child);
            }
        }
    }

    private void Find() {// 查找
        final JDialog dislog = new JDialog(this, "查找", false);// 创建一个窗体
        Container con = dislog.getContentPane();// 将窗体转化为容器
        JPanel jPanel=new JPanel();
        jPanel.setLayout(null);

        //con.setLayout(null);// 取消布局
        final JLabel j1 = new JLabel("查找内容(N)：");

        //
        JTextField textfield = new JTextField(18);// 文本框

        //文件内容
        JTextArea textarea =this.getComponent("main.textarea.edit");

        final JButton b1 = new JButton("查找下一个(F)");
        final JButton b2 = new JButton("取消");
        b1.setBounds(330, 10, 115, 25);
        //b1.setContentAreaFilled(false);
        b2.setBounds(330, 40, 115, 25);
        //b2.setContentAreaFilled(false);

        /*
         * 加入到按钮组中的按钮只能选中其一，其他的咋会关闭
         */
        JRadioButton up = new JRadioButton("向上(U)");
        JRadioButton down = new JRadioButton("向下(D)");
//        final ButtonGroup group = new ButtonGroup();// 按钮组
//        group.add(up);
//        group.add(down);
        down.setSelected(true);// 默认选中向下
        JCheckBox check1 = new JCheckBox("区分大小写");// 复选框
        JCheckBox check2 = new JCheckBox("循环");// 复选框

        check1.setBounds(0, 100, 140, 30);
        check1.setFont(new Font("黑体", Font.PLAIN, 14));// 设置字体
        check2.setBounds(0, 130, 140, 30);
        check2.setFont(new Font("黑体", Font.PLAIN, 14));
        j1.setFont(new Font("黑体", Font.PLAIN, 14));

        /*
         * 设置快捷键
         */
        up.setMnemonic('U');
        down.setMnemonic('D');
        b1.setMnemonic('F');
        check1.setMnemonic('C');
        check2.setMnemonic('R');

        JPanel p1 = new JPanel();// j1,textfield
        JPanel p4 = new JPanel();// 放up down
        // 设置面板p1
        p1.setLayout(new FlowLayout(FlowLayout.LEFT));// 流体布局，左对齐
        p1.setLocation(0, 10);
        p1.setSize(330, 60);
        p1.add(j1);
        p1.add(textfield);

        /*
         * 设置d4组件的边框; BorderFactory.createTitledBorder(String title)创建一个新标题边框，
         * 使用默认边框（浮雕化）、默认文本位置（位于顶线上）、默认调整 (leading) 以及由当前外观确定的默认字体和文本颜色，并指定了标题文本。
         */
        p4.setBorder(BorderFactory.createTitledBorder("方向"));
        p4.setBounds(150, 80, 180, 70);
        p4.setLayout(null);
        up.setFont(new Font("黑体", Font.PLAIN, 13));
        down.setFont(new Font("黑体", Font.PLAIN, 13));
        up.setBounds(20,20,100,20);
        down.setBounds(20,40,100,20);
        p4.add(up);
        p4.add(down);
//
//        con.add(p1);
//        con.add(b1);
//        con.add(b2);
//        con.add(check1);
//        con.add(check2);
//        con.add(p4);

        jPanel.add(p1);
        jPanel.add(b1);
        jPanel.add(b2);
        jPanel.add(check1);
        jPanel.add(check2);
        jPanel.add(p4);
        jPanel.setBounds(200, 200, 460, 220);
        con.add(jPanel);
        dislog.setBounds(200, 200, 460, 220);
        dislog.setResizable(false);// 设置窗体大小不可改变
        dislog.setVisible(true);// 显示窗体
        b1.addActionListener(new ActionListener() {// 查找下一个按钮

            public void actionPerformed(ActionEvent e) {


                String areastr = textarea.getText();// 获取文本区文本
                String fieldstr = textfield.getText();// 获取文本框文本
                String toupparea = areastr.toUpperCase();// 转为大写，用做区分大小写判断方便查找
                String touppfield = fieldstr.toUpperCase();
                String A;// 用做查找的文本域内容
                String B;// 用作查找的文本框内容
                if (check1.isSelected()) {// 区分大小写
                    A = areastr;
                    B = fieldstr;
                } else {// 全部换为大写
                    A = toupparea;
                    B = touppfield;
                }
                int n = textarea.getCaretPosition();// 获取光标的位置
                int m = 0;
                if (up.isSelected()) {// 向上查找
                    if (textarea.getSelectedText() == null) {
                        m = A.lastIndexOf(B, n - 1);
                    } else {
                        m = A.lastIndexOf(B, n - textfield.getText().length() - 1);
                    }
                    if (m != -1) {
                        textarea.setCaretPosition(m);
                        textarea.select(m, m + textfield.getText().length());
                    } else {
                        if (check2.isSelected()) {// 如果循环
                            m = A.lastIndexOf(B);// 从后面开始找
                            if (m != -1) {
                                textarea.setCaretPosition(m);
                                textarea.select(m, m + textfield.getText().length());
                            } else {
                                JOptionPane.showMessageDialog(null, "找不到 “" + textfield.getText() + "“", "查找",
                                        JOptionPane.INFORMATION_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "找不到 “" + textfield.getText() + "“", "查找",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                    }

                } else {// 向下查找
                    m = A.indexOf(B, n);
                    if (m != -1) {
                        textarea.setCaretPosition(m + textfield.getText().length());
                        textarea.select(m, m + textfield.getText().length());
                    } else {
                        if (check2.isSelected()) {// 如果循环
                            m = A.indexOf(B);// 从头开始找
                            if (m != -1) {
                                textarea.setCaretPosition(m + textfield.getText().length());
                                textarea.select(m, m + textfield.getText().length());
                            } else {
                                JOptionPane.showMessageDialog(null, "找不到 “" + textfield.getText() + "“", "查找",
                                        JOptionPane.INFORMATION_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "找不到 “" + textfield.getText() + "“", "查找",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                }
            }
        });

        b2.addActionListener(new ActionListener() {// 取消

            public void actionPerformed(ActionEvent e) {
                dislog.dispose();// 销毁窗体
            }
        });

    }

    @Override
    protected boolean onClose() {
        //关闭之前如果还有运行的项目没有结束，一定要结束掉
        ProcessExecuteEngine.stopProcess();
        //然后回到初始界面
        KGWelcomeWindow window = new KGWelcomeWindow();
        window.openWindow();

        return true;
    }

    /**
     * NodeData是JTree的专用结点信息存储介质，包括文件相关信息。
     */

    public static void main(String[] args) throws UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(new FlatIntelliJLaf());
        KGWindow window = new KGWindow("KG rule mining","d:/test");
        window.openWindow();
    }


}
