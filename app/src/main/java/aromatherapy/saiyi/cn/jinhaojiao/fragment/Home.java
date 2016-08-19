package aromatherapy.saiyi.cn.jinhaojiao.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.HashMap;
import java.util.Map;

import aromatherapy.saiyi.cn.jinhaojiao.R;
import aromatherapy.saiyi.cn.jinhaojiao.activity.CardiacRate;
import aromatherapy.saiyi.cn.jinhaojiao.activity.DistanceActivity;
import aromatherapy.saiyi.cn.jinhaojiao.activity.LineDataActivity;
import aromatherapy.saiyi.cn.jinhaojiao.activity.VolocityActivity;
import aromatherapy.saiyi.cn.jinhaojiao.app.MyApplication;
import aromatherapy.saiyi.cn.jinhaojiao.base.BaseFragment;
import aromatherapy.saiyi.cn.jinhaojiao.bean.DeviceInfo;
import aromatherapy.saiyi.cn.jinhaojiao.bean.User;
import aromatherapy.saiyi.cn.jinhaojiao.util.Constant;
import aromatherapy.saiyi.cn.jinhaojiao.util.Log;
import aromatherapy.saiyi.cn.jinhaojiao.util.NormalPostRequest;
import aromatherapy.saiyi.cn.jinhaojiao.util.Toastor;
import aromatherapy.saiyi.cn.jinhaojiao.view.LoadingDialog;
import aromatherapy.saiyi.cn.jinhaojiao.view.RoundProgressBar;
import de.hdodenhof.circleimageview.CircleImageView;

public class Home extends BaseFragment implements Response.ErrorListener {
    private final static String TAG = Home.class.getSimpleName();
    User user;
    private static double STEPNUM=3000.0;
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
    @ViewInject(R.id.home_calorie_tv)
    TextView home_calorie_tv;
    @ViewInject(R.id.textClock)
    TextClock textClock;

    @ViewInject(R.id.home_name_tv)
    TextView home_name_tv;
    @ViewInject(R.id.home_sex_iv)
    ImageView home_sex_iv;
    @ViewInject(R.id.home_pic_iv)
    CircleImageView home_pic_iv;
    @ViewInject(R.id.home_num_tv)
    TextView home_num_tv;
    MyBroadcastReciver reciver;
    private Map<String, String> map = new HashMap<String, String>();
    private LoadingDialog dialog;
    private Toastor toastor;
    private RequestQueue mQueue;
    @Override
    protected void initData(View layout) {
        IntentFilter intentFilter = new IntentFilter("DATA_RECEIVE_BROADCAST");
        reciver = new MyBroadcastReciver();
        getActivity().registerReceiver(reciver, intentFilter);
        Log.e("Home", "initData");
        if (MyApplication.newInstance().getDeviceInfo().getSteps()!=null){
            transferMessage(MyApplication.newInstance().getDeviceInfo());
        }
        toastor = new Toastor(getActivity());
        dialog = new LoadingDialog(getActivity(), "获取数据中...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        mQueue = MyApplication.newInstance().getmQueue();
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

    @Event(value = {R.id.home_distance_rl, R.id.home_heartthrob_rl, R.id.home_step_rl, R.id.home_volocity_rl,R.id.home_calorie_rl})
    private void ClickView(View v) {
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
            if (user.getEquipmentID() != null) {
                map.clear();
                map.put("equipmentID", user.getEquipmentID());
                map.put("time", textClock.getText().toString());
                mQueue.add(normalPostRequest);
            }
        }
    }

    NormalPostRequest normalPostRequest = new NormalPostRequest(Constant.FINDHEARTRATEMOTION, new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            Log.e(TAG, jsonObject.toString());
            dialog.dismiss();
            if (jsonObject.optInt("resCode") == 1) {
                MyApplication.newInstance().outLogin();
                toastor.showToast(jsonObject.optString("resMessage"));
            } else if (jsonObject.optInt("resCode") == 0) {
                JSONObject object=jsonObject.optJSONObject("resBody");
                home_distance_tv.setText(object.optString("distance"));
                home_heartthrob_tv.setText(object.optString("Heartrate"));
                home_step_tv.setText(object.optString("steps"));
                home_volocity_tv.setText(object.optString("speed"));
                home_calorie_tv.setText(object.optString("Calorie"));
            }

        }
    }, this, map);

    public void transferMessage(DeviceInfo deviceInfo) {
        double num=Integer.parseInt(deviceInfo.getSteps())/STEPNUM;
        home_num_tv.setText((int)(num*10)+"");
        rpBar01.setProgress((int)(num*10));
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
            }
        }
    }
    @Override
    public void onErrorResponse(VolleyError volleyError) {
        dialog.dismiss();
        toastor.showToast("服务器异常");
    }
}
