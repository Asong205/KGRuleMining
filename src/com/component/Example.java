package com.component;


import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class Example {
    public static void main(String[] args) {

        // 创建数据
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(1.0, "A", "11111111111111111111111111111111");
        dataset.addValue(3.0, "A", "Ⅱ2222222222222222222222222222");
        dataset.addValue(5.0, "A", "Ⅲ33333333333333333333333333333");
        dataset.addValue(5.0, "A", "Ⅳ44444444444444444444444444444444");
        dataset.addValue(5.0, "B", "Ⅰ");
        dataset.addValue(6.0, "B", "Ⅱ");
        dataset.addValue(10.0, "B", "Ⅲ");
        dataset.addValue(4.0, "B", "Ⅳ");


        // 创建JFreeChart对象
        JFreeChart chart = ChartFactory.createBarChart3D(
                "Example", // 图标题
                "Category",
                "Score",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);


        // 利用awt进行显示
        ChartFrame chartFrame = new ChartFrame("Test", chart);
        chartFrame.pack();
        chartFrame.setVisible(true);
    }

}