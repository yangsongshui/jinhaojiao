package aromatherapy.saiyi.cn.jinhaojiao.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import aromatherapy.saiyi.cn.jinhaojiao.R;
import aromatherapy.saiyi.cn.jinhaojiao.base.BaseActivity;
import aromatherapy.saiyi.cn.jinhaojiao.fragment.Month;
import aromatherapy.saiyi.cn.jinhaojiao.fragment.Week;
import aromatherapy.saiyi.cn.jinhaojiao.fragment.Year;
import butterknife.BindView;
import butterknife.OnClick;

public class CardiacRate extends BaseActivity {

    @BindView(R.id.line_rgrpNavigation)
    RadioGroup rgrpNavigation;
    @BindView(R.id.volocity_load_tv)
    TextView volocity_load_tv;
    private Fragment[] frags = new Fragment[3];
    private int currentFragIndex = -1;

    @Override
    protected int getContentView() {
        return R.layout.activity_cardiac_rate;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        String  data=getIntent().getStringExtra("data");
        volocity_load_tv.setText(data);
        initNavigation();
        showFrag(0);
        rgrpNavigation.check(R.id.line_week_rb);
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
            fragTran.replace(R.id.cardiac_fl, frags[index]);
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


    @OnClick(R.id.cardiac_back_iv)
    public void onViewClicked(View v) {
        finish();
    }

    private Fragment getFrag(int index) {
        switch (index) {
            case 0:
                return new Week();
            case 1:
                return new Month();
            case 2:
                return new Year();
            default:
                return null;
        }
    }


    private class CheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.line_week_rb:
                    showFrag(0);
                    break;
                case R.id.line_month_rb:
                    showFrag(1);
                    break;
                case R.id.line_year_rb:
                    showFrag(2);
                    break;
            }
        }

    }

}
