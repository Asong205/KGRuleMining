package com.component.basic.button;

import com.component.basic.color.ColorUtil;
import com.component.util.SwingTestUtil;

import java.awt.*;

public class SwitchButtonTest {
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			SwingTestUtil.loadSkin();
			SwingTestUtil.setDefaultTimingSource();

			SwitchButton button = new SwitchButton(true);
			button.addActionListener(e -> {
				System.out.println(button.isOpen());
			});
			button.setOpenColor(ColorUtil.PRIMARY);
			button.setCloseColor(ColorUtil.BACKGROUND);
			button.setPreferredSize(new Dimension(100, 40));
			// button.setEnabled(false);

			SwingTestUtil.test(button);
		});
	}
}
