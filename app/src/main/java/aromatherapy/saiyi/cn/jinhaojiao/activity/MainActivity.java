package aromatherapy.saiyi.cn.jinhaojiao.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioGroup;

import org.xutils.view.annotation.ViewInject;

import aromatherapy.saiyi.cn.jinhaojiao.R;
import aromatherapy.saiyi.cn.jinhaojiao.app.MyApplication;
import aromatherapy.saiyi.cn.jinhaojiao.base.BaseActivity;
import aromatherapy.saiyi.cn.jinhaojiao.fragment.Community;
import aromatherapy.saiyi.cn.jinhaojiao.fragment.Home;
import aromatherapy.saiyi.cn.jinhaojiao.fragment.Location;
import aromatherapy.saiyi.cn.jinhaojiao.fragment.LoginFrag;
import aromatherapy.saiyi.cn.jinhaojiao.fragment.Me;

public class MainActivity extends BaseActivity {
    public static final String TAG = "MainActivity";
    @ViewInject(R.id.main_rgrpNavigation)
    RadioGroup rgrpNavigation;

    private Fragment[] frags = new Fragment[4];

    private int currentFragIndex = -1;

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void init() {
        initNavigation();
        showFrag(0);
        rgrpNavigation.check(R.id.rab_purpose);
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
            fragTran.replace(R.id.main_frame, frags[index]);
        } else
            fragTran.attach(frags[index]);
        currentFragIndex = index;
        fragTran.commit();
    }

    /**
     * 初始化底部导航栏
     */
    private void initNavigation() {
        rgrpNavigation = (RadioGroup) findViewById(R.id.main_rgrpNavigation);
        rgrpNavigation
                .setOnCheckedChangeListener(new CheckedChangeListener());
        // 设置选中第一个rbtn
        rgrpNavigation.check(currentFragIndex + 1);
    }

    private Fragment getFrag(int index) {
        switch (index) {
            case 0:
                return new Home();
            case 1:
                return new Location();
            case 2:
                return new Community();
            case 3:
                if (MyApplication.newInstance().getUser() == null)
                    return new LoginFrag();
                else
                    return new Me();
            default:
                return null;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (type==1) {
            FragmentTransaction fragTran = getSupportFragmentManager()
                    .beginTransaction();
            frags[3]=new Me();
            fragTran.replace(R.id.main_frame,
                    frags[3]);
            fragTran.commit();
            type=0;
        }
    }

    int type = 0;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 1) {
            type = resultCode;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class CheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.rab_purpose:
                    showFrag(0);
                    break;
                case R.id.rab_rent:
                    showFrag(1);
                    break;
                case R.id.rab_flowdeal:
                    showFrag(2);
                    break;
                case R.id.rab_me:
                    showFrag(3);
                    break;
            }
        }

    }
}
