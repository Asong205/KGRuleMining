package com.mining;

import com.mining.manage.ProjectManager;
import com.mining.window.KGWelcomeWindow;
import com.mining.window.component.ColorUtil;
import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.util.Enumeration;

public class kgmain {
    public static void main(String[] args) throws Exception {
        System.setProperty("sun.java2d.noddraw", "true");
        BeautyEyeLNFHelper.translucencyAtFrameInactive = false;
        BeautyEyeLNFHelper.frameBorderStyle = BeautyEyeLNFHelper.FrameBorderStyle.translucencySmallShadow;
        BeautyEyeLNFHelper.commonFocusedBorderColor= ColorUtil.PRIMARY;
        //BeautyEyeLNFHelper.frameBorderStyle
                //ColorUtil.PRIMARY;
        //option 1
        org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();

        UIManager.put("RootPane.setupButtonVisible", false);

//        FlatLaf.setGlobalExtraDefaults( Collections.singletonMap( "@accentColor", "#f00" ) );
//        FlatIntelliJLaf.setup();

        //option2
//        UIManager.put("TextComponent.arc",800);
//        UIManager.put("Button.arc",800);
//        UIManager.put("ScrollBar.thumbArc",999);
//        UIManager.put("Button.darkShadow", ColorUtil.PRIMARY);
//
//        UIManager.put("Component.arc",800);
//
//        UIManager.put("ProgressBar.arc",800);
//
//        UIManager.put("TextComponent.arc",800);
        //UIManager.setLookAndFeel(new FlatIntelliJLaf());
        //UIManager.setLookAndFeel(new FlatLightLaf());


        InitGlobalFont(new Font("微软雅黑",Font.PLAIN, 13));
        //UIManager.put("Menu.border", BorderFactory.createLineBorder(Color.black, 1));

        //ui2
        //KGWindow t=new KGWindow("KG rule mining","d:/test");
        //t.openWindow();

        //ui1
        ProjectManager.loadProjects();

        KGWelcomeWindow startWindow = new KGWelcomeWindow();

        startWindow.openWindow();
    }


    private static void InitGlobalFont(Font font) {

        FontUIResource fontRes = new FontUIResource(font);

        for (Enumeration keys = UIManager.getDefaults().keys();

             keys.hasMoreElements(); ) {

            Object key = keys.nextElement();

            Object value = UIManager.get(key);

            if (value instanceof FontUIResource) {

                UIManager.put(key, fontRes);

            }

        }

    }

}
