package aromatherapy.saiyi.cn.jinhaojiao.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import aromatherapy.saiyi.cn.jinhaojiao.R;
import aromatherapy.saiyi.cn.jinhaojiao.activity.LoginActivity;
import aromatherapy.saiyi.cn.jinhaojiao.base.BaseFragment;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/5/26.
 */
public class LoginFrag extends BaseFragment  {



    @Override
    protected void initData(View layout, Bundle savedInstanceState) {

    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_login;
    }

    @OnClick(R.id.login_login_tv)
    public void ClickLogin(View view) {
        startActivityForResult(new Intent(getActivity(), LoginActivity.class),1);
    }


}
