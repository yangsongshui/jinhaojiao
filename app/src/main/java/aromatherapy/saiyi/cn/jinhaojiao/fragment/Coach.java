package aromatherapy.saiyi.cn.jinhaojiao.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aromatherapy.saiyi.cn.jinhaojiao.R;
import aromatherapy.saiyi.cn.jinhaojiao.activity.StudentActivity;
import aromatherapy.saiyi.cn.jinhaojiao.activity.StudentInfoActivity;
import aromatherapy.saiyi.cn.jinhaojiao.adapter.CoachAdapter;
import aromatherapy.saiyi.cn.jinhaojiao.app.MyApplication;
import aromatherapy.saiyi.cn.jinhaojiao.base.BaseFragment;
import aromatherapy.saiyi.cn.jinhaojiao.bean.Student;
import aromatherapy.saiyi.cn.jinhaojiao.bean.User;
import aromatherapy.saiyi.cn.jinhaojiao.connector.OnItemCheckListener;
import aromatherapy.saiyi.cn.jinhaojiao.connector.OnItemLongCheckListener;
import aromatherapy.saiyi.cn.jinhaojiao.connector.OnItemPhotoCheckListener;
import aromatherapy.saiyi.cn.jinhaojiao.presenter.AddStudenPresenterImp;
import aromatherapy.saiyi.cn.jinhaojiao.presenter.DeleteStudenPresenterImp;
import aromatherapy.saiyi.cn.jinhaojiao.presenter.FindStudenPresenterImp;
import aromatherapy.saiyi.cn.jinhaojiao.presenter.GetSpeedPresenterImp;
import aromatherapy.saiyi.cn.jinhaojiao.presenter.GetSportPresenterImp;
import aromatherapy.saiyi.cn.jinhaojiao.presenter.GetSportStrengthPresenterImp;
import aromatherapy.saiyi.cn.jinhaojiao.util.Constant;
import aromatherapy.saiyi.cn.jinhaojiao.util.DateUtil;
import aromatherapy.saiyi.cn.jinhaojiao.util.Log;
import aromatherapy.saiyi.cn.jinhaojiao.util.SpUtils;
import aromatherapy.saiyi.cn.jinhaojiao.util.Toastor;
import aromatherapy.saiyi.cn.jinhaojiao.view.MsgView;
import aromatherapy.saiyi.cn.jinhaojiao.widget.LoadingDialog;
import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static aromatherapy.saiyi.cn.jinhaojiao.util.AppUtil.stringtoBitmap;


public class Coach extends BaseFragment implements MsgView, RadioGroup.OnCheckedChangeListener, OnItemCheckListener, OnItemPhotoCheckListener, OnItemLongCheckListener {
    private final static String TAG = Coach.class.getSimpleName();
    User user;
    @BindView(R.id.coach_pic_iv)
    CircleImageView coachPicIv;
    @BindView(R.id.coach_name_tv)
    TextView coachNameTv;
    @BindView(R.id.coach_sex_iv)
    ImageView coachSexIv;
    @BindView(R.id.coach_studen)
    RecyclerView coachStuden;
    @BindView(R.id.radio_group)
    RadioGroup radio_group;
    @BindView(R.id.me_title_iv)
    ImageView me_title_iv;
    private Map<String, String> map = new HashMap<String, String>();
    private LoadingDialog dialog;
    private Toastor toastor;
    private List<Student> mList;
    MyBroadcastReciver reciver;
    private Handler handler;
    private Runnable myRunnable;

    private FindStudenPresenterImp findStudenPresenterImp;  // 我队
    private GetSportPresenterImp getSportPresenterImp;      //负荷
    private GetSportStrengthPresenterImp getSportStrengthPresenterImp;//强度
    private GetSpeedPresenterImp getSpeedPresenterImp;//强度
    private DeleteStudenPresenterImp deleteStudenPresenterImp;//强度

    private AddStudenPresenterImp addStudenPresenterImp;
    private boolean isOne = true;
    private CoachAdapter adapter;
    private int listType = 0;

    @Override
    protected int getContentView() {
        return R.layout.fragment_coach;
    }

    @Override
    protected void initData(View layout, Bundle savedInstanceState) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("QQ_ABEL_ACTION_BROADCAST");
        intentFilter.addAction("CN.ABEL.ACTION.BROADCAST");
        mList = new ArrayList<>();
        reciver = new MyBroadcastReciver();
        getActivity().registerReceiver(reciver, intentFilter);
        if (SpUtils.getBoolean("out", false))
            user = MyApplication.newInstance().getUser();
        handler = new Handler();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        coachStuden.setLayoutManager(layoutManager);
        adapter = new CoachAdapter(mList, getActivity(), listType);

        coachStuden.setAdapter(adapter);
        adapter.setOnItemCheckListener(this);
        adapter.setOnItemPhotoCheckListener(this);
        adapter.setOnItemLongCheckListener(this);
        myRunnable = new Runnable() {
            @Override
            public void run() {
                if (SpUtils.getBoolean("out", false)) {
                    user = MyApplication.newInstance().getUser();
                    if (user != null && user.getUserID() != null) {
                        Log.e(TAG, "更新数据");
                        getStudent();
                        handler.postDelayed(this, 60 * 1000);
                    }
                }
            }
        };

        toastor = new Toastor(getActivity());
        dialog = new LoadingDialog(getActivity());
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        radio_group.check(R.id.rab_student);
        radio_group.setOnCheckedChangeListener(this);

        findStudenPresenterImp = new FindStudenPresenterImp(this, getActivity());
        getSportPresenterImp = new GetSportPresenterImp(this, getActivity());
        getSportStrengthPresenterImp = new GetSportStrengthPresenterImp(this, getActivity());
        getSpeedPresenterImp = new GetSpeedPresenterImp(this, getActivity());
        deleteStudenPresenterImp = new DeleteStudenPresenterImp(new MsgView() {
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
                    getStudent();
                }
            }

            @Override
            public void loadDataError(Throwable throwable) {
                toastor.showSingletonToast("服务器连接失败");

            }
        }, getActivity());

        addStudenPresenterImp = new AddStudenPresenterImp(new MsgView() {
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
                    getStudent();
                }
            }

            @Override
            public void loadDataError(Throwable throwable) {
                Log.e(TAG, throwable.getLocalizedMessage());
                toastor.showSingletonToast("服务器连接失败");
            }
        }, getActivity());
    }


    private void showDialog2() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        DigitsKeyListener numericOnlyListener = new DigitsKeyListener(false, true);
        final EditText editText = new EditText(getActivity());
        editText.setKeyListener(numericOnlyListener);

        editText.setMaxLines(1);
        alertDialog.setTitle("请输入学员手机号").setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (editText.getText().toString().equals("") || editText.getText().toString().length() == 0)
                    return;
                else {
                    map.clear();

                    map.put("coachID", user.getUserID());
                    map.put("phoneNumber", editText.getText().toString().trim());
                    addStudenPresenterImp.loadMsg(map);
                }


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
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        tempDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        initUser();
        getStudent();
    }

    private void initUser() {
        if (SpUtils.getBoolean("out", false)) {
            user = MyApplication.newInstance().getUser();
            if (user != null && MyApplication.newInstance().isLogin) {
                coachNameTv.setText(user.getNikename());
                if (user.getHead_pic() != null && user.getHead_pic().length() > 5) {
                    if (user.getHead_pic().contains("http:")) {
                        MyApplication.newInstance().getGlide().load(user.getHead_pic()).into(coachPicIv);
                    } else {
                        coachPicIv.setImageBitmap(stringtoBitmap(user.getHead_pic()));
                    }

                } else {
                    coachPicIv.setImageDrawable(getResources().getDrawable(R.mipmap.logo));

                }
                String bg = SpUtils.getString(Constant.IMAGE_FILE_NAME, "");
                if (bg.length() > 1) {
                    MyApplication.newInstance().getGlide().load(new File(bg)).centerCrop().diskCacheStrategy(DiskCacheStrategy.RESULT).into(me_title_iv);
                } else {
                    me_title_iv.setBackground(getResources().getDrawable(R.drawable.dakuai));
                }
                if (user.getSex() != null) {
                    if (user.getSex().equals("男")) {
                        coachSexIv.setImageResource(R.drawable.manwhite);
                    } else if (user.getSex().equals("女"))
                        coachSexIv.setImageResource(R.drawable.nvxingbai);

                }

            }
        } else {
            me_title_iv.setBackground(getResources().getDrawable(R.drawable.dakuai));
            mList.clear();
            adapter.setItems(mList, listType);
        }
    }

    @OnClick(R.id.coach_add_people_iv)
    public void click(View view) {
        if (user != null)
            showDialog2();
        else
            toastor.showSingletonToast("未登陆");
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(reciver);
        handler.removeCallbacks(myRunnable);


    }

    private void getUser(JSONArray jsonArray) {
        mList.clear();
        adapter.setItems(mList, listType);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.optJSONObject(i);
            Student user = new Student();
            user.setNikename(jsonObject.optString("name"));
            user.setUserID(jsonObject.optString("userID"));
            user.setSex(jsonObject.optString("sex"));
            if (jsonObject.optString("headPicURL").length() > 0) {
                user.setHead_pic(jsonObject.optString("headPicURL"));
                Log.e("--------1", jsonObject.optString("headPicURL"));
            }
            if (jsonObject.optString("sportLoad") != null)
                user.setLoad(jsonObject.optString("sportLoad"));
            if (jsonObject.optString("sportMin") != null)
                user.setSportMin(jsonObject.optString("sportMin"));
            if (jsonObject.optString("speed") != null)
                user.setSpeed(jsonObject.optString("speed"));
            if (jsonObject.optString("sportStrength") != null && !jsonObject.optString("sportStrength").equals(""))
                user.setStrength(jsonObject.optString("sportStrength") + "");
            else
                user.setStrength("0");
            if (jsonObject.optString("rate") != null)
                user.setHeartrate(jsonObject.optString("rate"));
            if (jsonObject.optString("step") != null)
                user.setSteps(jsonObject.optString("step"));
            if (jsonObject.optString("sportMin") != null)
                user.setTime(jsonObject.optString("sportMin"));
            mList.add(user);

        }
        //处理数据

        adapter.setItems(mList, listType);
    }


    @Override
    public void showProgress() {
        if (dialog != null && !dialog.isShowing() && isOne) {
            dialog.show();
            isOne = false;
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
       // Log.e(TAG, jsonObject.toString());

           // toastor.showSingletonToast(jsonObject.optString("resMessage"));
        if (jsonObject.optInt("resCode") == 0) {
            if (listType == 0) {
                getUser(jsonObject.optJSONObject("resBody").optJSONArray("myTeamData"));
            } else if (listType == 1) {
                getUser(jsonObject.optJSONObject("resBody").optJSONArray("sportLoadList"));
            } else if (listType == 2) {
                getUser(jsonObject.optJSONObject("resBody").optJSONArray("sportStrengthList"));
            } else if (listType == 3) {
                getUser(jsonObject.optJSONObject("resBody").optJSONArray("speedList"));
            }

        }
    }


    @Override
    public void loadDataError(Throwable throwable) {
        Log.e(TAG, throwable.getLocalizedMessage() + throwable.toString());
        toastor.showSingletonToast("服务器连接失败");
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.rab_student:
                listType = 0;

                break;
            case R.id.rab_qiangdu:
                listType = 2;

                break;
            case R.id.rab_sudu:
                listType = 3;

                break;
            case R.id.rab_fuhe:
                listType = 1;

                break;
        }
        isOne = true;
        getStudent();
    }

    private void getStudent() {
        if (SpUtils.getBoolean("out", false)) {
            if (user != null && user.getUserID() != null) {
                map.clear();
                map.put("coachID", user.getUserID());
                map.put("time", DateUtil.getCurrDate(DateUtil.LONG_DATE_FORMAT2));
                if (listType == 0) {
                    findStudenPresenterImp.loadMsg(map);
                } else if (listType == 1) {
                    getSportPresenterImp.loadMsg(map);
                } else if (listType == 2) {
                    getSportStrengthPresenterImp.loadMsg(map);
                } else if (listType == 3) {
                    getSpeedPresenterImp.loadMsg(map);
                }
            }
        } else {
            mList.clear();
            adapter.setItems(mList, listType);
        }
    }

    @Override
    public void OnItemCheck(RecyclerView.ViewHolder viewHolder, int position) {
        startActivity(new Intent(getActivity(), StudentActivity.class).putExtra("student", mList.get(position)));
    }

    @Override
    public void OnPhotoCheck(RecyclerView.ViewHolder viewHolder, int position) {
        startActivity(new Intent(getActivity(), StudentInfoActivity.class).putExtra("student", mList.get(position)));
    }

    @Override
    public void OnItemLong(RecyclerView.ViewHolder viewHolder, int position) {
        deletestu(position);
    }

    private class MyBroadcastReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("QQ_ABEL_ACTION_BROADCAST")) {
                if (user != null && user.getUserID() != null && mList.size() == 0) {
                    //handler.removeCallbacks(myRunnable);
                    Log.e("广播", "收到广播");
                    initUser();
                    getStudent();
                }

            } else if (action.equals("CN.ABEL.ACTION.BROADCAST")) {
                String msg = intent.getStringExtra("author");
                Log.e(TAG, "msg:" + msg + " " + msg.substring(0, msg.length()) + " " + msg.substring(msg.length() - 1, msg.length()));
                for (int i = 0; i < mList.size(); i++) {
                    if (mList.get(i).getUserID().equals(msg.substring(0, msg.length() - 2))) {
                        if (msg.substring(msg.length() - 1, msg.length()).equals("1")) {
                            mList.get(i).setIsLine(true);
                        } else {
                            mList.get(i).setIsLine(false);
                        }
                        //学员状态更新广播
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

        }
    }

    private void deletestu(final int position) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setTitle("是否删除");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                map.clear();
                map.put("studentID", mList.get(position).getUserID());
                map.put("teacherID", user.getUserID());
                deleteStudenPresenterImp.loadMsg(map);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });
        builder.create().show();
    }
}
