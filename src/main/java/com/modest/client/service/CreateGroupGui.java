package com.modest.client.service;

import com.modest.util.CommUtils;
import com.modest.vo.MessageVo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * description
 *
 * @author modest
 * @date 2019/08/20
 */
public class CreateGroupGui {
    private JPanel friendLabelPanel;
    private JPanel createGroupPanel;
    private JTextField createGroupText;
    private JButton createButton;
    private JLabel createLabel;

    private String myName;
    /**
     * 在线好友列表
     */
    private Set<String> friends;
    private Connect2Server connect2Server;
    private FriendsList friendsList;

    public CreateGroupGui(String myName, Set<String> friends, Connect2Server connect2Server,FriendsList friendsList) {
        this.myName = myName;
        this.friends = friends;
        this.connect2Server = connect2Server;
        this.friendsList = friendsList;

        JFrame frame = new JFrame("创建群组");
        frame.setContentPane(createGroupPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // 设置friendLabelPanel中的组件纵列排列
        friendLabelPanel.setLayout(new BoxLayout(friendLabelPanel, BoxLayout.Y_AXIS));
        Iterator<String> iterator = friends.iterator();
        while (iterator.hasNext()) {
            String labelName = iterator.next();
            JCheckBox checkBox = new JCheckBox(labelName);
            friendLabelPanel.add(checkBox);
        }

        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Set<String> selectFriends = new HashSet<>();
                Component[] comps = friendLabelPanel.getComponents();
                for(Component comp : comps) {
                    JCheckBox checkBox = (JCheckBox)comp;
                    if(checkBox.isSelected()) {
                        //将选中的checkBox中的名称添加到选中用户队列中
                        selectFriends.add(checkBox.getText());
                    }
                }
                //用户拉取群组时，默认自己在群组中，将自己默认添加到选中用户队列中
                selectFriends.add(myName);
                System.out.println(myName);

                //获取群名称
                String groupName = createGroupText.getText();

                //将群名和选中的用户列表发送给服务器端
                // 发送格式
                //type : 3
                //content : groupName
                //to : [user1, user2,...]
                MessageVo messageVo = new MessageVo();
                messageVo.setType("3");
                messageVo.setContent(groupName);
                //将Set对象转成json字符串发送到服务器端
                messageVo.setTo(CommUtils.object2Json(selectFriends));

                try {
                    PrintStream out = new PrintStream(connect2Server.getOut(),
                            true, "UTF-8");
                    out.println(CommUtils.object2Json(messageVo));
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }

                // 将当前创建群界面隐藏，刷新好友列表界面的群列表
                frame.setVisible(false);

                //添加好友后刷新用户列表
                friendsList.addGroup(groupName, selectFriends);
                friendsList.loadGroupList();

            }
        });





    }


}
