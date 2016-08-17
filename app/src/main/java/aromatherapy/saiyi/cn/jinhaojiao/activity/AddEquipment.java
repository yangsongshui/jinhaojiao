package aromatherapy.saiyi.cn.jinhaojiao.activity;

import android.view.View;

import org.xutils.view.annotation.Event;

import aromatherapy.saiyi.cn.jinhaojiao.R;
import aromatherapy.saiyi.cn.jinhaojiao.base.BaseActivity;

public class AddEquipment extends BaseActivity {

    @Override
    protected int getContentView() {
        return R.layout.activity_add_equipment;
    }

    @Override
    protected void init() {

    }
    @Event(R.id.add_back_iv)
    private void Click(View v){
        finish();
    }
    @Event(R.id.add_determine_tv)
    private void ClickAdd(View v){
        finish();
    }
}
