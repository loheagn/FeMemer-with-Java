package com.loheagn.fememer.dbop;

import java.sql.*;

/**
 * Dbop
 */
public class Dbop {

    private Connection connection;
    private Statement statement;

    /**
     * 连接数据库
     * 
     * @throws SQLException           如果连接数据库出现错误，抛出该异常
     * @throws ClassNotFoundException
     */
    private void ConnectDatabase() throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection(
                "jdbc:sqlite:/Users/loheagn/Code/FeMemer-with-Java/fememer-server/src/main/resources/fememer.db");
        statement = connection.createStatement();
    }

    /**
     * 关闭数据库
     */
    private void CloseDateabase() {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据用户名判断数据库中是否存在该用户
     * 
     * @param name 用户名
     * @return 如果数据库中存在该用户，就返回真，否则，返回假
     */
    public boolean thereIsThisUser(String name) {
        try {
            ConnectDatabase();
            ResultSet resultSet = statement
                    .executeQuery(String.format("select * from userINFO where userName='%s'", name));
            return resultSet.next();
        } catch (NullPointerException nullPointerException) {
            nullPointerException.printStackTrace();
            return false;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return false;
        } catch (ClassNotFoundException classNotFoundException) {
            classNotFoundException.printStackTrace();
            return false;
        } finally {
            CloseDateabase();
        }
    }

    /**
     * 根据传入的用户名返回一个用户对象。 注意，这里的用户名是前面验证过的，所以本方法不用判断用户名是否存在。
     * 
     * @param name 传入的用户名
     * @return 返回一个用户对象，如果对象不存在，那么返回null
     */
    public User getUserByName(String name) {
        try {
            ConnectDatabase();
            User user = null;
            ResultSet resultSet = statement
                    .executeQuery(String.format("select * from userINFO where userName='%s'", name));
            if (resultSet.next()) {
                int id = resultSet.getInt(1);
                String password = resultSet.getString(3);
                user = new User(id, name, password);
            }
            return user;
        } catch (NullPointerException nullPointerException) {
            nullPointerException.printStackTrace();
            return null;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return null;
        } catch (ClassNotFoundException classNotFoundException) {
            classNotFoundException.printStackTrace();
            return null;
        } finally {
            CloseDateabase();
        }
    }

    public static void main(String[] args) {
        System.out.println(new Dbop().getUserByName("15131049"));
    }

}