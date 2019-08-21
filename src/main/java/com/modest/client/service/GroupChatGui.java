package com.modest.client.service;

import com.modest.util.CommUtils;
import com.modest.vo.MessageVo;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Set;

/**
 * description
 *
 * @author modest
 * @date 2019/08/21
 */
public class GroupChatGui {
    private JTextArea readText;
    private JTextField writeText;
    private JPanel groupPanel;
    private JPanel friendListPanel;

    private JFrame frame;
    private Connect2Server connect2Server;
    private String groupName;
    private Set<String> friends;
    private String myName;

    public GroupChatGui(String groupName, Set<String> friends, String myName, Connect2Server connect2Server) {
        this.groupName = groupName;
        this.friends = friends;
        this.myName = myName;
        this.connect2Server = connect2Server;
        frame = new JFrame(groupName);

        frame.setContentPane(groupPanel);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setSize(400, 400);
        frame.setVisible(true);

        //用户显示列表
        friendListPanel.setLayout(new BoxLayout(friendListPanel, BoxLayout.Y_AXIS));
        Iterator<String> iterator = friends.iterator();
        while(iterator.hasNext()) {
            String friendName = iterator.next();
            JLabel jLabel = new JLabel(friendName);
            friendListPanel.add(jLabel);
        }

        writeText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(writeText.getText());
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String strServer = stringBuilder.toString();
                    MessageVo messageVo = new MessageVo();
                    messageVo.setType("4");
                    messageVo.setContent(myName+"-"+strServer);
                    messageVo.setTo(groupName);

                    try {
                        PrintStream out = new PrintStream(connect2Server.getOut(), true, "UTF-8");
                        out.println(CommUtils.object2Json(messageVo));
                        System.out.println("客户端发送的群聊信息为"+strServer);
                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                    }

                }
            }
        });
    }

    public void readFromServer(String msg) {
        readText.append(msg+"\n");
    }

    public JFrame getFrame() {
        return frame;
    }
}
