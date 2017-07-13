package aromatherapy.saiyi.cn.jinhaojiao.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import aromatherapy.saiyi.cn.jinhaojiao.R;
import aromatherapy.saiyi.cn.jinhaojiao.app.MyApplication;
import aromatherapy.saiyi.cn.jinhaojiao.base.BaseActivity;
import aromatherapy.saiyi.cn.jinhaojiao.bean.User;
import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static aromatherapy.saiyi.cn.jinhaojiao.util.AppUtil.stringtoBitmap;

public class MassiveData extends BaseActivity {
    @BindView(R.id.massive_name_tv)
    TextView massive_name_tv;
    @BindView(R.id.massive_pic_iv)
    CircleImageView massive_pic_iv;
    @BindView(R.id.massive_sex_iv)
    ImageView massive_sex_iv;

    @Override
    protected int getContentView() {
        return R.layout.activity_massive_data;
    }

    @Override
    protected void init(Bundle savedInstanceState) {

    }

    @OnClick(value = {R.id.massive_back_iv, R.id.massive_my_ll, R.id.massive_video_rl,
            R.id.massive_zhibo1_rl, R.id.massive_zhibo2_rl, R.id.massive_zhibo3_rl, R.id.massive_process_rl})
    public void ClickView(View view) {
        switch (view.getId()) {
            case R.id.massive_back_iv:
                finish();
                break;
            case R.id.massive_my_ll:
                startActivity(new Intent(this, MeInfoAcitvity.class));
                break;
            case R.id.massive_video_rl:
                break;
            case R.id.massive_zhibo1_rl:
                break;
            case R.id.massive_zhibo2_rl:
                break;
            case R.id.massive_zhibo3_rl:
                break;
            case R.id.massive_process_rl:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        User user = MyApplication.newInstance().getUser();
        if (user != null) {
            if (user.getHead_pic() != null && user.getHead_pic().length() > 5) {
                if (user.getHead_pic().contains("http:")) {
                    MyApplication.newInstance().getGlide().load(user.getHead_pic()).into(massive_pic_iv);
                } else
                    massive_pic_iv.setImageBitmap(stringtoBitmap(user.getHead_pic()));
            } else {
                massive_pic_iv.setImageDrawable(getResources().getDrawable(R.mipmap.logo));
            }

            massive_name_tv.setText(user.getNikename());
            if (user.getSex().equals("男")) {
                massive_sex_iv.setImageResource(R.drawable.manwhite);
            } else if (user.getSex().equals("女"))
                massive_sex_iv.setImageResource(R.drawable.nvxingbai);

        }
    }
}
