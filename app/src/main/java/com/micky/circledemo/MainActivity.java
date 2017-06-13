package com.micky.circledemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.micky.circleimage.adapter.NineGridAdapter;
import com.micky.circleimage.view.NineGridImageView;
import com.micky.circleimage.transfer.glideloader.GlideImageLoader;
import com.micky.circleimage.transfer.transfer.Transferee;
import com.micky.circledemo.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private NineGridImageView mNineGridImageView;
    protected List<String> mUrlList;
    private NineGridAdapter mNineGridAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUrlList = new ArrayList<>();
        mUrlList.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=903548017,2680278453&fm=26&gp=0.jpg");
        mUrlList.add("http://i3.17173cdn.com/2fhnvk/YWxqaGBf/cms3/DWABSHbkCdElEDA.jpg!a-3-640x.jpg");
        mUrlList.add("http://i2.17173cdn.com/2fhnvk/YWxqaGBf/cms3/xNLzqtbkCdElEyC.jpg!a-3-640x.jpg");
        mUrlList.add("http://i1.17173cdn.com/2fhnvk/YWxqaGBf/cms3/vUFNHwbljxruktB.jpg!a-3-640x.jpg");
        mUrlList.add("http://ac-QYgvX1CC.clouddn.com/36f0523ee1888a57.jpg");
        mUrlList.add("http://ac-QYgvX1CC.clouddn.com/9ec4bc44bfaf07ed.jpg");
        mUrlList.add("http://ac-QYgvX1CC.clouddn.com/de13315600ba1cff.jpg");
        mUrlList.add("http://ac-QYgvX1CC.clouddn.com/10762c593798466a.jpg");
        mUrlList.add("http://ac-QYgvX1CC.clouddn.com/ad99de83e1e3f7d4.jpg");

        mNineGridImageView = (NineGridImageView) findViewById(R.id.nineGridImageView);
        mNineGridAdapter  = new NineGridAdapter(this);
        mNineGridImageView.setAdapter(mNineGridAdapter);
        int singleImgWidth = (int) (ViewUtils.getScreenInfo(this).widthPixels - 2 * getResources().getDimension(R.dimen.activity_horizontal_margin));
        mNineGridImageView.setSingleImgWidth(singleImgWidth);
        mNineGridImageView.setMultiImgWidth((singleImgWidth - 2 * mNineGridImageView.getGap()) / 3);
        mNineGridImageView.setImagesData(mUrlList);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Transferee.destroy();
    }
}
