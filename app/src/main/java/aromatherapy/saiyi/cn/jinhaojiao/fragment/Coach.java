package aromatherapy.saiyi.cn.jinhaojiao.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aromatherapy.saiyi.cn.jinhaojiao.R;
import aromatherapy.saiyi.cn.jinhaojiao.app.MyApplication;
import aromatherapy.saiyi.cn.jinhaojiao.base.BaseFragment;
import aromatherapy.saiyi.cn.jinhaojiao.bean.User;
import aromatherapy.saiyi.cn.jinhaojiao.presenter.AddStudenPresenterImp;
import aromatherapy.saiyi.cn.jinhaojiao.presenter.FindStudenPresenterImp;
import aromatherapy.saiyi.cn.jinhaojiao.util.Log;
import aromatherapy.saiyi.cn.jinhaojiao.util.Toastor;
import aromatherapy.saiyi.cn.jinhaojiao.view.MsgView;
import aromatherapy.saiyi.cn.jinhaojiao.widget.LoadingDialog;


public class Coach extends BaseFragment implements MsgView {
    private final static String TAG = Coach.class.getSimpleName();
    User user;

    private Map<String, String> map = new HashMap<String, String>();
    private LoadingDialog dialog;
    private Toastor toastor;

    private List<User> mList = new ArrayList<>();
    MyBroadcastReciver reciver;
    private int indext = 0;
    private String Remarks = "";
    private Handler handler;
    private Runnable myRunnable;
    private FindStudenPresenterImp findStudenPresenterImp;
    private AddStudenPresenterImp addStudenPresenterImp;
    private boolean isOne = true;

    @Override
    protected void initData(View layout, Bundle savedInstanceState) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("QQ_ABEL_ACTION_BROADCAST");
        intentFilter.addAction("CN.ABEL.ACTION.BROADCAST");
        reciver = new MyBroadcastReciver();
        getActivity().registerReceiver(reciver, intentFilter);
        user = MyApplication.newInstance().getUser();
        handler = new Handler();
        myRunnable = new Runnable() {
            @Override
            public void run() {
                user = MyApplication.newInstance().getUser();
                if (user != null && user.getUserID() != null) {
                    Log.e(TAG, "更新数据");
                    getStudent();
                }
            }
        };

        toastor = new Toastor(getActivity());
        dialog = new LoadingDialog(getActivity());
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        handler.postDelayed(myRunnable, 500);
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

    /*@OnClick(R.id.coach_add_people_iv)
    public void click(View view) {
        if (user != null)
            showDialog2();
        else
            toastor.getSingletonToast("未登陆");
    }*/

    @Override
    protected int getContentView() {
        return R.layout.fragment_coach;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(reciver);
        handler.removeCallbacks(myRunnable);
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
        user = MyApplication.newInstance().getUser();
        if (user != null && user.getUserID() != null && mList.size() == 0) {
            getStudent();
        }


    }

    private void getUser(JSONArray jsonArray) {
        mList.clear();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.optJSONObject(i);
            User user = new User();
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
            mList.add(user);
        }
        //处理数据
    }


    private void getStudent() {
        map.clear();
        map.put("userID", user.getUserID());
        findStudenPresenterImp.loadMsg(map);

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
            //toastor.getSingletonToast(jsonObject.optString("resMessage"));
            getUser(jsonObject.optJSONObject("resBody").optJSONArray("list"));
        }
    }


    @Override
    public void loadDataError(Throwable throwable) {
        Log.e(TAG, throwable.getLocalizedMessage());
        toastor.showSingletonToast("服务器连接失败");
    }

    private class MyBroadcastReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("QQ_ABEL_ACTION_BROADCAST")) {
                if (user != null && user.getUserID() != null && mList.size() == 0) {
                    handler.removeCallbacks(myRunnable);
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
}
