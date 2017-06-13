package com.micky.circleimage.view;

import android.content.Context;
import android.widget.ImageView;

import java.util.List;

public abstract class NineGridImageViewAdapter<T> {
    protected abstract void onDisplayImage(Context context, ImageView imageView, T t, NineGridImageView.GridInfo gridInfo);

    protected void onItemImageClick(Context context, ImageView imageView, int index, List<T> list) {
    }

    protected ImageView generateImageView(Context context) {
        GridImageView imageView = new GridImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return imageView;
    }
}