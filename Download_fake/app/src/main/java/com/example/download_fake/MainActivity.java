package com.example.download_fake;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URL;

public class MainActivity extends AppCompatActivity {
    Download download;
    String Url="http://gdown.baidu.com/data/wisegame/d2fbbc8e64990454/wangyiyunyinle_87.apk";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        download= new Download(5,Url,new DownloadListener(){

            @Override
            public void onProgress(int progress) {
                //获取下载百分比
            }

            @Override
            public void onSuccess() {
                //下载成功
            }

            @Override
            public void onFailed() {
                //下载失败
            }

            @Override
            public void onPause() {
                //下载暂停
            }

            @Override
            public void onCancel() {
                //下载取消
            }
        });
    }
}

