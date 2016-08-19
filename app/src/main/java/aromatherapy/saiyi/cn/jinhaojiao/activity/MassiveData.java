package aromatherapy.saiyi.cn.jinhaojiao.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import aromatherapy.saiyi.cn.jinhaojiao.R;
import aromatherapy.saiyi.cn.jinhaojiao.app.MyApplication;
import aromatherapy.saiyi.cn.jinhaojiao.base.BaseActivity;
import aromatherapy.saiyi.cn.jinhaojiao.bean.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class MassiveData extends BaseActivity {
    User user;
    @ViewInject(R.id.massive_name_tv)
    TextView massive_name_tv;
    @ViewInject(R.id.massive_pic_iv)
    CircleImageView massive_pic_iv;
    @ViewInject(R.id.massive_sex_iv)
    ImageView massive_sex_iv;

    @Override
    protected int getContentView() {
        return R.layout.activity_massive_data;
    }

    @Override
    protected void init() {

    }

    @Event(value = {R.id.massive_back_iv, R.id.massive_my_ll, R.id.massive_video_rl,
            R.id.massive_zhibo1_rl, R.id.massive_zhibo2_rl, R.id.massive_zhibo3_rl, R.id.massive_process_rl})
    private void ClickView(View view) {
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
            if (user.getBitmap() != null){
                massive_pic_iv.setImageBitmap(user.getBitmap());
            }else {
                massive_pic_iv.setImageDrawable(getResources().getDrawable(R.drawable.movie_2));
            }
            massive_name_tv.setText(user.getNikename());
            if (user.getSex().equals("男")) {
                massive_sex_iv.setImageResource(R.drawable.man);
            } else if (user.getSex().equals("女"))
                massive_sex_iv.setImageResource(R.drawable.lady);

        }
    }
}
