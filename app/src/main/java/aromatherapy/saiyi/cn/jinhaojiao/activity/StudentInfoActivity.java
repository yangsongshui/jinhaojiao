package aromatherapy.saiyi.cn.jinhaojiao.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import aromatherapy.saiyi.cn.jinhaojiao.R;
import aromatherapy.saiyi.cn.jinhaojiao.app.MyApplication;
import aromatherapy.saiyi.cn.jinhaojiao.base.BaseActivity;
import aromatherapy.saiyi.cn.jinhaojiao.bean.User;
import aromatherapy.saiyi.cn.jinhaojiao.presenter.FindPersonalPresenterImp;
import aromatherapy.saiyi.cn.jinhaojiao.util.Toastor;
import aromatherapy.saiyi.cn.jinhaojiao.view.MsgView;
import aromatherapy.saiyi.cn.jinhaojiao.widget.LoadingDialog;
import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class StudentInfoActivity extends BaseActivity implements MsgView {
    private final static String TAG = StudentInfoActivity.class.getSimpleName();
    @BindView(R.id.student_name_tv)
    TextView student_name_tv;
    @BindView(R.id.student_height_tv)
    TextView student_height_tv;
    @BindView(R.id.student_weight_tv)
    TextView student_weight_tv;
    @BindView(R.id.student_sex_tv)
    TextView student_sex_tv;
    @BindView(R.id.student_birthday_tv)
    TextView student_birthday_tv;

    @BindView(R.id.student_school_tv)
    TextView student_school_tv;
    @BindView(R.id.student_class_tv)
    TextView student_class_tv;

    @BindView(R.id.student_identity2_tv)
    TextView student_identity2_tv;
    @BindView(R.id.student_info_pic_iv)
    CircleImageView student_info_pic_iv;

    private Map<String, String> map = new HashMap<String, String>();
    private LoadingDialog dialog;
    private Toastor toastor;

    private FindPersonalPresenterImp findPersonalPresenterImp;
    private User user;

    @Override
    protected int getContentView() {
        return R.layout.activity_student_info;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        user = (User) getIntent().getSerializableExtra("student");
        toastor = new Toastor(this);
        dialog = new LoadingDialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        findPersonalPresenterImp = new FindPersonalPresenterImp(this, this);
        map.clear();
        map.put("userID", user.getUserID());
        findPersonalPresenterImp.loadMsg(map);
        if (user.getHead_pic() != null && user.getHead_pic().length() > 5) {
            if (user.getHead_pic().contains("http:")) {
                MyApplication.newInstance().getGlide().load(user.getHead_pic()).into(student_info_pic_iv);

            } else
                student_info_pic_iv.setImageBitmap(stringtoBitmap(user.getHead_pic()));
        } else {
            student_info_pic_iv.setImageDrawable(getResources().getDrawable(R.mipmap.logo));

        }
    }


    @OnClick(R.id.student_back_iv)
    public void ClickeBack(View view) {
        finish();
    }

    public Bitmap stringtoBitmap(String string) {
        //将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    @Override
    public void showProgress() {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }

    }

    @Override
    public void disimissProgress() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    public void loadDataSuccess(JSONObject jsonObject) {
        Log.e(TAG, jsonObject.toString());
        toastor.showSingletonToast(jsonObject.optString("resMessage"));
        if (jsonObject.optInt("resCode") == 0) {
            JSONObject object = jsonObject.optJSONObject("resBody");
            student_sex_tv.setText(object.optString("sex"));
            student_identity2_tv.setText(object.optString("identity"));
            student_class_tv.setText(object.optString("uclass"));
            student_weight_tv.setText(object.optString("weight"));
            student_height_tv.setText(object.optString("height"));
            student_school_tv.setText(object.optString("school"));
            student_school_tv.setText(object.optString("school"));
            student_name_tv.setText(object.optString("name"));
            student_birthday_tv.setText(object.optString("birthday"));

        }
    }

    @Override
    public void loadDataError(Throwable throwable) {
        Log.e(TAG, throwable.getLocalizedMessage());
        toastor.showSingletonToast("服务器连接失败");
    }
}
