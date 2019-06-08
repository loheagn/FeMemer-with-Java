package com.loheagn.fememer.dbop;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

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

    /**
     * 向数据库中插入一个新的用户 本方法不负责校验用户名是否重复
     * 
     * @param name     用户名
     * @param password 用户密码
     * @return
     */
    public int insertUser(String name, String password) {
        try {
            ConnectDatabase();
            statement.execute(String.format(
                    "insert into userINFO (userName, password, signTime) values ('%s','%s', datetime(CURRENT_TIMESTAMP,'localtime'))",
                    name, password));
            ResultSet resultSet = statement
                    .executeQuery(String.format("select * from userINFO where userName='%s'", name));
            return resultSet.getInt(1);
        } catch (NullPointerException nullPointerException) {
            nullPointerException.printStackTrace();
            return -1;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return -1;
        } catch (ClassNotFoundException classNotFoundException) {
            classNotFoundException.printStackTrace();
            return -1;
        } finally {
            CloseDateabase();
        }
    }

    /**
     * 根据传入的信息，向数据库中插入一篇文章的信息
     * 
     * @param id       文章所属用户的id
     * @param source   文章的来源类别
     * @param title    文章的标题
     * @param webUrl   文章的互联网链接
     * @param localUrl 文章在服务器上的本地位置
     * @return 如果向数据库插入成功，那么返回一个文章对象
     */
    public Article insertArticle(int id, String source, String title, String webUrl, String localUrl) {
        String download = "F";
        String tag = "默认";
        long addTime = System.currentTimeMillis() / 1000l;
        try {
            ConnectDatabase();
            statement.execute(String.format(
                    "insert into articleINFO (id, source, title, web_url, local_url, is_downloaded, add_time, tag) values (%d , '%s', '%s', '%s', '%s', '%s', '%s', '%s')",
                    id, source, title, webUrl, localUrl, download, Long.toString(addTime), tag));
            return new Article(id, source, title, webUrl, localUrl, download, addTime, tag);
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

    /**
     * 根据传入的用户id在数据库中删除该用户
     * 
     * @param id 用户id
     * @return 如果删除用户成功，那么返回真，否则返回假
     */
    public boolean deleteUserByID(int id) {
        try {
            ConnectDatabase();
            statement.execute(String.format("delete from userINFO where id=%d", id));
            return true;
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
     * 根据传入的用户id，在数据库中查找所有属于该用户的文章
     * 
     * @param id 传入的用户id
     * @return 返回找到的文章的集合
     */
    public List<Article> selectAllArticlesByID(int id) {
        List<Article> list = new ArrayList<Article>();
        list.clear();
        try {
            ConnectDatabase();
            ResultSet resultSet = statement.executeQuery("select * from articleINFO where id=" + id);
            while (resultSet.next()) {
                list.add(generateArticleFromResault(resultSet));
            }
            return list;
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

    /**
     * 根据传入的用户id，查找相应类别的文章
     * 
     * @param id
     * @param source 查询时指定的文章来源，比如微信、知乎、豆瓣等等
     * @return
     */
    public List<Article> selectArticlesByIDAndSource(int id, String source) {
        List<Article> list = new ArrayList<Article>();
        list.clear();
        try {
            ConnectDatabase();
            ResultSet resultSet = statement
                    .executeQuery("select * from articleINFO where id=" + id + " and source='" + source + "'");
            while (resultSet.next()) {
                list.add(generateArticleFromResault(resultSet));
            }
            return list;
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

    /**
     * 根据从数据库中的查询到的一条记录生成一个文章对象
     * 
     * @param resultSet 从数据库中得到的一条文章信息的记录
     * @return 生成的文章对象
     * @throws SQLException
     */
    private Article generateArticleFromResault(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt(1);
        String source = resultSet.getString(2);
        String title = resultSet.getString(3);
        String webUrl = resultSet.getString(4);
        String localUrl = resultSet.getString(5);
        String downloaded = resultSet.getString(6);
        long addTime = resultSet.getLong(7);
        String tag = resultSet.getString(8);
        return new Article(id, source, title, webUrl, localUrl, downloaded, addTime, tag);
    }

    /**
     * 根据传入的用户id和文章标题，找到相应的文章，并返回 如果出现错误的话，返回null
     * 
     * @param id
     * @param title
     * @return
     */
    public Article getArticleByIDAndTitle(int id, String title) {
        try {
            ConnectDatabase();
            ResultSet resultSet = statement
                    .executeQuery(String.format("select * from articleINFO where id=%d and title='%s'", id, title));
            return generateArticleFromResault(resultSet);
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

    /**
     * 根据传入的用户id和文章标题，删除相应文章，若删除成功，则返回真，否则返回假
     * 
     * @param id
     * @param title
     * @return
     */
    public boolean deleteArticleByIDAndTitle(int id, String title) {
        try {
            ConnectDatabase();
            statement.execute(String.format("delete from articleINFO where id=%d and title='%s'", id, title));
            return true;
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
     * 根据用户id和文章标题，找到文章，然后更新其标签
     * 
     * @param id
     * @param title
     * @param tag   用户自己设置的新标签
     * @return 如果修改成功，返回真，否则返回假
     */
    public boolean updateTagBytitleAndTitle(int id, String title, String tag) {
        try {
            ConnectDatabase();
            statement.execute(
                    String.format("update articleINFO set tag='%s' where id=%d and title='%s'", tag, id, title));
            return true;
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

    public static void main(String[] args) {
        System.out.println(new Dbop().insertArticle(1, "source", "title", "webUrl", "localUrl"));
    }

}