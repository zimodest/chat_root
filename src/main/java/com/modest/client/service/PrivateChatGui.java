package com.modest.client.service;

import com.modest.util.CommUtils;
import com.modest.vo.MessageVo;

import javax.swing.*;
import java.awt.event.*;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

/**
 * description
 *
 * @author modest
 * @date 2019/08/19
 */
public class PrivateChatGui {
    private JTextArea readText;
    private JTextField writeText;
    private JPanel privatePanel;

    private JFrame frame;
    private Connect2Server connect2Server;
    private PrintStream out;
    private String friendName;
    private String myName;

    public PrivateChatGui(String myName, String friendName, Connect2Server connect2Server) {
        this.myName = myName;
        this.friendName = friendName;
        this.connect2Server = connect2Server;
        try {
            this.out = new PrintStream(connect2Server.getOut(), true, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        this.frame = new JFrame(friendName);
        frame.setContentPane(privatePanel);
        this.frame.setSize(400, 300);
        this.frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.frame.setLocationRelativeTo(null);
        this.frame.setVisible(true);

        writeText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                StringBuilder sb = new StringBuilder();
                sb.append(writeText.getText()) ;

                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    //将信息发送到服务器端
                    String msg = sb.toString();
                    MessageVo messageVo = new MessageVo();
                    messageVo.setType("2");
                    messageVo.setContent(myName+"-"+msg);
                    messageVo.setTo(friendName);
                    PrivateChatGui.this.out.println(CommUtils.object2Json(messageVo));

                    //将自己发出的信息展示到当前私聊界面
                    readFromServer(msg);
                    //发送完，将输入框中的值置为空字符串
                    writeText.setText("");
                }
            }
        });


    }

    private void readFromServer(String msg) {
        readText.append(msg+"\n");
    }

    public JFrame getFrame(){
        return this.frame;
    }
}
