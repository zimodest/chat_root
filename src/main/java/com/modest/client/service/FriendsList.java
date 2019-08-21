package com.modest.client.service;

import com.modest.util.CommUtils;
import com.modest.vo.MessageVo;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * description
 *
 * @author modest
 * @date 2019/08/18
 */
public class FriendsList {
    private JButton groupButton;
    private JScrollPane friendsList;
    private JScrollPane groupList;
    private JPanel friendsPanel;
    private JFrame frame;

    private String userName;

    /**
     * 存储所有在线好友列表
     */
    private Set<String> users;

    /**
     * 存储群名以及群成员
     */
    private Map<String, Set<String>> groupSetMap = new ConcurrentHashMap<>();

    private Connect2Server connect2Server;


    /**
     * 存储所有私聊界面
     */
    private Map<String, PrivateChatGui> privateChatGuiMap = new ConcurrentHashMap<>();


    private Map<String, GroupChatGui> groupChatGuiMap = new ConcurrentHashMap<>();
    /**
     * 　
     * 好友列表后台任务
     * 好友上线信息 私聊 群聊
     *
     * 收到服务器发来的信息，判断是否是json字符串
     *
     * 判断服务器发来的信息
     */

    private class DaemonTask implements Runnable {


        private Scanner in = new Scanner(connect2Server.getIn());

        @Override
        public void run() {

            while (true) {
                if (in.hasNextLine()) {
                    String strFromServer = in.nextLine();

                    //传来的字符串时json格式
                    if (strFromServer.startsWith("{")) {
                        MessageVo messageVo = (MessageVo) CommUtils.json2Object(strFromServer, MessageVo.class);

                        if (messageVo.getType().equals("2")) {
                            //服务器发来私聊信息
                            String[] inform = messageVo.getContent().split("-");
                            String friendName = inform[0];
                            String msg = inform[1];

                            //判断私聊是否第一次创建
                            if (privateChatGuiMap.containsKey(friendName)) {
                                privateChatGuiMap.get(friendName).readFromServer(friendName+msg);
                                privateChatGuiMap.get(friendName).getFrame().setVisible(true);
                            } else {
                                PrivateChatGui privateChatGui = new PrivateChatGui(userName, friendName, connect2Server);
                                privateChatGui.readFromServer(msg);
                                privateChatGuiMap.put(friendName, privateChatGui);
                                privateChatGui.getFrame().setVisible(true);
                            }

                        } else if(messageVo.getType().equals("4")) {
                            String groupName = messageVo.getTo().split("-")[0];
                            String sendName = messageVo.getContent().split("-")[0];
                            String groupMsg = messageVo.getContent().split("-")[1];

                            if(groupSetMap.containsKey(groupMsg)) {
                                if (groupChatGuiMap.containsKey(groupName)) {
                                    GroupChatGui groupChatGui = groupChatGuiMap.get(groupName);
                                    groupChatGui.getFrame().setVisible(true);
                                    groupChatGui.readFromServer(sendName + "说" + groupMsg);
                                } else {
                                    Set<String> names = groupSetMap.get(groupName);
                                    GroupChatGui groupChatGui = new GroupChatGui(groupName, names, userName, connect2Server);
                                    groupChatGuiMap.put(groupMsg, groupChatGui);
                                }
                            }else {
                                Set<String> friends = (Set<String>) CommUtils.json2Object(messageVo.getTo().split("-")[1],Set.class);
                                groupSetMap.put(groupMsg,friends);
                                loadGroupList();

                                GroupChatGui groupChatGui = new GroupChatGui(groupName,friends, userName, connect2Server);
                                groupChatGuiMap.put(groupName, groupChatGui);
                                groupChatGui.readFromServer(sendName+"说:"+groupMsg);
                            }
                        }

                    } else {
                        System.out.println("----------------------------------------------");
                        if(strFromServer.startsWith("newLogin:")) {
                            String newFriendName = strFromServer.split(":")[1];
                            users.add(newFriendName);

                            JOptionPane.showMessageDialog(frame, newFriendName+"上线了!",
                                    "上线提醒", JOptionPane.INFORMATION_MESSAGE);
                            loadUsers();
                        }
                    }
                }
            }
        }
    }
    /**
     * 鼠标点击事件
     */

    private class PrivateChatLabelMouser implements MouseListener{

        /**
         * 当前界面的名称   朋友的名称
         */
        private String friendName;

        public PrivateChatLabelMouser(String name) {
            this.friendName = name;
            System.out.println(name);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if(privateChatGuiMap.containsKey(friendName)) {
                //私聊界面的名称是私聊的好友名
                PrivateChatGui privateChatGui = privateChatGuiMap.get(friendName);
                privateChatGuiMap.get(friendName).getFrame().setVisible(true);
            } else {

                //friendName  是好友名
                PrivateChatGui privateChatGui = new PrivateChatGui(userName, friendName, connect2Server);
                privateChatGuiMap.put(friendName, privateChatGui);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

    }


    /**
     *
     * @param userName 用户名
     * @param users 好友列表
     * @param connect2Server
     */
    public FriendsList(String userName, Set<String> users, Connect2Server connect2Server) {
        this.userName = userName;
        this.users = users;
        this.connect2Server = connect2Server;

        frame = new JFrame(userName);
        frame.setContentPane(friendsPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400,300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        loadUsers();
        //启动后台线程，设置成后台线程 不断监听服务器发来的信息
       Thread daemonThread = new Thread(new DaemonTask());
       daemonThread.setDaemon(true);
       daemonThread.start();

       //创建群组
        groupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CreateGroupGui(userName, users, connect2Server, FriendsList.this);
            }
        });

    }

    /**
     * 加载所有在线的用户信息
     */
    private void loadUsers() {
        JLabel[] userLabels = new JLabel[users.size()];
        JPanel friends = new JPanel();
        friends.setLayout(new BoxLayout(friends, BoxLayout.Y_AXIS));


        //遍历好友列表
        Iterator<String> iterator = users.iterator();
        int i = 0;
        while(iterator.hasNext()) {
            //获取好友名称
            String friendName = iterator.next();

            //好友Label
            userLabels[i] = new JLabel(friendName);
            userLabels[i].addMouseListener(new PrivateChatLabelMouser(friendName));
            friends.add(userLabels[i]);
            i++;
        }



        // 给私聊界面加在线用户显示label
        friendsList.setViewportView(friends);

        //设置滚动条垂直滚动
        friendsList.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        friends.revalidate();
        friendsList.revalidate();
    }

    public void loadGroupList() {
        JPanel groupNamePanel = new JPanel();
        groupNamePanel.setLayout(new BoxLayout(groupNamePanel, BoxLayout.Y_AXIS));
        JLabel[] labels = new JLabel[groupSetMap.size()];

        Set<Map.Entry<String, Set<String>>> entries = groupSetMap.entrySet();

        Iterator<Map.Entry<String, Set<String>>> iterator = entries.iterator();

        int i=0;

        while(iterator.hasNext()) {
            Map.Entry<String, Set<String>> entry = iterator.next();
            labels[i] = new JLabel(entry.getKey());
            groupNamePanel.add(labels[i]);
            i++;
        }

        groupList.setViewportView(groupNamePanel);
        groupList.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        groupList.revalidate();
    }

    public void addGroup(String groupName, Set<String> friends) {
        groupSetMap.put(groupName, friends);
    }
}
