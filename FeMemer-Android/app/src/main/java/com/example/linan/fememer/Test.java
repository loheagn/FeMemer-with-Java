package com.example.linan.fememer;

import android.util.Patterns;

import java.util.regex.Matcher;

public class Test {
    public static void main(String[] args) {
        try{
            CharSequence s = "版权归作者所有，任何形式转载请联系作者。\n" +
                    "作者：海上的卡妇卡（来自豆瓣）\n" +
                    "来源：https://www.douban.com/doubanapp/dispatch/review/10163255\n" +
                    "\n" +
                    "《大河深处》的豆瓣书评:【人生总是这么痛苦吗？还是只有小时候这样？】“人生总是这么痛苦吗？还是只有小时候这样？”\n" +
                    "\n" +
                    "“总是如此。”\n" +
                    "\n" +
                    "阖上书本，想起这段对话，出自电影\n" +
                    "\n" +
                    " https://www.douban.com/doubanapp/dispatch/review/10163255";
            Matcher matcher = Patterns.WEB_URL.matcher(s);
            if (matcher.find()){
                System.out.println(matcher.group());
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }
}
