package com.modest;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.modest.util.CommUtils;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Properties;

/**
 * description
 *
 * @author modest
 * @date 2019/08/09
 */
public class JDBCTest {

    private static DruidDataSource dataSource;

    static {
        Properties pro = CommUtils.loadProperties("datasource.properties");

        try {
            dataSource = (DruidDataSource)DruidDataSourceFactory.createDataSource(pro) ;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void insertTest() throws SQLException {

        Connection connection = null;
        Statement statement = null;

        try {
            connection = dataSource.getConnection();
            String sql = "insert into user (username, passwrod, brief) values ('shuai','123','shuai')";
            statement = connection.prepareStatement(sql);
            ((PreparedStatement) statement).execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            CommUtils.closeResource(connection,statement);

        }


    }

    @Test
    public void queryTest() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = dataSource.getConnection();
            String sql = "select * from user";
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();
            while(resultSet.next()) {
                System.out.println(resultSet.getInt("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            CommUtils.closeResource(connection,statement,resultSet);
        }
    }
}
