package com.example.linan.fememer;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class AddArticleActivity extends AppCompatActivity {

    private int mid;
    private ProgressDialog progressDialog;
    private String content;
    private String responseData;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getResponseData() {
        return responseData;
    }

    public void setResponseData(String responseData) {
        this.responseData = responseData;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_article);
        SharedPreferences sharedPreferences = getSharedPreferences("userINFO", MODE_PRIVATE);
        mid = sharedPreferences.getInt("userID", 1);
        final EditText editText = (EditText) findViewById(R.id.add_edit);
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        /**
         * 是外部分享来的话，获取分享来的url直接添加此文章
         * 否则加载添加文章的样式，输入url点击按钮分享
         * 爬取文章失败的话会有错误提示
         * 输入url不合法的话同样有错误提示
         * 加载文章的时候会有一个遮罩层，且不可退出
         *
         */
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            String content = intent.getStringExtra(Intent.EXTRA_TEXT);
            //System.out.println(sharedurl);
            this.content = content;
            try {
                this.content = this.content.substring(this.content.indexOf("http"));
                content = this.content;
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            editText.setText(content);
            Matcher matcher = Patterns.WEB_URL.matcher(content);
            if (matcher.find()) {
                //System.out.println(content);
                System.out.println();
                System.out.println();
                System.out.println(matcher.group());
                this.content = matcher.group();
                content = this.content;
                progressDialog = new ProgressDialog(AddArticleActivity.this);
                progressDialog.setTitle("文章加载中...");
                progressDialog.setMessage("Loading...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                AddArticleByURLAndId();
            } else
                Toast.makeText(AddArticleActivity.this, "不合法的URL", Toast.LENGTH_SHORT).show();

        } else {
            editText.setText("");
            ImageButton buttonBack = (ImageButton) findViewById(R.id.add_back);
            buttonBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            ImageButton imageButton = (ImageButton) findViewById(R.id.done);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddArticleActivity.this.setContent(editText.getText().toString());
                    Matcher matcher = Patterns.WEB_URL.matcher(AddArticleActivity.this.getContent());
                    if(matcher.find()) {
                        AddArticleActivity.this.setContent(matcher.group());
                        editText.setText("");
                        progressDialog = new ProgressDialog(AddArticleActivity.this);
                        progressDialog.setTitle("文章加载中...");
                        progressDialog.setMessage("Loading...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        AddArticleByURLAndId();
                    }
                     else
                        Toast.makeText(AddArticleActivity.this, "不合法的URL", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * 向服务器发送请求，以此用户的ID以及文章的标题来添加文章
     */



    private void AddArticleByURLAndId() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    OkHttpClient client = new OkHttpClient();
                    content = URLEncoder.encode(content, "UTF-8");
                    RequestBody requestBody = new FormBody.Builder().add("id", String.valueOf(mid)).add("url", content).build();
                    String url = Values.rootIP + "/addarticle";
                    Request request = new Request.Builder().url(url).post(requestBody).build();
                    Response response = client.newCall(request).execute();
                    if (response.body() == null)
                        showresponse2();
                    else {
                        try {
                            responseData = response.body().string();
                            if (responseData.equals("error") || responseData.equals(""))
                                showresponse2();
                            else
                                showresponse();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void showresponse() {
        runOnUiThread(new Runnable() {
            @Override

            public void run() {
                Gson gson = new Gson();
                final ArticleINFO articleINFO = gson.fromJson(responseData, ArticleINFO.class);
                progressDialog.dismiss();
                Intent intent = new Intent(AddArticleActivity.this, ShowArticleActivity.class);
                intent.putExtra("title", articleINFO.getTitle());
                startActivity(intent);

            }
        });
    }

    /**
     * 请求失败的反应
     */

    private void showresponse2() {
        runOnUiThread(new Runnable() {
            @Override

            public void run() {

                progressDialog.dismiss();
                AlertDialog.Builder dialog = new AlertDialog.Builder(AddArticleActivity.this);
                dialog.setTitle("请求失败");
                dialog.setMessage("请检查输入链接的格式和内容，并重试。");
                dialog.setCancelable(true);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();

            }
        });
    }


}
