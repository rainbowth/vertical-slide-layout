package cn.rainbow.android.app.verticalslide;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.SwipeDismissBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(BlankFragment.newInstance());
        fragments.add(BlankFragment.newInstance());

        ViewPager bannel = (ViewPager) findViewById(R.id.vp_bannel);

        List<View> viewList = new ArrayList<>();

        View view = makeView(Color.BLUE);
        viewList.add(view);

        View view2 = makeView(Color.GREEN);
        viewList.add(view2);

        View view3 = makeView(Color.BLACK);
        viewList.add(view3);

        View view4 = makeView(Color.CYAN);
        viewList.add(view4);

        View view5 = makeView(Color.YELLOW);
        viewList.add(view5);

        bannel.setAdapter(new BannerAdapter(viewList));

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(new MyAdapter(getSupportFragmentManager(),fragments));

    }

    @NonNull
    private View makeView(int green) {
        View view2 = new View(this);
        view2.setBackgroundColor(green);
        view2.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.MATCH_PARENT));
        return view2;
    }

    class BannerAdapter extends PagerAdapter {

        private List<View> mViewList;

        public BannerAdapter(List<View> mViewList) {
            this.mViewList = mViewList;
        }

        @Override
        public int getCount() {//必须实现
            return mViewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {//必须实现
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {//必须实现，实例化
            container.addView(mViewList.get(position));
            return mViewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {//必须实现，销毁
            container.removeView(mViewList.get(position));
        }
    }

    class MyAdapter extends FragmentStatePagerAdapter{

        private List<Fragment> mFragments;

        public MyAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            mFragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }
}
