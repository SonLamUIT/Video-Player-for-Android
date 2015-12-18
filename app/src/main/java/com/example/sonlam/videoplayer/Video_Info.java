package com.example.sonlam.videoplayer;

import android.graphics.Bitmap;

/**
 * Created by Son Lam on 12/18/2015.
 */
public class Video_Info {
    public Bitmap getBitmap() {
        return bitmap;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    private Bitmap bitmap;
    private String title;
    private String url;

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    private String mimeType;
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
