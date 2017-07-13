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
import aromatherapy.saiyi.cn.jinhaojiao.connector.OnItemPhotoCheckListener;
import aromatherapy.saiyi.cn.jinhaojiao.presenter.AddStudenPresenterImp;
import aromatherapy.saiyi.cn.jinhaojiao.presenter.FindStudenPresenterImp;
import aromatherapy.saiyi.cn.jinhaojiao.util.Log;
import aromatherapy.saiyi.cn.jinhaojiao.util.Toastor;
import aromatherapy.saiyi.cn.jinhaojiao.view.MsgView;
import aromatherapy.saiyi.cn.jinhaojiao.widget.LoadingDialog;
import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static aromatherapy.saiyi.cn.jinhaojiao.util.AppUtil.stringtoBitmap;


public class Coach extends BaseFragment implements MsgView, RadioGroup.OnCheckedChangeListener, OnItemCheckListener, OnItemPhotoCheckListener {
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
    private FindStudenPresenterImp findStudenPresenterImp;
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
        user = MyApplication.newInstance().getUser();
        handler = new Handler();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        coachStuden.setLayoutManager(layoutManager);
        adapter = new CoachAdapter(mList, getActivity(), listType);

        coachStuden.setAdapter(adapter);
        adapter.setOnItemCheckListener(this);
        adapter.setOnItemPhotoCheckListener(this);
        myRunnable = new Runnable() {
            @Override
            public void run() {
                user = MyApplication.newInstance().getUser();
                if (user != null && user.getUserID() != null) {
                    Log.e(TAG, "更新数据");
                    getStudent();
                    // handler.postDelayed(this, 60 * 1000);
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
                    toastor.showSingletonToast(jsonObject.optString("resMessage"));
                    map.clear();
                    map.put("userID", user.getUserID());
                    findStudenPresenterImp.loadMsg(map);
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
        user = MyApplication.newInstance().getUser();
        if (user != null) {
            coachNameTv.setText(user.getNikename());
            if (user.getHead_pic() != null && user.getHead_pic().length() > 5) {
                if (user.getHead_pic().contains("http:")) {
                    MyApplication.newInstance().getGlide().load(user.getHead_pic()).into(coachPicIv);
                    MyApplication.newInstance().getGlide().load(user.getHead_pic()).centerCrop().diskCacheStrategy(DiskCacheStrategy.RESULT).into(me_title_iv);

                } else
                    coachPicIv.setImageBitmap(stringtoBitmap(user.getHead_pic()));
            } else {
                coachPicIv.setImageDrawable(getResources().getDrawable(R.mipmap.logo));
                me_title_iv.setBackground(getResources().getDrawable(R.drawable.dakuai));
            }
            if (user.getSex() != null) {
                if (user.getSex().equals("男")) {
                    coachSexIv.setImageResource(R.drawable.manwhite);
                } else if (user.getSex().equals("女"))
                    coachSexIv.setImageResource(R.drawable.nvxingbai);

            }

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
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.optJSONObject(i);
            Student user = new Student();
            if (jsonObject.optString("Remarks").length() > 0) {
                user.setNikename(jsonObject.optString("Remarks"));
                Log.e("-------", jsonObject.optString("Remarks"));
            } else {
                user.setNikename(jsonObject.optString("nickName"));
            }

            user.setUserID(jsonObject.optString("userID"));
            user.setEquipmentID(jsonObject.optString("equipmentID"));
            if (jsonObject.optInt("zhuangtai") == 1) {
                user.setIsLine(true);
            } else if (jsonObject.optInt("zhuangtai") == 0) {
                user.setIsLine(false);
            }
            if (jsonObject.optString("headPicByte").length() > 0) {
                user.setHead_pic(jsonObject.optString("headPicByte"));
                Log.e("--------1", jsonObject.optString("headPicByte"));
            }
            user.setLoad("30");
            user.setSpeed("40");
            user.setStrength("50");
            user.setHeartrate("130");
            user.setDistance("8");
            user.setTime("30");
            mList.add(user);
            mList.add(user);
        }
        //处理数据

        adapter.setItems(mList, listType);
    }


    private void getStudent() {
        if (user != null) {
            map.clear();
            map.put("userID", user.getUserID());
            findStudenPresenterImp.loadMsg(map);
        }


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
        Log.e(TAG, jsonObject.toString());
        if (isOne)
            toastor.showSingletonToast(jsonObject.optString("resMessage"));
        if (jsonObject.optInt("resCode") == 0) {

            getUser(jsonObject.optJSONObject("resBody").optJSONArray("list"));
        }
    }


    @Override
    public void loadDataError(Throwable throwable) {
        Log.e(TAG, throwable.getLocalizedMessage());
        toastor.showSingletonToast("服务器连接失败");
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.rab_student:
                listType = 0;
                adapter.setItems(mList, listType);
                break;
            case R.id.rab_qiangdu:
                listType = 2;
                adapter.setItems(mList, listType);
                break;
            case R.id.rab_sudu:
                listType = 3;
                adapter.setItems(mList, listType);
                break;
            case R.id.rab_fuhe:
                listType = 1;
                adapter.setItems(mList, listType);
                break;
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

    private class MyBroadcastReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("QQ_ABEL_ACTION_BROADCAST")) {
                if (user != null && user.getUserID() != null && mList.size() == 0) {
                    //handler.removeCallbacks(myRunnable);
                    Log.e("广播", "收到广播");
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
                        //adapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        //判断Fragment中的ListView时候存在，判断该Fragment时候已经正在前台显示  通过这两个判断，就可以知道什么时候去加载数据了
        if (isVisibleToUser) {
        } else {

        }
        super.setUserVisibleHint(isVisibleToUser);
    }
}
