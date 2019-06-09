package com.example.linan.fememer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private String responseData = "";
    private String name = "";
    private String passwod = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        Button button1 = (Button) findViewById(R.id.buttonLogin);
        Button button2 = (Button) findViewById(R.id.buttonRegister);
        final EditText userName = (EditText) findViewById(R.id.editTextUserName);
        final EditText password = (EditText) findViewById(R.id.editTextUserPassword);
        TextView forgetpwd = (TextView) findViewById(R.id.forgetpwd);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    name = userName.getText().toString();
                    passwod = password.getText().toString();
                    if (name != null && passwod != null && !name.equals("") && !passwod.equals("")) {
                        yanzhengdenglu();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisiterActivity.class);
                startActivity(intent);
            }
        });

        forgetpwd.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(LoginActivity.this);
                        final EditText editText = new EditText(LoginActivity.this);
                        dialog.setTitle("请输入您绑定的邮箱");
                        dialog.setView(editText);
                        dialog.setCancelable(true);
                        dialog.setPositiveButton("发送", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String email = editText.getText().toString();
                                try{
                                    if(check.isEmail(email)){
                                        new Thread(new emailrequest(email)).start();
                                        Toast.makeText(LoginActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                        Toast.makeText(LoginActivity.this, "错误的邮箱格式", Toast.LENGTH_SHORT).show();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                            }
                        });
                        dialog.show();
                    }
                }
        );
    }

    /**
     * 像服务器发送请求，验证账号密码是否是对的
     *
     */

    private void yanzhengdenglu() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder().add("userName", name).add("password", passwod).build();
                    Request request = new Request.Builder().url(Values.rootIP + "/login").addHeader("Connection", "close").post(requestBody).build();
                    Response response = client.newCall(request).execute();
                    responseData = response.body().string();
                    showresponse();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 对于之前发送的登录请求，如果说验证通过，跳转到主界面，否则提示错误信息
     *
     */

    private void showresponse() {
        runOnUiThread(new Runnable() {
            @Override

            public void run() {
                Gson gson = new Gson();
                final LoginReaponse loginReaponse = gson.fromJson(responseData, LoginReaponse.class);
                if (loginReaponse.getFlag().equals("OK")) {
                    SharedPreferences.Editor editor = getSharedPreferences("userINFO", MODE_PRIVATE).edit();
                    editor.putInt("userID", loginReaponse.id);
                    editor.putString("userName", name);
                    editor.putString("userPassword", passwod);
                    editor.apply();
                    Intent intent = new Intent(LoginActivity.this, ImportantActivity.class);
                    startActivity(intent);
                } else if (loginReaponse.getFlag().equals("false")) {
                    Toast.makeText(LoginActivity.this, "密码错误，请重试！", Toast.LENGTH_LONG);
                } else if (loginReaponse.getFlag().equals("new")) {
                    //弹出对话提示框，询问是否新建一个账号
                    AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
                    dialog.setTitle("提示");
                    dialog.setMessage("确定要新注册一个账号吗？");
                    dialog.setCancelable(true);
                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences.Editor editor = getSharedPreferences("userINFO", MODE_PRIVATE).edit();
                            editor.putInt("userID", loginReaponse.id);
                            editor.putString("userName", name);
                            editor.putString("userPassword", passwod);
                            editor.apply();
                            Intent intent = new Intent(LoginActivity.this, ImportantActivity.class);
                            startActivity(intent);
                        }
                    });
                    dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteUser(String.valueOf(loginReaponse.id));
                        }
                    });
                    dialog.show();
                } else {
                    Toast.makeText(LoginActivity.this, "请求失败，请重试！", Toast.LENGTH_LONG);
                }
            }
        });
    }


    private void deleteUser(final String mid) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder().add("id", mid).build();
                    Request request = new Request.Builder().url(Values.rootIP + "/deleteuser").post(requestBody).build();
                    Response response = client.newCall(request).execute();
                    responseData = response.body().string();
                    ClearTextView();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 清空所有输入框
     *
     */
    private void ClearTextView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                EditText editText1 = (EditText) findViewById(R.id.editTextUserPassword);
                editText1.setText("");
                EditText editText = (EditText) findViewById(R.id.editTextUserName);
                editText.setText("");
                editText.setFocusable(true);
                editText.setFocusableInTouchMode(true);
                editText.requestFocus();
                InputMethodManager inputMethodManager = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(editText, 0);
            }
        });


    }


}
