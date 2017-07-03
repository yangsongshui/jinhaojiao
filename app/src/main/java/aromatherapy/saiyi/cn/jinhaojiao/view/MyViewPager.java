package aromatherapy.saiyi.cn.jinhaojiao.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyViewPager extends ViewPager {
	private static final String TAG = "MyViewPager";

	private boolean isDisallowParentInterceptTouchEvent;
	private boolean isBroadCast = true; // 是否广告条类型的viewpager，否则为碎片类型的viewpager

	private int currentPageIndex;

	public MyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	float oldX;

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// 页数
		int pagesNum = getAdapter().getCount();
		// 设置屏幕滑动事件的分配
		if (isBroadCast) {
			if (isDisallowParentInterceptTouchEvent)
				getParent().requestDisallowInterceptTouchEvent(true);

		} else {
			if (currentPageIndex == pagesNum - 1) {
				float x = ev.getX();
				switch (ev.getAction()) {
				case MotionEvent.ACTION_DOWN:
					oldX = x;
					break;
				case MotionEvent.ACTION_MOVE:
					if ((oldX - x) > 0)
						getParent().requestDisallowInterceptTouchEvent(false);
					else
						getParent().requestDisallowInterceptTouchEvent(true);
					oldX = x;
					break;
				}
			} else {
				if (isDisallowParentInterceptTouchEvent)
					getParent().requestDisallowInterceptTouchEvent(true);
			}
		}
		return super.dispatchTouchEvent(ev);
	}

	public boolean isDisallowParentInterceptTouchEvent() {
		return isDisallowParentInterceptTouchEvent;
	}

	public void setDisallowParentInterceptTouchEvent(
			boolean isDisallowParentInterceptTouchEvent) {
		this.isDisallowParentInterceptTouchEvent = isDisallowParentInterceptTouchEvent;
	}

	public boolean isBroadCast() {
		return isBroadCast;
	}

	public void setBroadCast(boolean isBroadCast) {
		this.isBroadCast = isBroadCast;
	}

	public int getCurrentPageIndex() {
		return currentPageIndex;
	}

	public void setCurrentPageIndex(int currentPageIndex) {
		this.currentPageIndex = currentPageIndex;
	}
}
