package com.uuch.adlibrary;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.flyco.pageindicator.indicator.FlycoPageIndicaor;
import com.uuch.adlibrary.bean.AdInfo;
import com.uuch.adlibrary.utils.DisplayUtil;
import java.util.List;

/**
 * Created by Administrator on 2015/10/20 0020.
 * 首页广告管理类
 */
public class AdManager {

    List<AdInfo> advInfoListList;
    private Activity context;
    private DisplayMetrics displayMetrics = new DisplayMetrics();
    private View contentView;
    private ViewPager viewPager;
    private RelativeLayout adRootContent;
    private AdAdapter adAdapter;
    private FlycoPageIndicaor mIndicator;
    private AnimDialogUtils animDialogUtils;
    /**
     * 广告弹窗距离两侧的距离-单位(dp)
     */
    private int padding = 44;
    /**
     * 广告弹窗的宽高比,默认高度为wrap_content
     */
    private float widthPerHeight = 0f;

    // 弹窗背景是否透明
    private boolean isAnimBackViewTransparent = false;
    // 弹窗是否可关闭
    private boolean isDialogCloseable = true;
    // 弹窗关闭点击事件
    private View.OnClickListener onCloseClickListener = null;
    // 设置弹窗背景颜色
    private int backViewColor = Color.parseColor("#bf000000");
    // 弹性动画弹性参数
    private double bounciness = AdConstant.BOUNCINESS;
    // 弹性动画速度参数
    private double speed = AdConstant.SPEED;
    // viewPager滑动动画效果
    private ViewPager.PageTransformer pageTransformer = null;
    // 是否覆盖全屏幕
    private boolean isOverScreen = true;

    private OnImageClickListener onImageClickListener = null;
    private View.OnClickListener imageOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            AdInfo advInfo = (AdInfo) view.getTag(R.id.tag_data);
            if (advInfo != null && onImageClickListener != null) {
                onImageClickListener.onImageClick(view, advInfo);
            }
        }
    };

    public AdManager(Activity context, List<AdInfo> advInfoListList) {
        this.context = context;
        this.advInfoListList = advInfoListList;
    }

    /**
     * 开始执行显示广告弹窗的操作
     */
    public void showAdDialog(final int animType) {

        contentView = LayoutInflater.from(context).inflate(R.layout.ad_dialog_content_layout, null);
        adRootContent = (RelativeLayout) contentView.findViewById(R.id.ad_root_content);

        viewPager = (ViewPager) contentView.findViewById(R.id.viewPager);
        mIndicator = (FlycoPageIndicaor) contentView.findViewById(R.id.indicator);

        adAdapter = new AdAdapter();
        viewPager.setAdapter(adAdapter);

        if (pageTransformer != null) {
            viewPager.setPageTransformer(true, pageTransformer);
        }

        mIndicator.setViewPager(viewPager);
        isShowIndicator();

        animDialogUtils = AnimDialogUtils.getInstance(context)
                .setAnimBackViewTransparent(isAnimBackViewTransparent)
                .setDialogCloseable(isDialogCloseable)
                .setDialogBackViewColor(backViewColor)
                .setOnCloseClickListener(onCloseClickListener)
                .setOverScreen(isOverScreen)
                .initView(contentView);
        setRootContainerHeight();

        // 延迟1s展示，为了避免ImageLoader还为加载完缓存图片时就展示了弹窗的情况
        adRootContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                animDialogUtils.show(animType, bounciness, speed);
            }
        }, 1000);
    }

    /**
     * 开始执行销毁弹窗的操作
     */
    public void dismissAdDialog() {
        animDialogUtils.dismiss(AdConstant.ANIM_STOP_DEFAULT);
    }

    private void setRootContainerHeight() {

        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int widthPixels = displayMetrics.widthPixels;
        int totalPadding = DisplayUtil.dip2px(context, padding * 2);
        int width = widthPixels - totalPadding;

        ViewGroup.LayoutParams params = adRootContent.getLayoutParams();
        if (widthPerHeight == 0f) {
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        } else {
            final int height = (int) (width / widthPerHeight);
            params.height = height;
        }
    }

    /**
     * 根据页面数量，判断是否显示Indicator
     */
    private void isShowIndicator() {
        if (advInfoListList.size() > 1) {
            mIndicator.setVisibility(View.VISIBLE);
        } else {
            mIndicator.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 设置弹窗距离屏幕左右两侧的距离
     */
    public AdManager setPadding(int padding) {
        this.padding = padding;

        return this;
    }

    // ######################## 点击事件处理操作类 ########################

    /**
     * 设置弹窗宽高比
     */
    public AdManager setWidthPerHeight(float widthPerHeight) {
        this.widthPerHeight = widthPerHeight;

        return this;
    }

    // ######################## get set方法 #########################

    /**
     * 设置ViewPager Item点击事件
     */
    public AdManager setOnImageClickListener(OnImageClickListener onImageClickListener) {
        this.onImageClickListener = onImageClickListener;

        return this;
    }

    /**
     * 设置背景是否透明
     */
    public AdManager setAnimBackViewTransparent(boolean animBackViewTransparent) {
        isAnimBackViewTransparent = animBackViewTransparent;

        return this;
    }

    /**
     * 设置弹窗关闭按钮是否可见
     */
    public AdManager setDialogCloseable(boolean dialogCloseable) {
        isDialogCloseable = dialogCloseable;

        return this;
    }

    /**
     * 设置弹窗关闭按钮点击事件
     */
    public AdManager setOnCloseClickListener(View.OnClickListener onCloseClickListener) {
        this.onCloseClickListener = onCloseClickListener;

        return this;
    }

    /**
     * 设置弹窗背景颜色
     */
    public AdManager setBackViewColor(int backViewColor) {
        this.backViewColor = backViewColor;

        return this;
    }

    /**
     * 设置弹窗弹性动画弹性参数
     */
    public AdManager setBounciness(double bounciness) {
        this.bounciness = bounciness;

        return this;
    }

    /**
     * 设置弹窗弹性动画速度参数
     */
    public AdManager setSpeed(double speed) {
        this.speed = speed;

        return this;
    }

    /**
     * 设置ViewPager滑动动画效果
     */
    public AdManager setPageTransformer(ViewPager.PageTransformer pageTransformer) {
        this.pageTransformer = pageTransformer;

        return this;
    }

    /**
     * 设置弹窗背景是否覆盖全屏幕
     */
    public AdManager setOverScreen(boolean overScreen) {
        isOverScreen = overScreen;

        return this;
    }

    /**
     * ViewPager每一项的单击事件
     */
    public interface OnImageClickListener {

        void onImageClick(View view, AdInfo advInfo);
    }

    class AdAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return advInfoListList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            AdInfo advInfo = advInfoListList.get(position);

            final View rootView =
                    context.getLayoutInflater().inflate(R.layout.viewpager_item, container, false);
            final ViewGroup errorView = (ViewGroup) rootView.findViewById(R.id.error_view);
            final ViewGroup loadingView = (ViewGroup) rootView.findViewById(R.id.loading_view);

            final ImageView simpleImageView =
                    (ImageView) rootView.findViewById(R.id.simpleImageView);
            simpleImageView.setTag(R.id.tag_data, advInfo);
            simpleImageView.setOnClickListener(imageOnClickListener);
            ViewGroup.LayoutParams params =
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);
            container.addView(rootView, params);

            Glide.with(context)
                    .load(advInfo.getActivityImg())
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource,
                                GlideAnimation<? super GlideDrawable> glideAnimation) {
                            if (resource == null) {
                                return;
                            }
                            errorView.setVisibility(View.GONE);
                            loadingView.setVisibility(View.GONE);
                            simpleImageView.setVisibility(View.VISIBLE);
                            simpleImageView.setImageDrawable(resource);
                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            super.onLoadFailed(e, errorDrawable);
                            errorView.setVisibility(View.VISIBLE);
                            loadingView.setVisibility(View.GONE);
                            simpleImageView.setVisibility(View.GONE);
                        }
                    });

            return rootView;
        }
    }
}
