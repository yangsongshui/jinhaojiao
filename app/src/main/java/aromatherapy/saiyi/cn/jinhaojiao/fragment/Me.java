package aromatherapy.saiyi.cn.jinhaojiao.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import aromatherapy.saiyi.cn.jinhaojiao.R;
import aromatherapy.saiyi.cn.jinhaojiao.activity.AddDevice;
import aromatherapy.saiyi.cn.jinhaojiao.activity.MassiveData;
import aromatherapy.saiyi.cn.jinhaojiao.activity.ResetPwdActivity;
import aromatherapy.saiyi.cn.jinhaojiao.app.MyApplication;
import aromatherapy.saiyi.cn.jinhaojiao.base.BaseFragment;
import aromatherapy.saiyi.cn.jinhaojiao.bean.User;
import aromatherapy.saiyi.cn.jinhaojiao.util.FastBlur;
import de.hdodenhof.circleimageview.CircleImageView;


public class Me extends BaseFragment  {
    @ViewInject(R.id.me_title_rl)
    RelativeLayout me_title_rl;
    @ViewInject(R.id.me_pic_iv)
    CircleImageView me_pic_iv;
    @ViewInject(R.id.me_name_tv)
    TextView me_name_tv;
    @ViewInject(R.id.me_sex_iv)
    ImageView me_sex_iv;

    @Override
    protected void initData(View layout) {

    }

    private void applyBlur() {
        me_title_rl.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                me_title_rl.getViewTreeObserver().removeOnPreDrawListener(this);
                me_title_rl.buildDrawingCache();
                Bitmap bmp = me_title_rl.getDrawingCache();
                blur(bmp, me_title_rl);
                return true;
            }
        });
    }

    @Event(value = {R.id.me_massive_data_rl, R.id.me_pass_rl, R.id.me_life_rl, R.id.me_add_rl})
    private void ClickView(View view) {
        switch (view.getId()) {
            case R.id.me_massive_data_rl:
                startActivity(new Intent(getActivity(), MassiveData.class));
                break;
            case R.id.me_pass_rl:
                startActivityForResult(new Intent(getActivity(), ResetPwdActivity.class), 2);
                break;
            case R.id.me_life_rl:
                break;
            case R.id.me_add_rl:
                startActivity(new Intent(getActivity(), AddDevice.class));
                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        applyBlur();
        User user = MyApplication.newInstance().getUser();
        if (user != null) {
            if (user.getBitmap() != null) {
                me_title_rl.setBackground(new BitmapDrawable(getResources(), user.getBitmap()));
                me_pic_iv.setImageBitmap(user.getBitmap());
            } else {
                me_title_rl.setBackground(getResources().getDrawable(R.drawable.movie_2));
                me_pic_iv.setImageDrawable(getResources().getDrawable(R.drawable.movie_2));
            }
            me_name_tv.setText(user.getNikename());
            if (user.getSex() != null) {
                if (user.getSex().equals("男")) {
                    me_sex_iv.setImageResource(R.drawable.man);
                } else if (user.getSex().equals("女"))
                    me_sex_iv.setImageResource(R.drawable.lady);
            }

        }
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_me;
    }

    private void blur(Bitmap bkg, View view) {
        float scaleFactor = 2;
        float radius = 20;
        Bitmap overlay = Bitmap.createBitmap((int) (view.getMeasuredWidth() / scaleFactor),
                (int) (view.getMeasuredHeight() / scaleFactor), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.translate(-view.getLeft() / scaleFactor, -view.getTop() / scaleFactor);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bkg, 0, 0, paint);
        overlay = FastBlur.doBlur(overlay, (int) radius, true);
        view.setBackground(new BitmapDrawable(getResources(), overlay));
    }


}
