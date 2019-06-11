package com.example.linan.fememer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        resleshArticlesMenu();
    }

    /**
     * 如果说用户已经登录过，则跳过登录界面直接进入主界面
     * 否则，要求用户输入账号密码验证
     * 进入具体活动前会展示一个过场动画，时长两秒
     *
     */
    private void resleshArticlesMenu() {
        new Thread(new Runnable() {
            @Override
            public void run() {
               try{
                   Thread.sleep(2000);
               }catch (Exception e)
               {
                  e.printStackTrace();
             }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            SharedPreferences sharedPreferences = getSharedPreferences("userINFO",MODE_PRIVATE);
                            String name = sharedPreferences.getString("userName","");
                            String password = sharedPreferences.getString("userPassword","");
                            if(!name.equals("") && !password.equals(""))
                            {
                                Intent intent = new Intent(MainActivity.this,ImportantActivity.class);
                                intent.putExtra("userName",name);
                                intent.putExtra("userPassword",password);
                                startActivity(intent);
                            }
                            else {
                                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                                startActivity(intent);
                            }
                        }
                        catch (Error e)
                        {
                            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                            startActivity(intent);
                        }
                    }
                });
            }
        }).start();
    }


}
