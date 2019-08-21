package com.modest.util;

import com.modest.vo.MessageVo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
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
 * @date 2019/08/09
 */
public class CommUtils {

    private static final Gson GSON = new GsonBuilder().create();
    /**
     * 加载配置文件
     * @param fileName 配置文件名称
     * @return
     */
    public static Properties loadProperties(String fileName) {
        Properties properties = new Properties();
        //获取该类的类加载器，把配置文件加载到程序中(stream)
        InputStream resourceAsStream = CommUtils.class.getClassLoader().getResourceAsStream(fileName);
        try {
            properties.load(resourceAsStream);
        } catch (IOException e) {
            return null;
        }

        return properties;
    }


    /**
     * 将任意对象序列化为json 字符串
     * @param obj
     * @return
     */
    public static String object2Json(Object obj) {
        return GSON.toJson(obj);
    }

    /**
     * 将任意json字符串反序列化为对象
     * @param jsonStr json字符串
     * @param objClass 反序列化的类反射对象
     * @return
     */
    public static Object json2Object(String jsonStr,Class objClass) {
        return GSON.fromJson(jsonStr,objClass);
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
