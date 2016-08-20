package aromatherapy.saiyi.cn.jinhaojiao.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import aromatherapy.saiyi.cn.jinhaojiao.R;
import aromatherapy.saiyi.cn.jinhaojiao.app.MyApplication;
import aromatherapy.saiyi.cn.jinhaojiao.base.BaseActivity;
import aromatherapy.saiyi.cn.jinhaojiao.bean.User;
import aromatherapy.saiyi.cn.jinhaojiao.util.Constant;
import aromatherapy.saiyi.cn.jinhaojiao.util.NormalPostRequest;
import aromatherapy.saiyi.cn.jinhaojiao.util.Toastor;
import aromatherapy.saiyi.cn.jinhaojiao.view.LoadingDialog;
import aromatherapy.saiyi.cn.jinhaojiao.view.MyRadioGroup;
import de.hdodenhof.circleimageview.CircleImageView;

public class MeInfoAcitvity extends BaseActivity implements Response.ErrorListener {
    private final static String TAG = MeInfoAcitvity.class.getSimpleName();
    private int TYPE;
    private int COACH = 0;
    private int MEMBER = 1;
    private static final int RESULT = 1;
    private static final int PHOTO_REQUEST_CUT = 2;
    private User user;
    @ViewInject(R.id.me_name_tv)
    TextView me_name_tv;
    @ViewInject(R.id.me_height_tv)
    TextView me_height_tv;
    @ViewInject(R.id.me_weight_tv)
    TextView me_weight_tv;
    @ViewInject(R.id.me_sex_tv)
    TextView me_sex_tv;
    @ViewInject(R.id.me_birthday_tv)
    TextView me_birthday_tv;
    @ViewInject(R.id.me_nickname_tv)
    TextView me_nickname_tv;
    @ViewInject(R.id.me_address_tv)
    TextView me_address_tv;
    @ViewInject(R.id.me_school_tv)
    TextView me_school_tv;
    @ViewInject(R.id.me_class_tv)
    TextView me_class_tv;
    @ViewInject(R.id.me_club_tv)
    TextView me_club_tv;
    @ViewInject(R.id.me_identity_tv)
    TextView me_identity_tv;
    @ViewInject(R.id.me_identity2_tv)
    TextView me_identity2_tv;
    @ViewInject(R.id.me_phone_tv)
    TextView me_phone_tv;
    @ViewInject(R.id.me_info_pic_iv)
    CircleImageView me_info_pic_iv;

    @ViewInject(R.id.me_identity2_rl)
    RelativeLayout me_identity2_rl;
    @ViewInject(R.id.me_identity_rl)
    RelativeLayout me_identity_rl;
    private Map<String, String> map = new HashMap<String, String>();
    private LoadingDialog dialog;
    private Toastor toastor;
    private RequestQueue mQueue;
    Bitmap bitmap;
    String photo = "";

    @Override
    protected int getContentView() {
        return R.layout.activity_me_info;
    }

    @Override
    protected void init() {
        user = MyApplication.newInstance().getUser();
        TYPE = user.getType();
        if (TYPE == COACH) {
            me_identity_rl.setVisibility(View.VISIBLE);
            me_identity2_rl.setVisibility(View.GONE);
        } else if (TYPE == MEMBER) {
            me_identity_rl.setVisibility(View.GONE);
            me_identity2_rl.setVisibility(View.VISIBLE);
        }
        toastor = new Toastor(this);
        dialog = new LoadingDialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        mQueue = MyApplication.newInstance().getmQueue();
        dialog.show();
        map.clear();
        map.put("userID", user.getUserID());
        mQueue.add(normalPostRequest);
    }

    @Event(value = {R.id.me_definite_tv, R.id.me_back_iv,
            R.id.me_name_rl, R.id.me_sex_rl,
            R.id.me_birthday_rl, R.id.me_nickname_rl,
            R.id.me_address_rl, R.id.me_school_rl,
            R.id.me_class_rl, R.id.me_club_rl,
            R.id.me_identity2_rl, R.id.me_identity_rl, R.id.me_pic_rl})
    private void ClickView(View view) {
        switch (view.getId()) {
            case R.id.me_definite_tv:
                //点击完成
                definite();
                break;
            case R.id.me_back_iv:
                finish();//返回
                break;
            case R.id.me_name_rl:
                //点击姓名
                showDialog(me_name_tv);
                break;
            case R.id.me_sex_rl:
                //点击性别
                showDialog();
                break;
            case R.id.me_birthday_rl:
                //点击生日
                showDate();
                break;
            case R.id.me_nickname_rl:
                //点击昵称
                showDialog(me_nickname_tv);
                break;
            case R.id.me_address_rl:
                //点击地址
                showDialog(me_address_tv);
                break;
            case R.id.me_school_rl:
                //点击学校
                showDialog(me_school_tv);
                break;
            case R.id.me_club_rl:
                //点击俱乐部
                showDialog(me_club_tv);
                break;
            case R.id.me_identity2_rl:
                show();
                //点击身份
                break;
            case R.id.me_identity_rl:
                //点击位置
                show2();
                break;
            case R.id.me_class_rl:
                //点击班级
                showDialog(me_class_tv);
                break;
            case R.id.me_pic_rl:
                //点击头像
                openGallery();
                break;
        }
    }

    private void showDialog(final TextView textView) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MeInfoAcitvity.this);
        final EditText editText = new EditText(this);
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

    private void showDate() {
        Calendar c = Calendar.getInstance();
        DatePickerDialog pickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                me_birthday_tv.setText(year + "/" + (monthOfYear + 1) + "/" + dayOfMonth);
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_YEAR));
        DatePicker datePicker = pickerDialog.getDatePicker();
        datePicker.setCalendarViewShown(false);

        pickerDialog.show();
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MeInfoAcitvity.this);
        builder.setTitle("选择性别");
        builder.setPositiveButton("男", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                me_sex_tv.setText("男");
            }
        });
        builder.setNegativeButton("女", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                me_sex_tv.setText("女");
            }
        });
        builder.create().show();
    }

    private void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MeInfoAcitvity.this);
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
        MyRadioGroup meRadioGroup = (MyRadioGroup) view.findViewById(R.id.my_rg);
        meRadioGroup.setOnCheckedChangeListener(new MyRadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(MyRadioGroup group, int checkedId) {
                me_identity2_tv.setText(((RadioButton) group.findViewById(checkedId)).getText());
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }


    private void show2() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MeInfoAcitvity.this);
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
                me_identity_tv.setText(check(view));
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
        String name = me_name_tv.getText().toString();
        String birthday = me_birthday_tv.getText().toString();
        String nickname = me_nickname_tv.getText().toString();
        String sex = me_sex_tv.getText().toString();
        String address = me_address_tv.getText().toString();
        String school = me_school_tv.getText().toString();
        String banji = me_class_tv.getText().toString();
        String club = me_club_tv.getText().toString();
        String height = me_height_tv.getText().toString();
        String weight = me_height_tv.getText().toString();
        if (TYPE == COACH) {
            identity = me_identity2_tv.getText().toString();
        } else if (TYPE == MEMBER) {
            identity = me_identity2_tv.getText().toString();
        }
        if (name.length() > 0 && !name.equals("")) {
            user.setUsername(name);
            if (birthday.length() > 0 && !birthday.equals("")) {
                user.setBirthday(birthday);
                if (height.length() > 0 && !height.equals("")) {
                    user.setHeight(height);
                    if (weight.length() > 0 && !weight.equals("")) {
                        user.setWeight(weight);
                        if (sex.length() > 0 && !sex.equals("")) {
                            user.setSex(sex);
                            if (address.length() > 0 && !address.equals("")) {
                                user.setAddress(address);
                                if (school.length() > 0 && !school.equals("")) {
                                    user.setSchool(school);
                                    if (banji.length() > 0 && !banji.equals("")) {
                                        user.setBanji(banji);
                                        if (club.length() > 0 && !club.equals("")) {
                                            user.setClub(club);
                                            if (identity.length() > 0 && !identity.equals("")) {
                                                user.setIdentity(identity);
                                                if (nickname.length() > 0 && !nickname.equals("")) {
                                                    user.setNikename(nickname);
                                                    map.clear();
                                                    map.put("userID", user.getUserID());
                                                    map.put("nickName", user.getNikename());
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
                                                    if (photo.trim().length() > 0) {
                                                        map.put("headPicByte", photo);
                                                    }
                                                    mQueue.add(normalPostRequest2);
                                                } else
                                                    toastor.showToast("昵称不能为空");
                                            } else
                                                toastor.showToast("身份或位置不能为空");
                                        } else
                                            toastor.showToast("俱乐部不能为空");
                                    } else
                                        toastor.showToast("班级不能为空");
                                } else
                                    toastor.showToast("学校不能为空");
                            } else
                                toastor.showToast("地址不能为空");
                        } else
                            toastor.showToast("性别不能为空");
                    } else
                        toastor.showToast("体重不能为空");
                } else
                    toastor.showToast("身高不能为空");

            } else
                toastor.showToast("生日不能为空");

        } else
            toastor.showToast("真实姓名不能为空");
        if (bitmap != null)
            user.setBitmap(bitmap);
        MyApplication.newInstance().setUser(user);
        finish();
    }

    private void initView(User user) {
        me_phone_tv.setText(user.getPhone());
        me_name_tv.setText(user.getUsername());
        me_birthday_tv.setText(user.getBirthday());
        me_nickname_tv.setText(user.getNikename());
        me_sex_tv.setText(user.getSex());
        me_address_tv.setText(user.getAddress());
        me_school_tv.setText(user.getSchool());
        me_class_tv.setText(user.getBanji());
        me_club_tv.setText(user.getClub());
        me_height_tv.setText(user.getHeight());
        me_weight_tv.setText(user.getWeight());
        if (TYPE == COACH)
            me_identity_tv.setText(user.getIdentity());
        if (TYPE == MEMBER)
            me_identity2_tv.setText(user.getIdentity());
        if (user.getBitmap() != null) {
            me_info_pic_iv.setImageBitmap(user.getBitmap());
        } else {
            me_info_pic_iv.setImageDrawable(getResources().getDrawable(R.drawable.movie_2));
        }
    }

    /**
     * 打开相册
     */
    public void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);// 打开相册
        intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
        intent.setType("image/*");
        startActivityForResult(intent, RESULT);
    }

    private void crop(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);
        intent.putExtra("outputFormat", "JPEG");// 图片格式
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT) {
            if (data != null) {
                Uri uri = data.getData();
                crop(uri);
            }
        } else if (requestCode == PHOTO_REQUEST_CUT) {
            if (data != null) {
                setImageToHeadView(data);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void setImageToHeadView(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            bitmap = extras.getParcelable("data");
            me_info_pic_iv.setImageBitmap(bitmap);
            byte[] bytes = bitmap2Bytes(bitmap);
            photo = Base64.encodeToString(bytes, 0, bytes.length, Base64.DEFAULT);
            Log.e("registerUser", photo);
        }
    }

    /***
     * 照片转二进制
     */
    public byte[] bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    NormalPostRequest normalPostRequest = new NormalPostRequest(Constant.FINDPERSONAL, new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            Log.e(TAG, jsonObject.toString());
            dialog.dismiss();
            if (jsonObject.optInt("resCode") == 1) {
                toastor.showToast(jsonObject.optString("resMessage"));
            } else if (jsonObject.optInt("resCode") == 0) {
                JSONObject object = jsonObject.optJSONObject("resBody");
                user.setAddress(object.optString("address"));
                user.setBirthday(object.optString("birthday"));
                user.setSex(object.optString("sex"));
                user.setIdentity(object.optString("identity"));
                user.setHeight(object.optString("height"));
                user.setWeight(object.optString("weight"));
                user.setSchool(object.optString("school"));
                user.setUsername(object.optString("name"));
                user.setBanji(object.optString("uclass"));
                user.setClub(object.optString("clubname"));
                if (object.optString("headPicByte").length() > 0) {
                    user.setBitmap(stringtoBitmap(object.optString("headPicByte")));
                }
                initView(user);
            }
        }
    }, this, map);
    NormalPostRequest normalPostRequest2 = new NormalPostRequest(Constant.UPDATEUSER, new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            Log.e(TAG, jsonObject.toString());
            dialog.dismiss();
            if (jsonObject.optInt("resCode") == 1) {
                toastor.showToast(jsonObject.optString("resMessage"));
            } else if (jsonObject.optInt("resCode") == 0) {
                toastor.showToast(jsonObject.optString("resMessage"));
                finish();
            }
        }
    }, this, map);

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
    public void onErrorResponse(VolleyError volleyError) {
        dialog.dismiss();
        toastor.showToast("服务器异常");
    }
}
