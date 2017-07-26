package aromatherapy.saiyi.cn.jinhaojiao.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.method.DigitsKeyListener;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import aromatherapy.saiyi.cn.jinhaojiao.R;
import aromatherapy.saiyi.cn.jinhaojiao.app.MyApplication;
import aromatherapy.saiyi.cn.jinhaojiao.base.BaseActivity;
import aromatherapy.saiyi.cn.jinhaojiao.bean.User;
import aromatherapy.saiyi.cn.jinhaojiao.presenter.AddDeteileduserPresenterImp;
import aromatherapy.saiyi.cn.jinhaojiao.util.Log;
import aromatherapy.saiyi.cn.jinhaojiao.util.Toastor;
import aromatherapy.saiyi.cn.jinhaojiao.view.MsgView;
import aromatherapy.saiyi.cn.jinhaojiao.widget.LoadingDialog;
import aromatherapy.saiyi.cn.jinhaojiao.widget.MyRadioGroup;
import butterknife.BindView;
import butterknife.OnClick;

public class MyInfoActivity extends BaseActivity implements MsgView {
    private final String TAG = "MyInfoActivity";
    private int TYPE;
    private int COACH = 0;
    private int MEMBER = 1;
    private User user;
    @BindView(R.id.my_name_tv)
    TextView my_name_tv;
    @BindView(R.id.my_sex_tv)
    TextView my_sex_tv;
    @BindView(R.id.my_birthday_tv)
    TextView my_birthday_tv;
    @BindView(R.id.my_height_tv)
    TextView my_height_tv;
    @BindView(R.id.my_address_tv)
    TextView my_address_tv;
    @BindView(R.id.my_school_tv)
    TextView my_school_tv;
    @BindView(R.id.my_class_tv)
    TextView my_class_tv;
    @BindView(R.id.my_club_tv)
    TextView my_club_tv;
    @BindView(R.id.my_identity_tv)
    TextView my_identity_tv;
    @BindView(R.id.coach_identity_tv)
    TextView coach_identity_tv;
    @BindView(R.id.coach_identity_rl)
    RelativeLayout coach_identity_rl;
    @BindView(R.id.my_identity_rl)
    RelativeLayout my_identity_rl;
    @BindView(R.id.my_weight_tv)
    TextView my_weight_tv;
    @BindView(R.id.my_name_rl)
    RelativeLayout my_name_rl;
    private Map<String, String> map = new HashMap<String, String>();
    private LoadingDialog dialog;
    private Toastor toastor;
    private AddDeteileduserPresenterImp addDeteileduserPresenterImp;

    @Override
    protected int getContentView() {
        return R.layout.activity_my_info;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        addDeteileduserPresenterImp = new AddDeteileduserPresenterImp(this, this);
        toastor = new Toastor(this);
        dialog = new LoadingDialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        user = (User) getIntent().getSerializableExtra("user");
        TYPE = user.getType();
        if (TYPE == COACH) {
            coach_identity_rl.setVisibility(View.VISIBLE);
            my_identity_rl.setVisibility(View.GONE);
            my_name_rl.setVisibility(View.VISIBLE);
        } else if (TYPE == MEMBER) {
            coach_identity_rl.setVisibility(View.GONE);
            my_identity_rl.setVisibility(View.VISIBLE);
            my_name_rl.setVisibility(View.GONE);
        }
    }

    @OnClick(value = {R.id.my_definite_tv, R.id.my_back_iv,
            R.id.my_name_rl, R.id.my_sex_rl,
            R.id.my_birthday_rl, R.id.my_height_rl,
            R.id.my_address_rl, R.id.my_school_rl,
            R.id.my_class_rl, R.id.my_club_rl,
            R.id.my_identity_rl, R.id.coach_identity_rl
            , R.id.my_weight_rl})
    public void ClickView(View view) {
        switch (view.getId()) {
            case R.id.my_definite_tv:
                //点击确定
                definite();
                break;
            case R.id.my_back_iv:
                finish();//返回
                break;
            case R.id.my_name_rl:
                //点击姓名
                showDialog(my_name_tv);
                break;
            case R.id.my_sex_rl:
                //点击性别
                showDialog();
                break;
            case R.id.my_birthday_rl:
                //点击生日
                // showDate();
                showDialog2(my_birthday_tv);
                break;
            case R.id.my_height_rl:
                //点击身高
                showDialog2(my_height_tv);
                break;
            case R.id.my_address_rl:
                //点击地址
                showDialog(my_address_tv);
                break;
            case R.id.my_school_rl:
                //点击学校
                showDialog(my_school_tv);
                break;
            case R.id.my_club_rl:
                //点击俱乐部
                showDialog(my_club_tv);
                break;
            case R.id.coach_identity_rl:
                show2();
                //点击身份
                break;
            case R.id.my_identity_rl:
                //点击位置
                show();
                break;
            case R.id.my_class_rl:
                //点击班级
                showDialog(my_class_tv);
                break;
            case R.id.my_weight_rl:
                showDialog2(my_weight_tv);
                break;
        }
    }

    private void showDialog(final TextView textView) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MyInfoActivity.this);
        DigitsKeyListener numericOnlyListener = new DigitsKeyListener(false, true);
        final EditText editText = new EditText(this);
        if (textView.getId() == R.id.my_weight_rl || textView.getId() == R.id.my_height_rl) {
            editText.setKeyListener(numericOnlyListener);
        }
        editText.setMaxLines(1);
        alertDialog.setTitle("请输入").setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (editText.getText().toString().equals("") || editText.getText().toString().length() == 0)
                    return;
                textView.setText(editText.getText().toString());

            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog tempDialog = alertDialog.create();
        tempDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            public void onShow(DialogInterface dialog) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        tempDialog.show();
    }

    private void showDialog2(final TextView textView) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MyInfoActivity.this);
        DigitsKeyListener numericOnlyListener = new DigitsKeyListener(false, true);
        final EditText editText = new EditText(this);
        editText.setKeyListener(numericOnlyListener);

        editText.setMaxLines(1);
        alertDialog.setTitle("请输入").setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (editText.getText().toString().equals("") || editText.getText().toString().length() == 0)
                    return;
                textView.setText(editText.getText().toString());

            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog tempDialog = alertDialog.create();
        tempDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            public void onShow(DialogInterface dialog) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        tempDialog.show();
    }


    private void showDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MyInfoActivity.this);
        builder.setTitle("选择性别");
        builder.setPositiveButton("男", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                my_sex_tv.setText("男");
            }
        });
        builder.setNegativeButton("女", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                my_sex_tv.setText("女");
            }
        });
        builder.create().show();
    }

    private void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MyInfoActivity.this);
        TextView textView = new TextView(this);
        textView.setText("选择身份");
        textView.setTextSize(20);
        textView.setPadding(10, 20, 10, 20);
        textView.setTextColor(getResources().getColor(R.color.lawngreen));
        textView.setGravity(Gravity.CENTER);
        builder.setCustomTitle(textView);
        View view = getLayoutInflater().inflate(R.layout.identity_item, null);
        builder.setView(view);
        builder.setTitle("选择位置");
        final AlertDialog alertDialog = builder.create();
        MyRadioGroup myRadioGroup = (MyRadioGroup) view.findViewById(R.id.my_rg);
        myRadioGroup.setOnCheckedChangeListener(new MyRadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(MyRadioGroup group, int checkedId) {
                my_identity_tv.setText(((RadioButton) group.findViewById(checkedId)).getText());
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }


    private void show2() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MyInfoActivity.this);
        TextView textView = new TextView(this);
        textView.setText("选择身份");
        textView.setTextSize(20);
        textView.setPadding(10, 20, 10, 20);
        textView.setTextColor(getResources().getColor(R.color.lawngreen));
        textView.setGravity(Gravity.CENTER);
        builder.setCustomTitle(textView);
        final View view = getLayoutInflater().inflate(R.layout.identity_item2, null);

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                coach_identity_tv.setText(check(view));
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setView(view);
        builder.create().show();
    }

    private String check(View view) {
        String str = "";
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.identity_Goalkeeper_rb);
        CheckBox checkBox2 = (CheckBox) view.findViewById(R.id.identity_Bianwei_rb);
        CheckBox checkBox3 = (CheckBox) view.findViewById(R.id.identity_Defender_tv);
        CheckBox checkBox4 = (CheckBox) view.findViewById(R.id.identity_Midfielder_tv);
        if (checkBox.isChecked()) {
            if (str.length() > 0)
                str += "," + checkBox.getText();
            else
                str += checkBox.getText();
        }
        if (checkBox3.isChecked()) {
            if (str.length() > 0)
                str += "," + checkBox3.getText();
            else
                str += checkBox3.getText();
        }
        if (checkBox4.isChecked()) {
            if (str.length() > 0)
                str += "," + checkBox4.getText();
            else
                str += checkBox4.getText();
        }
        if (checkBox2.isChecked()) {
            if (str.length() > 0)
                str += "," + checkBox2.getText();
            else
                str += checkBox2.getText();
        }

        return str;
    }

    private void definite() {
        String identity = "";
        String name = my_name_tv.getText().toString();
        String birthday = my_birthday_tv.getText().toString();
        String height = my_height_tv.getText().toString();
        String weight = my_weight_tv.getText().toString();
        String sex = my_sex_tv.getText().toString();
        String address = my_address_tv.getText().toString();
        String school = my_school_tv.getText().toString();
        String banji = my_class_tv.getText().toString();
        String club = my_club_tv.getText().toString();
        if (TYPE == COACH) {
            identity = coach_identity_tv.getText().toString();
        } else if (TYPE == MEMBER) {
            identity = my_identity_tv.getText().toString();
        }

        if (height.length() > 0 && !height.equals("0")) {
            if (weight.length() > 0 && !weight.equals("0")) {
                if (sex.length() > 0 && !sex.equals("未填写")) {
                    if (birthday.length() > 0 && !birthday.equals("未填写")) {
                        map.clear();
                        user.setWeight(weight);
                        user.setAddress(address);
                        user.setSchool(school);
                        user.setBanji(banji);
                        user.setClub(club);
                        user.setSex(sex);
                        user.setUsername(name);
                        user.setIdentity(identity);
                        user.setHeight(height);
                        user.setBirthday(birthday);
                        map.put("userID", user.getUserID());
                        map.put("name", user.getUsername());
                        map.put("sex", user.getSex());
                        map.put("height", user.getHeight());
                        map.put("weight", user.getWeight());
                        map.put("birthday", user.getBirthday());
                        map.put("address", user.getAddress());
                        map.put("school", user.getSchool());
                        map.put("uclass", user.getBanji());
                        map.put("identity", user.getIdentity());
                        map.put("clubname", user.getClub());
                        addDeteileduserPresenterImp.loadMsg(map);
                    } else
                        toastor.showSingletonToast("年龄不能为空");
                } else
                    toastor.showSingletonToast("性别不能为空");
            } else
                toastor.showSingletonToast("体重不能为0");

        } else
            toastor.showSingletonToast("身高不能为0");
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
        toastor.showSingletonToast(jsonObject.optString("resMessage"));
        if (jsonObject.optInt("resCode") == 0) {
            MyApplication.newInstance().setUser(user);
           // toastor.showSingletonToast("资料填写成功");
            finish();

        }
    }

    @Override
    public void loadDataError(Throwable throwable) {
        Log.e(TAG, throwable.getLocalizedMessage());
        toastor.showSingletonToast("服务器连接失败");
    }
}
