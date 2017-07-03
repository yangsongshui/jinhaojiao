package aromatherapy.saiyi.cn.jinhaojiao.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
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
import butterknife.BindView;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity implements Response.ErrorListener {
    private final static String TAG = LoginActivity.class.getSimpleName();
    @BindView(R.id.login_pasw_et)
    EditText login_pasw_et;
    @BindView(R.id.login_phone_et)
    EditText login_phone_et;
    private UMShareAPI mShareAPI;
    private Map<String, String> map = new HashMap<String, String>();
    private LoadingDialog dialog;
    private Toastor toastor;
    private RequestQueue mQueue;
    String name = "";
    String openid = "";

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
                dialog.show();
                map.clear();
                map.put("phoneNumber", login_phone_et.getText().toString());
                map.put("passWord", MD5.getMD5(login_pasw_et.getText().toString()));
                mQueue.add(normalPostRequest);
            } else {
                toastor.getSingletonToast("密码长度不正确");
            }
        } else {
            toastor.getSingletonToast("手机号码输入错误");
        }

    }

    NormalPostRequest normalPostRequest2 = new NormalPostRequest(Constant.QQLOGIN, new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            Log.e(TAG, jsonObject.toString());
            dialog.dismiss();
            if (jsonObject.optInt("resCode") == 1) {
                toastor.getSingletonToast(jsonObject.optString("resMessage"));
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
                if (json.optString("headPicByte").length() > 0) {
                    user.setBitmap(stringtoBitmap(json.optString("headPicByte")));
                    Log.e("--------2", jsonObject.optString("headPicByte"));
                }
                MyApplication.newInstance().setUser(user);
                toastor.getSingletonToast("登陆成功");
                setResult(1);
                finish();
            }

        }
    }, this, map);

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
        if (MyApplication.newInstance().getUser() != null) {
            setResult(1);
            finish();
        }
    }

    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            //授权开始的回调
        }
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
           // Toast.makeText(getApplicationContext(), "Authorize succeed", Toast.LENGTH_SHORT).show();
            for (String key : data.keySet()) {
                Log.e("-----", "QQ" + "key= " + key + " and value= " + data.get(key));
            }
            if (platform.equals(SHARE_MEDIA.QQ)) {
                if (data.get("screen_name") != null) {

                    Log.e("-----", data.get("openid"));
                    Log.e("-----", data.get("screen_name"));
                    name = data.get("screen_name");
                    openid = data.get("openid");
                    map.clear();
                    map.put("account", data.get("openid"));
                    mQueue.add(normalPostRequest2);
                    dialog.show();
                } else {
                    mShareAPI.getPlatformInfo(LoginActivity.this, platform, umAuthListener);
                }

            } else if (platform.equals(SHARE_MEDIA.WEIXIN) || platform.equals(SHARE_MEDIA.WEIXIN_CIRCLE) || platform.equals(SHARE_MEDIA.WEIXIN_FAVORITE)) {

                if (data.get("nickname") != null) {
                    name = data.get("nickname");
                    openid = data.get("openid");
                    map.clear();
                    map.put("account", data.get("openid"));
                    mQueue.add(normalPostRequest2);
                    dialog.show();
                } else {
                    mShareAPI.getPlatformInfo(LoginActivity.this, platform, umAuthListener);
                }

            }

        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            if (platform.equals(SHARE_MEDIA.QQ)) {
                Toast.makeText(getApplicationContext(), "QQ登陆失败", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(), "微信登陆失败", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            if (platform.equals(SHARE_MEDIA.QQ)) {
                Toast.makeText(getApplicationContext(), "QQ登陆取消", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(), "微信登陆取消", Toast.LENGTH_SHORT).show();
            }

        }
    };

    NormalPostRequest normalPostRequest = new NormalPostRequest(Constant.LOGIN, new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {

            Log.e(TAG, jsonObject.toString());
            dialog.dismiss();
            if (jsonObject.optInt("resCode") == 1) {
                MyApplication.newInstance().outLogin();
                toastor.getSingletonToast(jsonObject.optString("resMessage"));
            } else if (jsonObject.optInt("resCode") == 0) {
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
                if (json.optString("headPicByte").length() > 0) {
                    user.setBitmap(stringtoBitmap(json.optString("headPicByte")));
                    Log.e("--------2", jsonObject.optString("headPicByte"));

                }
                MyApplication.newInstance().setUser(user);
                toastor.getSingletonToast("登陆成功");
                setResult(1);
                finish();

            }

        }
    }, this, map);

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
        toastor.getSingletonToast("服务器异常");
        Log.e(TAG, "服务器异常");
    }
}
