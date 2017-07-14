package aromatherapy.saiyi.cn.jinhaojiao.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.widget.RadioGroup;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aromatherapy.saiyi.cn.jinhaojiao.R;
import aromatherapy.saiyi.cn.jinhaojiao.adapter.TestFragmentAdapter;
import aromatherapy.saiyi.cn.jinhaojiao.app.MyApplication;
import aromatherapy.saiyi.cn.jinhaojiao.base.BaseActivity;
import aromatherapy.saiyi.cn.jinhaojiao.bean.DeviceInfo;
import aromatherapy.saiyi.cn.jinhaojiao.bean.User;
import aromatherapy.saiyi.cn.jinhaojiao.fragment.Coach;
import aromatherapy.saiyi.cn.jinhaojiao.fragment.Community;
import aromatherapy.saiyi.cn.jinhaojiao.fragment.Home;
import aromatherapy.saiyi.cn.jinhaojiao.fragment.Location;
import aromatherapy.saiyi.cn.jinhaojiao.fragment.LoginFrag;
import aromatherapy.saiyi.cn.jinhaojiao.fragment.Me;
import aromatherapy.saiyi.cn.jinhaojiao.presenter.FindHomePresenterImp;
import aromatherapy.saiyi.cn.jinhaojiao.presenter.LoginPresenterImp;
import aromatherapy.saiyi.cn.jinhaojiao.presenter.ThreeLoginPresenterImp;
import aromatherapy.saiyi.cn.jinhaojiao.util.DateUtil;
import aromatherapy.saiyi.cn.jinhaojiao.util.Log;
import aromatherapy.saiyi.cn.jinhaojiao.util.MD5;
import aromatherapy.saiyi.cn.jinhaojiao.util.Toastor;
import aromatherapy.saiyi.cn.jinhaojiao.view.MsgView;
import aromatherapy.saiyi.cn.jinhaojiao.widget.LoadingDialog;
import aromatherapy.saiyi.cn.jinhaojiao.widget.MyViewPager;
import butterknife.BindView;

public class MainActivity extends BaseActivity implements MsgView {
    public static final String TAG = "MainActivity";
    @BindView(R.id.main_rgrpNavigation)
    RadioGroup rgrpNavigation;
    @BindView(R.id.pager)
    MyViewPager pager;
    private List<Fragment> frags;
    private int currentFragIndex = -1;
    private Map<String, String> map = new HashMap<String, String>();
    private LoadingDialog dialog;
    private Toastor toastor;
    private Handler handler;
    private Runnable myRunnable;
    private Runnable myRunnable2;
    private User user;
    LoginPresenterImp loginPresenterImp;
    ThreeLoginPresenterImp threeLoginPresenterImp;
    FindHomePresenterImp findHomePresenterImp;
    TestFragmentAdapter mAdapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        user = MyApplication.newInstance().getUser();
        toastor = new Toastor(this);
        dialog = new LoadingDialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        frags = new ArrayList<>();
        frags.add(new Home());
        frags.add(new Location());
        frags.add(new Community());
        if (MyApplication.newInstance().getUser() == null)
            frags.add(new LoginFrag());
        else
            frags.add(new Me());
        mAdapter = new TestFragmentAdapter(getSupportFragmentManager(), frags);
        pager.setAdapter(mAdapter);
        pager.setOffscreenPageLimit(3);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        rgrpNavigation.check(R.id.rab_purpose);
                        break;
                    case 1:
                        rgrpNavigation.check(R.id.rab_rent);
                        break;
                    case 2:
                        rgrpNavigation.check(R.id.rab_flowdeal);
                        break;
                    case 3:
                        rgrpNavigation.check(R.id.rab_me);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        initHttp();
        if (user != null) {
            if (user.getPassword() != null && user.getPassword().length() > 0) {
                dialog.show();
                map.clear();
                map.put("phoneNumber", user.getPhone());
                map.put("passWord", MD5.getMD5(user.getPassword()));
                loginPresenterImp.loadMsg(map);
            } else if (user.getOpenid() != null && user.getOpenid().length() > 0) {
                dialog.show();
                map.clear();
                map.put("account", user.getOpenid());
                threeLoginPresenterImp.loadMsg(map);
            }

        }
        initNavigation();

        rgrpNavigation.check(R.id.rab_purpose);
        handler = new Handler();
        myRunnable = new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "更新数据");
                map.clear();
                if (user.getEquipmentID() != null && user.getEquipmentID().length() > 0) {
                    map.put("equipmentID", user.getEquipmentID());
                    map.put("userID", user.getUserID());
                    map.put("time", DateUtil.getCurrDate(DateUtil.LONG_DATE_FORMAT2));
                    findHomePresenterImp.loadMsg(map);
                   // handler.postDelayed(this, 10000);
                }


            }
        };

        myRunnable2 = new Runnable() {
            @Override
            public void run() {
                user = MyApplication.newInstance().getUser();
                if (user != null) {
                    frags.remove(0);
                    if (user.getType() == 1) {

                        frags.add(0, new Home());

                    } else if (user.getType() == 0) {
                        //教练

                        frags.add(0, new Coach());

                    }
                    mAdapter.setCount(frags);
                    type = 0;
                    //学员
                    rgrpNavigation.check(R.id.rab_purpose);
                }
            }
        };

    }


    /**
     * 初始化底部导航栏
     */
    private void initNavigation() {
        rgrpNavigation = (RadioGroup) findViewById(R.id.main_rgrpNavigation);
        rgrpNavigation
                .setOnCheckedChangeListener(new CheckedChangeListener());
        // 设置选中第一个rbtn
        rgrpNavigation.check(currentFragIndex + 1);
    }


    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        Log.e("onResumeFragments", type + "");
        if (user != null)
            if (type == 1) {
                frags.remove(3);
                frags.add(new Me());
                mAdapter.setCount(frags);
                type = 0;
            }
    }

    int type = 0;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 1) {
            type = resultCode;
            handler.postDelayed(myRunnable2, 2);
        }
        if (resultCode == 2) {
            handler.removeCallbacks(myRunnable);
            type = resultCode;
            frags.remove(3);
            frags.add(new LoginFrag());
            mAdapter.setCount(frags);
            type = 0;
        }
        Log.e("type", resultCode + " " + requestCode);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        user = MyApplication.newInstance().getUser();
        if (user != null) {
            if (user.getEquipmentID() != null && user.getEquipmentID().length() > 0) {
                map.clear();
                map.put("equipmentID", MyApplication.newInstance().getUser().getEquipmentID());
                map.put("time", DateUtil.getCurrDate(DateUtil.LONG_DATE_FORMAT2));
                map.put("userID", user.getUserID());
                findHomePresenterImp.loadMsg(map);
            }
        }
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

        if (jsonObject.optInt("resCode") == 1) {
            MyApplication.newInstance().outLogin();
            toastor.showSingletonToast(jsonObject.optString("resMessage"));
            frags.remove(3);
            frags.add(new LoginFrag());
            mAdapter.setCount(frags);
        } else if (jsonObject.optInt("resCode") == 0) {
            JSONObject json = jsonObject.optJSONObject("resBody");
            User user = MyApplication.newInstance().getUser();
            user.setUserID(json.optString("userID"));
            user.setSex(json.optString("sex"));
            if (json.optInt("flag") == 1) {
                user.setType(1);
            } else
                user.setType(0);
            user.setNikename(json.optString("nickName"));
            if (json.optString("headPicURL").length() > 0) {
                user.setHead_pic(json.optString("headPicURL"));
            }

            if (json.optString("equipment").length() > 0) {
                user.setEquipment(json.optString("equipment"));
                Log.e(TAG, user.getEquipment());

            }
            if (json.optString("equipmentID").length() > 0) {
                user.setEquipmentID(json.optString("equipmentID"));
                handler.postDelayed(myRunnable, 10);

            }
            MyApplication.newInstance().setUser(user);

            Intent intent2 = new Intent();
            intent2.setAction("QQ_ABEL_ACTION_BROADCAST");
            //发送 一个无序广播
            sendBroadcast(intent2);
            handler.postDelayed(myRunnable2, 2);
        }
    }

    @Override
    public void loadDataError(Throwable throwable) {
        Log.e(TAG, throwable.getLocalizedMessage());
        toastor.showSingletonToast("服务器连接失败");
        frags.remove(3);
        frags.add(new LoginFrag());
        mAdapter.setCount(frags);
    }

    private class CheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.rab_purpose:
                    pager.setCurrentItem(0);
                    break;
                case R.id.rab_rent:
                    pager.setCurrentItem(1);
                    break;
                case R.id.rab_sign:
                    toastor.showSingletonToast("签到暂未开放");
                    break;
                case R.id.rab_flowdeal:
                    pager.setCurrentItem(2);
                    break;
                case R.id.rab_me:
                    pager.setCurrentItem(3);
                    break;
            }
        }
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

    private void initHttp() {
        loginPresenterImp = new LoginPresenterImp(this, this);
        //第三方登陆
        threeLoginPresenterImp = new ThreeLoginPresenterImp(new MsgView() {
            @Override
            public void showProgress() {

            }

            @Override
            public void disimissProgress() {

            }

            @Override
            public void loadDataSuccess(JSONObject jsonObject) {
                toastor.showSingletonToast(jsonObject.optString("resMessage"));
                if (jsonObject.optInt("resCode") == 0) {
                    JSONObject json = jsonObject.optJSONObject("resBody");
                    User user = MyApplication.newInstance().getUser();
                    user.setUserID(json.optString("userID"));
                    user.setSex(json.optString("sex"));
                    if (json.optInt("flag") == 1) {
                        user.setType(1);
                    } else {
                        user.setType(0);

                    }
                    user.setNikename(json.optString("nickName"));
                    if (json.optString("equipmentID").length() > 0) {
                        user.setEquipmentID(json.optString("equipmentID"));

                    }
                    if (json.optString("equipment").length() > 0) {
                        user.setEquipment(json.optString("equipment"));
                    }
                    if (json.optString("headPicURL").length() > 0) {
                        user.setHead_pic(json.optString("headPicURL"));

                    }
                    MyApplication.newInstance().setUser(user);

                    Intent intent2 = new Intent();
                    intent2.setAction("QQ_ABEL_ACTION_BROADCAST");
                    //发送 一个无序广播
                    sendBroadcast(intent2);
                    handler.postDelayed(myRunnable2, 2);
                }else {
                    MyApplication.newInstance().outLogin();
                    toastor.showSingletonToast(jsonObject.optString("resMessage"));
                    frags.remove(3);
                    frags.add(new LoginFrag());
                    mAdapter.setCount(frags);
                }
            }

            @Override
            public void loadDataError(Throwable throwable) {
                Log.e(TAG, throwable.getLocalizedMessage());
                toastor.showSingletonToast("服务器连接失败");
                frags.remove(3);
                frags.add(new LoginFrag());
                mAdapter.setCount(frags);
              
            }
        }, this);
        //查询首页数据
        findHomePresenterImp = new FindHomePresenterImp(new MsgView() {
            @Override
            public void showProgress() {

            }

            @Override
            public void disimissProgress() {

            }

            @Override
            public void loadDataSuccess(JSONObject jsonObject) {
               // toastor.showSingletonToast(jsonObject.optString("resMessage"));
                if (jsonObject.optInt("resCode") == 0) {
                    JSONObject json = jsonObject.optJSONObject("resBody");
                    DeviceInfo deviceInfo = new DeviceInfo();
                    deviceInfo.setCalorie(json.optString("Calorie"));
                    deviceInfo.setDistance(json.optString("distance"));
                    deviceInfo.setSpeed(json.optString("speed"));
                    deviceInfo.setHeartrate(json.optString("heartrate"));
                    deviceInfo.setSteps(json.optString("steps"));
                    deviceInfo.setEquipmentID(json.optString("equipmentID"));
                    MyApplication.newInstance().setDeviceInfo(deviceInfo);
                    Intent intent = new Intent();
                    intent.setAction("DATA_RECEIVE_BROADCAST");
                    //要发送的内容
                    intent.putExtra("device", deviceInfo);
                    sendBroadcast(intent);
                }
            }

            @Override
            public void loadDataError(Throwable throwable) {
                Log.e(TAG, throwable.getLocalizedMessage());
                toastor.showSingletonToast("服务器连接失败");
            }
        }, this);
    }

}
