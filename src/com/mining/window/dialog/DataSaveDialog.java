package com.mining.window.dialog;


import com.component.data.table.ETable;
import com.mining.manage.FileManager;
import com.mining.window.OutputWindow;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 项目创建对话框
 */

public class DataSaveDialog extends AbstractDialog {
    private JTextField nameField;   //名称输入框
    private JTextField pathField;   //路径输入框
    private  JComboBox<String> box;

    private JLabel finalPath;     //最终保存位置展示标签

    private JButton createButton;   //按钮

    private final OutputWindow parentWindow; //这里暂时存一下父窗口，后面方便一起关掉

    public DataSaveDialog(OutputWindow parent){
        super(parent, "保存文件", new Dimension(500, 300));
        this.parentWindow = parent;
    }

    @Override
    protected void initDialogContent() {
        JPanel jPanel=new JPanel();
        jPanel.setBounds(0,0,500,300);
        jPanel.setLayout(null);
        //首先添加最左侧的标签
        this.addComponent(jPanel,new JLabel("文件名："), label -> label.setBounds(20, 20, 100, 20));
        this.addComponent(jPanel,new JLabel("路径："), label -> label.setBounds(20, 60, 100, 20));
        this.addComponent(jPanel,new JLabel("保存格式："), label -> label.setBounds(20, 120, 100, 20));
        //this.addComponent(new JLabel("工具路径："), label -> label.setBounds(20, 160, 100, 20));

        //然后是两个文本框，每个文本框都要添加监听器，当输入时，会实时更新最终路径展示标签
        this.addComponent(jPanel,(nameField = new JTextField()), field -> {
            field.setBounds(100, 20, 280, 25);
            field.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    onKeyPress();
                }
            });
        });
        //路径选择文本框，此文本框还有一个文件选择器打开按钮和最终路径展示的标签
        this.addComponent(jPanel,(pathField = new JTextField()), field -> {
            field.setBounds(100, 60, 230, 25);
            //field.setText(parentWindow.getPath());
//            String path=parentWindow.getPath();
//            field.setText(path);
            field.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    onKeyPress();
                }
            });
        });
        this.addComponent(jPanel,(finalPath = new JLabel("请填写文件名称和保存路径！")),
                label -> label.setBounds(100, 85, 380, 20));
        this.addComponent(jPanel,new JButton("..."), button -> {
            button.setBounds(335, 60, 45, 25);
            button.addActionListener(e -> selectDirectory());
        });

        //选择框
        this.addComponent(jPanel,box=new JComboBox<String>(), box -> {
            box.setBounds(100, 120, 200, 25);
            box.addItem("txt");
            box.addItem("csv");
            box.addItem("xlsx");


        });
        //工具路径

//        this.addComponent(toolField= new JTextField(), field -> {
//            toolField.setBounds(100, 160, 200, 25);
//        });
//        this.addComponent(new JButton("..."), button -> {
//            button.setBounds(335, 160, 45, 25);
//            button.addActionListener(e -> selectDirectory());
//        });



        //最后是是否生成默认代码的勾选框和创建按钮
//        this.addComponent((defaultCode = new JCheckBox("是否生成默认代码")),
//                box -> box.setBounds(100, 190, 200, 25))

        this.addComponent(jPanel,(createButton = new JButton("保存")), button -> {
            button.setBounds(380, 230, 100, 25);
            button.setEnabled(false);
            button.setToolTipText("请先填写上述配置信息！");
            button.addActionListener(e -> {
                //boolean hasDefaultCode = defaultCode.isSelected();
                boolean hasDefaultCode = false;
                String name = nameField.getText();
                String path = pathField.getText();
                String type = (String) box.getSelectedItem();
                ETable table=parentWindow.getDatatable();
                try {

                    if (type.trim().equals("txt")) {
                        DefaultTableModel model = (DefaultTableModel) table.getModel();

                        File file = new File(path + "/" + name + ".txt");

                        if (!file.exists()) {

                            file.createNewFile();

                        }

                        FileWriter fileWritter = new FileWriter(file, true);
                        for (int i = 0; i < model.getRowCount(); i++) {
                            String input = (String) model.getValueAt(i, 0) +
                                    (String) model.getValueAt(i, 1) +
                                    (String) model.getValueAt(i, 2) + "\n";
                            fileWritter.write(input);
                            System.out.println(input);
                        }
                        fileWritter.close();

                    } else if (type.trim().equals("xlsx")) {
                        FileManager fileManager = new FileManager();
                        File file = new File(path + "/" + name + ".xls");

                        if (!file.exists()) {
                            file.createNewFile();
                        }

                        fileManager.exportTable(table, file);


                    } else if (type.trim().equals("csv")) {
                        FileManager fileManager = new FileManager();
                        File file = new File(path + "/" + name + ".csv");

                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        fileManager.exportCSV(table, file);
                    }



                } catch (IOException ioException) {
                    JOptionPane.showMessageDialog(this, "未知错误，创建失败！");
                    return;
                }
                JOptionPane.showMessageDialog(this, "success");

                this.closeDialog();

            });
        });

        this.add(jPanel);

    }

    private void onKeyPress(){
        if(nameField.getText() == null || pathField.getText() == null) return;
        if(!pathField.getText().isEmpty() && !nameField.getText().isEmpty()) {
            finalPath.setText("保存位置："+ pathField.getText() + "/" + nameField.getText());
            createButton.setEnabled(true);
            createButton.setToolTipText("点击保存文件");
        } else {
            createButton.setEnabled(false);
            createButton.setToolTipText("请先填写上述配置信息！");
        }
    }

    private void selectDirectory(){
        DirectoryChooserDialog directoryChooserDialog = new DirectoryChooserDialog(DataSaveDialog.this);
        directoryChooserDialog.openDialog();
        File selectedFile = directoryChooserDialog.getSelectedFile();
        if(selectedFile != null) {
            pathField.setText(selectedFile.getAbsolutePath());
            if(nameField.getText() != null && !nameField.getText().isEmpty()) {
                finalPath.setText("保存位置："+ pathField.getText() + "/" + nameField.getText());
                createButton.setEnabled(true);
                createButton.setToolTipText("点击保存文件");
            } else {
                createButton.setEnabled(false);
                createButton.setToolTipText("请先填写上述配置信息！");
            }
        }
    }
}
