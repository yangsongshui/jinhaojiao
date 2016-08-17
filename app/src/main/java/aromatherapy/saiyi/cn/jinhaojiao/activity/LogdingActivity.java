package aromatherapy.saiyi.cn.jinhaojiao.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import aromatherapy.saiyi.cn.jinhaojiao.R;
import aromatherapy.saiyi.cn.jinhaojiao.base.BaseActivity;

/**
 * Created by Administrator on 2016/6/1.
 */
public class LogdingActivity extends BaseActivity {

    private Handler handler;
    private Runnable myRunnable;


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
        handler.postDelayed(myRunnable, 1500);
    }
}
