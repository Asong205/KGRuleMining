package com.component;

import com.component.basic.color.ColorUtil;
import com.component.notice.loading.LoadingLabel;
import com.component.notice.loading.LoadingLabel2;
import com.component.notice.loading.LoadingLabel3;
import com.component.svg.icon.regular.CrosshairSvg;
import com.component.util.SwingTestUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoadingLabelTest {
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			SwingTestUtil.setDefaultTimingSource();
			SwingTestUtil.loadSkin();
			LoadingLabel2 c = new LoadingLabel2(ColorUtil.PRIMARY, 4);

			// c.setPreferredSize(new Dimension(400, 400));
			c.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					SwingTestUtil.getFrame().getContentPane().remove(c);
					SwingTestUtil.getFrame().getContentPane().repaint();
				}
			});
			SwingTestUtil.test(c);
		});
	}



}
