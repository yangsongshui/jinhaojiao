package aromatherapy.saiyi.cn.jinhaojiao.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import aromatherapy.saiyi.cn.jinhaojiao.R;
import aromatherapy.saiyi.cn.jinhaojiao.activity.CardiacRate;
import aromatherapy.saiyi.cn.jinhaojiao.activity.DistanceActivity;
import aromatherapy.saiyi.cn.jinhaojiao.activity.LineDataActivity;
import aromatherapy.saiyi.cn.jinhaojiao.activity.VolocityActivity;
import aromatherapy.saiyi.cn.jinhaojiao.app.MyApplication;
import aromatherapy.saiyi.cn.jinhaojiao.base.BaseFragment;
import aromatherapy.saiyi.cn.jinhaojiao.bean.User;
import aromatherapy.saiyi.cn.jinhaojiao.presenter.GetPersonMsgPresenterImp;
import aromatherapy.saiyi.cn.jinhaojiao.util.Constant;
import aromatherapy.saiyi.cn.jinhaojiao.util.DateUtil;
import aromatherapy.saiyi.cn.jinhaojiao.util.Log;
import aromatherapy.saiyi.cn.jinhaojiao.util.SpUtils;
import aromatherapy.saiyi.cn.jinhaojiao.util.Toastor;
import aromatherapy.saiyi.cn.jinhaojiao.view.MsgView;
import aromatherapy.saiyi.cn.jinhaojiao.widget.LoadingDialog;
import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static aromatherapy.saiyi.cn.jinhaojiao.util.AppUtil.stringtoBitmap;


public class Home extends BaseFragment implements MsgView {
    private final static String TAG = Home.class.getSimpleName();
    private String percent = "%";
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
    @BindView(R.id.home_sex_iv)
    ImageView home_sex_iv;
    @BindView(R.id.home_pic_iv)
    CircleImageView home_pic_iv;
    @BindView(R.id.home_name_tv)
    TextView home_name_tv;
    @BindView(R.id.speed_tv)
    TextView speedTv;
    @BindView(R.id.time_tv)
    TextView timeTv;
    @BindView(R.id.load_tv)
    TextView loadTv;
    @BindView(R.id.strength_tv)
    TextView strengthTv;
    @BindView(R.id.me_title_iv)
    ImageView me_title_iv;
    MyBroadcastReciver reciver;

    private Map<String, String> map = new HashMap<String, String>();
    private LoadingDialog dialog;
    private boolean isOne = true;
    private Toastor toastor;
    private Handler handler;
    private Runnable myRunnable;
    private User user;
    private GetPersonMsgPresenterImp getPersonMsgPresenterImp;

    @Override
    protected void initData(View layout, Bundle savedInstanceState) {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("QQ_ABEL_ACTION_BROADCAST");
        reciver = new MyBroadcastReciver();
        getActivity().registerReceiver(reciver, intentFilter);
        toastor = new Toastor(getActivity());
        dialog = new LoadingDialog(getActivity());
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        handler = new Handler();
        getPersonMsgPresenterImp = new GetPersonMsgPresenterImp(this, getActivity());
        myRunnable = new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "更新数据");
                map.clear();
                map.put("userID", user.getUserID());
                map.put("time", DateUtil.getCurrDate(DateUtil.LONG_DATE_FORMAT2));

                getPersonMsgPresenterImp.loadMsg(map);
                //handler.postDelayed(this, 10000);

            }
        };
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_home;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(reciver);
        handler.removeCallbacks(myRunnable);
    }

    @OnClick(value = {R.id.home_distance_rl, R.id.home_heartthrob_rl, R.id.home_step_rl, R.id.home_volocity_rl, R.id.home_calorie_rl})
    public void ClickView(View v) {
        if (user != null && MyApplication.newInstance().isLogin) {
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
        if (user != null && MyApplication.newInstance().isLogin) {
            home_name_tv.setText(user.getNikename());
            if (user.getHead_pic() != null && user.getHead_pic().length() > 5) {
                if (user.getHead_pic().contains("http:")) {
                    MyApplication.newInstance().getGlide().load(user.getHead_pic()).into(home_pic_iv);
                } else
                    home_pic_iv.setImageBitmap(stringtoBitmap(user.getHead_pic()));
            } else {
                home_pic_iv.setImageDrawable(getResources().getDrawable(R.mipmap.logo));

            }
            String bg = SpUtils.getString(Constant.IMAGE_FILE_NAME, "");
            if (bg.length() > 1) {
                MyApplication.newInstance().getGlide().load(new File(bg)).centerCrop().diskCacheStrategy(DiskCacheStrategy.RESULT).into(me_title_iv);
            } else {
                me_title_iv.setBackground(getResources().getDrawable(R.drawable.dakuai));
            }
            if (user.getSex() != null) {
                if (user.getSex().equals("男")) {
                    home_sex_iv.setImageResource(R.drawable.manwhite);
                } else if (user.getSex().equals("女"))
                    home_sex_iv.setImageResource(R.drawable.nvxingbai);
            }
            handler.postDelayed(myRunnable, 0);
        }
    }

    @Override
    public void showProgress() {
        if (dialog != null && !dialog.isShowing() && isOne) {
            dialog.show();
            isOne = false;
        }

    }

    @Override
    public void disimissProgress() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    public void loadDataSuccess(JSONObject jsonObject) {
        Log.e(TAG, jsonObject.toString());
        if (isOne)
            toastor.showSingletonToast(jsonObject.optString("resMessage"));
        if (jsonObject.optInt("resCode") == 0) {
            JSONObject json = jsonObject.optJSONObject("resBody").optJSONObject("personMsgMap");
            home_distance_tv.setText(json.optString("calorie"));
            home_calorie_tv.setText(json.optString("distance"));
            home_heartthrob_tv.setText(json.optString("rate"));
            home_volocity_tv.setText(json.optString("speed"));
            home_step_tv.setText(json.optString("step"));
            speedTv.setText(json.optString("acceleration") + percent);
            timeTv.setText(json.optString("sportMin") + percent);
            strengthTv.setText(json.optString("sportStrength") + percent);
            loadTv.setText(json.optString("sportLoad") + percent);
        }
    }

    @Override
    public void loadDataError(Throwable throwable) {
        toastor.showSingletonToast("服务器连接失败");
    }


    private class MyBroadcastReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("QQ_ABEL_ACTION_BROADCAST")) {
                initUser();

            }
        }

    }


}
