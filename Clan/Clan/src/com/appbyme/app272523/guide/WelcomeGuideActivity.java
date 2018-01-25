package com.appbyme.app272523.guide;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.appbyme.app272523.R;
import com.appbyme.app272523.main.bottomtab.BottomTabMainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 欢迎页
 *
 * @author wwj_748
 */
public class WelcomeGuideActivity extends Activity implements OnClickListener {

    private ViewPager vp;
    private GuideViewPagerAdapter adapter;
    private List<View> views;
    private ImageView startBtn;

    // 引导页图片资源
    private static final int[] pics = {R.layout.guid_view1,
            R.layout.guid_view2};

    // 底部小点图片
//	private ImageView[] dots;

    // 记录当前选中位置
    private int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide1);
        views = new ArrayList<View>();

        // 初始化引导页视图列表
        for (int i = 0; i < pics.length; i++) {
            View view = LayoutInflater.from(this).inflate(pics[i], null);

            if (i == pics.length - 1) {
                startBtn = (ImageView) view.findViewById(R.id.btn_login);
                startBtn.setTag("enter");
                startBtn.setOnClickListener(this);
            }

            views.add(view);

        }

        vp = (ViewPager) findViewById(R.id.vp_guide);
        // 初始化adapter
        adapter = new GuideViewPagerAdapter(views);
        vp.setAdapter(adapter);
        vp.setOnPageChangeListener(new PageChangeListener());

//		initDots();

    }


//    @OnClick(R.id.btn_login)
//    public void guide(View view) {
//        Intent intent = new Intent(WelcomeGuideActivity.this, BottomTabMainActivity.class);
//        startActivity(intent);
//        finish();
//    }


    private void enterHomeActivity() {
        Intent intent = new Intent(this, GuideActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 如果切换到后台，就设置下次不进入功能引导页
        SpUtils.putBoolean(WelcomeGuideActivity.this, AppConstants.FIRST_OPEN, true);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

//	private void initDots() {
//		LinearLayout ll = (LinearLayout) findViewById(R.id.ll);
//		dots = new ImageView[pics.length];

    // 循环取得小点图片
//		for (int i = 0; i < pics.length; i++) {
    // 得到一个LinearLayout下面的每一个子元素
//			dots[i] = (ImageView) ll.getChildAt(i);
//			dots[i].setEnabled(false);// 都设为灰色
//			dots[i].setOnClickListener(this);
//			dots[i].setTag(i);// 设置位置tag，方便取出与当前位置对应
//		}

//		currentIndex = 0;
//		dots[currentIndex].setEnabled(true); // 设置为白色，即选中状态

//	}

    /**
     * 设置当前view
     *
     * @param position
     */
    private void setCurView(int position) {
        if (position < 0 || position >= pics.length) {
            return;
        }
        vp.setCurrentItem(position);
    }

    /**
     * 设置当前指示点
     *
     * @param position
     */
    private void setCurDot(int position) {
        if (position < 0 || position > pics.length || currentIndex == position) {
            return;
        }
//		dots[position].setEnabled(true);
//		dots[currentIndex].setEnabled(false);
        currentIndex = position;
    }

    @Override
    public void onClick(View v) {
        if (v.getTag().equals("enter")) {
            enterMainActivity();
            return;
        }

        int position = (Integer) v.getTag();
        setCurView(position);
        setCurDot(position);
    }


    private void enterMainActivity() {
        Intent intent = new Intent(WelcomeGuideActivity.this,
                BottomTabMainActivity.class);
        startActivity(intent);
        SpUtils.putBoolean(WelcomeGuideActivity.this, AppConstants.FIRST_OPEN, true);
        finish();
    }

    private class PageChangeListener implements OnPageChangeListener {
        // 当滑动状态改变时调用
        @Override
        public void onPageScrollStateChanged(int position) {
            // arg0 ==1的时辰默示正在滑动，arg0==2的时辰默示滑动完毕了，arg0==0的时辰默示什么都没做。

        }

        // 当前页面被滑动时调用
        @Override
        public void onPageScrolled(int position, float arg1, int arg2) {
            // arg0 :当前页面，及你点击滑动的页面
            // arg1:当前页面偏移的百分比
            // arg2:当前页面偏移的像素位置

        }

        // 当新的页面被选中时调用
        @Override
        public void onPageSelected(int position) {
            // 设置底部小点选中状态
            setCurDot(position);
        }

    }
}
