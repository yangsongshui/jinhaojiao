package aromatherapy.saiyi.cn.jinhaojiao.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import aromatherapy.saiyi.cn.jinhaojiao.R;
import aromatherapy.saiyi.cn.jinhaojiao.app.MyApplication;
import aromatherapy.saiyi.cn.jinhaojiao.base.BaseActivity;
import aromatherapy.saiyi.cn.jinhaojiao.bean.User;
import aromatherapy.saiyi.cn.jinhaojiao.presenter.LoginPresenterImp;
import aromatherapy.saiyi.cn.jinhaojiao.presenter.ThreeLoginPresenterImp;
import aromatherapy.saiyi.cn.jinhaojiao.util.Log;
import aromatherapy.saiyi.cn.jinhaojiao.util.MD5;
import aromatherapy.saiyi.cn.jinhaojiao.util.Toastor;
import aromatherapy.saiyi.cn.jinhaojiao.view.MsgView;
import aromatherapy.saiyi.cn.jinhaojiao.widget.LoadingDialog;
import butterknife.BindView;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity implements MsgView {
    private final static String TAG = LoginActivity.class.getSimpleName();
    @BindView(R.id.login_pasw_et)
    EditText login_pasw_et;
    @BindView(R.id.login_phone_et)
    EditText login_phone_et;
    private UMShareAPI mShareAPI;

    private LoadingDialog dialog;
    private Toastor toastor;
    private RequestQueue mQueue;
    String name = "";
    String openid = "";
    LoginPresenterImp loginPresenterImp;
    ThreeLoginPresenterImp threeLoginPresenterImp;
    Map<String, String> map = new HashMap<>();

    @Override
    protected int getContentView() {
        return R.layout.activity_login;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mShareAPI = UMShareAPI.get(this);
        toastor = new Toastor(this);
        dialog = new LoadingDialog(this, "登陆中...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        loginPresenterImp = new LoginPresenterImp(this, this);
        threeLoginPresenterImp = new ThreeLoginPresenterImp(new MsgView() {
            @Override
            public void showProgress() {

            }

            @Override
            public void disimissProgress() {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }

            @Override
            public void loadDataSuccess(JSONObject jsonObject) {
                toastor.showSingletonToast(jsonObject.optString("resMessage"));
                if (jsonObject.optInt("resCode") == 1) {
                    showDialog();
                } else if (jsonObject.optInt("resCode") == 0) {
                    JSONObject json = jsonObject.optJSONObject("resBody");
                    User user = new User();
                    user.setOpenid(openid);
                    user.setUserID(json.optString("userID"));
                    user.setSex(json.optString("sex"));
                    if (json.optInt("flag") == 1) {
                        user.setType(1);
                    } else
                        user.setType(0);
                    user.setNikename(json.optString("nickName"));
                    if (json.optString("equipmentID").length() > 0) {
                        user.setEquipmentID(json.optString("equipmentID"));
                    }
                    if (json.optString("equipment").length() > 0) {
                        user.setEquipment(json.optString("equipment"));
                    }
                    if (json.optString("headPicURL").length() > 0) {
                        user.setHead_pic(json.optString("headPicURL"));
                        Log.e("--------2", jsonObject.optString("headPicByte"));
                    }
                    MyApplication.newInstance().setUser(user);
                    setResult(1);
                    finish();
                }

            }

            @Override
            public void loadDataError(Throwable throwable) {
                Log.e(TAG, throwable.getLocalizedMessage());
                toastor.showSingletonToast("服务器连接失败");
            }
        }, this);
    }


    @OnClick({R.id.login_login_tv, R.id.login_pasw_tv, R.id.login_back_iv,
            R.id.login_registered_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.login_login_tv:
                login();
                break;
            case R.id.login_pasw_tv:
                startActivity(new Intent(this, ResetPwdActivity.class));
                break;
            case R.id.login_back_iv:
                finish();
                break;
            case R.id.login_registered_tv:
                showDialog();
                break;

        }
    }

    @OnClick({R.id.login_qq_iv, R.id.login_wechat_iv})
    public void ClickLoing(View view) {
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

                map.clear();
                map.put("phoneNumber", login_phone_et.getText().toString());
                map.put("passWord", MD5.getMD5(login_pasw_et.getText().toString()));

                loginPresenterImp.loadMsg(map);
            } else {
                toastor.showSingletonToast("密码长度不正确");
            }
        } else {
            toastor.showSingletonToast("手机号码输入错误");
        }

    }

    public Bitmap stringtoBitmap(String string) {
        //将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("选择身份");
        builder.setPositiveButton("教练", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (name.length() > 0) {
                    startActivity(new Intent(LoginActivity.this, QQRegisteredActivity.class).putExtra("type", 0).putExtra("name", name).putExtra("openid", openid));
                } else {
                    startActivity(new Intent(LoginActivity.this, RegisteredActivity.class).putExtra("type", 0));
                }

            }
        });
        builder.setNegativeButton("学员", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (name.length() > 0) {
                    startActivity(new Intent(LoginActivity.this, QQRegisteredActivity.class).putExtra("type", 1).putExtra("name", name).putExtra("openid", openid));
                } else {
                    startActivity(new Intent(LoginActivity.this, RegisteredActivity.class).putExtra("type", 1));
                }

            }
        });
        builder.create().show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MyApplication.newInstance().getUser() != null&&MyApplication.newInstance().isLogin) {
            setResult(1);
            finish();
        }
    }

    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            //授权开始的回调
            if (dialog != null && !dialog.isShowing()) {
                dialog.show();
            }
        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            // Toast.makeText(getApplicationContext(), "Authorize succeed", Toast.LENGTH_SHORT).show();
            for (String key : data.keySet()) {
                Log.e("-----", "QQ" + "key= " + key + " and value= " + data.get(key));
            }
            if (data.get("name") != null) {
                name = data.get("name");
                openid = data.get("openid");
                map.put("account", data.get("openid"));
                threeLoginPresenterImp.loadMsg(map);
            }

        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            toastor.showSingletonToast(platform+"第三方登陆失败");
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            toastor.showSingletonToast(platform+"第三方登陆取消");


        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mShareAPI.onActivityResult(requestCode, resultCode, data);
    }



    @Override
    public void showProgress() {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
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
        toastor.showSingletonToast(jsonObject.optString("resMessage"));
        if (jsonObject.optInt("resCode") == 0) {
            JSONObject json = jsonObject.optJSONObject("resBody");
            User user = new User(login_phone_et.getText().toString(), login_pasw_et.getText().toString());
            user.setUserID(json.optString("userID"));
            user.setSex(json.optString("sex"));
            if (json.optInt("flag") == 1) {
                user.setType(1);
            } else
                user.setType(0);
            user.setNikename(json.optString("nickName"));
            if (json.optString("equipmentID").length() > 0) {
                user.setEquipmentID(json.optString("equipmentID"));
            }
            if (json.optString("equipment").length() > 0) {
                user.setEquipment(json.optString("equipment"));
            }
            if (json.optString("headPicURL").length() > 0) {
                user.setHead_pic(json.optString("headPicURL"));
                Log.e("--------2", jsonObject.optString("headPicURL"));

            }
            MyApplication.newInstance().setUser(user);
            // toastor.getSingletonToast("登陆成功");
            setResult(1);
            finish();

        }
    }

    @Override
    public void loadDataError(Throwable throwable) {
        Log.e(TAG, throwable.getLocalizedMessage());
        toastor.showSingletonToast("服务器连接失败");
    }

}
