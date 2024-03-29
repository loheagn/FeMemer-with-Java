package com.example.linan.fememer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ImportantActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private List<ArticleCard> articleCardList = new ArrayList<>();
    private ArticleCardAdapter adapter;
    private int userID;
    private String responseData;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String source;
    private String searchData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_important);
        //resleshArticles();
        source = "all";
        SharedPreferences sharedPreferences = getSharedPreferences("userINFO", MODE_PRIVATE);
        userID = sharedPreferences.getInt("userID", 0);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.Toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        }
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.help);
        /**
         * 设置导航界面的功能
         * 可以查看软件帮助
         * 可以注销登录，退回登录界面
         *
         */
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.help:
                        Intent intent = new Intent(ImportantActivity.this, HelpActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.account:
                        AlertDialog.Builder dia = new AlertDialog.Builder(ImportantActivity.this);
                        dia.setCancelable(true);
                        dia.setTitle("确定要注销账户吗？");
                        dia.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences.Editor editor = getSharedPreferences("userINFO", MODE_PRIVATE).edit();
                                editor.putInt("userID", 0);
                                editor.putString("userName", "");
                                editor.putString("userPassword", "");
                                editor.apply();
                                Intent intent = new Intent(ImportantActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }
                        });
                        dia.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        dia.show();
                        break;
                     case R.id.change_pwd:
                         LayoutInflater inflater = getLayoutInflater();
                         final View layout = inflater.inflate(R.layout.change_ac_pwd,(ViewGroup) findViewById(R.id.dialog));
                         new AlertDialog.Builder(ImportantActivity.this).setView(layout).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialog, int which) {

                                 EditText oldpwd = (EditText) layout.findViewById(R.id.old_pwd);
                                 EditText newpwd1 = (EditText) layout.findViewById(R.id.new_pwd1);
                                 EditText newpwd2 = (EditText) layout.findViewById(R.id.new_pwd2);
                                 System.out.println(oldpwd.getText().toString());
                                 String erromsg = errorMessage(oldpwd.getText().toString(),newpwd1.getText().toString(),newpwd2.getText().toString());
                                 if(erromsg.equals("成功修改")){
                                     SharedPreferences sharedPreferences = getSharedPreferences("userINFO",MODE_PRIVATE);
                                     String name = sharedPreferences.getString("userName","");
                                     String password = sharedPreferences.getString("userPassword","");
                                     findpasswordback(name,newpwd1.getText().toString());
                                     SharedPreferences.Editor editor = getSharedPreferences("userINFO", MODE_PRIVATE).edit();
                                     editor.putInt("userID", 0);
                                     editor.putString("userName", "");
                                     editor.putString("userPassword", "");
                                     editor.apply();
                                     Intent intent = new Intent(ImportantActivity.this, LoginActivity.class);
                                     startActivity(intent);
                                 }
                                 else{
                                     Toast.makeText(ImportantActivity.this,erromsg,Toast.LENGTH_SHORT).show();
                                 }

                             }
                         }).setNegativeButton("取消",null).show();



                }
                return true;
            }
        });



        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.add);
        /**
         * 悬浮的加好按钮，点击可以前往添加文章页面
         *
         */
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ImportantActivity.this, AddArticleActivity.class);
                startActivity(intent);
            }
        });
        articleCardList.clear();
        getArticleListFromWed("all", 0);

    }

    private String errorMessage(String oldpwd, String newpwd1, String newpwd2){
        SharedPreferences sharedPreferences = getSharedPreferences("userINFO",MODE_PRIVATE);
        String password = sharedPreferences.getString("userPassword","");
        System.out.println(password);
        if(!oldpwd.equals(password))
            return "旧密码错误";
        else if(!newpwd1.equals(newpwd2))
            return "两次密码不一致";
        else{
            String check = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z_]{8,20}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(newpwd1);
            if(matcher.matches())
                return "成功修改";
            else
                return "密码不符合格式要求，要是8-20位的数字或字母";
        }
    }

    private void findpasswordback(final String email, final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder().add("name", email).add("password", password).build();
                    String url = Values.rootIP + "/updateuser";
                    Request request = new Request.Builder().url(url).post(requestBody).build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    if(responseData.equals("ok"))
                        Toast.makeText(ImportantActivity.this,"发送成功，请耐心等待",Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(ImportantActivity.this,"发送失败",Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void onResume() {
        super.onResume();
        articleCardList.clear();
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                getArticleListFromWed(source, 0);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    /**
     * 对已收藏的文章进行检索，可通过多个关键字搜索
     * 也可以通过预设的几个标签点击进行过滤
     *
     * @param item 菜单条目
     * @return 操作成功则返回真，否则返回假
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.all:
                source = "all";
                resleshArticlesMenu();
                break;

            case R.id.weixin:
                source = "weixin";
                resleshArticlesMenu();
                break;
            case R.id.search:
                AlertDialog.Builder dialog = new AlertDialog.Builder(ImportantActivity.this);
                final EditText editText = new EditText(ImportantActivity.this);
                dialog.setTitle("请输入关键词（空格隔开）");
                dialog.setView(editText);
                dialog.setCancelable(true);
                dialog.setPositiveButton("搜索", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String tmp = editText.getText().toString();
                        if (tmp != null && !tmp.equals("")) {
                            searchData = tmp;
                            SearchAndRefresh();
                        }
                    }
                });
                dialog.show();
                break;
            case R.id.zhihu:
                source = "zhihu";
                resleshArticlesMenu();
                break;
            case R.id.weibo:
                source = "weibo";
                resleshArticlesMenu();
                break;
            case R.id.douban:
                source = "douban";
                resleshArticlesMenu();
                break;
            case R.id.toutiao:
                source = "toutiao";
                resleshArticlesMenu();
                break;
            case R.id.bili:
                source = "bizhan";
                resleshArticlesMenu();
                break;
            case R.id.fenghuang:
                source = "fenghuang";
                resleshArticlesMenu();
                break;

            default:
        }
        return true;
    }

    /**
     * 获取所有的文章资源
     *
     * @param source 文章资源号
     * @param flag   标志位
     */

    private void getArticleListFromWed(final String source, final int flag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder().add("id", String.valueOf(userID)).add("source", source).build();
                    String url = Values.rootIP + "/selectarticle";
                    Request request = new Request.Builder().url(url).post(requestBody).build();
                    Response response = client.newCall(request).execute();
                    responseData = response.body().string();
                    initArcticleList();
                    if (flag == 0) {
                        CreateRecyclerview();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 初始化所有文章对象的列表，根据不同的文章tag配置不同的logo
     */

    private void initArcticleList() {
        articleCardList.clear();
        Gson gson = new Gson();
        List<ArticleINFO> articleList = gson.fromJson(responseData, new TypeToken<List<ArticleINFO>>() {
        }.getType());
        for (ArticleINFO articleINFO : articleList) {
            int image_src;
            if (articleINFO.getSource().equals("weixin")) {
                image_src = R.drawable.ic_wechat;
            } else if (articleINFO.getSource().equals("weibo")) {
                image_src = R.drawable.ic_weibo;
            } else if (articleINFO.getSource().equals("zhihu")) {
                image_src = R.drawable.ic_zhihu;
            } else if (articleINFO.getSource().equals("douban")) {
                image_src = R.drawable.ic_douban;
            } else if (articleINFO.getSource().equals("toutiao")) {
                image_src = R.drawable.ic_toutiao;
            } else if (articleINFO.getSource().equals("bili")) {
                image_src = R.drawable.ic_bizhan;
            } else {
                image_src = R.drawable.ic_fenghuang;
            }
            articleCardList.add(new ArticleCard(image_src, articleINFO.getTitle()));
        }
    }

    /**
     * 创建列表控件的实例，并设置它的样式，并且定义刷新操作
     */

    private void CreateRecyclerview() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
                GridLayoutManager layoutManager = new GridLayoutManager(ImportantActivity.this, 1);
                recyclerView.setLayoutManager(layoutManager);
                adapter = new ArticleCardAdapter(articleCardList);
                recyclerView.setAdapter(adapter);
                swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
                swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
                swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        resleshArticles();
                    }
                });
            }
        });


    }

    /**
     * 刷新文章列表
     */

    private void resleshArticles() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder().add("id", String.valueOf(userID)).add("source", source).build();
                    String url = Values.rootIP + "/selectarticle";
                    Request request = new Request.Builder().url(url).post(requestBody).build();
                    Response response = client.newCall(request).execute();
                    responseData = response.body().string();
                    initArcticleList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    private void resleshArticlesMenu() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder().add("id", String.valueOf(userID)).add("source", source).build();
                    String url = Values.rootIP + "/selectarticle";
                    Request request = new Request.Builder().url(url).post(requestBody).build();
                    Response response = client.newCall(request).execute();
                    responseData = response.body().string();
                    initArcticleList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    /**
     * 搜索，返回后刷新列表
     */

    private void SearchAndRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder().add("id", String.valueOf(userID)).add("searchData", URLEncoder.encode(searchData, "UTF-8")).build();
                    String url = Values.rootIP + "/searcharticle";
                    Request request = new Request.Builder().url(url).post(requestBody).build();
                    Response response = client.newCall(request).execute();
                    responseData = response.body().string();
                    initArcticleList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }


    private long clickTime;

//    @Override
//    public void onBackPressed() {
//        exit();
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 是否触发按键为back键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        } else { // 如果不是back键正常响应
            return super.onKeyDown(keyCode, event);
        }
    }

//    private void exit() {
//        if ((System.currentTimeMillis() - clickTime) > 2000) {
//            Toast.makeText(this, "再按一次后退键退出程序", Toast.LENGTH_SHORT).show();
//            clickTime = System.currentTimeMillis();
//        } else {
//            this.finish();
//        }
//    }
}