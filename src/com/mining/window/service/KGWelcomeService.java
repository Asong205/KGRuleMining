package com.mining.window.service;



import com.mining.entity.ProjectEntity;
import com.mining.manage.FileManager;
import com.mining.manage.ProjectManager;
import com.mining.window.KGWelcomeWindow;
import com.mining.window.KGWindow;
import com.mining.window.WelcomeWindow;
import com.mining.window.dialog.CreateKGProjectDialog;
import com.mining.window.dialog.DirectoryChooserDialog;
import com.mining.window.dialog.GitProjectDialog;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

public class KGWelcomeService extends AbstractService {
    /**
     * 创建新项目行为
     */
    public void createNewProject(){
        CreateKGProjectDialog dialog = new CreateKGProjectDialog((KGWelcomeWindow) this.getWindow());
        dialog.openDialog();
    }

    /**
     * 打开已有项目行为
     */
    public void openProject(){
        DirectoryChooserDialog dialog = new DirectoryChooserDialog(this.getWindow());
        dialog.openDialog();
        File selectedFile = dialog.getSelectedFile();
        if(selectedFile == null) return;
        ProjectManager.createProject(selectedFile.getName(), selectedFile.getParentFile().getAbsolutePath());

        this.getWindow().dispose();
        KGWindow window = new KGWindow(selectedFile.getName(), selectedFile.getAbsolutePath());
        window.openWindow();
    }

    /**
     * 从版本控制系统打开已有项目行为
     */
    public void openVcsProject() {
        GitProjectDialog dialog = new GitProjectDialog((WelcomeWindow) this.getWindow());
        dialog.openDialog();
    }

    /**
     * 生成刷新项目列表的KeyAdapter
     * @return Adapter
     */
    public KeyAdapter refreshListAdapter(){
        return new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                KGWelcomeWindow window = (KGWelcomeWindow) getWindow();
                window.initProjectList();
            }
        };
    }

    /**
     * 进入项目，也就是关闭当前窗口打开编辑界面
     * @param project 项目实体
     */
    public void enterProject(ProjectEntity project){
        this.getWindow().dispose();
        KGWindow window = new KGWindow(project.getName(), project.getFilePath());
        window.openWindow();
    }

    /**
     * 删除项目，包括项目文件
     * @param project 项目实体
     */
    public void deleteProject(ProjectEntity project){
        KGWelcomeWindow window = (KGWelcomeWindow) getWindow();
        int res = JOptionPane.showConfirmDialog(window,
                "你确定要关闭删除这个项目吗（不可恢复）", "警告", JOptionPane.YES_NO_OPTION);
        if(res == JOptionPane.YES_OPTION) {
            FileManager.deleteProject(project.getName(), project.getFilePath());
            window.initProjectList();
        }
    }
}
