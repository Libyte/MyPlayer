package cn.edu.bistu.cs.myplayer;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;

/**
 * 获取音乐url线程类
 */
public class UrlThread implements Runnable{
    private final String songId;
    private String url;

    public UrlThread(String songId) {
        this.songId = songId;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public void run() {
        try{
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().
                    url("http://api.we-chat.cn/song/url?id=" + songId).build();
            Response response = client.newCall(request).execute();
            String responseData = Objects.requireNonNull(response.body()).string();
            SongsUrl songsUrl = JSON.parseObject(responseData, SongsUrl.class);
            List<SongsUrl.Data> dataList = songsUrl.getData();
            url = (String) dataList.get(0).getUrl();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
