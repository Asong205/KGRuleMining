package com.mining.window.dialog;

import com.mining.window.AbstractWindow;

import javax.swing.*;
import java.awt.*;

/**
 RDF KB可以被认为是事实的集合，其中每个事实是形式为x，r，y的三元组，x表示主语，r表示关系（或谓词），y表示事实的宾语。事实有几种等价的可供选择的表现形式
 */
public class HelpDialog extends AbstractDialog {
    public HelpDialog(AbstractWindow parent, String text) {

        super(parent, "帮助", new Dimension(600, 300));
        JPanel jPanel=new JPanel();

        jPanel.setBounds(0,0,600, 300);
        jPanel.setLayout(null);

        //this.setLayout(new BorderLayout());

        if(text.trim().equals("1.2.1")) {
            JTextArea area = new JTextArea();
            area.setFont(new Font("微软雅黑",Font.PLAIN, 15));
            JTabbedPane tb = new JTabbedPane();

            area.setText("##本系统可从从三元组格式的医疗知识库(KB)中提取可信逻辑规则。\n" +
                    "\n" +
                    "##规则示例如下:\n" +
                    "  ?x <hasChild> ?c ?y <hasChild> ?c => ?x <isMarriedTo> ?y\n"+
                    "##有共同孩子的人通常都结婚了。"
                    );
            tb.add("规则提取", new JScrollPane(area));
            tb.setBounds(0,0,580, 260);
            this.addComponent(jPanel,tb, pane -> area.setEditable(false));
        }else
        if(text.trim().equals("1.2.2")) {
            JTextArea area = new JTextArea();
            area.setFont(new Font("微软雅黑",Font.PLAIN, 15));
            JTabbedPane tb = new JTabbedPane();


            area.setText("##左侧为项目文件夹预览列表，可进行文件的查看、检索和修改\n" +
                    "\n" +
                    "##右侧为参数管理及文件管理区域\n" +
                    "\n"+
                    "##下方为规则提取流程进度"
            );
            tb.add("关于本系统", new JScrollPane(area));
            tb.setBounds(0,0,580, 260);
            this.addComponent(jPanel,tb, pane -> area.setEditable(false));
        }else
        if(text.trim().equals("arg")) {
            JTextArea area = new JTextArea();
            area.setFont(new Font("微软雅黑",Font.PLAIN, 15));
            JTabbedPane tb = new JTabbedPane();


            area.setText("##参数设置模块介绍:\n" +
                    "##填写参数后点击[更新]确认对参数的更改,成功运行后才可进入下一步\n" +
                    "##其中*数据集为必填项，其他参数如不填写则按默认值运行:\n" +
                    "##默认值:\n" +
                    "  分隔符:\\t\n" +
                    "  标准置信度阈值:0.0\n" +
                    "  头覆盖率阈值:0.01\n" +
                    "  PCA置信度阈值:0.01\n" +
                    "  递归层数:3\n" +
                    "  线程数:8\n" +
                    "\n"

            );
            tb.add("关于参数", new JScrollPane(area));
            tb.setBounds(0,0,580, 260);
            this.addComponent(jPanel,tb, pane -> area.setEditable(false));
        }else
            if(text.trim().equals("arg2")) {
                JTextArea area = new JTextArea();
                area.setFont(new Font("微软雅黑",Font.PLAIN, 15));
                JTabbedPane tb = new JTabbedPane();


                area.setText("##ANYBurl参数设置模块介绍:\n" +
                        "##填写参数后点击[更新]确认对参数的更改,成功运行后才可进入下一步\n" +
                        "##其中*数据集、*迭代时间为必填项，其他参数如不填写则按默认值运行:\n" +
                        "##默认值:\n" +
                        "  分隔符:\\t\n" +
                        "  支持度阈值:10\n" +

                        "  置信度阈值:0.0\n" +

                        "  线程数:20\n" +
                        "\n"

                );
                tb.add("关于参数", new JScrollPane(area));
                tb.setBounds(0,0,580, 260);
                this.addComponent(jPanel,tb, pane -> area.setEditable(false));
            }
            else
            if(text.trim().equals("out")) {
                JTextArea area = new JTextArea();
                area.setFont(new Font("微软雅黑",Font.PLAIN, 15));
                JTabbedPane tb = new JTabbedPane();


                area.setText("##数据管理模块介绍:\n" +
                        "##表格内容：\n" +
                        "  对规则提取的结果进行展示 包括规则体和规则头，以及对应的得分\n" +
                        "  单击表头可对表格进行排序\n" +
                        "  点击单元格可选中该行\n" +
                        "##上方工具栏：\n" +
                        "  可对规则进行关键词搜索\n" +
                        "  选中多行后点击[生成图标]可生成对应柱状图\n" +
                        "  可对数据进行导出保存\n"


                );
                tb.add("关于参数", new JScrollPane(area));
                tb.setBounds(0,0,580, 260);
                this.addComponent(jPanel,tb, pane -> area.setEditable(false));
            }
            else
            if(text.trim().equals("out2")) {
                JTextArea area = new JTextArea();
                area.setFont(new Font("微软雅黑",Font.PLAIN, 15));
                JTabbedPane tb = new JTabbedPane();


                area.setText("##规则管理模块介绍:\n" +
                        "  对最终得到规则进行修改与保存\n" +

                        "\n"

                );
                tb.add("关于参数", new JScrollPane(area));
                tb.setBounds(0,0,580, 260);
                this.addComponent(jPanel,tb, pane -> area.setEditable(false));
            }



        this.add(jPanel);
        }




    @Override
    protected void initDialogContent() {}


}


