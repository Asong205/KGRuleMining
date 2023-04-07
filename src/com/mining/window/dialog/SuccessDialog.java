package com.mining.window.dialog;

import com.mining.window.AbstractWindow;

import javax.swing.*;
import java.awt.*;

/**
 * 运行对话框
 */
public class SuccessDialog extends AbstractDialog {
    public SuccessDialog(AbstractWindow parent, String text) {
        super(parent, "运行成功", new Dimension(600, 300));
        this.setLayout(new BorderLayout());
        JTextArea area = new JTextArea(text);
        this.addComponent(new JScrollPane(area), pane -> area.setEditable(false));

    }

    @Override
    protected void initDialogContent() {}
}
