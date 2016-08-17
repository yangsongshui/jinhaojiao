package aromatherapy.saiyi.cn.jinhaojiao.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.Map;

import aromatherapy.saiyi.cn.jinhaojiao.R;
import aromatherapy.saiyi.cn.jinhaojiao.app.MyApplication;
import aromatherapy.saiyi.cn.jinhaojiao.base.BaseActivity;
import aromatherapy.saiyi.cn.jinhaojiao.bean.User;
import aromatherapy.saiyi.cn.jinhaojiao.util.Log;

public class LoginActivity extends BaseActivity {
    @ViewInject(R.id.login_pasw_et)
    EditText login_pasw_et;
    @ViewInject(R.id.login_phone_et)
    EditText login_phone_et;
    UMShareAPI mShareAPI;
    @Override
    protected int getContentView() {
        return R.layout.activity_login;
    }

    @Override
    protected void init() {
        mShareAPI = UMShareAPI.get( this );

    }


    @Event(value = {R.id.login_login_tv, R.id.login_pasw_tv, R.id.login_back_iv,
            R.id.login_registered_tv})
    private void ClickView(View view) {
        switch (view.getId()) {
            case R.id.login_login_tv:
                login();
                break;
            case R.id.login_pasw_tv:
                break;
            case R.id.login_back_iv:
                finish();
                break;
            case R.id.login_registered_tv:
                showDialog();
                break;

        }
    }
    @Event(value = {R.id.login_qq_iv,R.id.login_wechat_iv})
    private void ClickLoing(View view){
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
    private void login(){
        User user=new User(login_phone_et.getText().toString(),login_pasw_et.getText().toString(),0);
        user.setSex("男");
        MyApplication.newInstance().setUser(user);
        setResult(1);
        finish();
    }
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
            Toast.makeText( getApplicationContext(), "Authorize fail", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Toast.makeText( getApplicationContext(), "Authorize cancel", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("auth", "on activity re 2");
        mShareAPI.onActivityResult(requestCode, resultCode, data);
        Log.d("auth", "on activity re 3");
    }
}
