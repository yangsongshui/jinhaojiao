package aromatherapy.saiyi.cn.jinhaojiao.base;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Window;

import aromatherapy.saiyi.cn.jinhaojiao.app.MyApplication;
import butterknife.ButterKnife;


public abstract class BaseActivity extends FragmentActivity {
    //添加到活动管理集合中
    {
        MyApplication.newInstance().addActyToList(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getContentView());
        ButterKnife.bind(this);
        //用于显示当前位于哪个活动
        Log.d("BaseActivity", getClass().getSimpleName());
        init(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.newInstance().removeActyFromList(this);
    }

    //注入布局
    protected abstract int getContentView();

    //初始化
    protected abstract void init(Bundle savedInstanceState);
}
