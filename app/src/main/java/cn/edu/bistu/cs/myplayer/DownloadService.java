package cn.edu.bistu.cs.myplayer;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class DownloadService extends Service {
    private DownloadTask downloadTask;
    Intent intent = new Intent("cn.edu.bistu.cs.DOWNLOAD_FINISHED");

    private final DownloadListener listener = new DownloadListener() {
        @Override
        public void onProgress(int progress) {
            // 使用notify显示下载的通知
            getNotificationManager().notify(1, getNotification("Downloading...", progress));
        }

        @Override
        public void onSuccess() {
            downloadTask = null;
            // 关闭下载进度通知
            stopForeground(true);
            // 显示下载成功的通知
            getNotificationManager().notify(1, getNotification("Download Success", -1));
            Toast.makeText(DownloadService.this, "Download success", Toast.LENGTH_SHORT).show();
            sendBroadcast(intent);
        }

        @Override
        public void onFailed() {
            downloadTask = null;
            // 关闭下载进度通知
            stopForeground(true);
            // 显示下载失败的通知
            getNotificationManager().notify(1, getNotification("Download failed", -1));
            Toast.makeText(DownloadService.this, "Download failed", Toast.LENGTH_SHORT).show();

        }

    };



    // 用于活动与服务之间的通信，其中包括开始下载，暂停下载与取消下载，使用内部类进行实现
    private final DownLoadBinder binder = new DownLoadBinder();


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return binder;
    }

    class DownLoadBinder extends Binder {
        public void startDownload(String url, String name){
            if(downloadTask == null){
                // 开始下载并显示通知
                Log.e("Download Url:",url+name);
                downloadTask = new DownloadTask(listener);
                downloadTask.execute(url, name);
                startForeground(1, getNotification("Downloading...", 0));
                Toast.makeText(DownloadService.this, "Start Download", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public DownloadService() {
    }


    private NotificationManager getNotificationManager(){
        // 获取系统通知
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }
    @SuppressLint("UnspecifiedImmutableFlag")
    private Notification getNotification(String title, int progress){
        NotificationChannel notificationChannel;

        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            notificationChannel = new NotificationChannel("Test", "test", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        // 使用PendingIntent延迟执行Intent，用于启动
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Test");
        // 设置通知图标
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        // 接受PendingIntent，实现点击通知跳转
        builder.setContentIntent(pendingIntent);
        builder.setContentTitle(title);
        // 当进程大于0才显示下载进度
        if(progress > 0){
            builder.setContentText(progress+"%");
            builder.setProgress(100, progress, false);
        }

        return builder.build();
    }
}