package com.modest.client.dao;

import com.modest.client.entity.User;
import org.apache.commons.codec.digest.DigestUtils;

import java.sql.*;

/**
 * description
 *
 * @author modest
 * @date 2019/08/18
 */
public class AccountDao extends BaseDao {

    public boolean userReg(User user) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = getConnection();

            String sql = "insert into user(username,password,brief) " +
                    "values (?,?,?)";
            statement = connection.prepareStatement(sql);
            statement.setString(1,user.getUserName());
            statement.setString(2,user.getPassword());
            statement.setString(3,user.getBrief());
            int rows = statement.executeUpdate();
            if(rows == 1) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println("用户注册失败");
            e.printStackTrace();
        }finally {
            closeResource(connection,statement);
        }

        return false;
    }


    public User userLogin(String username, String password) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            String sql = "select * from user where username = ? and password= ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1,username);
            statement.setString(2,password);
            resultSet = statement.executeQuery();
            while(resultSet.next()) {
                User user = getUser(resultSet);
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResource(connection,statement,resultSet);
        }

        return null;
    }

    private User getUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setUserName(resultSet.getString("username"));
        user.setPassword(resultSet.getString("password"));
        user.setBrief(resultSet.getString("brief"));
        return user;
    }

}
