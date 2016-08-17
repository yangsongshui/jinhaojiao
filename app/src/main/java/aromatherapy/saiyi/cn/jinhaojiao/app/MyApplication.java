package aromatherapy.saiyi.cn.jinhaojiao.app;

import android.app.Activity;
import android.app.Application;

import com.umeng.socialize.PlatformConfig;

import java.util.ArrayList;
import java.util.List;

import aromatherapy.saiyi.cn.jinhaojiao.bean.User;

public class MyApplication extends Application {
    private static final String TAG = "MyApplication";

    private static MyApplication instance;
    public static List<Activity> activitiesList = new ArrayList<Activity>();    //活动管理集合
    //保存登录用户信息
    private User user;

    {
        PlatformConfig.setWeixin("wx9b8ccd08509634e2", "69101961f470240bae0997616143fda1");
        PlatformConfig.setQQZone("1105335562", "BXMI7zik4IkUh31f");
    }



    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
        instance = this;

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
