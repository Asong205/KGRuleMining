package com.mining.window.service;

import com.component.basic.color.ColorUtil;
import com.component.notice.loading.LoadingLabel2;
import com.component.notice.notification.NotificationComponent;
import com.component.radiance.common.api.icon.RadianceIcon;
import com.component.svg.icon.fill.XCircleSvg;
import com.mining.entity.ProcessResult;
import com.mining.entity.config.ProjectConfigure;
import com.mining.manage.ProcessExecuteEngine;
import com.mining.window.KGWindow;
import com.mining.window.MainWindow;
import com.mining.window.dialog.CompileErrorDialog;
import com.mining.window.dialog.ProjectConfigDialog;
import com.mining.window.layout.XCardLayout;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.component.notice.notification.NotificationFactory.openNotification;

public class MainService extends AbstractService {
    //当前项目的路径和项目名称
    private String path;
    //当前项目的配置文件，包括主类、java可执行文件位置等。
    private ProjectConfigure configure;
    //用于记录当前正在编辑的文件
    private File currentFile;
    //重做管理器，用于编辑框支持撤销和重做操作的
    private UndoManager undoManager;
    //用于记录当前项目是否处于运行状态
    private boolean isProjectRunning = false;

    private Process p;

    private String tool ="amie" ;

    private String datapath;

    private enum OS { Windows, Linux, MacOS }


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
     */
    public ProjectConfigure getConfigure() {
        return configure;
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
     * 运行按钮的行为，包括以下两种行为：
     * - 如果项目处于运行状态，那么点击就会停止项目。
     * - 如果项目没有处于运行状态，那么就会启动项目。
     * @param text
     */

    //不同系统执行命令不同

    public void runButtonAction2(String text) throws InterruptedException, IOException {
        KGWindow window = (KGWindow) this.getWindow();
        JButton button = this.getComponent("main.button.run");
        JTextArea consoleArea = this.getComponent("main.textarea.console");
        LoadingLabel2 loadingLabel2 =this.getComponent("main.load");
        consoleArea.setLineWrap(true);        //激活自动换行功能
        button.setEnabled(false);



        //判断当前项目是否已经开始运行了，分别进行操作
        if(!this.isProjectRunning) {
            //如果项目没有运行，那么需要先编译项目源代码，如果编译成功，那么就可以开始运行项目了

            consoleArea.setText("正在运行...");

                //ProcessResult result = ProcessExecuteEngine.runCommand2(text);
//            if(result.getExitCode() != 0) {
//                CompileErrorDialog dialog = new CompileErrorDialog(this.getWindow(), result.getOutput());
//                dialog.openDialog();
//                button.setEnabled(true);
//                return;
//            }

                //新开一个线程实时对项目的运行进行监控，并实时将项目的输出内容更新到控制台
//            new Thread(() -> {
//                this.isProjectRunning = true;
//                consoleArea.setText("程序已启动：\n");
//                button.setText("停止");
//                button.setEnabled(true);
//                //准备工作完成之后，就可以正式启动进程了，这里最后会返回执行结果
////                ProcessResult res = ProcessExecuteEngine.startProcess(
////                        path, configure.getJavaCommand(), configure.getMainClass(), consoleArea::append);
//
//
//
//
//                ProcessResult res = new ProcessResult(0,"");
//                if(res.getExitCode() != 0)
//                    consoleArea.append(res.getOutput());
//                consoleArea.append("\n进程已结束，退出代码 "+res.getExitCode());
//                button.setText("运行");
//                this.isProjectRunning = false;
//            }).start();

            OS os = osType();

            Thread thread0= new Thread(() -> {

                Process process = null;
                this.isProjectRunning = true;

                consoleArea.setText("正在编译项目源代码...编译完成，程序已启动：\n");
                button.setText("停止");
                button.setEnabled(true);

                StringBuilder sbout = new StringBuilder();

                // run.exec("cmd /k shutdown -s -t 3600");


                try {

                    if(os == OS.Linux || os == OS.MacOS) {
                        process = Runtime.getRuntime().exec(new String[]{"/bin/bash", "-c", text});
                        p=process;
                    }else {
                    process = Runtime.getRuntime().exec("cmd /c" + text);
                        p=process;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                BufferedReader bfout = new BufferedReader(new InputStreamReader(
                        process.getInputStream()));
                final InputStreamReader err = new InputStreamReader(
                        process.getErrorStream());

                final InputStreamReader in = new InputStreamReader(
                        process.getInputStream());
                String outline = "";
                //int flag = process.waitFor();

                Thread thread2 = new Thread(() -> {
                    {
                        BufferedReader bferr = new BufferedReader(err);
                        String errline = "";
                        StringBuilder sberr = new StringBuilder();
                        try {
                            while ((errline = bferr.readLine()) != null) {
                                sberr.append(errline);
                                sberr.append("\n");
                                //System.out.println(errline);
                                consoleArea.append(errline + "\r\n");

                            }
                        } catch (Exception e) {

                            e.printStackTrace();
                        } finally {
                            try {
                                err.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });
                Thread thread3 = new Thread(() -> {
                    {
                        BufferedReader bferr = new BufferedReader(in);
                        String errline = "";
                        StringBuilder sberr = new StringBuilder();
                        try {
                            while ((errline = bferr.readLine()) != null) {
                                sberr.append(errline);
                                sberr.append("\n");
                                //System.out.println(errline);
                                consoleArea.append(errline + "\r\n");
                            }
                            //                consoleArea.append("***complete***");
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                in.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });
                thread2.start();
                thread3.start();
                int exitCode = 0;
                try {
                    exitCode = process.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (exitCode == 0) {
                    JPanel panel=this.getComponent("main.panel.tools");

                    RadianceIcon icon = XCircleSvg.of(16, 16);
                    icon.setColorFilter(color -> ColorUtil.SUCCESS);
                    NotificationComponent c = openNotification(panel, "提示", icon, new JLabel("运行成功              "),
                            true, true, SwingConstants.NORTH_EAST, 0, 4500);
//                    SuccessDialog dialog = new SuccessDialog(this.getWindow(), consoleArea.getText());
//                    dialog.openDialog();
                    //button.setEnabled(true);

                    this.getComponent("main.load").setVisible(false);
                    this.getComponent("main.button.next").setEnabled(true);
                    button.setText("运行");
                    this.isProjectRunning = false;

                }
                if (exitCode != 0) {
                    CompileErrorDialog dialog = new CompileErrorDialog(this.getWindow(), consoleArea.getText());
                    dialog.openDialog();
                    //button.setEnabled(true);
                    this.getComponent("main.button.next").setEnabled(true);
                    this.getComponent("main.load").setVisible(false);
                    button.setText("运行");
                    this.isProjectRunning = false;
                }


            });

                //项目编译完成之后，可能会新增文件，所以需要刷新一下文件树

            {
                Thread thread1 = new Thread(() -> {


                    loadingLabel2.setVisible(true);
                    button.setEnabled(false);

                });

                thread1.start();

                try {
                    thread1.join();
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
                thread0.start();

            }

                window.refreshFileTree();
            loadingLabel2.setVisible(true);

                button.setEnabled(true);
            } else{
                //如果项目正在运行，那么点击按钮就相当于是结束项目运行
                p.destroyForcibly();
                this.isProjectRunning = false;
                button.setEnabled(true);

            }


    }


    public void runButtonAction3(String text) throws InterruptedException {
        KGWindow window = (KGWindow) this.getWindow();
        JButton button = this.getComponent("main.button.run");
        JTextArea consoleArea = this.getComponent("main.textarea.console");
        LoadingLabel2 loadingLabel2 =this.getComponent("main.load");
        consoleArea.setLineWrap(true);        //激活自动换行功能

        //判断当前项目是否已经开始运行了，分别进行操作
        if(!this.isProjectRunning) {
            //如果项目没有运行，那么需要先编译项目源代码，如果编译成功，那么就可以开始运行项目了

            consoleArea.setText("正在运行...");


            Thread thread0= new Thread(() -> {
//todo
                this.isProjectRunning = true;

                consoleArea.setText("正在编译项目源代码...编译完成，程序已启动：\n");
                button.setText("停止");
                button.setEnabled(true);

                StringBuilder sbout = new StringBuilder();

                // run.exec("cmd /k shutdown -s -t 3600");

                Process process = null;
                try {
                    process = Runtime.getRuntime().exec("cmd /c" + text);
                    p=process;
                } catch (IOException e) {
                    e.printStackTrace();
                }

                BufferedReader bfout = new BufferedReader(new InputStreamReader(
                        process.getInputStream()));
                final InputStreamReader err = new InputStreamReader(
                        process.getErrorStream());

                final InputStreamReader in = new InputStreamReader(
                        process.getInputStream());
                String outline = "";
                //int flag = process.waitFor();

                Thread thread2 = new Thread(() -> {
                    {
                        BufferedReader bferr = new BufferedReader(err);
                        String errline = "";
                        StringBuilder sberr = new StringBuilder();
                        try {
                            while ((errline = bferr.readLine()) != null) {
                                sberr.append(errline);
                                sberr.append("\n");
                                //System.out.println(errline);
                                consoleArea.append(errline + "\r\n");

                            }
                        } catch (Exception e) {

                            e.printStackTrace();
                        } finally {
                            try {
                                err.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });
                Thread thread3 = new Thread(() -> {
                    {
                        BufferedReader bferr = new BufferedReader(in);
                        String errline = "";
                        StringBuilder sberr = new StringBuilder();
                        try {
                            while ((errline = bferr.readLine()) != null) {
                                sberr.append(errline);
                                sberr.append("\n");
                                //System.out.println(errline);
                                consoleArea.append(errline + "\r\n");
                            }
                            //                consoleArea.append("***complete***");
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                in.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });
                thread2.start();
                thread3.start();
                int exitCode = 0;
                try {
                    exitCode = process.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (exitCode == 0) {
                    JPanel panel=this.getComponent("main.panel.tools");

                    RadianceIcon icon = XCircleSvg.of(16, 16);
                    icon.setColorFilter(color -> ColorUtil.SUCCESS);
                    NotificationComponent c = openNotification(panel, "提示", icon, new JLabel("运行成功              "),
                            true, true, SwingConstants.NORTH_EAST, 0, 4500);
//                    SuccessDialog dialog = new SuccessDialog(this.getWindow(), consoleArea.getText());
//                    dialog.openDialog();
                    //System.out.printf("yes");
                    button.setEnabled(true);
                    this.getComponent("main.load").setVisible(false);
                    this.getComponent("main.button.next2").setEnabled(true);

                }
                if (exitCode != 0) {
                    CompileErrorDialog dialog = new CompileErrorDialog(this.getWindow(), consoleArea.getText());
                    dialog.openDialog();
                    button.setEnabled(true);
                    this.getComponent("main.button.next2").setEnabled(true);
                    this.getComponent("main.load").setVisible(false);
                }


            });

            //项目编译完成之后，可能会新增文件，所以需要刷新一下文件树

            {
                Thread thread1 = new Thread(() -> {


                    loadingLabel2.setVisible(true);
                    button.setEnabled(false);

                });

                thread1.start();

                try {
                    thread1.join();
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
                thread0.start();

            }

            window.refreshFileTree();
            loadingLabel2.setVisible(true);

            button.setEnabled(true);
        } else{
            //如果项目正在运行，那么点击按钮就相当于是结束项目运行
            p.destroyForcibly();
            this.isProjectRunning = false;
            button.setEnabled(true);
        }


    }


    public void runButtonAction() throws InterruptedException {
        MainWindow window = (MainWindow) this.getWindow();
        JButton button = this.getComponent("main.button.run");
        JTextArea consoleArea = this.getComponent("main.textarea.console");
        //判断当前项目是否已经开始运行了，分别进行操作
        if(!this.isProjectRunning) {
            //如果项目没有运行，那么需要先编译项目源代码，如果编译成功，那么就可以开始运行项目了
            button.setEnabled(false);
            consoleArea.setText("正在编译项目源代码...");
            ProcessResult result = ProcessExecuteEngine.buildProject(path);
            if(result.getExitCode() != 0) {
                CompileErrorDialog dialog = new CompileErrorDialog(this.getWindow(), result.getOutput());
                dialog.openDialog();
                button.setEnabled(true);
                return;
            }
            //项目编译完成之后，可能会新增文件，所以需要刷新一下文件树
            window.refreshFileTree();
            //新开一个线程实时对项目的运行进行监控，并实时将项目的输出内容更新到控制台
            new Thread(() -> {
                this.isProjectRunning = true;
                consoleArea.setText("正在编译项目源代码...编译完成，程序已启动：\n");
                button.setText("停止");
                button.setEnabled(true);
                //准备工作完成之后，就可以正式启动进程了，这里最后会返回执行结果
                ProcessResult res = ProcessExecuteEngine.startProcess(
                                path, configure.getJavaCommand(), configure.getMainClass(), consoleArea::append);
                if(res.getExitCode() != 0)
                    consoleArea.append(res.getOutput());
                consoleArea.append("\n进程已结束，退出代码 "+res.getExitCode());
                button.setText("运行");
                this.isProjectRunning = false;
            }).start();
        } else {
            //如果项目正在运行，那么点击按钮就相当于是结束项目运行
            ProcessExecuteEngine.stopProcess();
            this.isProjectRunning = false;
        }
    }

    /**
     *
     */
    public void freshButtonAction(){

        String cmd ="java -jar D:\\下载\\amie-milestone-intKB.jar ";
        //参数 路径
        JTextField pathf =this.getComponent("main.arg.path");
        cmd=cmd+pathf.getText();
        //命令行
        JTextField textf =this.getComponent("main.text");

        //final
        textf.setText(cmd);


    }

    public void consoleButtonAction(){


    }

    public void pathButtonAction() {

        // 创建一个JFrame组件为parent组件
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 创建一个默认打开用户文件夹的选择器
        JFileChooser chooser = new JFileChooser();
        int flag = chooser.showOpenDialog(frame);

        JTextField jtf = this.getComponent("main.arg.path");
        if (chooser.getSelectedFile() != null)
        {
            chooser.getSelectedFile().getPath();

        jtf.setText(chooser.getSelectedFile().getPath()+"");
        }

        //System.out.printf(chooser.getSelectedFile().getPath());
    }

    public void pathButtonAction2() throws IOException {

        // 创建一个JFrame组件为parent组件
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 创建一个默认打开用户文件夹的选择器
        JFileChooser chooser = new JFileChooser();
        int flag = chooser.showOpenDialog(frame);

        JTextField jtf = this.getComponent("main.arg.path2");
        if (chooser.getSelectedFile() != null)
        {
            chooser.getSelectedFile().getPath();

            jtf.setText(chooser.getSelectedFile().getCanonicalPath().replace('\\','/')+"");
        }
        JButton jButton=this.getComponent("main.button.graph");
        jButton.setEnabled(true);

        //System.out.printf(chooser.getSelectedFile().getPath());
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
        XCardLayout cardLayout= (XCardLayout)panelright.getLayout();

        if (tool.equals("amie")){
        cardLayout.show(panelright,"arg1");}
        if (tool.equals("anyburl")){
            cardLayout.show(panelright,"arg2");}


    }

    /**
     *  切换按钮的行为，点击后参数区改变  运行逻辑也要改变
     */
    public String argChangeButtonAction(){
        // KGWindow window = (KGWindow) this.getWindow();
        JTree tree=this.getComponent("main.tree.files");
        tree.clearSelection();
        JSplitPane panel = this.getComponent("main.panel.content");

        JSplitPane paneltop= (JSplitPane) panel.getTopComponent();

        JPanel panelright= (JPanel) paneltop.getRightComponent();


        XCardLayout cardLayout= (XCardLayout)panelright.getLayout();

                if(cardLayout.getCurrentCard().getName().equals("arg1")){
                    //System.out.printf("arg1");
                    cardLayout.show(panelright, "arg2");
                    tool="anyburl";
                } else {
                        cardLayout.show(panelright, "arg1");
                    tool="amie";
                    }
                return tool;



    }

    /**
     * 构建按钮的行为，很明显，直接构建就完事了
     */
    public void buildButtonAction(){
        MainWindow window = (MainWindow) this.getWindow();
        ProcessResult result = ProcessExecuteEngine.buildProject(path);
        if(result.getExitCode() == 0) {
            JOptionPane.showMessageDialog(window, "编译成功！");
        } else {
            CompileErrorDialog dialog = new CompileErrorDialog(window, result.getOutput());
            dialog.openDialog();
        }
        window.refreshFileTree();
    }

    /**
     * 设置按钮的行为，更简单了，直接打开设置面板就完事
     */
    public void settingButtonAction(){
        MainWindow window = (MainWindow) this.getWindow();
        ProjectConfigDialog dialog = new ProjectConfigDialog(window, this, configure);
        dialog.openDialog();
    }

    /**
     * 创建一个新的源代码新的文件并生成默认代码
     */
    public void createNewFile(){
        String newFileName = JOptionPane.showInputDialog(this.getWindow(),
                "请输入你要创建的文件名", "创建新文件", JOptionPane.PLAIN_MESSAGE);
        this.createFile(newFileName);
    }

    public void deleteProjectFile(){
        int f=JOptionPane.showConfirmDialog(this.getWindow(),
                "确定要删除吗", "删除文件", JOptionPane.WARNING_MESSAGE);
        if(f==0)
        this.deleteFile(currentFile.getName());
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

    /**
     * 配置编辑框的各项功能
     */
    public void setupEditArea(){
        JTextArea editArea = this.getComponent("main.textarea.edit");
        //当文本内容发生变化时，自动写入到文件中
        editArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                MainService.this.saveFile();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                MainService.this.saveFile();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                MainService.this.saveFile();
            }
        });
        //按下Tab键时，应该输入四个空格，而不是一个Tab缩进（不然太丑）
        editArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == 9) {
                    e.consume();
                    editArea.insert("    ", editArea.getCaretPosition());
                }
            }
        });


        //由于默认的文本区域不支持重做和撤销操作，需要使用UndoManager进行配置，这里添加快捷键
        editArea.getInputMap().put(KeyStroke.getKeyStroke("control Y"), "Redo");
        editArea.getActionMap().put("Redo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(undoManager.canRedo()) undoManager.redo();
            }
        });
        editArea.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");
        editArea.getActionMap().put("Undo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(undoManager.canUndo()) undoManager.undo();
            }
        });
    }

    /**
     * 让控制台输入重定向到进程的系统输入中
     * @return KeyAdapter
     */
    public KeyAdapter inputRedirect(){
        JTextArea consoleArea = this.getComponent("main.textarea.console");
        return new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if(isProjectRunning) {
                    String str = String.valueOf(e.getKeyChar());
                    ProcessExecuteEngine.redirectToProcess(str);
                    consoleArea.append(str);
                }
            }
        };
    }

    /**
     * 切换当前编辑的文件，并更新编辑面板中的内容
     * @param path 文件路径
     */
    public void switchEditFile(String path) {
        JTextArea editArea = this.getComponent("main.textarea.edit");
        //切换card
        JSplitPane panel = this.getComponent("main.panel.content");
        JSplitPane paneltop= (JSplitPane) panel.getTopComponent();
        JPanel panelright= (JPanel) paneltop.getRightComponent();
        CardLayout cardLayout= (CardLayout)panelright.getLayout();
        cardLayout.show(panelright,"text");
        //System.out.printf("yes");

        currentFile = null;
        File file = new File(path);
        if(file.isDirectory()) return;
        editArea.getDocument().removeUndoableEditListener(undoManager);
        if(file.getName().endsWith(".class")) {
            editArea.setText(ProcessExecuteEngine.decompileCode(file.getAbsolutePath()));
            editArea.setEditable(false);
        } else {
            try(FileReader reader = new FileReader(file)) {
                StringBuilder builder = new StringBuilder();
                int len;
                char[] chars = new char[1024];
                while ((len = reader.read(chars)) > 0)
                    builder.append(chars, 0, len);
                editArea.setText(builder.toString());
                editArea.setEditable(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        editArea.getDocument().addUndoableEditListener((undoManager = new UndoManager()));
        currentFile = file;
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

    private static OS osType() {
        switch (System.getProperty("os.name")) {
            case "Mac OS X":
                return OS.MacOS;
            case "Linux":
                return OS.Linux;
            case "Windows 11":
            case "Windows 10":
            case "Windows 7":
            case "Windows 8":
            case "Windows 8.1":
                return OS.Windows;
            default:
                throw new IllegalStateException("未知的操作系统类型！");
        }
    }

    /**
     * 保存当前编辑框中的内容到当前文件中
     */
    private void saveFile(){
        JTextArea editArea = this.getComponent("main.textarea.edit");
        if(currentFile == null) return;
        try (FileWriter writer = new FileWriter(currentFile)){
            writer.write(editArea.getText());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
