package com.example.download_fake;

/**
 * Created by 郝书逸 on 2018/6/6.
 */

public class DownloadInfo {
    private int threadid;//线程id
    private long startspot;//下载的起始位置
    private long endspot;//下载的结束位置
    private long blockSize;//每条下载的大小
    private long downloadedlength;//该条线程已经下载的大小
    private String downloadUrl;//下载地址

    public void setThreadid(int threadid) {
        this.threadid = threadid;
    }

    public void setStartspot(long startspot) {
        this.startspot = startspot;
    }

    public void setEndspot(long endspot) {
        this.endspot = endspot;
    }

    public void setBlockSize(long blockSize) {
        this.blockSize = blockSize;
    }

    public void setDownloadedlength(long downloadedlength) {
        this.downloadedlength = downloadedlength;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public int getThreadid() {
        return threadid;
    }

    public long getStartspot() {
        return startspot;
    }

    public long getEndspot() {
        return endspot;
    }

    public long getBlockSize() {
        return blockSize;
    }

    public long getDownloadedlength() {
        return downloadedlength;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }
}
