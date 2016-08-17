package aromatherapy.saiyi.cn.jinhaojiao.fragment;

import android.content.Intent;
import android.view.View;

import org.xutils.view.annotation.Event;

import aromatherapy.saiyi.cn.jinhaojiao.R;
import aromatherapy.saiyi.cn.jinhaojiao.activity.LoginActivity;
import aromatherapy.saiyi.cn.jinhaojiao.base.BaseFragment;

/**
 * Created by Administrator on 2016/5/26.
 */
public class LoginFrag extends BaseFragment {

    @Override
    protected void initData(View layout) {

    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_login;
    }

    @Event(R.id.login_login_tv)
    private void ClickLogin(View view) {
        startActivityForResult(new Intent(getActivity(), LoginActivity.class),1);
    }
}
