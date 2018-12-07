package com.uca.devceargo.internic;

import android.widget.ImageView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.AlbumLoader;

public class MediaLoader implements AlbumLoader {

    @Override
    public void load(ImageView imageView, AlbumFile albumFile) {
        load(imageView, albumFile.getPath());
    }

    @Override
    public void load(ImageView imageView, String url) {
        Glide.with(imageView.getContext())
                .load(url)
                .apply(new RequestOptions().placeholder(R.drawable.placeholder))
                .into(imageView);
    }
}
