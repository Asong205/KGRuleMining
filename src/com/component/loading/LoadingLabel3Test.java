package com.component.loading;

import com.component.notice.loading.LoadingLabel3;
import com.component.util.SwingTestUtil;

import javax.swing.*;
import java.awt.*;

public class LoadingLabel3Test {
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			JPanel p = new JPanel();

			LoadingLabel3 label = new LoadingLabel3();
			label.startAnimation();
			p.add(label);
			SwingTestUtil.test();
		});
	}
}
