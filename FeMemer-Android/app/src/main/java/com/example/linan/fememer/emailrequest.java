package com.example.linan.fememer;
/**
 *  发送邮件是网络请求要放到另一个线程里去
 *
 */

public class emailrequest implements Runnable {
    private String email;
    emailrequest(String email){
        this.email = email;
    }
    @Override
    public void run() {
        try{
            SendMailUtil.sendemail(email);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
