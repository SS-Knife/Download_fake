package com.example.download_fake;

/**
 * Created by 郝书逸 on 2018/6/6.
 */

public interface DownloadListener {
    void onProgress(int progress);//提供已下载百分比

    void onSuccess();

    void onFailed();

    void onPause();

    void onCancel();

}
