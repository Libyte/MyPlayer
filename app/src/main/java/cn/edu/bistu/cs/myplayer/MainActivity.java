package cn.edu.bistu.cs.myplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private EditText searchName;
    private Button search;
    private ListView songs;
    List<String> songsList = new ArrayList<>();
    List<String> artistList = new ArrayList<>();
    List<String> idList = new ArrayList<>();

    private DownloadService.DownLoadBinder downLoadBinder;
    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downLoadBinder = (DownloadService.DownLoadBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            downLoadBinder = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("MyPlayer");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        searchName.setText("陈奕迅");
        Intent intent = new Intent(MainActivity.this, DownloadService.class);
        startService(intent);
        Log.e("MainActivity", "Service start");
        bindService(intent, connection, BIND_AUTO_CREATE);
        Log.e("MainActivity", "Service bind");

        // 动态获取访问存储权限
        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
        // 开始时即搜索陈奕迅的单曲
        sendRequestSearch("陈奕迅");
    }


    private void init() {
        searchName = findViewById(R.id.search_name);
        search = findViewById(R.id.search);
        songs = findViewById(R.id.show_songs);

        search.setOnClickListener(v -> {
            String singer = searchName.getText().toString();
            Log.e("Search name:", singer);
            sendRequestSearch(singer);
        });
    }

    /**
     * 异步获取歌手单曲信息
     * @param singerName 歌手名
     */
    private void sendRequestSearch(String singerName) {
        new Thread(() ->{
            try{
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("http://api.we-chat.cn/search?keywords="+singerName).build();
                Response response = client.newCall(request).execute();
                String responseData = Objects.requireNonNull(response.body()).string();
                Log.e("Singer's songs:", responseData);
                SearchSongs searchSongs = JSON.parseObject(responseData, SearchSongs.class);
                SearchSongs.Result result = searchSongs.getResult();
                List<SearchSongs.Result.Songs> songs = result.getSongs();

                idList.clear();
                songsList.clear();
                artistList.clear();
                for(SearchSongs.Result.Songs i : songs){
                    idList.add(String.valueOf(i.getId()));
                    songsList.add(i.getName());
                    artistList.add(i.getArtists().get(0).getName());
                }
                showOnView();
            }catch (Exception e){
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 获取对应音乐id的下载url
     * @param id 音乐id
     * @return 该音乐的url
     */
    private String getUrl(String id){
        UrlThread urlThread = new UrlThread(id);
        Thread thread = new Thread(urlThread);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return urlThread.getUrl();
    }

    /**
     * 更新显示
     * 设置ListView点击事件
     * 点击单曲后启动服务进行下载
     * 跳转播放界面
     */
    private void showOnView() {
        runOnUiThread(() -> {
            List<Map<String, String>> items = new ArrayList<>();
            for(int i = 0; i < songsList.size(); i++){
                Map<String, String> map = new HashMap<>();
                map.put("songName", songsList.get(i));
                map.put("artist", artistList.get(i));
                items.add(map);
            }
            SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, items,
                    android.R.layout.simple_list_item_2, new String[]{"songName", "artist"},
                    new int[]{android.R.id.text1, android.R.id.text2});
            songs.setAdapter(adapter);

            songs.setOnItemClickListener((parent, view, position, id) -> {
                String songId = idList.get(position);
                String url = getUrl(songId);

                if(url == null || "".equals(url)){
                    Toast.makeText(this, "Get url failed", Toast.LENGTH_SHORT).show();
                }

                String fileName = songId + ".mp3";
                String songName = songsList.get(position);
                String singer = artistList.get(position);

                if(downLoadBinder == null){
                    Log.e("MainActivity", "bind miss");
                    return;
                }


                downLoadBinder.startDownload(url, fileName);


                Intent intent = new Intent(MainActivity.this, PlayerActivity.class);

                intent.putExtra("url", url);
                intent.putExtra("song_id", songId);
                intent.putExtra("song_name", songName);
                intent.putExtra("file_name", fileName);
                intent.putExtra("singer", singer);
                intent.putExtra("position", position);
                intent.putExtra("songs_list", (Serializable) songsList);
                intent.putExtra("id_list", (Serializable) idList);

                startActivity(intent);
            });
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder backAction = new AlertDialog.Builder(MainActivity.this);
        backAction.setTitle("确定退出My Player？");
        backAction.setPositiveButton("确定", (dialog, which) -> super.onBackPressed());
        backAction.setNegativeButton("取消", null);
        backAction.create().show();
    }
}