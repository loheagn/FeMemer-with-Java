package com.example.linan.fememer;
/**
 *  发送邮件是网络请求要放到另一个线程里去
 *
 */

public class emailrequest implements Runnable {
    private String email;
    private String newpwd;
    emailrequest(String email, String newpwd){
        this.email = email;
        this.newpwd = newpwd;
    }
    @Override
    public void run() {
        try{
            SendMailUtil.sendemail(email,newpwd);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
