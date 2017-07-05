package aromatherapy.saiyi.cn.jinhaojiao.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import aromatherapy.saiyi.cn.jinhaojiao.R;
import aromatherapy.saiyi.cn.jinhaojiao.base.BaseActivity;
import aromatherapy.saiyi.cn.jinhaojiao.util.Toastor;

/**
 * Created by Administrator on 2016/6/1.
 */
public class LogdingActivity extends BaseActivity {

    private Handler handler;
    private Runnable myRunnable;
    private final static int REQUECT_CODE_COARSE = 1;
    Toastor toastor;
    @Override
    protected int getContentView() {
        return R.layout.activity_logding;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        toastor=new Toastor(this);
        handler = new Handler(Looper.getMainLooper());
        myRunnable = new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(LogdingActivity.this, MainActivity.class));
                finish();
            }
        };

        MPermissions.requestPermissions(LogdingActivity.this, REQUECT_CODE_COARSE, Manifest.permission.ACCESS_COARSE_LOCATION);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @PermissionGrant(REQUECT_CODE_COARSE)
    public void requestSdcardSuccess() {
        handler.postDelayed(myRunnable, 1500);
        toastor.showSingletonToast( "权限获取成功");
    }

    @PermissionDenied(REQUECT_CODE_COARSE)
    public void requestSdcardFailed() {
        toastor.showSingletonToast( "权限获取失败");

    }
}
