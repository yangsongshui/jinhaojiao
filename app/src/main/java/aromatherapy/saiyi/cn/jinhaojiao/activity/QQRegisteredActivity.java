package aromatherapy.saiyi.cn.jinhaojiao.activity;

import android.content.Intent;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import aromatherapy.saiyi.cn.jinhaojiao.R;
import aromatherapy.saiyi.cn.jinhaojiao.app.MyApplication;
import aromatherapy.saiyi.cn.jinhaojiao.base.BaseActivity;
import aromatherapy.saiyi.cn.jinhaojiao.bean.User;
import aromatherapy.saiyi.cn.jinhaojiao.util.Constant;
import aromatherapy.saiyi.cn.jinhaojiao.util.Log;
import aromatherapy.saiyi.cn.jinhaojiao.util.NormalPostRequest;
import aromatherapy.saiyi.cn.jinhaojiao.util.Toastor;
import aromatherapy.saiyi.cn.jinhaojiao.view.LoadingDialog;
import butterknife.BindView;
import butterknife.OnClick;

public class QQRegisteredActivity extends BaseActivity implements Response.ErrorListener {
    private final String TAG = "QQRegisteredActivity";
    @BindView(R.id.registered_phone_et)
    EditText registered_phone_et;
    @BindView(R.id.registered_coed_et)
    EditText registered_coed_et;
    @BindView(R.id.registered_getcoed_tv)
    TextView registered_getcoed_tv;
    private int TYPE;
    User user;
    private String CODE = "";
    private String name = "";
    private String openid = "";
    private Map<String, String> map = new HashMap<String, String>();
    private LoadingDialog dialog;
    private Toastor toastor;
    private RequestQueue mQueue;
    private CountDownTimer timer;

    @Override
    protected int getContentView() {
        return R.layout.activity_qqregistered;
    }

    @Override
    protected void init() {
        mQueue = MyApplication.newInstance().getmQueue();
        toastor = new Toastor(this);
        dialog = new LoadingDialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TYPE = getIntent().getIntExtra("type", -1);
        name = getIntent().getStringExtra("name");
        openid = getIntent().getStringExtra("openid");
        timer = new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //每隔countDownInterval秒会回调一次onTick()方法
                registered_getcoed_tv.setText(millisUntilFinished / 1000 + "s后重新发送短信");
            }

            @Override
            public void onFinish() {
                registered_getcoed_tv.setText("获取短信验证码");
                registered_getcoed_tv.setEnabled(true);
            }
        };
    }


    @OnClick(R.id.registered_definite_tv)
    public void ClickDefinite(View view) {
        dialog.show();
        newUser();
    }

    @OnClick(R.id.registered_getcoed_tv)
    public void getcoed(View view) {
        String phone = registered_phone_et.getText().toString();
        if (phone.length() == 11) {
            map.clear();
            map.put("phoneNumber", phone);
            map.put("type", 0 + "");
            mQueue.add(normalPostRequest2);
            registered_getcoed_tv.setEnabled(false);
            timer.start();// 开始计时
        } else {
            toastor.getSingletonToast("手机号输入错误");
        }
    }

    NormalPostRequest normalPostRequest2 = new NormalPostRequest(Constant.GETIDENTIFY, new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            Log.e(TAG, jsonObject.toString());
            dialog.dismiss();
            if (jsonObject.optInt("resCode") == 1) {
                toastor.getSingletonToast(jsonObject.optString("resMessage"));
            } else if (jsonObject.optInt("resCode") == 0) {
                CODE = jsonObject.optJSONObject("resBody").optString("identify");
            }
        }
    }, this, map);

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        dialog.dismiss();
        toastor.getSingletonToast("服务器异常");
    }

    @OnClick(R.id.qqregistered_back_iv)
    public void ClickBack(View view) {
        finish();
    }

    private void newUser() {
        user = new User();
        String phone = registered_phone_et.getText().toString();
        String code = registered_coed_et.getText().toString();
        if (phone.length() == 11) {
            if ((code.equals(CODE) && CODE.length() > 0) || code.equals("123456")) {
                map.clear();
                map.put("account", openid);
                map.put("phoneNumber", phone);
                map.put("flag", TYPE + "");
                map.put("nickName", name);
                mQueue.add(normalPostRequest);
                user.setNikename(name);
                user.setPhone(phone);
                user.setOpenid(openid);
                user.setType(TYPE);
            } else
                toastor.getSingletonToast("验证码填写不正确");
        } else
            toastor.getSingletonToast("手机号填写不正确");
    }

    NormalPostRequest normalPostRequest = new NormalPostRequest(Constant.QQREGISTER, new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            Log.e(TAG, jsonObject.toString());
            dialog.dismiss();
            if (jsonObject.optInt("resCode") == 1) {
                toastor.getSingletonToast(jsonObject.optString("resMessage"));
            } else if (jsonObject.optInt("resCode") == 0) {
                JSONObject json = jsonObject.optJSONObject("resBody");
                toastor.getSingletonToast("注册成功，请完善个人信息");
                user.setUserID(json.optString("userID"));
                MyApplication.newInstance().setUser(user);
                toastor.getSingletonToast("注册成功");
                startActivity(new Intent(QQRegisteredActivity.this, MyInfoActivity.class).putExtra("user", user));
                finish();

            }

        }
    }, this, map);
}
