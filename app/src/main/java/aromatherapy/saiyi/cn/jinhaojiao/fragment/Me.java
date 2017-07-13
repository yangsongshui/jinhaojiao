package aromatherapy.saiyi.cn.jinhaojiao.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

import aromatherapy.saiyi.cn.jinhaojiao.R;
import aromatherapy.saiyi.cn.jinhaojiao.activity.AddDevice;
import aromatherapy.saiyi.cn.jinhaojiao.activity.MassiveData;
import aromatherapy.saiyi.cn.jinhaojiao.activity.ResetPwdActivity;
import aromatherapy.saiyi.cn.jinhaojiao.activity.Setting;
import aromatherapy.saiyi.cn.jinhaojiao.app.MyApplication;
import aromatherapy.saiyi.cn.jinhaojiao.base.BaseFragment;
import aromatherapy.saiyi.cn.jinhaojiao.bean.User;
import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static aromatherapy.saiyi.cn.jinhaojiao.util.AppUtil.stringtoBitmap;


public class Me extends BaseFragment {
    @BindView(R.id.me_title_iv)
    ImageView me_title_iv;
    @BindView(R.id.me_add_rl)
    RelativeLayout me_add_rl;
    @BindView(R.id.me_pic_iv)
    CircleImageView me_pic_iv;
    @BindView(R.id.me_name_tv)
    TextView me_name_tv;
    @BindView(R.id.me_sex_iv)
    ImageView me_sex_iv;

    @OnClick(value = {R.id.me_massive_data_rl, R.id.me_pass_rl, R.id.me_life_rl, R.id.me_add_rl})
    public void ClickView(View view) {
        switch (view.getId()) {
            case R.id.me_massive_data_rl:
                startActivity(new Intent(getActivity(), MassiveData.class));
                break;
            case R.id.me_pass_rl:
                startActivityForResult(new Intent(getActivity(), ResetPwdActivity.class), 2);
                break;
            case R.id.me_life_rl:
                Toast.makeText(getActivity(), "改功能暂未开放....", Toast.LENGTH_SHORT).show();
                break;
            case R.id.me_add_rl:
                startActivity(new Intent(getActivity(), AddDevice.class));
                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    protected void initData(View layout, Bundle savedInstanceState) {

    }

    @OnClick(R.id.me_set_iv)
    public void ClickSet(View v) {
        startActivityForResult(new Intent(getActivity(), Setting.class), 2);
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_me;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        //判断Fragment中的ListView时候存在，判断该Fragment时候已经正在前台显示  通过这两个判断，就可以知道什么时候去加载数据了
        if (isVisibleToUser && isVisible()) {

            User user = MyApplication.newInstance().getUser();

            if (user != null) {
                if (user.getHead_pic() != null && user.getHead_pic().length() > 5) {
                    if (user.getHead_pic().contains("http:")) {
                        MyApplication.newInstance().getGlide().load(user.getHead_pic()).into(me_pic_iv);
                        MyApplication.newInstance().getGlide().load(user.getHead_pic()).centerCrop().diskCacheStrategy(DiskCacheStrategy.RESULT).into(me_title_iv);

                    } else
                        me_pic_iv.setImageBitmap(stringtoBitmap(user.getHead_pic()));
                } else {
                    me_pic_iv.setImageDrawable(getResources().getDrawable(R.mipmap.logo));
                    me_title_iv.setBackground(getResources().getDrawable(R.drawable.dakuai));
                }

                me_name_tv.setText(user.getNikename());
                if (user.getSex() != null) {
                    if (user.getSex().equals("男")) {
                        me_sex_iv.setImageResource(R.drawable.manwhite);
                    } else if (user.getSex().equals("女"))
                        me_sex_iv.setImageResource(R.drawable.nvxingbai);
                }
                if (user.getType() == 0) {
                    me_add_rl.setVisibility(View.GONE);

                } else {
                    me_add_rl.setVisibility(View.VISIBLE);
                }
            }
        }
        super.setUserVisibleHint(isVisibleToUser);
    }
}
