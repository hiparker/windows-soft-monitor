package org.soft.monitor.view;

import cn.hutool.core.util.StrUtil;
import org.soft.monitor.job.MonitorJob;
import org.soft.monitor.view.model.ConfigData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

public class ViewFrame extends JFrame {

    private static final String TITLE = "监控程序 v1.0.0";


    /** 画布宽高 */
    public static final int WINDOW_WIDTH = 560,WINDOW_HEIGHT = 400;

    private List logList;

    public ViewFrame(){
        this.getContentPane().setLocation(0, 0);

        ConfigData load = ConfigData.load();
        MonitorJob.setConfigData(load);

        // 创建日志
        createLog();

        // 创建选择框
        createArouseFileChoice("唤起程序：", StrUtil.isNotBlank(load.getArouseFileName())?load.getArouseFileName():null, -162, 50);
        createMonitorFileChoice("监控程序：", StrUtil.isNotBlank(load.getMonitorFileName())?load.getMonitorFileName():null, 0, 100);


        this.setTitle(TITLE);
        this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.setVisible(true);
        // 禁止改变大小
        this.setResizable(false);
        this.setLocationRelativeTo(null);

        // window 监听器
        this.addWindowListener(new WindowAdapter() {

            // 监听 关闭事件 关闭当前java程序
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }



    /**
     * 执行日志
     */
    private List createLog(){
        JPanel panel = new JPanel();
        LayoutManager layout = new BorderLayout();
        panel.setLayout(layout);
        panel.setBounds(0, 150, WINDOW_WIDTH, 0);
        this.logList = new List(15, true);
        panel.add(logList);
        this.getContentPane().add(panel, BorderLayout.SOUTH);
        return logList;
    }

    public List getLogList() {
        return logList;
    }


    /**
     * 创建程序选择按钮
     * @param title
     * @param fileName
     * @param x
     * @param y
     */
    private void createArouseFileChoice(final String title,final String fileName, final int x, final int y) {
        JPanel panel = new JPanel();
        LayoutManager layout = new FlowLayout();
        panel.setLayout(layout);
        panel.setBounds(x, y, WINDOW_WIDTH, 80);
        final JLabel titleJ = new JLabel(title);
        JButton button = new JButton("选择文件");
        final JLabel fileNameJ = new JLabel(StrUtil.isNotBlank(fileName)?fileName:"未选择文件");
        button.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            int option = fileChooser.showOpenDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                ConfigData configData = MonitorJob.getConfigData();
                File file = fileChooser.getSelectedFile();

                configData.setArouseFileName(file.getName());
                configData.setArouseFilePath(file.getPath());
                ConfigData.save(configData);
                MonitorJob.setConfigData(configData);

                fileNameJ.setText(file.getName());
            } else {
                fileNameJ.setText("未选择文件");
            }
        });

        panel.add(titleJ);
        panel.add(fileNameJ);
        panel.add(button);
        this.add(panel);
        this.getContentPane().add(panel, BorderLayout.WEST);
    }

    /**
     * 创建程序选择按钮
     * @param title
     * @param fileName
     * @param x
     * @param y
     */
    private void createMonitorFileChoice(final String title,final String fileName, final int x, final int y) {
        JPanel panel = new JPanel();
        LayoutManager layout = new FlowLayout();
        panel.setLayout(layout);
        panel.setBounds(x, y, WINDOW_WIDTH, 80);
        final JLabel titleJ = new JLabel(title);
        JButton button = new JButton("选择文件");
        final JLabel fileNameJ = new JLabel(StrUtil.isNotBlank(fileName)?fileName:"未选择文件");
        button.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            int option = fileChooser.showOpenDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                ConfigData configData = MonitorJob.getConfigData();
                File file = fileChooser.getSelectedFile();

                configData.setMonitorFileName(file.getName());
                configData.setMonitorFilePath(file.getPath());
                ConfigData.save(configData);
                MonitorJob.setConfigData(configData);

                fileNameJ.setText(file.getName());
            } else {
                fileNameJ.setText("未选择文件");
            }
        });

        panel.add(titleJ);
        panel.add(fileNameJ);
        panel.add(button);
        this.add(panel);
        this.getContentPane().add(panel, BorderLayout.WEST);
    }


    public static void main(String[] args) {
        ViewFrame viewFrame = new ViewFrame();
    }

}

