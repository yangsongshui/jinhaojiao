package aromatherapy.saiyi.cn.jinhaojiao.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import java.util.List;

public class TestFragmentAdapter extends FragmentStatePagerAdapter {


    private List<Fragment> mFragments;

    public TestFragmentAdapter(FragmentManager fm,List<Fragment> fragments) {
        super(fm);
        mFragments=fragments;

    }

    @Override
    public Fragment getItem(int position) {

        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return  mFragments.size();
    }

    public void setCount(List<Fragment> list) {
        this.mFragments = list;
        notifyDataSetChanged();
    }




    /*继承FragmentStatePagerAdapter重写改方法 数据和页面可以动态更改*/
    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }
}