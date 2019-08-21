package com.modest.client.service;

import com.modest.client.dao.AccountDao;
import com.modest.client.entity.User;
import com.modest.util.CommUtils;
import com.modest.vo.MessageVo;
import org.apache.commons.codec.digest.DigestUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.Set;

/**
 * description
 *
 * @author modest
 * @date 2019/08/18
 */
public class UserLogin {
    private JPanel userLoginPanel;
    private JPanel userPanel;
    private JPanel buttonPanel;
    private JTextField usernameText;
    private JPasswordField passwordText;
    private JButton regButton;
    private JButton loginButton;
    private JFrame frame;

    private AccountDao accountDao = new AccountDao();

    public UserLogin() {
        frame = new JFrame("用户登录");
        frame.setContentPane(userLoginPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400,300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        regButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new UserReg();
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameText.getText();
                String password = DigestUtils.md5Hex(String.valueOf(passwordText.getPassword()));

                User user = accountDao.userLogin(username, password);


                if(user != null) {
                    JOptionPane.showMessageDialog(frame,
                            "登录成功", "提示信息", JOptionPane.INFORMATION_MESSAGE);
                    frame.setVisible(true);
                    Connect2Server connect2Server = new Connect2Server();
                    MessageVo messageVo = new MessageVo();
                    messageVo.setType("1");
                    messageVo.setContent(username);

                    String json2Server = CommUtils.object2Json(messageVo);

                    try {
                        PrintStream out = new PrintStream(connect2Server.getOut());
                        out.println(json2Server);

                        Scanner in = new Scanner(connect2Server.getIn());

                        if(in.hasNextLine()) {
                            String msgFromServerStr = in.nextLine();
                            MessageVo msgFromServer = (MessageVo)CommUtils.json2Object(msgFromServerStr,
                                    MessageVo.class);
                            Set<String> users = (Set<String>) CommUtils.json2Object(msgFromServer.getContent(), Set.class);
                            System.out.println("所有在线用户为:"+users);

                            new FriendsList(username, users, connect2Server);
                        }

                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(frame,"登陆失败","错误信息",JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public static void main(String[] args) {
        UserLogin userLogin = new UserLogin();
    }
}
