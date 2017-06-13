package com.micky.circleimage.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.micky.circleimage.R;
import com.micky.circleimage.view.NineGridImageView;
import com.micky.circleimage.view.NineGridImageViewAdapter;
import com.micky.circleimage.transfer.glideloader.GlideImageLoader;
import com.micky.circleimage.transfer.style.progress.ProgressPieIndicator;
import com.micky.circleimage.transfer.transfer.TransferConfig;
import com.micky.circleimage.transfer.transfer.Transferee;

import java.util.ArrayList;
import java.util.List;

public class NineGridAdapter extends NineGridImageViewAdapter<String> {

    private Activity mActivity;
    private Transferee mTransfereee;
    private List<ImageView> mImageViewList = new ArrayList<>();

    private int mPlaceHolderResId = R.mipmap.ic_empty_photo;

    public NineGridAdapter(Activity activity) {
        mActivity = activity;
        mTransfereee = Transferee.getDefault(activity);
    }

    public void setPlaceHolderResId(int resId) {
        mPlaceHolderResId = resId;
    }

    @Override
    protected ImageView generateImageView(Context context) {
        ImageView imageView = super.generateImageView(context);
        mImageViewList.add(imageView);
        return imageView;
    }

    @Override
    protected void onDisplayImage(Context context, ImageView imageView, String photo, final NineGridImageView.GridInfo info) {
        Glide.with(context).load(photo).placeholder(mPlaceHolderResId).diskCacheStrategy(DiskCacheStrategy.ALL).into(new ViewTarget<ImageView, GlideDrawable>(imageView) {

            @Override
            public void onLoadStarted(Drawable placeholder) {
                super.onLoadStarted(placeholder);
                view.setImageDrawable(placeholder);
            }

            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation anim) {
                this.view.setImageDrawable(resource);
                if (info.singleImage) {
                    int realWidth = resource.getIntrinsicWidth();
                    int realHeigth = resource.getIntrinsicHeight();
                    int scaledWidth = realWidth;
                    int scaledHeight = realHeigth;

                    int maxWidth = info.singleImageWidth;
                    int maxHeight = (int) (maxWidth * 0.6);
                    if (realWidth > maxWidth || realHeigth > maxHeight) {
                        float widthRate = maxWidth * 1.0F / realWidth;
                        float heightRate = maxHeight * 1.0F / realHeigth;
                        float minRate = Math.min(widthRate, heightRate);
                        scaledWidth = (int) (realWidth * minRate);
                        scaledHeight = (int) (realHeigth * minRate);
                    }
                    this.view.layout(0, 0, scaledWidth, scaledHeight);
                }
            }
        });
    }

    @Override
    protected void onItemImageClick(Context context, ImageView imageView, int index, List<String> photoList) {
        TransferConfig config = TransferConfig.build()
                .setNowThumbnailIndex(index)
                .setSourceImageList(photoList)
                .setMissPlaceHolder(mPlaceHolderResId)
                .setOriginImageList(mImageViewList)
                .setOffscreenPageLimit(3)
                .setProgressIndicator(new ProgressPieIndicator())
                .setImageLoader(GlideImageLoader.with(mActivity))
                .create();
        mTransfereee.apply(config).show(new Transferee.OnTransfereeStateChangeListener() {
            @Override
            public void onShow() {
                Glide.with(mActivity).pauseRequests();
            }

            @Override
            public void onDismiss() {
                Glide.with(mActivity).resumeRequests();
            }
        });
    }

    public void destory() {
        if (mTransfereee != null) {
            mTransfereee.destroy();
        }
    }
}
