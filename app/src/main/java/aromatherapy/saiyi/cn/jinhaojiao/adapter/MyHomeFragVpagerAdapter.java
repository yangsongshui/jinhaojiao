package aromatherapy.saiyi.cn.jinhaojiao.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;

import java.util.List;

public class MyHomeFragVpagerAdapter extends FragmentPagerAdapter {
	private static final String TAG="MyHomeFragVpagerAdapter";
	
	List<Fragment> mList;
	
	public MyHomeFragVpagerAdapter(FragmentManager fm,List<Fragment> list) {
		super(fm);
		mList=list;
	}

	@Override
	public Fragment getItem(int arg0) {
		return mList.get(arg0);
	}

	@Override
	public int getCount() {
		return mList==null?0:mList.size();
	}

	@Override
	public int getItemPosition(Object object) {
		return PagerAdapter.POSITION_NONE;
	}
}