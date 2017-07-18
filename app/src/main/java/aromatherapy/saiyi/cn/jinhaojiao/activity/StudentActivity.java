package aromatherapy.saiyi.cn.jinhaojiao.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import aromatherapy.saiyi.cn.jinhaojiao.R;
import aromatherapy.saiyi.cn.jinhaojiao.app.MyApplication;
import aromatherapy.saiyi.cn.jinhaojiao.base.BaseActivity;
import aromatherapy.saiyi.cn.jinhaojiao.bean.Student;
import aromatherapy.saiyi.cn.jinhaojiao.fragment.Home;
import aromatherapy.saiyi.cn.jinhaojiao.presenter.GetPersonMsgPresenterImp;
import aromatherapy.saiyi.cn.jinhaojiao.util.DateUtil;
import aromatherapy.saiyi.cn.jinhaojiao.util.Log;
import aromatherapy.saiyi.cn.jinhaojiao.util.Toastor;
import aromatherapy.saiyi.cn.jinhaojiao.view.MsgView;
import aromatherapy.saiyi.cn.jinhaojiao.widget.LoadingDialog;
import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static aromatherapy.saiyi.cn.jinhaojiao.util.AppUtil.stringtoBitmap;

public class StudentActivity extends BaseActivity implements MsgView {
    private final static String TAG = Home.class.getSimpleName();
    private static double STEPNUM = 3000.0;
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
    @BindView(R.id.me_title_iv)
    ImageView me_title_iv;
    @BindView(R.id.home_name_tv)
    TextView home_name_tv;
    @BindView(R.id.speed_tv)
    TextView speedTv;
    @BindView(R.id.time_tv)
    TextView timeTv;
    @BindView(R.id.strength_tv)
    TextView strengthTv;
    @BindView(R.id.load_tv)
    TextView loadTv;
    @BindView(R.id.home_back)
    ImageView homeBack;
    private Map<String, String> map = new HashMap<String, String>();
    private LoadingDialog dialog;
    private boolean isOne = true;
    private Toastor toastor;
    private Handler handler;
    private Runnable myRunnable;
    private Student user;
    private GetPersonMsgPresenterImp getPersonMsgPresenterImp;


    @Override
    protected int getContentView() {
        return R.layout.fragment_home;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        translucentStatusBar();
        user = (Student) getIntent().getSerializableExtra("student");

        toastor = new Toastor(this);
        dialog = new LoadingDialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        handler = new Handler();
        getPersonMsgPresenterImp = new GetPersonMsgPresenterImp(this, this);
        myRunnable = new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "更新数据");
                map.clear();
                map.put("userID", user.getUserID());
                map.put("time", DateUtil.getCurrDate(DateUtil.LONG_DATE_FORMAT2));
                getPersonMsgPresenterImp.loadMsg(map);
                handler.postDelayed(this, 10000);

            }
        };
        initUser();
    }

    private void initUser() {
        homeBack.setVisibility(View.VISIBLE);

        if (user != null) {
            home_name_tv.setText(user.getNikename());
            if (user.getHead_pic() != null && user.getHead_pic().length() > 5) {
                if (user.getHead_pic().contains("http:")) {
                    MyApplication.newInstance().getGlide().load(user.getHead_pic()).into(home_pic_iv);

                } else
                    home_pic_iv.setImageBitmap(stringtoBitmap(user.getHead_pic()));
            } else {
                home_pic_iv.setImageDrawable(getResources().getDrawable(R.mipmap.logo));

            }

            if (user.getSex() != null) {
                if (user.getSex().equals("男")) {
                    home_sex_iv.setImageResource(R.drawable.manwhite);
                } else if (user.getSex().equals("女"))
                    home_sex_iv.setImageResource(R.drawable.nvxingbai);
            }

        }
    }

    @OnClick(R.id.home_back)
    public void ClickBack(View v) {
        finish();
    }

    @OnClick(value = {R.id.home_distance_rl, R.id.home_heartthrob_rl, R.id.home_step_rl, R.id.home_volocity_rl, R.id.home_calorie_rl})
    public void ClickView(View v) {
        if (user != null) {
            switch (v.getId()) {
                case R.id.home_distance_rl:
                    startActivity(new Intent(this, DistanceActivity.class).putExtra("type", 4).putExtra("student", user));
                    break;
                case R.id.home_step_rl:
                    startActivity(new Intent(this, LineDataActivity.class).putExtra("type", 0).putExtra("student", user));
                    break;
                case R.id.home_volocity_rl:
                    startActivity(new Intent(this, VolocityActivity.class).putExtra("type", 3).putExtra("student", user));
                    break;
                case R.id.home_heartthrob_rl:
                    startActivity(new Intent(this, CardiacRate.class).putExtra("type", 2).putExtra("student", user));
                    break;
                case R.id.home_calorie_rl:
                    startActivity(new Intent(this, LineDataActivity.class).putExtra("type", 1).putExtra("student", user));
                    break;
            }
        } else {
            Toast.makeText(this, "请登录查看", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(myRunnable, 0);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(myRunnable);
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
            JSONObject json = jsonObject.optJSONObject("resBody");
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

    private void translucentStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0及以上
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4到5.0
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
    }
}
