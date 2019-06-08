package com.example.linan.fememer;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xujiaji.happybubble.BubbleDialog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisiterActivity extends AppCompatActivity {
    private String responseData = "";
    private String name = "";
    private static String passwod = "";
    private EditText email;
    private EditText passwordT;
    private EditText passwordTagain;
    private Boolean canclick1 = false;
    private Boolean canclick2 = false;

    public Boolean getCanclick2() {
        return canclick2;
    }

    public void setCanclick2(Boolean canclick2) {
        this.canclick2 = canclick2;
    }

    public Boolean getCanclick1() {
        return canclick1;
    }

    public void setCanclick1(Boolean canclick) {
        this.canclick1 = canclick;
    }

    public EditText getEmail() {
        return email;
    }

    public void setEmail(EditText email) {
        this.email = email;
    }

    public EditText getPasswordT() {
        return passwordT;
    }

    public void setPasswordT(EditText passwordT) {
        this.passwordT = passwordT;
    }

    public EditText getPasswordTagain() {
        return passwordTagain;
    }


    public void setPasswordTagain(EditText passwordTagain) {
        this.passwordTagain = passwordTagain;
    }

    /**
     * 初始化注册的各组件，需要输入您的email（必须符合格式要求，不然不予注册）
     * 您想设定的密码并且再输入一次，确保两次输入的密码一致才可以注册
     * 有两个按钮，一个可以返回登录界面，另一个可以提交注册信息
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regisiter);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        email = (EditText) findViewById(R.id.UsernameReg);
        passwordT = (EditText) findViewById(R.id.PasswordReg);
        passwordTagain = (EditText) findViewById(R.id.PasswordRegAgain);
        Button button1 = (Button) findViewById(R.id.BackToLogin);
        final Button button2 = (Button) findViewById(R.id.submit);
        TextView forgetpwd = (TextView) findViewById(R.id.forgetpwd);
        button1.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(RegisiterActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                }
        );


        button2.setOnClickListener(
                new View.OnClickListener() {
                    /**
                     * 检查信息的合法性，canclick1代表email合法，canclick2代表两次输入密码是否一致
                     * 信息合法性校验通过后才能注册
                     *
                     * @param v 当前视图
                     */
                    @Override
                    public void onClick(View v) {
                        new check(RegisiterActivity.this).isright();
                        if (canclick1 && canclick2)
                            Toast.makeText(RegisiterActivity.this, "点击成功", Toast.LENGTH_SHORT).show();
                        else if (canclick1 && !canclick2)
                            new BubbleDialog(RegisiterActivity.this)
                                    .addContentView(LayoutInflater.from(RegisiterActivity.this).inflate(R.layout.activity_tips, null))
                                    .setLayout(250, 220, 0)
                                    .setClickedView(button2)
                                    .calBar(true)
                                    .show();
                        else if (!canclick1 && !canclick2)
                            new BubbleDialog(RegisiterActivity.this)
                                    .addContentView(LayoutInflater.from(RegisiterActivity.this).inflate(R.layout.activity_samepwd, null))
                                    .setLayout(250, 220, 0)
                                    .setClickedView(button2)
                                    .calBar(true)
                                    .show();
                    }
                }
        );

        forgetpwd.setOnClickListener(
                new View.OnClickListener() {
                    /**
                     * 找回密码的函数，输入您注册时绑定的邮箱，系统会自动生成一个随机密码到此邮箱作为新密码
                     *
                     * @param v 当前视图
                     */
                    @Override
                    public void onClick(View v) {
                        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(RegisiterActivity.this);
                        final EditText editText = new EditText(RegisiterActivity.this);
                        dialog.setTitle("请输入您绑定的邮箱");
                        dialog.setView(editText);
                        dialog.setCancelable(true);
                        dialog.setPositiveButton("发送", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String email = editText.getText().toString();
                                try {
                                    if (check.isEmail(email)) {
                                        new Thread(new emailrequest(email)).start();
                                        Toast.makeText(RegisiterActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                                    } else
                                        Toast.makeText(RegisiterActivity.this, "错误的邮箱格式", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        dialog.show();
                    }
                }
        );
    }

}


class check {
    private RegisiterActivity regisiterActivity;

    check(RegisiterActivity regisiterActivity) {
        this.regisiterActivity = regisiterActivity;
    }

    /**
     * 判断是否符合要求
     *
     */
    public void isright() {
        String email = regisiterActivity.getEmail().getText().toString();
        String password1 = regisiterActivity.getPasswordT().getText().toString();
        String password2 = regisiterActivity.getPasswordTagain().getText().toString();
        if (!isEmail(email)) {
            regisiterActivity.setCanclick1(false);
        } else if (!same(password1, password2) || password1.isEmpty() || password2.isEmpty()) {
            regisiterActivity.setCanclick2(false);
        } else {
            regisiterActivity.setCanclick1(true);
            regisiterActivity.setCanclick2(true);
        }


    }

    /**
     * 使用正则表达式判断是否符合Email的标准
     *
     * @param string 传入要判断的字符串
     * @return 如果符合正则表达式，返回真，否则返回假
     */

    public static boolean isEmail(String string) {
        if (string == null)
            return false;
        String regEx1 = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern p;
        Matcher m;
        p = Pattern.compile(regEx1);
        m = p.matcher(string);
        if (m.matches())
            return true;
        else
            return false;
    }

    /**
     * 判断两个密码是否相等
     *
     * @param pwd1 密码一
     * @param pwd2 密码二
     * @return 如果两个密码相等，返回真，否则返回假
     */

    public static boolean same(String pwd1, String pwd2) {
        return pwd1.equals(pwd2);
    }

}
