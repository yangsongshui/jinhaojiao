package aromatherapy.saiyi.cn.jinhaojiao.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.widget.RadioGroup;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;
import org.xutils.view.annotation.ViewInject;

import java.util.HashMap;
import java.util.Map;

import aromatherapy.saiyi.cn.jinhaojiao.R;
import aromatherapy.saiyi.cn.jinhaojiao.app.MyApplication;
import aromatherapy.saiyi.cn.jinhaojiao.base.BaseActivity;
import aromatherapy.saiyi.cn.jinhaojiao.bean.DeviceInfo;
import aromatherapy.saiyi.cn.jinhaojiao.bean.User;
import aromatherapy.saiyi.cn.jinhaojiao.fragment.Community;
import aromatherapy.saiyi.cn.jinhaojiao.fragment.Home;
import aromatherapy.saiyi.cn.jinhaojiao.fragment.Location;
import aromatherapy.saiyi.cn.jinhaojiao.fragment.LoginFrag;
import aromatherapy.saiyi.cn.jinhaojiao.fragment.Me;
import aromatherapy.saiyi.cn.jinhaojiao.util.Constant;
import aromatherapy.saiyi.cn.jinhaojiao.util.DateUtil;
import aromatherapy.saiyi.cn.jinhaojiao.util.Log;
import aromatherapy.saiyi.cn.jinhaojiao.util.MD5;
import aromatherapy.saiyi.cn.jinhaojiao.util.NormalPostRequest;
import aromatherapy.saiyi.cn.jinhaojiao.util.Toastor;
import aromatherapy.saiyi.cn.jinhaojiao.view.LoadingDialog;

public class MainActivity extends BaseActivity implements Response.ErrorListener {
    public static final String TAG = "MainActivity";
    @ViewInject(R.id.main_rgrpNavigation)
    RadioGroup rgrpNavigation;
    private Fragment[] frags = new Fragment[4];
    private int currentFragIndex = -1;
    private Map<String, String> map = new HashMap<String, String>();
    private LoadingDialog dialog;
    private Toastor toastor;
    private RequestQueue mQueue;
    private Handler handler;
    private Runnable myRunnable;
    private User user;
    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void init() {
        user=MyApplication.newInstance().getUser();
        mQueue = MyApplication.newInstance().getmQueue();
        toastor = new Toastor(this);
        dialog = new LoadingDialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        if (user != null) {
            dialog.show();
            map.clear();
            map.put("phoneNumber", MyApplication.newInstance().getUser().getPhone());
            map.put("passWord", MD5.getMD5(MyApplication.newInstance().getUser().getPassword()));
            mQueue.add(normalPostRequest);
        }
        initNavigation();
        showFrag(0);
        rgrpNavigation.check(R.id.rab_purpose);
        handler = new Handler();
        myRunnable = new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "更新数据");
                map.clear();
                if (user.getEquipmentID()!=null){
                    map.put("equipmentID", user.getEquipmentID());
                    map.put("time", DateUtil.getCurrDate(DateUtil.LONG_DATE_FORMAT2));
                    mQueue.add(normalPostRequest2);
                    handler.postDelayed(this, 10000);
                }


            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    /**
     * 初始化碎片的viewpager
     */
    private void showFrag(int index) {
        if (index == currentFragIndex)
            return;
        FragmentTransaction fragTran = getSupportFragmentManager()
                .beginTransaction();
        if (currentFragIndex != -1) {
            // 已经创建过frag
            fragTran.detach(frags[currentFragIndex]);
        }
        // 现在选择的碎片没有创建过
        if (frags[index] == null) {
            frags[index] = getFrag(index);
            fragTran.replace(R.id.main_frame, frags[index]);
        } else
            fragTran.attach(frags[index]);
        currentFragIndex = index;
        fragTran.commit();
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

    private Fragment getFrag(int index) {
        switch (index) {
            case 0:
                return new Home();
            case 1:
                return new Location();
            case 2:
                return new Community();
            case 3:
                if (MyApplication.newInstance().getUser() == null)
                    return new LoginFrag();
                else
                    return new Me();
            default:
                return null;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (type == 1) {
            FragmentTransaction fragTran = getSupportFragmentManager()
                    .beginTransaction();
            frags[3] = new Me();
            fragTran.replace(R.id.main_frame,
                    frags[3]);
            Log.e(TAG, "Me");
            fragTran.commit();
            type = 0;
        }
    }

    int type = 0;
    NormalPostRequest normalPostRequest = new NormalPostRequest(Constant.LOGIN, new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            Log.e(TAG, jsonObject.toString());
            if (jsonObject.optInt("resCode") == 1) {
                MyApplication.newInstance().outLogin();
                toastor.showToast(jsonObject.optString("resMessage"));
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
                if (json.optString("headPicByte").length() > 0) {
                    user.setBitmap(stringtoBitmap(json.optString("headPicByte")));
                }

                if (json.optString("equipment").length() > 0) {
                    user.setEquipment(json.optString("equipment"));
                    Log.e(TAG, user.getEquipment());
                }
                if (json.optString("equipmentID").length() > 0) {
                    user.setEquipmentID(json.optString("equipmentID"));
                    handler.postDelayed(myRunnable, 10);
                }
                toastor.showToast("登陆成功");
                Intent intent2 = new Intent();
                intent2.setAction("CN_ABEL_ACTION_BROADCAST");
                //发送 一个无序广播
                sendBroadcast(intent2);
            }
        }
    }, this, map);
    NormalPostRequest normalPostRequest2 = new NormalPostRequest(Constant.FINDHEARTRATEMOTION, new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            Log.e(TAG, jsonObject.toString());
            dialog.dismiss();
            if (jsonObject.optInt("resCode") == 1) {
                toastor.showToast(jsonObject.optString("resMessage"));
            } else if (jsonObject.optInt("resCode") == 0) {
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
    }, this, map);

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 1) {
            type = resultCode;
        }
        if (resultCode == 2) {
            type = resultCode;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (type == 2) {
            FragmentTransaction fragTran = getSupportFragmentManager()
                    .beginTransaction();
            frags[3] = new LoginFrag();
            fragTran.replace(R.id.main_frame,
                    frags[3]);
            fragTran.commit();
        }
        if (MyApplication.newInstance().getUser().getEquipmentID() != null) {
            Log.e("-------", MyApplication.newInstance().getUser().getEquipmentID()+"------");
            map.clear();
            map.put("equipmentID", MyApplication.newInstance().getUser().getEquipmentID());
            map.put("time", DateUtil.getCurrDate(DateUtil.LONG_DATE_FORMAT2));
            mQueue.add(normalPostRequest2);
        }
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        dialog.dismiss();
        MyApplication.newInstance().outLogin();
        toastor.showToast("登陆异常,请重新登陆...   ");
    }

    private class CheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.rab_purpose:
                    showFrag(0);
                    break;
                case R.id.rab_rent:
                    showFrag(1);
                    break;
                case R.id.rab_flowdeal:
                    showFrag(2);
                    break;
                case R.id.rab_me:
                    showFrag(3);
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


}
