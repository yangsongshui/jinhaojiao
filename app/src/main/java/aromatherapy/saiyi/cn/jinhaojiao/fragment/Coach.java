package aromatherapy.saiyi.cn.jinhaojiao.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.text.method.DigitsKeyListener;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

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
import aromatherapy.saiyi.cn.jinhaojiao.bean.User;
import aromatherapy.saiyi.cn.jinhaojiao.util.Constant;
import aromatherapy.saiyi.cn.jinhaojiao.util.Log;
import aromatherapy.saiyi.cn.jinhaojiao.util.NormalPostRequest;
import aromatherapy.saiyi.cn.jinhaojiao.util.Toastor;
import aromatherapy.saiyi.cn.jinhaojiao.view.LoadingDialog;
import butterknife.BindView;
import butterknife.OnClick;

public class Coach extends BaseFragment implements Response.ErrorListener, AdapterView.OnItemClickListener, SwipeMenuListView.OnMenuItemClickListener {
    private final static String TAG = Coach.class.getSimpleName();
    User user;
    @BindView(R.id.coach_people_lv)
    SwipeMenuListView coach_people_lv;
    CoachAdapter adapter;
    private Map<String, String> map = new HashMap<String, String>();
    private LoadingDialog dialog;
    private Toastor toastor;
    private RequestQueue mQueue;
    private List<User> mList = new ArrayList<>();
    MyBroadcastReciver reciver;
    private int indext = 0;
    private String Remarks = "";
    private Handler handler;
    private Runnable myRunnable;

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
        mQueue = MyApplication.newInstance().getmQueue();
        toastor = new Toastor(getActivity());
        dialog = new LoadingDialog(getActivity());
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        adapter = new CoachAdapter(mList, getActivity());
        coach_people_lv.setMenuCreator(creator);
        coach_people_lv.setAdapter(adapter);
        coach_people_lv.setOnItemClickListener(this);
        coach_people_lv.setOnMenuItemClickListener(this);
        handler.postDelayed(myRunnable, 500);
    }

    @OnClick(R.id.coach_add_people_iv)
    public void click(View view) {
        if (user != null)
            showDialog2();
        else
            toastor.getSingletonToast("未登陆");
    }

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
                    mQueue.add(normalPostRequest2);
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
     /*   user = MyApplication.newInstance().getUser();
        if (user != null && user.getUserID() != null && mList.size() == 0){
            getStudent();
        }*/


    }

    /*添加侧滑菜单*/
    SwipeMenuCreator creator = new SwipeMenuCreator() {

        @Override
        public void create(SwipeMenu menu) {
            //改名菜单
            SwipeMenuItem alterItem = new SwipeMenuItem(
                    getActivity().getApplicationContext());
            // 设置背景颜色
            alterItem.setBackground(new ColorDrawable(getResources().getColor(R.color.dimgrey)));
            // 设置宽度
            alterItem.setWidth(dp2px(70));
            // 设置内容
            alterItem.setTitle("更名");
            // 设置字体大小
            alterItem.setTitleSize(18);
            // 字体颜色
            alterItem.setTitleColor(getResources().getColor(R.color.white));
            // 添加菜单
            menu.addMenuItem(alterItem);

            // 删除菜单
            SwipeMenuItem deleteItem = new SwipeMenuItem(
                    getActivity().getApplicationContext());
            // set item background
            deleteItem.setBackground(new ColorDrawable(getResources().getColor(R.color.red_f03_98)));
            // set item width
            deleteItem.setWidth(dp2px(70));
            // 设置内容
            deleteItem.setTitle("删除");
            // 设置字体大小
            deleteItem.setTitleSize(18);
            // 字体颜色
            deleteItem.setTitleColor(getResources().getColor(R.color.white));
            // add to menu
            menu.addMenuItem(deleteItem);
        }
    };
    NormalPostRequest normalPostRequest = new NormalPostRequest(Constant.FIND_STUDENT, new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            Log.e(TAG, jsonObject.toString());
            dialog.dismiss();
            if (jsonObject.optInt("resCode") == 1) {
                toastor.getSingletonToast(jsonObject.optString("resMessage"));
            } else if (jsonObject.optInt("resCode") == 0) {
                //toastor.getSingletonToast(jsonObject.optString("resMessage"));
                getUser(jsonObject.optJSONObject("resBody").optJSONArray("list"));
            }

        }
    }, this, map);

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
        adapter.notifyDataSetChanged();
    }

    NormalPostRequest normalPostRequest2 = new NormalPostRequest(Constant.ADD_STUDENT, new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            Log.e(TAG, jsonObject.toString());
            if (jsonObject.optInt("resCode") == 1) {
                toastor.getSingletonToast(jsonObject.optString("resMessage"));
            } else if (jsonObject.optInt("resCode") == 0) {
                toastor.getSingletonToast(jsonObject.optString("resMessage"));
                map.clear();
                map.put("userID", user.getUserID());
                mQueue.add(normalPostRequest);
            }

        }
    }, this, map);
    NormalPostRequest normalPostRequest3 = new NormalPostRequest(Constant.DELETESTUDENT, new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            Log.e(TAG, jsonObject.toString());
            dialog.dismiss();
            if (jsonObject.optInt("resCode") == 1) {
                toastor.getSingletonToast(jsonObject.optString("resMessage"));
            } else if (jsonObject.optInt("resCode") == 0) {
                toastor.getSingletonToast(jsonObject.optString("resMessage"));
                mList.remove(indext);
                adapter.notifyDataSetChanged();
            }

        }
    }, this, map);
    NormalPostRequest normalPostRequest4 = new NormalPostRequest(Constant.UPDATEBEIZHU, new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            Log.e(TAG, jsonObject.toString());
            dialog.dismiss();
            if (jsonObject.optInt("resCode") == 1) {
                toastor.getSingletonToast(jsonObject.optString("resMessage"));
            } else if (jsonObject.optInt("resCode") == 0) {
                toastor.getSingletonToast(jsonObject.optString("resMessage"));
                mList.get(indext).setNikename(Remarks);
                adapter.notifyDataSetChanged();
            }

        }
    }, this, map);

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        dialog.dismiss();
        toastor.getSingletonToast("服务器异常");
    }

    private void getStudent() {
        map.clear();
        map.put("userID", user.getUserID());
        mQueue.add(normalPostRequest);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        showDialog(position);
    }

    private void showDialog(final int position) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("请选择").setMessage("选择查看学员数据").setPositiveButton("查看个人资料", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                startActivity(new Intent(getActivity(), StudentInfoActivity.class).putExtra("student", mList.get(position)));

            }
        }).setNegativeButton("查看个人数据", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(getActivity(), StudentActivity.class).putExtra("student", mList.get(position)));
            }
        }).create().show();

    }

    @Override
    public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
        switch (index) {
            case 0:
                //改名
                dialog(position);
                break;
            case 1:
                dialog.show();
                //删除
                map.clear();
                map.put("teacherID", user.getUserID());
                map.put("studentID", mList.get(position).getUserID());
                mQueue.add(normalPostRequest3);
                indext = position;
                break;
        }
        return false;
    }


    private void dialog(final int position) {
        final EditText et = new EditText(getActivity());
        et.setMaxLines(1);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("更名");
        builder.setView(et);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (et.getText().toString().length() > 0) {
                    map.clear();
                    map.put("teacherID", user.getUserID());
                    map.put("studentID", mList.get(position).getUserID());
                    map.put("Remarks", et.getText().toString());
                    indext = position;
                    Remarks = et.getText().toString();
                    mQueue.add(normalPostRequest4);
                } else {
                    toastor.getSingletonToast("备注不可为空");
                }

                dialog.dismiss(); //关闭dialog
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
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
                Log.e(TAG, "msg:"+msg+" "+msg.substring(0, msg.length())+" "+msg.substring(msg.length() - 1, msg.length()));
                for (int i = 0; i < mList.size(); i++) {
                    if (mList.get(i).getUserID().equals(msg.substring(0, msg.length() - 2))) {
                        if (msg.substring(msg.length() - 1, msg.length()).equals("1")) {
                            mList.get(i).setIsLine(true);
                        } else {
                            mList.get(i).setIsLine(false);
                        }
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

        }
    }
}
