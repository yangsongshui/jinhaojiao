package aromatherapy.saiyi.cn.jinhaojiao.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.xutils.x;

public abstract class BaseFragment extends Fragment {

    protected View layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(getContentView(), null);
        // 注解Fragment
        x.view().inject(this, layout);
        initData(layout);
        return layout;
    }

    protected abstract void initData(View layout);

    protected abstract int getContentView();

}
