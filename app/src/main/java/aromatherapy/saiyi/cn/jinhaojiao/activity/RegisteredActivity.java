package aromatherapy.saiyi.cn.jinhaojiao.activity;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import aromatherapy.saiyi.cn.jinhaojiao.R;
import aromatherapy.saiyi.cn.jinhaojiao.app.MyApplication;
import aromatherapy.saiyi.cn.jinhaojiao.base.BaseActivity;
import aromatherapy.saiyi.cn.jinhaojiao.bean.User;
import aromatherapy.saiyi.cn.jinhaojiao.util.Toastor;

public class RegisteredActivity extends BaseActivity {
    private int TYPE;
    private final String TAG = "RegisteredActivity";
    @ViewInject(R.id.registered_phone_et)
    EditText registered_phone_et;
    @ViewInject(R.id.registered_coed_et)
    EditText registered_coed_et;
    @ViewInject(R.id.registered_pasw_et)
    EditText registered_pasw_et;
    @ViewInject(R.id.registered_pasw2_et)
    EditText registered_pasw2_et;
    @ViewInject(R.id.registered_name_et)
    EditText registered_name_et;
    User user;
    Toastor toastor;

    @Override
    protected int getContentView() {
        return R.layout.activity_registered;
    }

    @Override
    protected void init() {
        TYPE = getIntent().getIntExtra("type", -1);
        toastor = new Toastor(this);
    }

    @Event(R.id.registered_definite_tv)
    private void ClickDefinite(View view) {
        newUser();
    }

    @Event(R.id.registered_back_iv)
    private void ClickBack(View v) {
        finish();
    }

    private void newUser() {
        user = new User();
        String phone = registered_phone_et.getText().toString();
        String code = registered_coed_et.getText().toString();
        String pasw = registered_pasw_et.getText().toString();
        String pasw2 = registered_pasw2_et.getText().toString();
        String name = registered_name_et.getText().toString();
        if (phone.length() == 11) {
            if (code.equals("123456")) {
                if (pasw.length() >= 6 && pasw.length() <= 16) {
                    if (pasw2.equals(pasw)) {
                        if (!name.equals("") && name.length() > 0) {
                            user.setPhone(phone);
                            user.setPassword(pasw);
                            user.setType(TYPE);
                            toastor.showToast("注册成功，请完善个人信息");
                            MyApplication.newInstance().setUser(user);
                            startActivity(new Intent(this, MyInfoActivity.class).putExtra("user", user));
                            finish();
                        } else
                            toastor.showToast("昵称不能为空");
                    } else
                        toastor.showToast("两次密码输入不一致");
                } else
                    toastor.showToast("密码长度必须6~16位之间");
            } else
                toastor.showToast("验证码填写不正确");
        } else
            toastor.showToast("手机号填写不正确");
    }
}
