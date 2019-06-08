package com.example.linan.fememer;

import java.util.Date;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;


public class SendMailUtil {

    //发件人地址
    public static String senderAddress = "18210714886@163.com";
    //收件人地址
    public static String recipientAddress;
    //发件人账户名
    public static String senderAccount = "18210714886@163.com";
    //发件人账户密码
    public static String senderPassword = "liuziming";

    /**
     * 首先配置连接邮件服务器的参数配置
     * 之后设置用户的认证方式
     * 再之后设置传输协议为smtp
     * 然后设置发件人的SMTP服务器地址，这里我们用的是163邮箱
     * 然后创建定义整个应用程序所需的环境信息的 Session 对象设置调试信息在控制台打印出来
     * 之后创建邮件的实例对象，并根据之前创建的session对象获取邮件传输对象的Transport
     * 之后根据传入的参数设置发件人的账户名和密码，发送邮件后关闭连接，程序结束
     *
     * @param to 要发送到的邮箱地址
     * @throws Exception
     */

    public static void sendemail(String to) throws Exception {
        recipientAddress = to;
        Properties props = new Properties();
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.host", "smtp.163.com");
        Session session = Session.getInstance(props);
        session.setDebug(true);
        Message msg = getMimeMessage(session);
        Transport transport = session.getTransport();
        transport.connect(senderAccount, senderPassword);
        transport.sendMessage(msg,msg.getAllRecipients());
        transport.close();
    }

    /**
     * 获得创建一封邮件的实例对象
     * 将设置发件人的地址，邮件主题，发送时间等信息
     *
     * @param session 整个应用程序所需的环境信息的 Session 对象
     * @return
     * @throws MessagingException 信息传输异常
     * @throws AddressException 地址异常
     */
    public static MimeMessage getMimeMessage(Session session) throws Exception{
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(senderAddress));
        msg.setRecipient(MimeMessage.RecipientType.TO,new InternetAddress(recipientAddress));
        msg.setSubject("密码找回","UTF-8");
        msg.setContent("这是您的新密码，这次别忘啦" + getrandompwd(8), "text/html;charset=UTF-8");
        msg.setSentDate(new Date());
        return msg;
    }

    /**
     * 得到一个指定长度的随机密码
     *
     * @param length 输入希望得到随机密码的位数
     * @return 返回生成的随机密码
     */

    public static String getrandompwd(int length) {
        char[] ss = new char[length];
        int i=0;
        while(i<length) {
            int f = (int) (Math.random()*3);
            if(f==0)
                ss[i] = (char) ('A'+Math.random()*26);
            else if(f==1)
                ss[i] = (char) ('a'+Math.random()*26);
            else
                ss[i] = (char) ('0'+Math.random()*10);
            i++;
        }
        String str=new String(ss);
        return str;
    }

    public static void main(String[] args) {
        try {
            sendemail("liuziming@buaa.edu.cn");
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}