package aromatherapy.saiyi.cn.jinhaojiao.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import aromatherapy.saiyi.cn.jinhaojiao.R;
import aromatherapy.saiyi.cn.jinhaojiao.activity.CardiacRate;
import aromatherapy.saiyi.cn.jinhaojiao.activity.DistanceActivity;
import aromatherapy.saiyi.cn.jinhaojiao.activity.LineDataActivity;
import aromatherapy.saiyi.cn.jinhaojiao.activity.VolocityActivity;
import aromatherapy.saiyi.cn.jinhaojiao.app.MyApplication;
import aromatherapy.saiyi.cn.jinhaojiao.base.BaseFragment;
import aromatherapy.saiyi.cn.jinhaojiao.bean.DeviceInfo;
import aromatherapy.saiyi.cn.jinhaojiao.bean.User;
import aromatherapy.saiyi.cn.jinhaojiao.util.Log;
import aromatherapy.saiyi.cn.jinhaojiao.view.RoundProgressBar;
import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

;

public class Home extends BaseFragment {
    private final static String TAG = Home.class.getSimpleName();
    User user;
    private static double STEPNUM = 3000.0;
    @BindView(R.id.roundProgressBar01_id)
    RoundProgressBar rpBar01;
    @BindView(R.id.home_distance_tv)
    TextView home_distance_tv;
    @BindView(R.id.home_heartthrob_tv)
    TextView home_heartthrob_tv;
    @BindView(R.id.home_volocity_tv)
    TextView home_volocity_tv;
    @BindView(R.id.home_step_tv)
    TextView home_step_tv;
    @BindView(R.id.home_calorie_tv)
    TextView home_calorie_tv;


    @BindView(R.id.home_name_tv)
    TextView home_name_tv;
    @BindView(R.id.home_sex_iv)
    ImageView home_sex_iv;
    @BindView(R.id.home_pic_iv)
    CircleImageView home_pic_iv;
    @BindView(R.id.home_num_tv)
    TextView home_num_tv;
    MyBroadcastReciver reciver;



    @Override
    protected void initData(View layout, Bundle savedInstanceState) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("DATA_RECEIVE_BROADCAST");
        intentFilter.addAction("CN_ABEL_ACTION_BROADCAST");
        reciver = new MyBroadcastReciver();
        getActivity().registerReceiver(reciver, intentFilter);
        if (MyApplication.newInstance().getDeviceInfo().getSteps() != null) {
            transferMessage(MyApplication.newInstance().getDeviceInfo());
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_home;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(reciver);
    }

    @OnClick(value = {R.id.home_distance_rl, R.id.home_heartthrob_rl, R.id.home_step_rl, R.id.home_volocity_rl, R.id.home_calorie_rl})
    public void ClickView(View v) {
        if (user != null) {
            Log.e("----","123456"+user.getEquipmentID());
            if (user.getEquipmentID() != null&&user.getEquipmentID().length()>0) {
                switch (v.getId()) {
                    case R.id.home_distance_rl:
                        startActivity(new Intent(getActivity(), DistanceActivity.class).putExtra("type", 4));
                        break;
                    case R.id.home_step_rl:
                        startActivity(new Intent(getActivity(), LineDataActivity.class).putExtra("type", 0));
                        break;
                    case R.id.home_volocity_rl:
                        startActivity(new Intent(getActivity(), VolocityActivity.class).putExtra("type", 3));
                        break;
                    case R.id.home_heartthrob_rl:
                        startActivity(new Intent(getActivity(), CardiacRate.class).putExtra("type", 2));
                        break;
                    case R.id.home_calorie_rl:
                        startActivity(new Intent(getActivity(), LineDataActivity.class).putExtra("type", 1));
                        break;
                }
            } else {
                Toast.makeText(getActivity(), "未绑定设备", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "请登录查看", Toast.LENGTH_SHORT).show();
        }


    }


    @Override
    public void onResume() {
        super.onResume();
        initUser();
    }

    private void initUser() {
        user = MyApplication.newInstance().getUser();
        if (user != null) {
            home_name_tv.setText(user.getNikename());
            if (user.getBitmap() != null) {
                home_pic_iv.setImageBitmap(user.getBitmap());
            } else {
                home_pic_iv.setImageDrawable(getResources().getDrawable(R.drawable.movie_2));
            }
            if (user.getSex() != null) {
                if (user.getSex().equals("男")) {
                    home_sex_iv.setImageResource(R.drawable.man);
                } else if (user.getSex().equals("女"))
                    home_sex_iv.setImageResource(R.drawable.lady);
            }

        }
    }

    public void transferMessage(DeviceInfo deviceInfo) {
        double num = Integer.parseInt(deviceInfo.getSteps()) / STEPNUM;
        if (num>=100){
            num=100;
            home_num_tv.setText((int) (num * 10) + "");
            rpBar01.setProgress((int) (num * 10));
        }else {
            home_num_tv.setText((int) (num * 10) + "");
            rpBar01.setProgress((int) (num * 10));
        }

        home_distance_tv.setText(deviceInfo.getDistance());
        home_heartthrob_tv.setText(deviceInfo.getHeartrate());
        home_step_tv.setText(deviceInfo.getSteps());
        home_volocity_tv.setText(deviceInfo.getSpeed());
        home_calorie_tv.setText(deviceInfo.getCalorie());
        Log.e("home", "由Activity传输过来的信息");
    }

    private class MyBroadcastReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("DATA_RECEIVE_BROADCAST")) {
                transferMessage((DeviceInfo) intent.getSerializableExtra("device"));
            } else if (action.equals("CN_ABEL_ACTION_BROADCAST")) {
                initUser();
            }
        }
    }

}
