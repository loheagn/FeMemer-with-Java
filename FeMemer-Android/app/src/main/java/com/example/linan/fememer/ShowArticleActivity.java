package com.example.linan.fememer;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.net.URLEncoder;

public class ShowArticleActivity extends AppCompatActivity {
    private String title;
    private static int mid ;
    private String responseData;
    private ArticleINFO articleINFO;
    private List<String> tagList = new ArrayList<>();
    private String[] newTagList;
    private String newTag;

    public static int getMid() {
        return mid;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_article);
        tagList.clear();
        SharedPreferences sharedPreferences = getSharedPreferences("userINFO", MODE_PRIVATE);
        mid = sharedPreferences.getInt("id", 1);
        String StrJson = sharedPreferences.getString("tag", null);
        if (StrJson != null) {
            Gson gson = new Gson();
            tagList = gson.fromJson(StrJson, new TypeToken<List<String>>() {
            }.getType());
        }
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        Toolbar toolbar = (Toolbar) findViewById(R.id.ToolbarShowArticle);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        }
        getUrl();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.show_article_toobar, menu);
        return true;
    }

    /**
     * 在菜单中选择删除或者分享或者新加分组
     *
     * @param item
     * @return 操作成功返回真，否则返回假
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.share:
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String mess = articleINFO.getTitle()+Values.rootIP + "/" + articleINFO.getLocal_url();
                shareIntent.putExtra(Intent.EXTRA_TEXT,mess);
                startActivity(Intent.createChooser(shareIntent,"分享到："));
                break;
            case R.id.delete_article:
                AlertDialog.Builder dialog = new AlertDialog.Builder(ShowArticleActivity.this);
                dialog.setTitle("提示");
                dialog.setMessage("确定要在云端删除这篇文章吗？（不可恢复！）");
                dialog.setCancelable(false);
                dialog.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DeleteByTitleAndId();
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();
                //onBackPressed();
                break;

            default:
        }
        return true;
    }

    /**
     * 通过向服务器发送相关请求获取用于描述该文章的一个json对象(不只是url)
     *
     */

    private void getUrl() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    title = URLEncoder.encode(title,"UTF-8");
                    String url = Values.rootIP + "/getarticle";
                    RequestBody requestBody = new FormBody.Builder().add("id", String.valueOf(mid)).add("title", title).build();
                    Request request = new Request.Builder().url(url).post(requestBody).build();
                    Response response = client.newCall(request).execute();
                    responseData = response.body().string();
                    System.out.println("responseData is "+ responseData);
                    Gson gson = new Gson();
                    articleINFO = gson.fromJson(responseData, ArticleINFO.class);
                    showresponse();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 将获取到的相关内容展示到界面上
     *
     */

    private void showresponse() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WebView webView = (WebView) findViewById(R.id.webView);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.setWebViewClient(new WebViewClient());
                String url =Values.rootIP + "/" + articleINFO.getLocal_url();
                webView.loadUrl(url);
                askforTags();
            }
        });
    }


    /**
     * 获取文章的类型
     *
     */

    private void askforTags() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder().add("id", String.valueOf(mid)).build();
                    String url = Values.rootIP + "/getalltags";
                    Request request = new Request.Builder().url(url).post(requestBody).build();
                    Response response = client.newCall(request).execute();
                    responseData = response.body().string();
                    String StrJson = responseData;
                    if (StrJson != null) {
                        Gson gson = new Gson();
                        tagList = gson.fromJson(StrJson, new TypeToken<List<String>>() {
                        }.getType());

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 根据用户存在sharedperferenced的ID以及这个文章的标题来删除文章
     *
     */

    private void DeleteByTitleAndId() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder().add("id", String.valueOf(mid)).add("title", title).build();
                    String url = Values.rootIP + "/deletearticle";
                    Request request = new Request.Builder().url(url).post(requestBody).build();
                    Response response = client.newCall(request).execute();
                    responseData = response.body().string();
                    System.out.println("delete is" + responseData);
                    showresponse2();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private void showresponse2() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(ShowArticleActivity.this,ImportantActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showresponse3() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                articleINFO.setTag(newTag);;
                if(!tagList.contains(newTag))
                    tagList.add(newTag);
            }
        });
    }


}
