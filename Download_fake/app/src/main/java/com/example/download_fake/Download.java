package com.example.download_fake;

import java.net.URL;

/**
 * Created by 郝书逸 on 2018/6/7.
 */

public class Download {
    private DownloadListener downloadListener;
    private int threadcount;
    private String[] Url;
    private DownloadTask downloadTask;
    public Download(int threadcount,String Url,DownloadListener downloadListener){
        this.downloadListener=downloadListener;
        this.threadcount=threadcount;
        this.Url[0]=Url;
    }
    //开始下载
    public void onStart(){
        downloadTask=new DownloadTask(downloadListener, Url,threadcount);
        downloadTask.doInBackground();
    }
    //暂停下载
    public void onPause(){
        if(downloadTask!=null){
            downloadTask.pauseDownload();
        }
    }
    //取消下载
    public void onCancel(){
        if(downloadTask!=null){
            downloadTask.cancelDownload();
        }
    }
}
