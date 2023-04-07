package com.component;


import com.component.basic.color.ColorUtil;
import com.component.data.table.ETable;
import com.component.data.table.renderer.ETableCellRenderer;
import com.component.svg.icon.regular.QuestionSvg;
import com.component.util.SwingTestUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ETableTest {
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            SwingTestUtil.loadSkin();

            String[][] s1 = {{"张三", "12346", "12"}, {"李四", "234561", "18"}, {"王五", "34561", "22"},
                    {"王五", "34561", "22"}, {"王五", "34561", "22"}, {"王五", "34561", "22"},
                    {"王五", "34561", "22"}};
            String[] s2 = {"姓名", "学号", "年龄"};
            DefaultTableModel tableModel = new DefaultTableModel(s1, s2);

            ETable table = new ETable(tableModel);
            ETableCellRenderer renderer = table.getCellRenderer();
            renderer.setContainBorder(true);
            renderer.setContainStripe(true);
            renderer.setForeground(ColorUtil.PRIMARY);

//            renderer.addState(1, ColorUtil.PRIMARY);
//            renderer.addState(2, ColorUtil.DANGER);
//            renderer.addState(4, ColorUtil.WARNING);
            JButton addRow = new JButton("添加一行");
            JButton addColum = new JButton("添加一列");
            JButton deleteRow = new JButton("删除一行");

            addRow.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    //需要给表格添加一行
                    tableModel.addRow(new Object[]{1,1,1});
                }
            });

            deleteRow.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int selectedRow = table.getSelectedRow();
                    if(selectedRow!=-1)
                    tableModel.removeRow(selectedRow);
                }
            });

            addColum.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    tableModel.addColumn("职业");
                }
            });

            JPanel jp=new JPanel();

            QuestionSvg svg =new QuestionSvg();
            svg.setDimension(new Dimension(17,17));

            JButton jb =new JButton(svg);

            JScrollPane scrollPane = new JScrollPane(table);
            jp.add(scrollPane);
            //scrollPane.setBorder(null);
            jp.add(addColum);
            jp.add(addRow);
            jp.add(deleteRow);
            jp.add(jb);
            SwingTestUtil.test(jp);
        });
    }
}
