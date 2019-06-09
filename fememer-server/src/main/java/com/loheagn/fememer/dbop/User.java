package com.loheagn.fememer.dbop;

import com.loheagn.fememer.servlets.*;

/**
 * User
 */
public class User {

    private int id;
    private String name;
    private String password;

    /**
     * 根据传入各个属性，构造一个User对象
     * 
     * @param id       user在数据库中的序号，唯一
     * @param name     user在数据库中的用户名，唯一
     * @param password user在数据库中的密码
     */
    public User(int id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 该方法主要是方便调试
     */
    @Override
    public String toString() {
        return "用户ID：" + id + "  用户名：" + name + "    密码：" + password;
    }
}