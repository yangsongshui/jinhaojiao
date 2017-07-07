package aromatherapy.saiyi.cn.jinhaojiao.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by ys on 2017/7/7.
 */

public abstract class LazyLoadFragment extends Fragment {
    /**
     * 视图是否已经初初始化
     */
    protected boolean isInit = false;
    protected boolean isLoad = false;
    protected final String TAG = "LazyLoadFragment";
    private View view;
    Bundle savedInstanceState;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(setContentView(), container, false);
        this.savedInstanceState = savedInstanceState;
        isInit = true;
        unbinder = ButterKnife.bind(this, view);
        /**初始化的时候去加载数据**/
        isCanLoadData();
        return view;
    }

    /**
     * 视图是否已经对用户可见，系统的方法
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isLoad = true;
        isCanLoadData();
    }

    /**
     * 是否可以加载数据
     * 可以加载数据的条件：
     * 1.视图已经初始化
     * 2.视图对用户可见
     */
    private void isCanLoadData() {
        if (!isInit) {
            return;
        }
        if (isLoad) {
            lazyLoad(view, savedInstanceState);
        } else {
            if (isLoad) {
                stopLoad();
            }
        }
    }

    /**
     * 视图销毁的时候讲Fragment是否初始化的状态变为false
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isInit = false;
        isLoad = false;
        unbinder.unbind();

    }


    /**
     * 设置Fragment要显示的布局
     *
     * @return 布局的layoutId
     */
    protected abstract int setContentView();

    /**
     * 当视图初始化并且对用户可见的时候去真正的加载数据
     */
    protected abstract void lazyLoad(View layout, Bundle savedInstanceState);

    /**
     * 当视图已经对用户不可见并且加载过数据，如果需要在切换到其他页面时停止加载数据，可以调用此方法
     */
    protected void stopLoad() {
    }
}
