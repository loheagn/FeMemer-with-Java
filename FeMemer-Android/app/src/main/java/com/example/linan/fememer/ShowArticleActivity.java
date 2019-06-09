package com.example.linan.fememer;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.support.v7.widget.Toolbar;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;

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
            case R.id.down_load:
                if (articleINFO == null)
                    break;
                String message;
                message = "当前文章分组为" + articleINFO.getTag();
                AlertDialog.Builder dialogg = new AlertDialog.Builder(ShowArticleActivity.this);
                dialogg.setCancelable(true);
                dialogg.setTitle(message);
                dialogg.setMessage("要修改或增加新的分组吗？");
                dialogg.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder dialog4 = new AlertDialog.Builder(ShowArticleActivity.this);
                        dialog4.setTitle("选择分组");
                        dialog4.setCancelable(true);
                        newTagList = new String[tagList.size()+1];
                        newTagList[0] = "默认";
                        for (int i = 0; i < tagList.size(); i++) {
                            newTagList[i+1] = tagList.get(i);
                        }
                        dialog4.setSingleChoiceItems(newTagList, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                newTag = newTagList[which];
                            }
                        });
                        dialog4.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (newTag != null && tagList.contains(newTag)) {
                                    updateTagBytitleAndTitle();
                                } else {
                                    Toast.makeText(ShowArticleActivity.this, "请选择或新建分组", Toast.LENGTH_SHORT);
                                }
                            }
                        });
                        dialog4.setNegativeButton("+新建分组", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final EditText editText = new EditText(ShowArticleActivity.this);
                                final AlertDialog.Builder dialog3 = new AlertDialog.Builder(ShowArticleActivity.this);
                                dialog3.setTitle("请输入新的分组名");
                                dialog3.setView(editText);
                                dialog3.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String tmp = editText.getText().toString();
                                        if (tmp != null && tmp != "")
                                            newTag = tmp;
                                        updateTagBytitleAndTitle();
                                    }
                                });
                                dialog3.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                dialog3.show();
                            }
                        });
                        dialog4.show();
                    }
                });
                dialogg.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialogg.show();
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
                    RequestBody requestBody = new FormBody.Builder().add("id", String.valueOf(mid)).add("title", title).build();
                    Request request = new Request.Builder().url(Valuse.rootIP + "/getarticle").post(requestBody).build();
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
                webView.loadUrl(Valuse.rootIP + "/" + articleINFO.getLocal_url());
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
                    Request request = new Request.Builder().url(Values.rootIP + "/getalltags").post(requestBody).build();
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
                    Request request = new Request.Builder().url(Values.rootIP + "/deletearticle").post(requestBody).build();
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
                onBackPressed();
            }
        });
    }

    /**
     * 根据用户存在sharedperferenced的ID以及这个文章的标题来更新文章所属的类别
     *
     */

    private void updateTagBytitleAndTitle() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder().add("id", String.valueOf(mid)).add("title", title).add("tag", newTag).build();
                    Request request = new Request.Builder().url("http://39.96.166.183/cgi-bin/dbop.py").post(requestBody).build();
                    Response response = client.newCall(request).execute();
                    responseData = response.body().string();
                    showresponse3();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
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
