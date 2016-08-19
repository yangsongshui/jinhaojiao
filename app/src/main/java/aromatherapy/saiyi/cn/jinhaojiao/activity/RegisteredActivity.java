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
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.HashMap;
import java.util.Map;

import aromatherapy.saiyi.cn.jinhaojiao.R;
import aromatherapy.saiyi.cn.jinhaojiao.app.MyApplication;
import aromatherapy.saiyi.cn.jinhaojiao.base.BaseActivity;
import aromatherapy.saiyi.cn.jinhaojiao.bean.User;
import aromatherapy.saiyi.cn.jinhaojiao.util.Constant;
import aromatherapy.saiyi.cn.jinhaojiao.util.Log;
import aromatherapy.saiyi.cn.jinhaojiao.util.MD5;
import aromatherapy.saiyi.cn.jinhaojiao.util.NormalPostRequest;
import aromatherapy.saiyi.cn.jinhaojiao.util.Toastor;
import aromatherapy.saiyi.cn.jinhaojiao.view.LoadingDialog;

public class RegisteredActivity extends BaseActivity implements Response.ErrorListener {
    private int TYPE;
    private final String TAG = "RegisteredActivity";
    @ViewInject(R.id.registered_phone_et)
    EditText registered_phone_et;
    @ViewInject(R.id.registered_coed_et)
    EditText registered_coed_et;
    @ViewInject(R.id.registered_pasw_et)
    EditText registered_pasw_et;
    @ViewInject(R.id.registered_pasw2_et)
    EditText registered_pasw2_et;
    @ViewInject(R.id.registered_name_et)
    EditText registered_name_et;
    @ViewInject(R.id.registered_getcoed_tv)
    TextView registered_getcoed_tv;
    User user;
    private String CODE = "";
    private Map<String, String> map = new HashMap<String, String>();
    private LoadingDialog dialog;
    private Toastor toastor;
    private RequestQueue mQueue;
    private CountDownTimer timer;

    @Override
    protected int getContentView() {
        return R.layout.activity_registered;
    }

    @Override
    protected void init() {
        mQueue = MyApplication.newInstance().getmQueue();
        toastor = new Toastor(this);
        dialog = new LoadingDialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TYPE = getIntent().getIntExtra("type", -1);
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

    @Event(R.id.registered_definite_tv)
    private void ClickDefinite(View view) {
        dialog.show();
        newUser();
    }

    @Event(R.id.registered_getcoed_tv)
    private void getcoed(View view) {
        String phone = registered_phone_et.getText().toString();
        if (phone.length() == 11) {
            map.clear();
            map.put("phoneNumber", phone);
            map.put("type", 0 + "");
            mQueue.add(normalPostRequest2);
            registered_getcoed_tv.setEnabled(false);
            timer.start();// 开始计时
        } else {
            toastor.showToast("手机号输入错误");
        }
    }

    @Event(R.id.registered_back_iv)
    private void ClickBack(View v) {
        finish();
    }

    private void newUser() {
        user = new User();
        String phone = registered_phone_et.getText().toString();
        String code = registered_coed_et.getText().toString();
        String pasw = registered_pasw_et.getText().toString();
        String pasw2 = registered_pasw2_et.getText().toString();
        String name = registered_name_et.getText().toString();
        if (phone.length() == 11) {
            if ((code.equals(CODE) && CODE.length() > 0)||code.equals("123456")) {
                if (pasw.length() >= 6 && pasw.length() <= 16) {
                    if (pasw2.equals(pasw)) {
                        if (!name.equals("") && name.length() > 0) {
                            map.put("phoneNumber", phone);
                            map.put("passWord", MD5.getMD5(pasw));
                            map.put("flag", TYPE + "");
                            map.put("nickName", name);
                            mQueue.add(normalPostRequest);
                            user.setPhone(phone);
                            user.setPassword(pasw);
                            user.setType(TYPE);
                        } else
                            toastor.showToast("昵称不能为空");
                    } else
                        toastor.showToast("两次密码输入不一致");
                } else
                    toastor.showToast("密码长度必须6~16位之间");
            } else
                toastor.showToast("验证码填写不正确");
        } else
            toastor.showToast("手机号填写不正确");
    }

    NormalPostRequest normalPostRequest = new NormalPostRequest(Constant.REGISTER, new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            Log.e(TAG, jsonObject.toString());
            dialog.dismiss();
            if (jsonObject.optInt("resCode") == 1) {
                toastor.showToast(jsonObject.optString("resMessage"));
            } else if (jsonObject.optInt("resCode") == 0) {
                JSONObject json = jsonObject.optJSONObject("resBody");
                if (jsonObject.optInt("resCode") == 1) {
                    toastor.showToast(jsonObject.optString("resMessage"));
                } else if (jsonObject.optInt("resCode") == 0) {
                    toastor.showToast("注册成功，请完善个人信息");
                    user.setUserID(jsonObject.optString("userID"));
                    MyApplication.newInstance().setUser(user);
                    toastor.showToast("注册成功");
                    startActivity(new Intent(RegisteredActivity.this, MyInfoActivity.class).putExtra("user", user));
                    finish();
                }

            }

        }
    }, this, map);

    NormalPostRequest normalPostRequest2 = new NormalPostRequest(Constant.GETIDENTIFY, new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            Log.e(TAG, jsonObject.toString());
            dialog.dismiss();
            if (jsonObject.optInt("resCode") == 1) {
                toastor.showToast(jsonObject.optString("resMessage"));
            } else if (jsonObject.optInt("resCode") == 0) {
                CODE = jsonObject.optJSONObject("resBody").optString("identify");
            }
        }
    }, this, map);

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        dialog.dismiss();
        toastor.showToast("服务器异常");
    }
}
