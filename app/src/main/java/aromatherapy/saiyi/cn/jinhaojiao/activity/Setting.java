package aromatherapy.saiyi.cn.jinhaojiao.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import aromatherapy.saiyi.cn.jinhaojiao.R;
import aromatherapy.saiyi.cn.jinhaojiao.app.MyApplication;
import aromatherapy.saiyi.cn.jinhaojiao.base.BaseActivity;
import butterknife.OnClick;

public class Setting extends BaseActivity {
    @Override
    protected int getContentView() {
        return R.layout.activity_setting;
    }

    @Override
    protected void init(Bundle savedInstanceState) {

    }
    @OnClick(R.id.set_back_iv)
    public void ClickBack(View view){
        finish();
    }


    @OnClick(R.id.set_outlogin_iv)
    public void Clickoutlogin(View view){
        showDialog();

    }
    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Setting.this);
        builder.setTitle("是否退出");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MyApplication.newInstance().outLogin();
                setResult(2);
                finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });
        builder.create().show();
    }

}
