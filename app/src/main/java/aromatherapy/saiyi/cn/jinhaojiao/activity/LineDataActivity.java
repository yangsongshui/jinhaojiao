package aromatherapy.saiyi.cn.jinhaojiao.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import aromatherapy.saiyi.cn.jinhaojiao.R;
import aromatherapy.saiyi.cn.jinhaojiao.app.MyApplication;
import aromatherapy.saiyi.cn.jinhaojiao.base.BaseActivity;
import aromatherapy.saiyi.cn.jinhaojiao.bean.DeviceInfo;
import aromatherapy.saiyi.cn.jinhaojiao.fragment.Day;
import aromatherapy.saiyi.cn.jinhaojiao.fragment.Month;
import aromatherapy.saiyi.cn.jinhaojiao.fragment.Time;
import aromatherapy.saiyi.cn.jinhaojiao.fragment.Week;
import aromatherapy.saiyi.cn.jinhaojiao.fragment.Year;
import butterknife.BindView;
import butterknife.OnClick;

public class LineDataActivity extends BaseActivity {
    @BindView(R.id.line_rgrpNavigation)
    RadioGroup rgrpNavigation;
    @BindView(R.id.line_stpe_tv)
    TextView line_stpe_tv;
    @BindView(R.id.line_calorie_tv)
    TextView line_calorie_tv;
    @BindView(R.id.line_title_tv)
    TextView line_title_tv;
    private Fragment[] frags = new Fragment[5];
    private int currentFragIndex = -1;
    DeviceInfo deviceInfo;
    @Override
    protected int getContentView() {
        return R.layout.activity_line_data;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        if (getIntent().getIntExtra("type", -1) == 1) {
            line_title_tv.setText("卡路里");
        } else if (getIntent().getIntExtra("type", -1) == 0) {
            line_title_tv.setText("步数");
        }
        deviceInfo=MyApplication.newInstance().getDeviceInfo();
        if (deviceInfo.getCalorie()!=null){
            line_calorie_tv.setText(deviceInfo.getCalorie());
        }
        if (deviceInfo.getSteps()!=null){
            line_stpe_tv.setText(deviceInfo.getSteps());
        }
        initNavigation();
        showFrag(0);
        rgrpNavigation.check(R.id.line_tiem_rb);
    }

    /**
     * 初始化碎片的viewpager
     */
    private void showFrag(int index) {
        if (index == currentFragIndex)
            return;
        FragmentTransaction fragTran = getSupportFragmentManager()
                .beginTransaction();
        if (currentFragIndex != -1) {
            // 已经创建过frag
            fragTran.detach(frags[currentFragIndex]);
        }
        // 现在选择的碎片没有创建过
        if (frags[index] == null) {
            frags[index] = getFrag(index);
            fragTran.replace(R.id.line_fl, frags[index]);
        } else
            fragTran.attach(frags[index]);
        currentFragIndex = index;
        fragTran.commit();
    }

    /**
     * 初始化底部导航栏
     */
    private void initNavigation() {
        rgrpNavigation.setOnCheckedChangeListener(new CheckedChangeListener());
    }

    private Fragment getFrag(int index) {
        switch (index) {
            case 0:
                return new Time();
            case 1:
                return new Day();
            case 2:
                return new Week();
            case 3:
                return new Month();
            case 4:
                return new Year();
            default:
                return null;
        }
    }

    @OnClick(R.id.line_back_iv)
    public void onViewClicked(View v) {
        finish();
    }

    private class CheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.line_tiem_rb:
                    showFrag(0);
                    break;
                case R.id.line_day_rb:
                    showFrag(1);
                    break;
                case R.id.line_week_rb:
                    showFrag(2);
                    break;
                case R.id.line_month_rb:
                    showFrag(3);
                    break;
                case R.id.line_year_rb:
                    showFrag(4);
                    break;
            }
        }

    }
}
