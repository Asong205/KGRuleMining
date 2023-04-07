package com.mining.window.dialog;

import com.mining.window.AbstractWindow;

import javax.swing.*;
import java.awt.*;

/**
 * 编译失败对话框
 */
public class CompileErrorDialog extends AbstractDialog {
    public CompileErrorDialog(AbstractWindow parent, String text) {
        super(parent, "运行失败", new Dimension(600, 300));

        this.setLayout(new BorderLayout());
        JTextArea area = new JTextArea("未知错误");
        if(!text.isEmpty())
            area.setText(text);
        JPanel jPanel=new JPanel();
        jPanel.setBounds(0,0,500,300);
        jPanel.setLayout(null);
        this.addComponent(jPanel,new JScrollPane(area), pane -> area.setEditable(false));
        this.add(jPanel);

    }

    @Override
    protected void initDialogContent() {}
}
