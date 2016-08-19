package aromatherapy.saiyi.cn.jinhaojiao.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

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

public class LoginActivity extends BaseActivity implements Response.ErrorListener {
    private final static String TAG = LoginActivity.class.getSimpleName();
    @ViewInject(R.id.login_pasw_et)
    EditText login_pasw_et;
    @ViewInject(R.id.login_phone_et)
    EditText login_phone_et;
    private UMShareAPI mShareAPI;
    private Map<String, String> map = new HashMap<String, String>();
    private LoadingDialog dialog;
    private Toastor toastor;
    private RequestQueue mQueue;

    @Override
    protected int getContentView() {
        return R.layout.activity_login;
    }

    @Override
    protected void init() {
        mShareAPI = UMShareAPI.get(this);
        toastor = new Toastor(this);
        dialog = new LoadingDialog(this, "登陆中...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        mQueue = MyApplication.newInstance().getmQueue();
    }


    @Event(value = {R.id.login_login_tv, R.id.login_pasw_tv, R.id.login_back_iv,
            R.id.login_registered_tv})
    private void ClickView(View view) {
        switch (view.getId()) {
            case R.id.login_login_tv:
                login();
                break;
            case R.id.login_pasw_tv:
                startActivity(new Intent(this,ResetPwdActivity.class));
                break;
            case R.id.login_back_iv:
                finish();
                break;
            case R.id.login_registered_tv:
                showDialog();
                break;

        }
    }

    @Event(value = {R.id.login_qq_iv, R.id.login_wechat_iv})
    private void ClickLoing(View view) {
        SHARE_MEDIA platform = null;
        switch (view.getId()) {
            case R.id.login_wechat_iv:
                mShareAPI.isInstall(this, SHARE_MEDIA.WEIXIN);
                platform = SHARE_MEDIA.WEIXIN;
                break;
            case R.id.login_qq_iv:
                mShareAPI.isInstall(this, SHARE_MEDIA.QQ);
                platform = SHARE_MEDIA.QQ;
                break;
        }
        mShareAPI.doOauthVerify(LoginActivity.this, platform, umAuthListener);
    }

    private void login() {
        if (login_phone_et.getText().toString().length() == 11) {
            if (login_pasw_et.getText().toString().trim().length() >= 6 && login_pasw_et.getText().toString().trim().length() <= 16) {
                Log.e(TAG, "登陆");
                dialog.show();
                map.clear();
                map.put("phoneNumber", login_phone_et.getText().toString());
                map.put("passWord", MD5.getMD5(login_pasw_et.getText().toString()));
                mQueue.add(normalPostRequest);
            } else {
                toastor.showToast("密码长度不正确");
            }
        } else {
            toastor.showToast("手机号码输入错误");
        }

    }

    NormalPostRequest normalPostRequest = new NormalPostRequest(Constant.LOGIN, new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            Log.e(TAG, jsonObject.toString());
            dialog.dismiss();
            if (jsonObject.optInt("resCode") == 1) {
                MyApplication.newInstance().outLogin();
                toastor.showToast(jsonObject.optString("resMessage"));
            } else if (jsonObject.optInt("resCode") == 0) {
                JSONObject json = jsonObject.optJSONObject("resBody");
                User user = new User(login_phone_et.getText().toString(), login_pasw_et.getText().toString());
                user.setUserID(json.optString("userID"));
                user.setSex(json.optString("sex"));
                if (json.optInt("flag") == 1) {
                    user.setType(1);
                } else
                    user.setType(0);
                user.setNikename(json.optString("nickname"));
                if (json.optString("equipmentID").length() > 0) {
                    user.setEquipmentID(json.optString("equipmentID"));
                }
                if (json.optString("equipment").length() > 0) {
                    user.setEquipment(json.optString("equipment"));
                }
                MyApplication.newInstance().setUser(user);
                toastor.showToast("登陆成功");
                setResult(1);
                finish();

            }

        }
    }, this, map);

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("选择身份");
        builder.setPositiveButton("教练", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(LoginActivity.this, RegisteredActivity.class).putExtra("type", 0));
            }
        });
        builder.setNegativeButton("学员", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(LoginActivity.this, RegisteredActivity.class).putExtra("type", 1));
            }
        });
        builder.create().show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MyApplication.newInstance().getUser() != null) {
            setResult(1);
            finish();
        }
    }

    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            Toast.makeText(getApplicationContext(), "Authorize succeed", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            Toast.makeText(getApplicationContext(), "Authorize fail", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Toast.makeText(getApplicationContext(), "Authorize cancel", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("auth", "on activity re 2");
        mShareAPI.onActivityResult(requestCode, resultCode, data);
        Log.d("auth", "on activity re 3");
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        dialog.dismiss();
        toastor.showToast("服务器异常");
        Log.e(TAG, "服务器异常");
    }
}
