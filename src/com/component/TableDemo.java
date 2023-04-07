package com.component;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.TableModel;
/**
 *
 * @author Jeky
 */
public class TableDemo extends JFrame {
    public TableDemo() {
        table = new JTable(new String[][]{{"a"}, {"b"}, {"c"}, {"d"}},
                new String[]{"name"});
        this.add(new JScrollPane(table));
        JButton button = new JButton("search");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TableModel model = table.getModel();
                for (int i = 0; i < model.getRowCount(); i++) {
                    Object value = model.getValueAt(i, 0);
                    if(field.getText().equals(value)){
                        table.setRowSelectionInterval(i, i);
                        return;
                    }
                }
                table.clearSelection();
            }
        });
        JPanel panel = new JPanel();
        panel.add(new JLabel("key:"));
        field = new JTextField(5);
        panel.add(field);
        panel.add(button);
        this.add(panel,BorderLayout.SOUTH);
        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    public static void main(String[] args) {
        new TableDemo().setVisible(true);
    }
    private JTable table;
    private JTextField field;
}