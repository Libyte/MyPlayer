package cn.edu.bistu.cs.myplayer;

interface DownloadListener {
    void onProgress(int progress);

    void onSuccess();

    void onFailed();

}
