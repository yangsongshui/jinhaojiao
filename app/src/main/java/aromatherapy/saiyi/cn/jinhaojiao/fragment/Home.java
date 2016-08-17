package aromatherapy.saiyi.cn.jinhaojiao.fragment;


import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import aromatherapy.saiyi.cn.jinhaojiao.R;
import aromatherapy.saiyi.cn.jinhaojiao.activity.CardiacRate;
import aromatherapy.saiyi.cn.jinhaojiao.activity.DistanceActivity;
import aromatherapy.saiyi.cn.jinhaojiao.activity.LineDataActivity;
import aromatherapy.saiyi.cn.jinhaojiao.activity.VolocityActivity;
import aromatherapy.saiyi.cn.jinhaojiao.app.MyApplication;
import aromatherapy.saiyi.cn.jinhaojiao.base.BaseFragment;
import aromatherapy.saiyi.cn.jinhaojiao.bean.User;
import aromatherapy.saiyi.cn.jinhaojiao.view.RoundProgressBar;
import de.hdodenhof.circleimageview.CircleImageView;

public class Home extends BaseFragment {
    User user;

    @ViewInject(R.id.roundProgressBar01_id)
    RoundProgressBar rpBar01;
    @ViewInject(R.id.home_distance_tv)
    TextView home_distance_tv;
    @ViewInject(R.id.home_heartthrob_tv)
    TextView home_heartthrob_tv;
    @ViewInject(R.id.home_volocity_tv)
    TextView home_volocity_tv;
    @ViewInject(R.id.home_step_tv)
    TextView home_step_tv;


    @ViewInject(R.id.home_name_tv)
    TextView home_name_tv;
    @ViewInject(R.id.home_sex_iv)
    ImageView home_sex_iv;
    @ViewInject(R.id.home_pic_iv)
    CircleImageView home_pic_iv;
    @ViewInject(R.id.home_num_tv)
    TextView home_num_tv;

    @Override
    protected void initData(View layout) {
        rpBar01.setProgress(60);
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_home;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Event(value = { R.id.home_distance_rl, R.id.home_heartthrob_rl, R.id.home_step_rl, R.id.home_volocity_rl})
    private void ClickView(View v) {
        switch (v.getId()){

            case R.id.home_distance_rl:
                startActivity(new Intent(getActivity(), DistanceActivity.class).putExtra("type", 4));
                break;
            case R.id.home_step_rl:
                startActivity(new Intent(getActivity(), LineDataActivity.class).putExtra("type",0));
                break;
            case R.id.home_volocity_rl:
                startActivity(new Intent(getActivity(), VolocityActivity.class).putExtra("type", 3));
                break;
            case R.id.home_heartthrob_rl:
                startActivity(new Intent(getActivity(), CardiacRate.class).putExtra("type",2));
                break;
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        user = MyApplication.newInstance().getUser();
        if (user != null) {
            home_name_tv.setText(user.getNikename());
            home_pic_iv.setImageBitmap(user.getBitmap());
            if (user.getSex().equals("男")) {
                home_sex_iv.setImageResource(R.drawable.man);
            } else if (user.getSex().equals("男"))
                home_sex_iv.setImageResource(R.drawable.lady);
        }
    }
}
