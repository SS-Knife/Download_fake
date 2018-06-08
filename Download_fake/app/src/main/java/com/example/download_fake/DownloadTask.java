package com.example.download_fake;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * Created by 郝书逸 on 2018/6/6.
 */

public class DownloadTask extends AsyncTask<String, Integer, Integer> {

    public static final int TYPE_SUCCESS = 0;
    public static final int TYPE_FAILED = 1;
    public static final int TYPE_PAUSED = 2;
    public static final int TYPE_CANCELED = 3;

    private DownloadListener listener;

    private boolean isCanceled = false;

    private boolean isPaused = false;

    private int LastProgress;

    private String[] Url;

    private int threadcount;

    public static ArrayList<DownloadInfo> infos = new ArrayList<DownloadInfo>();

    // TODO: 2018/6/6 弄明白究竟什么样
    public DownloadTask(DownloadListener listener, String[] Url, int threadcount) {
        this.listener = listener;
        this.Url = Url;
        this.threadcount = threadcount;
    }

    @Override
    protected Integer doInBackground(String... strings) {
        DownloadThread[] threads = new DownloadThread[threadcount];
        File file = null;
        long downloadedlength = 0;  //记录已下载文件长度
        final String downloadUrl = Url[0];
        String filename = downloadUrl.substring(downloadUrl.lastIndexOf("/"));   //截取最后一个/后的文件名
        String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath(); //默认目录
        file = new File(directory + filename);
        if (file.exists()) {
            downloadedlength = file.length();
        }
        long contentlength = getContentLength(downloadUrl);
        if (contentlength == 0) {
            return TYPE_FAILED;
        } else if (contentlength == downloadedlength) {
            return TYPE_SUCCESS;
        }
        long blockSize = (contentlength - downloadedlength) % threadcount == 0 ? (contentlength - downloadedlength) / threadcount : (contentlength - downloadedlength) / threadcount + 1;
        for (int i = 0; i < threadcount; i++) {
            DownloadInfo info = new DownloadInfo();
            long startspot = 0, endspot = 0;
            if (i == threadcount - 1) {
                startspot = i * blockSize;
                endspot = downloadedlength - 1;
            } else {
                startspot = i * blockSize;
                endspot = (i + 1) * blockSize - 1;
            }
            info.setBlockSize(blockSize);
            info.setDownloadedlength(0);
            info.setStartspot(startspot);
            info.setEndspot(endspot);
            info.setDownloadUrl(downloadUrl);
            info.setThreadid(i);
            infos.add(info);

        }
        for (int i = 0; i < threads.length; i++) {
            // 启动线程，分别下载每个线程需要下载的部分
            threads[i] = new DownloadThread(downloadUrl, file, (i + 1));
            threads[i].setName("Thread:" + i);
            threads[i].start();
        }
        boolean isfinished = false;
        long downloadedAllSize = 0;
        isfinished = true;
        while (!isfinished) {
            isfinished = true;
            if (isCanceled) {
                return TYPE_CANCELED;
            } else if (isPaused) {
                return TYPE_PAUSED;
            } else {
                // 当前所有线程下载总量
                downloadedAllSize = 0;
                for (int i = 0; i < threads.length; i++) {
                    downloadedAllSize += threads[i].getDownloadLength();
                    int progress = (int) ((downloadedAllSize + downloadedlength) * 100 / contentlength);
                    publishProgress(progress);
                    if (!threads[i].isCompleted()) {
                        isfinished = false;
                    }
                    if (threads[i].isFailed()) {
                        if (isCanceled && file != null) {
                            file.delete();
                        }
                        return TYPE_FAILED;
                    }
                }
            }
        }
        return TYPE_SUCCESS;
    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        int progress = values[0];
        if (progress > LastProgress) {
            listener.onProgress(progress);
            LastProgress = progress;
        }
    }

    @Override
    protected void onPostExecute(Integer status) {
        switch (status) {
            case TYPE_SUCCESS:
                listener.onSuccess();
                break;
            case TYPE_FAILED:
                listener.onFailed();
                break;
            case TYPE_PAUSED:
                listener.onPause();
                break;
            case TYPE_CANCELED:
                listener.onCancel();
                break;
            default:
                break;
        }
    }

    public void pauseDownload() {
        isPaused = true;
    }

    public void cancelDownload() {
        isCanceled = true;
    }


    private long getContentLength(String downloadUrl) {
        URL url = null;
        HttpURLConnection http = null;
        long contentlength;
        try {
            url = new URL(downloadUrl);
            http = (HttpURLConnection) url
                    .openConnection();
            http.setConnectTimeout(5000);
            http.setReadTimeout(5000);
            http.setRequestMethod("GET");
            if (http.getResponseCode() == 200) {
                contentlength = http.getContentLength();//文件大小
            } else {
                contentlength = -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            contentlength = -1;
        } finally {
            http.disconnect();
        }
        return contentlength;
    }
}
