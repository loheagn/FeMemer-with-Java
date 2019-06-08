package com.example.linan.fememer;

/**
 *获取登录账户的相关信息，将这些信息封装成一个类
 *
 */

public class LoginReaponse {

    private String flag;
    int id;

    public LoginReaponse(String flag, int id) {
        this.flag = flag;
        this.id = id;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
