package cn.edu.bistu.cs.myplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;

import android.os.IBinder;

import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PlayerActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView songNameTextView, songSingerTextView, songBeginTextView, songEndTextView;
    private Button play, previous, next, download;

    private String songName;
    private String songUrl;
    private String singer;
    private String fileName;
    private SeekBar progress;
    private int position;
    List<String> idList = new ArrayList<>();
    List<String> songsList = new ArrayList<>();
    private final MediaPlayer mediaPlayer = new MediaPlayer();
    private TimerTask timerTask;
    private boolean isChanged = false;
    private boolean isBegin;

    private long currentPosition = 0;
    private MsgReceiver msgReceiver;
    // 本地广播接收器
    public class MsgReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            play.callOnClick();
        }
    }
    private DownloadService.DownLoadBinder downLoadBinder;
    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downLoadBinder = (DownloadService.DownLoadBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

//    private final Handler handler = new Handler(Looper.getMainLooper()){
//        @Override
//        public void handleMessage(@NonNull Message msg) {
//            if (msg.what == 1) {
//                Log.e("_____________", "______________");
//                play.callOnClick();
//            }
//            super.handleMessage(msg);
//        }
//    };
//    {
//        @SuppressLint("HandlerLeak")
//        @Override
//        public void handleMessage(@NonNull Message msg) {
//            switch(msg.what){
//                case 1:
//                    play.callOnClick();
//                    break;
//                default:
//                    break;
//            }
//            super.handleMessage(msg);
//        }
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        init();
        initAction();
        initView();

        // 绑定服务
        Intent intent = new Intent(PlayerActivity.this, DownloadService.class);
        startService(intent);
        bindService(intent, connection, BIND_AUTO_CREATE);

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
        // 检查权限
        if(ContextCompat.checkSelfPermission(PlayerActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(PlayerActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        else if(file.exists()){
            initMediaPlayer();
        }

        // 时间任务，对进度条进行更新
        Timer timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                // 如果正在修改，则不做修改，否则根据播放进度同步更新
                if(isChanged){
                    return;
                }
                currentPosition = mediaPlayer.getCurrentPosition();
                progress.setProgress(mediaPlayer.getCurrentPosition());
                showCurrentTime();
            }
        };
        timer.schedule(timerTask, 0, 10);
    }



    private void init() {
        songNameTextView = findViewById(R.id.song_name);
        songSingerTextView = findViewById(R.id.singer_name);
        songBeginTextView = findViewById(R.id.begin_time);
        songEndTextView = findViewById(R.id.end_time);
        progress = findViewById(R.id.seekbar);
        play = findViewById(R.id.start);
        previous = findViewById(R.id.before);
        next = findViewById(R.id.next);
        download = findViewById(R.id.download);

        Intent intent = getIntent();
        songUrl = intent.getStringExtra("url");
        songName = intent.getStringExtra("song_name");
        fileName = intent.getStringExtra("file_name");
        singer = intent.getStringExtra("singer");
        position = intent.getIntExtra("position", 0);
        songsList = intent.getStringArrayListExtra("songs_list");
        idList = intent.getStringArrayListExtra("id_list");
    }

    /**
     * 监听时间初始化
     * 播放器循环设置
     * 广播接受器
     * 进度条监听
     */
    private void initAction() {
        play.setOnClickListener(this);
        previous.setOnClickListener(this);
        next.setOnClickListener(this);
        download.setOnClickListener(this);
        // 设置循环播放
        mediaPlayer.setOnCompletionListener(mp -> {
            if(!mediaPlayer.isPlaying() && !isBegin){
//                next.callOnClick();
                play.callOnClick();
            }
        });
        // 本地广播
        msgReceiver = new MsgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("cn.edu.bistu.cs.DOWNLOAD_FINISHED");
        registerReceiver(msgReceiver, intentFilter);
        // 进度条监听
        progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // 表示正在拖动进度条，音乐继续播放，不对开始时间进行更新
                isChanged = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // 拖动停止时，将音乐便宜到对应位置，对开始是加紧进行更新
                mediaPlayer.seekTo(seekBar.getProgress());
                isChanged = false;
            }
        });
    }

    /**
     * 界面显示初始化
     */
    @SuppressLint("SetTextI18n")
    private void initView() {
        runOnUiThread(() -> {
            songNameTextView.setText(songName);
            songSingerTextView.setText(singer);
            songBeginTextView.setText("00:00");
            songEndTextView.setText("00:00");
        });
    }




    @Override
    public void onClick(View v) {
        if(downLoadBinder == null){
            Toast.makeText(this, "Bind failed!", Toast.LENGTH_SHORT).show();
            return;
        }
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),fileName);
        switch(v.getId()){
            case(R.id.start):
                if(file.exists()){
                    if(!mediaPlayer.isPlaying()){
                        if(isBegin){
                            initMediaPlayer();
                            isBegin = false;
                        }
                        mediaPlayer.start();
                        setPlayButton("暂停播放");

                    }
                    else{
                        mediaPlayer.pause();
                        setPlayButton("开始播放");
                    }
                }
                else{
                    Toast.makeText(this, "This song is not downloaded!",Toast.LENGTH_SHORT).show();
                }
                break;

            case(R.id.before):
                isBegin = true;
                mediaPlayer.reset();
                play.setText("开始播放");
                position = (position - 1 + songsList.size()) % songsList.size();
                songName = songsList.get(position);
                try {
                    songUrl = getUrl(idList.get(position));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
                fileName = idList.get(position) + ".mp3";
                initView();
                initMediaPlayer();
                play.callOnClick();
                break;

            case(R.id.next):
                isBegin = true;
                mediaPlayer.reset();
                play.setText("开始播放");
                position = (position + 1 + songsList.size()) % songsList.size();
                songName = songsList.get(position);
                try {
                    songUrl = getUrl(idList.get(position));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
                fileName = idList.get(position) + ".mp3";
                initView();
                initMediaPlayer();
                play.callOnClick();
                break;

            case(R.id.download):
                // 点击下载按钮选项，如果已有文件，调用服务开始下载，否则提示提示已经下载完
                if(!file.exists()){
                    downLoadBinder.startDownload(songUrl, fileName);
                }
                else{
                    Toast.makeText(PlayerActivity.this, "This song is already existed!",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 初始化MediaPlayer
     */
    private void initMediaPlayer()  {
        mediaPlayer.reset();
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),fileName);
            if(!file.exists()){
                return;
            }
            // 设置音频文件
            mediaPlayer.setDataSource(file.getPath());
            //AssetFileDescriptor afd = getAssets().openFd(file.getPath());
            //mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            // MediaPlayer进入准备状态
            mediaPlayer.prepare();
            // 设置进度条的最大值
            progress.setMax(mediaPlayer.getDuration());
            // 设置最大时间
            songEndTextView.setText(formatTime(mediaPlayer.getDuration()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新播放按钮
     * @param string 更新的字符串
     */
    private void setPlayButton(String string) {
        runOnUiThread(() -> play.setText(string));
    }

    /**
     * 获取下载歌曲的url
     * @param songId 要下载的歌曲id
     * @return 获取的字符串
     * @throws InterruptedException 中断异常
     */
    private String getUrl(String songId) throws InterruptedException {
        UrlThread urlThread = new UrlThread(songId);
        Thread thread = new Thread(urlThread);
        thread.start();
        thread.join();
        return urlThread.getUrl();
    }

    /**
     * 更新当前时间
     */
    private void showCurrentTime() {
        runOnUiThread(() -> songBeginTextView.setText(formatTime(currentPosition)));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 1){
            if(grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(PlayerActivity.this, "No granted no player", Toast.LENGTH_SHORT).show();
                finish();
            }
            else{
                initMediaPlayer();
            }
        }
    }

    /**
     * 时间格式化
     * @param time 要计算的时间
     * @return 时间字符串
     */
    @SuppressLint("SimpleDateFormat")
    private String formatTime(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        return simpleDateFormat.format(new Date(time));
    }

    @Override
    protected void onDestroy() {
        unbindService(connection);
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
        }
        assert mediaPlayer != null;
        mediaPlayer.reset();
        mediaPlayer.release();
        unregisterReceiver(msgReceiver);
        super.onDestroy();
    }
}