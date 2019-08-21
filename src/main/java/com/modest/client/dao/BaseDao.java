package com.modest.client.dao;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.modest.util.CommUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.Properties;

/**
 * description
 *
 * @author modest
 * @date 2019/08/18
 */
public class BaseDao {

    private static DruidDataSource dataSource;

    static {
        Properties properties = CommUtils
                .loadProperties("datasource.properties");
        try {
            dataSource = (DruidDataSource)DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected DruidPooledConnection getConnection() {
        try {
            return (DruidPooledConnection)dataSource.getConnection();
        } catch (Exception e) {
            System.out.println("数据库连接获取失败");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 用户插入 更新操作中关闭资源
     * @param connection 数据库连接
     * @param statement sql语句
     */
    public static void closeResource(Connection connection, Statement statement) {
        if(Objects.nonNull(statement)) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if(Objects.nonNull(connection)) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


    }

    /**
     * 用户查找操作中关闭资源
     * @param connection 连接
     * @param statement 准备sql
     * @param resultSet 结果集合
     */
    public static void closeResource(Connection connection, Statement statement, ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        closeResource(connection,statement);
    }
}
