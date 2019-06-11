package com.example.linan.fememer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * 设置cardview的点击事件以及长按事件
 * 点击cardview的item可以跳转到相应的文章界面
 * 长按item会弹出一个菜单，可以选择删除，更改分类以及分享操作
 *
 */

public class ArticleCardAdapter extends RecyclerView.Adapter<ArticleCardAdapter.ViewHolder> {

    private Context mContext;

    private List<ArticleCard> mArticleList;

    private int mid;

    ArticleINFO articleINFO;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView sourceImage;
        TextView articleTitle;


        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            sourceImage = (ImageView) view.findViewById(R.id.source_img);
            articleTitle = (TextView) view.findViewById(R.id.article_card_title);
        }
    }

    public ArticleCardAdapter(List<ArticleCard> fruitList) {
        mArticleList = fruitList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
            mContext = new ContextThemeWrapper(mContext, R.style.SelectTheme);
        }
        final View view = LayoutInflater.from(mContext).inflate(R.layout.article_card_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                ArticleCard articleCard = mArticleList.get(position);
                Intent intent = new Intent(mContext, ShowArticleActivity.class);
                intent.putExtra("title", articleCard.getTitle());
                mContext.startActivity(intent);
            }
        });

        holder.cardView.setOnLongClickListener(
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int position = holder.getAdapterPosition();
                        ArticleCard articleCard = mArticleList.get(position);
                        showpopmenu(view, articleCard.getTitle(), position);
                        return true;
                    }
                }
        );
        return holder;
    }

    public void showpopmenu(final View view, final String title, final int position) {
        PopupMenu popupMenu = new PopupMenu(mContext, view);
        popupMenu.getMenuInflater().inflate(R.menu.show_article_toobar, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final MenuItem item) {

                Runnable runnable = new getinfo(title);
                switch (item.getItemId()) {
                    case R.id.share:
                        new Thread(runnable).start();
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        String mess = null;
                        try {
                            mess = articleINFO.getTitle() + Values.rootIP + "/" + articleINFO.getLocal_url();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        shareIntent.putExtra(Intent.EXTRA_TEXT, mess);
                        mContext.startActivity(Intent.createChooser(shareIntent, "分享到："));
                        break;
                    case R.id.delete_article:
                        new Thread(runnable).start();
                        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                        dialog.setTitle("提示");
                        dialog.setMessage("确定要在云端删除这篇文章吗？（不可恢复！）");
                        dialog.setCancelable(false);
                        dialog.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new Thread(new deletearticle(title)).start();
                                mArticleList.remove(position);
                                ArticleCardAdapter.this.notifyDataSetChanged();
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
                }
                return true;
            }
        });
        popupMenu.show();
    }


    class getinfo implements Runnable {
        ArticleINFO articleINFO;
        String title;

        getinfo(String title) {
            this.title = title;
        }

        @Override
        public void run() {
            try {
                mid = mContext.getSharedPreferences("userINFO", MODE_PRIVATE).getInt("id", 1);
                OkHttpClient client = new OkHttpClient();
                title = URLEncoder.encode(title,"UTF-8");
                RequestBody requestBody = new FormBody.Builder().add("id", String.valueOf(mid)).add("title", title).build();
                String url = Values.rootIP + "/getarticle";
                Request request = new Request.Builder().url(url).post(requestBody).build();
                Response response = client.newCall(request).execute();
                String responseData = response.body().string();
                Gson gson = new Gson();
                //System.out.println(responseData);
                articleINFO = gson.fromJson(responseData, ArticleINFO.class);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }

        public ArticleINFO getArticleINFO() {
            return articleINFO;
        }
    }

    class deletearticle implements Runnable {
        private String title;
        deletearticle(String title){
            this.title = title;
        }
        @Override
        public void run() {
            try {
                OkHttpClient client = new OkHttpClient();
                title = URLEncoder.encode(title,"UTF-8");
                RequestBody requestBody = new FormBody.Builder().add("id", String.valueOf(mid)).add("title", title).build();
                System.out.println(requestBody);
                String url = Values.rootIP + "/deletearticle";
                Request request = new Request.Builder().url(url).post(requestBody).build();
                Response response = client.newCall(request).execute();
                String responseData = response.body().string();
                //System.out.println("data is" + responseData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ArticleCard articleCard = mArticleList.get(position);
        holder.articleTitle.setText(articleCard.getTitle());
        Glide.with(mContext).load(articleCard.getSource()).into(holder.sourceImage);
    }

    @Override
    public int getItemCount() {
        return mArticleList.size();
    }

}
