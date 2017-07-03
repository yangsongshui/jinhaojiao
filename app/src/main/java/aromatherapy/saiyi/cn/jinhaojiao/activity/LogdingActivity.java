package aromatherapy.saiyi.cn.jinhaojiao.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import aromatherapy.saiyi.cn.jinhaojiao.R;
import aromatherapy.saiyi.cn.jinhaojiao.base.BaseActivity;

/**
 * Created by Administrator on 2016/6/1.
 */
public class LogdingActivity extends BaseActivity {

    private Handler handler;
    private Runnable myRunnable;
    private final static int REQUECT_CODE_COARSE = 1;

    @Override
    protected int getContentView() {
        return R.layout.activity_logding;
    }

    @Override
    protected void init() {
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
        Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
    }

    @PermissionDenied(REQUECT_CODE_COARSE)
    public void requestSdcardFailed() {
        Toast.makeText(this, "定位权限获取失败", Toast.LENGTH_SHORT).show();


    }
}
