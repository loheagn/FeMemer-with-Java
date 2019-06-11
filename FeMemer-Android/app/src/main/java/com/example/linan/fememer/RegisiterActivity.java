package com.example.linan.fememer;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xujiaji.happybubble.BubbleDialog;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisiterActivity extends AppCompatActivity {
    private String responseData = "";
    private String name = "";
    private static String passwod = "";
    private EditText email;
    private EditText passwordT;
    private EditText passwordTagain;
    private Boolean canclick1 = false;  //Email是否合法
    private Boolean canclick2 = false;  //密码是否一致
    private Boolean canclick3 = false;  //密码是否是八到二十位字母和数字的组合


    public Boolean getCanclick3() {
        return canclick3;
    }

    public void setCanclick3(Boolean canclick3) {
        this.canclick3 = canclick3;
    }

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
                        if (canclick1 && canclick2 && canclick3) {
                            RegisiterActivity regisiterActivity = RegisiterActivity.this;
                            String email = regisiterActivity.getEmail().getText().toString();
                            String password1 = regisiterActivity.getPasswordT().getText().toString();
                            register(email, password1);
                            System.out.println("dsfsafasfas");
                        } else if (!canclick1)
                            new BubbleDialog(RegisiterActivity.this)
                                    .addContentView(LayoutInflater.from(RegisiterActivity.this).inflate(R.layout.activity_tips, null))
                                    .setLayout(250, 350, 0)
                                    .setClickedView(button2)
                                    .calBar(true)
                                    .show();
                        else if (!canclick2)
                            new BubbleDialog(RegisiterActivity.this)
                                    .addContentView(LayoutInflater.from(RegisiterActivity.this).inflate(R.layout.activity_samepwd, null))
                                    .setLayout(250, 350, 0)
                                    .setClickedView(button2)
                                    .calBar(true)
                                    .show();
                        else if (!canclick3)
                            new BubbleDialog(RegisiterActivity.this)
                                    .addContentView(LayoutInflater.from(RegisiterActivity.this).inflate(R.layout.wrongpwdformat, null))
                                    .setLayout(350, 350, 0)
                                    .setClickedView(button2)
                                    .calBar(true)
                                    .show();
                    }
                }
        );


    }





    private void register(final String email, final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder().add("userName", email).add("password", password).build();
                    String url = Values.rootIP + "/register";
                    Request request = new Request.Builder().url(url).post(requestBody).build();
                    Response response = client.newCall(request).execute();
                    responseData = response.body().string();
                    Gson gson = new Gson();
                    final LoginReaponse loginReaponse = gson.fromJson(responseData, LoginReaponse.class);
                    if (loginReaponse.getFlag().equals("ok")) {
                        SharedPreferences.Editor editor = getSharedPreferences("userINFO", MODE_PRIVATE).edit();
                        editor.putInt("userID", loginReaponse.id);
                        editor.putString("userName", name);
                        editor.putString("userPassword", passwod);
                        editor.apply();
                        Intent intent = new Intent(RegisiterActivity.this, ImportantActivity.class);
                        startActivity(intent);
                    } else if (loginReaponse.getFlag().equals("alreadyhave")) {
                        Looper.prepare();
                        Toast.makeText(RegisiterActivity.this, "已经有这个用户了", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    } else {
                        Looper.prepare();
                        Toast.makeText(RegisiterActivity.this, "错误", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}




class check {
    private RegisiterActivity regisiterActivity;

    check(RegisiterActivity regisiterActivity) {
        this.regisiterActivity = regisiterActivity;
    }

    /**
     * 判断是否符合要求
     */
    public void isright() {
        String email = regisiterActivity.getEmail().getText().toString();
        String password1 = regisiterActivity.getPasswordT().getText().toString();
        String password2 = regisiterActivity.getPasswordTagain().getText().toString();
        if (!isEmail(email)) {
            regisiterActivity.setCanclick1(false);
        } else if (!same(password1, password2) || password1.isEmpty() || password2.isEmpty()) {
            regisiterActivity.setCanclick1(true);
            regisiterActivity.setCanclick2(false);
        } else {
            String check = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z_]{8,20}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(password1);
            regisiterActivity.setCanclick1(true);
            regisiterActivity.setCanclick2(true);
            if (matcher.matches())
                regisiterActivity.setCanclick3(true);
            else
                regisiterActivity.setCanclick3(false);
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
