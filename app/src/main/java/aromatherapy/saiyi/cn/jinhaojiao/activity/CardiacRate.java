package aromatherapy.saiyi.cn.jinhaojiao.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RadioGroup;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import aromatherapy.saiyi.cn.jinhaojiao.R;
import aromatherapy.saiyi.cn.jinhaojiao.base.BaseActivity;
import aromatherapy.saiyi.cn.jinhaojiao.fragment.Day;
import aromatherapy.saiyi.cn.jinhaojiao.fragment.Time;

public class CardiacRate extends BaseActivity {

    @ViewInject(R.id.cardiac_rgrpNavigation)
    RadioGroup rgrpNavigation;

    private Fragment[] frags = new Fragment[2];
    private int currentFragIndex = -1;

    @Override
    protected int getContentView() {
        return R.layout.activity_cardiac_rate;
    }

    @Override
    protected void init() {
        initNavigation();
        showFrag(0);
        rgrpNavigation.check(R.id.cardiac_tiem_rb);
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

    private Fragment getFrag(int index) {
        switch (index) {
            case 0:
                return new Time();
            case 1:
                return new Day();

            default:
                return null;
        }
    }

    @Event(R.id.cardiac_back_iv)
    private void Click(View v) {
        finish();
    }

    private class CheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.cardiac_tiem_rb:
                    showFrag(0);
                    break;
                case R.id.cardiac_day_rb:
                    showFrag(1);
                    break;
            }
        }
    }

}
