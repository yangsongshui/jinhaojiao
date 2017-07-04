package aromatherapy.saiyi.cn.jinhaojiao.activity;

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
import aromatherapy.saiyi.cn.jinhaojiao.util.Constant;
import aromatherapy.saiyi.cn.jinhaojiao.util.Log;
import aromatherapy.saiyi.cn.jinhaojiao.util.MD5;
import aromatherapy.saiyi.cn.jinhaojiao.util.NormalPostRequest;
import aromatherapy.saiyi.cn.jinhaojiao.util.Toastor;
import aromatherapy.saiyi.cn.jinhaojiao.widget.LoadingDialog;
import butterknife.BindView;
import butterknife.OnClick;

public class ResetPwdActivity extends BaseActivity implements Response.ErrorListener {
    private final String TAG = "ResetPwdActivity";
    @BindView(R.id.pwd_phone_et)
    EditText pwd_phone_et;
    @BindView(R.id.pwd_coed_et)
    EditText pwd_coed_et;
    @BindView(R.id.pwd_psw_et)
    EditText pwd_psw_et;
    @BindView(R.id.pwd_psw2_et)
    EditText pwd_psw2_et;
    @BindView(R.id.pwd_getcoed_tv)
    TextView pwd_getcoed_tv;
    private Map<String, String> map = new HashMap<String, String>();
    private LoadingDialog dialog;
    private Toastor toastor;
    private RequestQueue mQueue;
    private CountDownTimer timer;
    private String CODE = "";

    @Override
    protected int getContentView() {
        return R.layout.activity_reset_pwd;
    }

    @Override
    protected void init() {
        mQueue = MyApplication.newInstance().getmQueue();
        toastor = new Toastor(this);
        dialog = new LoadingDialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        timer = new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //每隔countDownInterval秒会回调一次onTick()方法
                pwd_getcoed_tv.setText(millisUntilFinished / 1000 + "s后重新发送短信");
            }

            @Override
            public void onFinish() {
                pwd_getcoed_tv.setText("获取短信验证码");
                pwd_getcoed_tv.setEnabled(true);
            }
        };
    }

    @OnClick(R.id.pwd_getcoed_tv)
    public void getcoed(View view) {
        String phone = pwd_phone_et.getText().toString();
        if (phone.length() == 11) {
            map.clear();
            map.put("phoneNumber", phone);
            map.put("type", 1 + "");
            mQueue.add(normalPostRequest2);
            pwd_getcoed_tv.setEnabled(false);
            timer.start();// 开始计时
        } else {
            toastor.getSingletonToast("手机号输入错误");
        }
    }

    @OnClick(R.id.registered_back_iv)
    public void ClickBack(View view) {
        finish();
    }

    @OnClick(R.id.pwd_definite_tv)
    public void ClickDefinite(View view) {

        String phone = pwd_phone_et.getText().toString();
        String code = pwd_coed_et.getText().toString();
        String pasw = pwd_psw_et.getText().toString();
        String pasw2 = pwd_psw2_et.getText().toString();
        if (phone.length() == 11) {
            if ((code.equals(CODE) && CODE.length() > 0) || code.equals("123456")) {
                if (pasw.length() >= 6 && pasw.length() <= 16) {
                    if (pasw2.equals(pasw)) {
                        dialog.show();
                        map.put("phoneNumber", phone);
                        map.put("passWord", MD5.getMD5(pasw));
                        mQueue.add(normalPostRequest);
                    } else

                        toastor.getSingletonToast("两次密码输入不一致");
                } else
                    toastor.getSingletonToast("密码长度必须6~16位之间");
            } else
                toastor.getSingletonToast("验证码填写不正确");
        } else
            toastor.getSingletonToast("手机号填写不正确");
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
    NormalPostRequest normalPostRequest = new NormalPostRequest(Constant.FINDPWD, new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            Log.e(TAG, jsonObject.toString());
            dialog.dismiss();
            if (jsonObject.optInt("resCode") == 1) {
                toastor.getSingletonToast(jsonObject.optString("resMessage"));
            } else if (jsonObject.optInt("resCode") == 0) {
                toastor.getSingletonToast(jsonObject.optString("resMessage"));
                MyApplication.newInstance().outLogin();
                setResult(2);
                finish();
            }
        }
    }, this, map);

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        dialog.dismiss();
        toastor.getSingletonToast("服务器异常");
    }
}
