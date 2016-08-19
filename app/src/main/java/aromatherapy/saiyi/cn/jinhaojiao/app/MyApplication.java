package aromatherapy.saiyi.cn.jinhaojiao.app;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.umeng.socialize.PlatformConfig;

import java.util.ArrayList;
import java.util.List;

import aromatherapy.saiyi.cn.jinhaojiao.bean.DeviceInfo;
import aromatherapy.saiyi.cn.jinhaojiao.bean.User;

public class MyApplication extends Application {
    private static final String TAG = "MyApplication";

    private static MyApplication instance;
    public static List<Activity> activitiesList = new ArrayList<Activity>();    //活动管理集合
    private static DeviceInfo deviceInfo=new DeviceInfo();

    public  DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public  void setDeviceInfo(DeviceInfo deviceInfo) {
        MyApplication.deviceInfo = deviceInfo;
    }

    //保存登录用户信息
    private User user= new User();

    {
        PlatformConfig.setWeixin("wx9b8ccd08509634e2", "69101961f470240bae0997616143fda1");
        PlatformConfig.setQQZone("1105335562", "BXMI7zik4IkUh31f");
    }



    public User getUser() {
        SharedPreferences sharedPre = this.getSharedPreferences("user", this.MODE_PRIVATE);
        String phone = sharedPre.getString("phone", "");
        String password = sharedPre.getString("password", "");
        Log.e(TAG, password + " " + phone);
        if (password.equals("") || password.equals("")) {
            return null;
        } else {
            user.setPhone(phone);
            user.setPassword(password);
            return user;
        }
    }
    public void outLogin() {
        user = null;
        SharedPreferences sharedPre = this.getSharedPreferences("user", this.MODE_PRIVATE);
        //获取Editor对象
        SharedPreferences.Editor editor = sharedPre.edit();
        //设置参数
        editor.putString("phone", "");
        editor.putString("password", "");
        //提交
        editor.commit();
    }
    public void setUser(User user) {
        this.user = user;
        //获取SharedPreferences对象
        SharedPreferences sharedPre = this.getSharedPreferences("user", this.MODE_PRIVATE);
        //获取Editor对象
        SharedPreferences.Editor editor = sharedPre.edit();
        //设置参数
        editor.putString("phone", user.getPhone());
        editor.putString("password", user.getPassword());
        Log.e(TAG, user.getPassword() + " " + user.getPhone());
        //提交
        editor.commit();
    }

    /**
     * 获取单例
     *
     * @return
     */
    public static MyApplication newInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mQueue = Volley.newRequestQueue(this);
        instance = this;

    }
    private RequestQueue mQueue;

    public RequestQueue getmQueue() {
        return mQueue;
    }
    /**
     * 把活动添加到活动管理集合
     *
     * @param acty
     */
    public void addActyToList(Activity acty) {
        if (!activitiesList.contains(acty))
            activitiesList.add(acty);
    }

    /**
     * 把活动从活动管理集合移除
     *
     * @param acty
     */
    public void removeActyFromList(Activity acty) {
        if (activitiesList.contains(acty))
            activitiesList.remove(acty);
    }

    /**
     * 程序退出
     */
    public void clearAllActies() {
        for (Activity acty : activitiesList) {
            if (acty != null)
                acty.finish();
        }
        try {
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/*    *//**
     * 获取保存登录用户信息
     *//*
    public User getUser() {
        return user;
    }

    *//**
     * 添加自动登录用户信息
     *//*
    public void setUser(User user) {
        this.user = user;
    }

    *//**
     * 自动登录方法
     *//*
    public void autoLogin(String username, String password) {

        SharedPreferences sp = getSharedPreferences("autologin", MODE_PRIVATE);
        SharedPreferences.Editor et = sp.edit();
        et.putString("username", username);
        et.putString("password", password);
        et.commit();
    }*/

}
