package com.modest.client.service;

import com.modest.client.dao.AccountDao;
import com.modest.client.entity.User;
import org.apache.commons.codec.digest.DigestUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * description
 *
 * @author modest
 * @date 2019/08/18
 */
public class UserReg {
    private JTextField usernameText;
    private JTextField briefText;
    private JPasswordField passwordText;
    private JButton regButton;
    private JPanel userReg;
    private AccountDao accountDao = new AccountDao();

    public UserReg() {
        JFrame frame = new JFrame("用户注册");
        frame.setContentPane(userReg);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);

        regButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameText.getText();
                String password = DigestUtils.md5Hex(String.valueOf(passwordText.getPassword()));
                String brief = briefText.getText();

                User user = new User();
                user.setUserName(username);
                user.setPassword(password);
                user.setBrief(brief);

                if(accountDao.userReg(user)) {
                    JOptionPane.showMessageDialog(frame,"注册成功！",
                            "提示信息", JOptionPane.INFORMATION_MESSAGE);
                    frame.setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(frame,"注册失败",
                            "错误信息", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }


}
