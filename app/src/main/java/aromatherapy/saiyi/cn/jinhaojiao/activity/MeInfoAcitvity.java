package aromatherapy.saiyi.cn.jinhaojiao.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.DigitsKeyListener;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoImpl;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.InvokeParam;
import com.jph.takephoto.model.TContextWrap;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.permission.InvokeListener;
import com.jph.takephoto.permission.PermissionManager;
import com.jph.takephoto.permission.TakePhotoInvocationHandler;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import aromatherapy.saiyi.cn.jinhaojiao.R;
import aromatherapy.saiyi.cn.jinhaojiao.app.MyApplication;
import aromatherapy.saiyi.cn.jinhaojiao.base.BaseActivity;
import aromatherapy.saiyi.cn.jinhaojiao.bean.User;
import aromatherapy.saiyi.cn.jinhaojiao.presenter.FindPersonalPresenterImp;
import aromatherapy.saiyi.cn.jinhaojiao.presenter.UpdateUserPresenterImp;
import aromatherapy.saiyi.cn.jinhaojiao.util.Toastor;
import aromatherapy.saiyi.cn.jinhaojiao.view.MsgView;
import aromatherapy.saiyi.cn.jinhaojiao.widget.LoadingDialog;
import aromatherapy.saiyi.cn.jinhaojiao.widget.MyRadioGroup;
import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static aromatherapy.saiyi.cn.jinhaojiao.util.AppUtil.bitmapToString;

public class MeInfoAcitvity extends BaseActivity implements  MsgView, TakePhoto.TakeResultListener, InvokeListener {
    private final static String TAG = MeInfoAcitvity.class.getSimpleName();
    private int TYPE;
    private int COACH = 0;
    private int MEMBER = 1;
    private static final int RESULT = 1;
    private static final int PHOTO_REQUEST_CUT = 2;
    /* 头像文件 */
    private static final String IMAGE_FILE_NAME = "temp_head_image.jpg";
    private User user;
    private TakePhoto takePhoto;
    private InvokeParam invokeParam;
    @BindView(R.id.me_name_tv)
    TextView me_name_tv;
    @BindView(R.id.me_height_tv)
    TextView me_height_tv;
    @BindView(R.id.me_weight_tv)
    TextView me_weight_tv;
    @BindView(R.id.me_sex_tv)
    TextView me_sex_tv;
    @BindView(R.id.me_birthday_tv)
    TextView me_birthday_tv;
    @BindView(R.id.me_nickname_tv)
    TextView me_nickname_tv;
    @BindView(R.id.me_address_tv)
    TextView me_address_tv;
    @BindView(R.id.me_school_tv)
    TextView me_school_tv;
    @BindView(R.id.me_class_tv)
    TextView me_class_tv;
    @BindView(R.id.me_club_tv)
    TextView me_club_tv;
    @BindView(R.id.me_identity_tv)
    TextView me_identity_tv;
    @BindView(R.id.me_identity2_tv)
    TextView me_identity2_tv;
    @BindView(R.id.me_phone_tv)
    TextView me_phone_tv;
    @BindView(R.id.me_info_pic_iv)
    CircleImageView me_info_pic_iv;

    @BindView(R.id.me_identity2_rl)
    RelativeLayout me_identity2_rl;
    @BindView(R.id.me_identity_rl)
    RelativeLayout me_identity_rl;
    private Map<String, String> map = new HashMap<String, String>();
    private LoadingDialog dialog;
    private Toastor toastor;
    Bitmap bitmap;
    String photo = "";
    private UpdateUserPresenterImp updateUserPresenterImp;
    private FindPersonalPresenterImp findPersonalPresenterImp;

    @Override
    protected int getContentView() {
        return R.layout.activity_me_info;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        getTakePhoto().onCreate(savedInstanceState);
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
        updateUserPresenterImp = new UpdateUserPresenterImp(new MsgView() {
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
                    toastor.showSingletonToast(jsonObject.optString("resMessage"));
                    MyApplication.newInstance().setUser(user);
                    finish();
                }
            }

            @Override
            public void loadDataError(Throwable throwable) {
                Log.e(TAG, throwable.getLocalizedMessage());
                toastor.showSingletonToast("服务器连接失败");
            }
        }, this);
        findPersonalPresenterImp = new FindPersonalPresenterImp(this, this);
        map.clear();
        map.put("userID", user.getUserID());
        findPersonalPresenterImp.loadMsg(map);
    }

    @OnClick(value = {R.id.me_definite_tv, R.id.me_back_iv,
            R.id.me_name_rl, R.id.me_sex_rl,
            R.id.me_birthday_rl, R.id.me_nickname_rl,
            R.id.me_address_rl, R.id.me_school_rl,
            R.id.me_class_rl, R.id.me_club_rl,
            R.id.me_identity2_rl, R.id.me_identity_rl, R.id.me_height_rl, R.id.me_weight_rl, R.id.me_pic_rl})
    public void ClickView(View view) {
        switch (view.getId()) {
            case R.id.me_definite_tv:
                //点击完成
                definite();
                break;
            case R.id.me_height_rl:
                //点击完成
                showDialog2(me_height_tv);
                break;
            case R.id.me_weight_rl:
                //点击完成
                showDialog2(me_weight_tv);
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
                //showDate();
                showDialog2(me_birthday_tv);
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

    private void showDialog2(final TextView textView) {
        final android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(MeInfoAcitvity.this);
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
        android.support.v7.app.AlertDialog tempDialog = alertDialog.create();
        tempDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            public void onShow(DialogInterface dialog) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        tempDialog.show();
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
            if (height.length() > 0 && !height.equals("0")) {
                if (weight.length() > 0 && !weight.equals("0")) {
                    if (sex.length() > 0 && !sex.equals("")) {
                        if (birthday.length() > 0 && !birthday.equals("未填写")) {
                            if (nickname.length() > 0 && !nickname.equals("")) {
                                map.clear();
                                user.setWeight(weight);
                                user.setAddress(address);
                                user.setSchool(school);
                                user.setBanji(banji);
                                user.setClub(club);
                                user.setUsername(name);
                                user.setIdentity(identity);
                                user.setSex(sex);
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
                                map.put("phoneNumber", user.getPhone());
                                if (photo.trim().length() > 0) {
                                    map.put("headPicByte", photo);
                                }
                                //修改用户ixnxi
                                updateUserPresenterImp.loadMsg(map);

                            } else
                                toastor.showSingletonToast("昵称不能为空");
                        } else
                            toastor.showSingletonToast("年龄不能为空");
                    } else
                        toastor.showSingletonToast("性别不能为空");
                } else
                    toastor.showSingletonToast("体重不能为0");

            } else
                toastor.showSingletonToast("身高不能为0");
        } else
            toastor.showSingletonToast("真实姓名不能为空");
        if (bitmap != null)
            user.setBitmap(bitmap);
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
        File file = new File(Environment.getExternalStorageDirectory(), "/temp/" + System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        Uri imageUri = Uri.fromFile(file);
        //相册
        configCompress(takePhoto, 500, 500);
        takePhoto.onPickFromGalleryWithCrop(imageUri, getCropOptions());
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
        toastor.showSingletonToast(jsonObject.optString("resMessage"));
        if (jsonObject.optInt("resCode") == 0) {
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
            user.setPhone(object.optString("phoneNumber"));
            if (object.optString("headPicByte").length() > 0) {
                user.setBitmap(stringtoBitmap(object.optString("headPicByte")));
            }
            initView(user);
        }

    }

    @Override
    public void loadDataError(Throwable throwable) {
        Log.e(TAG, throwable.getLocalizedMessage());
        toastor.showSingletonToast("服务器连接失败");
    }

    private CropOptions getCropOptions() {
        CropOptions.Builder builder = new CropOptions.Builder();
        builder.setAspectX(500).setAspectY(500);
        builder.setOutputX(500).setOutputY(500);
        builder.setWithOwnCrop(false);
        return builder.create();
    }

    private void configCompress(TakePhoto takePhoto, int width, int height) {

        int maxSize = 1024 * 10;

        boolean showProgressBar = false;
        boolean enableRawFile = false;
        CompressConfig config;
        config = new CompressConfig.Builder()
                .setMaxSize(maxSize)
                .setMaxPixel(width >= height ? width : height)
                .enableReserveRaw(enableRawFile)
                .create();
        takePhoto.onEnableCompress(config, showProgressBar);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //以下代码为处理Android6.0、7.0动态权限所需
        PermissionManager.TPermissionType type = PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handlePermissionsResult(this, type, invokeParam, this);
    }

    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam.getMethod());
        if (PermissionManager.TPermissionType.WAIT.equals(type)) {
            this.invokeParam = invokeParam;
        }
        return type;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        getTakePhoto().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


    public TakePhoto getTakePhoto() {
        if (takePhoto == null) {
            takePhoto = (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this, this));
        }
        return takePhoto;
    }

    @Override
    public void takeSuccess(TResult result) {
        Log.e(MyInfoActivity.class.getName(), "takeSuccess：" + result.getImage().getCompressPath());
        Glide.with(this).load(new File(result.getImage().getCompressPath())).into(me_info_pic_iv);
        photo = bitmapToString(result.getImage().getCompressPath());
    }

    @Override
    public void takeFail(TResult result, String msg) {
        Log.e(MyInfoActivity.class.getName(), "takeFail:" + msg);

    }

    @Override
    public void takeCancel() {
        Log.i(MyInfoActivity.class.getName(), "操作已取消");
    }
}
