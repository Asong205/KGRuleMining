package com.component.navigation.navigation.pageheader;

import com.component.navigation.pageheader.PageHeader;
import com.component.util.SwingTestUtil;

import java.awt.*;

public class PageHeaderTest {
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			SwingTestUtil.loadSkin();
			SwingTestUtil.test(new PageHeader("详情页面"));
		});
	}
}
