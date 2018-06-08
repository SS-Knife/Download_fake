package com.example.download_fake;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.download_fake.DownloadTask.infos;

/**
 * Created by 郝书逸 on 2018/6/7.
 */

public class DownloadThread extends Thread {

    private boolean isCompleted = false;

    private boolean isFailed = false;

    private File file;

    private String downloadUrl;

    private int threadId;


    private int threadDownloadsize = 0;


    public DownloadThread(String downloadUrl, File file, int threadId) {
        this.downloadUrl = downloadUrl;
        this.file = file;
        this.threadId = threadId;

    }

    @Override
    public void run() {
        InputStream inputStream = null;
        RandomAccessFile savedFile = null;  //用于断点续传，可以指定读取位置
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            URL requestUrl = new URL(downloadUrl);
            connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);//链接超时
            connection.setReadTimeout(5000);//读取超时
            connection.setRequestProperty("Range", "bytes=" + infos.get(threadId).getStartspot() + "-" + infos.get(threadId).getEndspot());
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            if (connection.getResponseCode() == 200) {
                inputStream = connection.getInputStream();
                savedFile = new RandomAccessFile(file, "rw");
                // TODO: 2018/6/6  弄明白rw是啥
                savedFile.seek(infos.get(threadId).getStartspot());   //跳过已下载字节
                byte[] b = new byte[1024];
                int len;
                while ((len = inputStream.read(b, 0, 1024)) != -1) {
                    savedFile.write(b, 0, len);
                    threadDownloadsize += len;
                }
                inputStream.close();
                isCompleted = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.disconnect();//断开连接，释放资源
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                if (savedFile != null) {
                    savedFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            isFailed = true;
        }
    }

    public boolean isFailed() {
        return isFailed;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public int getDownloadLength() {
        return threadDownloadsize;
    }
}
