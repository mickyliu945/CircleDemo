package com.micky.circleimage.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.micky.circleimage.R;

import java.util.ArrayList;
import java.util.List;

public class NineGridImageView<T> extends ViewGroup {

    private int mRowCount;       // 行数
    private int mColumnCount;    // 列数

    private int mMaxSize;        // 最大图片数
    private int mGap;           // 宫格间距
    private int mSingleImgWidth; // 单张图片时的尺寸
    private int mMultiImgWidth; //多张图片时单张图片尺寸
    private int mGridSize;   // 宫格大小,即图片大小

    private List<ImageView> mImageViewList = new ArrayList<>();
    private List<T> mImgDataList;

    private NineGridImageViewAdapter<T> mAdapter;
    private ItemImageClickListener<T> mItemImageClickListener;

    public NineGridImageView(Context context) {
        this(context, null);
    }

    public NineGridImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NineGridImageView);
        this.mGap = (int) typedArray.getDimension(R.styleable.NineGridImageView_imgGap, 0);
        this.mSingleImgWidth = typedArray.getDimensionPixelSize(R.styleable.NineGridImageView_singleImgWidth, -1);
        this.mMaxSize = typedArray.getInt(R.styleable.NineGridImageView_maxSize, 9);
        typedArray.recycle();
    }

    public List<ImageView> getImageViewList() {
        return mImageViewList;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height;
        int totalWidth = width - getPaddingLeft() - getPaddingRight();
        if (mImgDataList != null && mImgDataList.size() > 0) {
            if (mImgDataList.size() == 1 && mSingleImgWidth != -1) {
                mGridSize = mSingleImgWidth > totalWidth ? totalWidth : mSingleImgWidth;
            } else {
                mImageViewList.get(0).setScaleType(ImageView.ScaleType.CENTER_CROP);
                mGridSize = (totalWidth - mGap * (mColumnCount - 1)) / mColumnCount;
            }
            height = mGridSize * mRowCount + mGap * (mRowCount - 1) + getPaddingTop() + getPaddingBottom();
            setMeasuredDimension(width, height);
        } else {
            height = width;
            setMeasuredDimension(width, height);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        layoutChildrenView();
    }

    /**
     * 布局 ImageView
     */
    private void layoutChildrenView() {
        if (mImgDataList == null) {
            return;
        }
        int showCount = getNeedShowCount(mImgDataList.size());
        for (int i = 0; i < showCount; i++) {
            int rowNum = i / mColumnCount;
            int columnNum = i % mColumnCount;
            GridInfo info = new GridInfo();
            info.rowNum = rowNum;
            info.columnNum = columnNum;
            info.singleImage = mImgDataList.size() == 1;
            info.singleImageWidth = mSingleImgWidth;
            ImageView childrenView = (ImageView) getChildAt(i);
            if (mAdapter != null) {
                mAdapter.onDisplayImage(getContext(), childrenView, mImgDataList.get(i), info);
            }
            if (mImgDataList.size() == 1) {
                return;
            }

            int left = (mMultiImgWidth + mGap) * columnNum + getPaddingLeft();
            int top = (mMultiImgWidth + mGap) * rowNum + getPaddingTop();
            int right = left + mMultiImgWidth;
            int bottom = top + mMultiImgWidth;
            childrenView.layout(left, top, right, bottom);
        }
    }

    public class GridInfo {
        public int rowNum;
        public int columnNum;
        public boolean singleImage;
        public int singleImageWidth;
    }

    /**
     * 设置图片数据
     *
     * @param lists 图片数据集合
     */
    public void setImagesData(List lists) {
        if (lists == null || lists.isEmpty()) {
            this.setVisibility(GONE);
            return;
        } else {
            this.setVisibility(VISIBLE);
        }

        int newShowCount = getNeedShowCount(lists.size());

        int[] gridParam = calculateGridParam(newShowCount);
        mRowCount = gridParam[0];
        mColumnCount = gridParam[1];
        if (mImgDataList == null) {
            int i = 0;
            while (i < newShowCount) {
                ImageView iv = getImageView(i);
                if (iv == null) {
                    return;
                }
                addView(iv, generateDefaultLayoutParams());
                i++;
            }
        } else {
            int oldShowCount = getNeedShowCount(mImgDataList.size());
            if (oldShowCount > newShowCount) {
                removeViews(newShowCount, oldShowCount - newShowCount);
            } else if (oldShowCount < newShowCount) {
                for (int i = oldShowCount; i < newShowCount; i++) {
                    ImageView iv = getImageView(i);
                    if (iv == null) {
                        return;
                    }
                    addView(iv, generateDefaultLayoutParams());
                }
            }
        }
        mImgDataList = lists;
        requestLayout();
    }

    private int getNeedShowCount(int size) {
        if (mMaxSize > 0 && size > mMaxSize) {
            return mMaxSize;
        } else {
            return size;
        }
    }

    /**
     * 获得 ImageView
     * 保证了 ImageView 的重用
     *
     * @param position 位置
     */
    private ImageView getImageView(final int position) {
        if (position < mImageViewList.size()) {
            return mImageViewList.get(position);
        } else {
            if (mAdapter != null) {
                ImageView imageView = mAdapter.generateImageView(getContext());
                mImageViewList.add(imageView);
                imageView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAdapter.onItemImageClick(getContext(), (ImageView) v, position, mImgDataList);
                        if (mItemImageClickListener != null) {
                            mItemImageClickListener.onItemImageClick(getContext(), (ImageView) v, position, mImgDataList);
                        }
                    }
                });
                return imageView;
            } else {
                Log.e("NineGirdImageView", "Your must set a NineGridImageViewAdapter for NineGirdImageView");
                return null;
            }
        }
    }

    /**
     * 设置 宫格参数
     *
     * @param imagesSize 图片数量
     * @return 宫格参数 gridParam[0] 宫格行数 gridParam[1] 宫格列数
     */
    protected static int[] calculateGridParam(int imagesSize) {
        int[] gridParam = new int[2];
        if (imagesSize < 3) {
            gridParam[0] = 1;
            gridParam[1] = imagesSize;
        } else if (imagesSize <= 4) {
            gridParam[0] = 2;
            gridParam[1] = 2;
        } else {
            gridParam[0] = imagesSize / 3 + (imagesSize % 3 == 0 ? 0 : 1);
            gridParam[1] = 3;
        }
        return gridParam;
    }

    /**
     * 设置适配器
     *
     * @param adapter 适配器
     */
    public void setAdapter(NineGridImageViewAdapter adapter) {
        mAdapter = adapter;
    }

    /**
     * 设置宫格间距
     *
     * @param gap 宫格间距 px
     */
    public void setGap(int gap) {
        mGap = gap;
    }

    /**
     * 获取宫格间距 px
     */
    public int getGap() {
        return mGap;
    }

    /**
     * 设置只有一张图片时的尺寸大小
     *
     * @param singleImgWidth 单张图片的尺寸大小
     */
    public void setSingleImgWidth(int singleImgWidth) {
        mSingleImgWidth = singleImgWidth;
    }

    /**
     * 设置多张图片时单张图片时的尺寸大小
     *
     * @param width 单张图片的尺寸大小
     */
    public void setMultiImgWidth(int width) {
        mMultiImgWidth = width;
    }

    /**
     * 设置最大图片数
     *
     * @param maxSize 最大图片数
     */
    public void setMaxSize(int maxSize) {
        mMaxSize = maxSize;
    }

    public void setItemImageClickListener(ItemImageClickListener<T> itemImageViewClickListener) {
        mItemImageClickListener = itemImageViewClickListener;
    }
}