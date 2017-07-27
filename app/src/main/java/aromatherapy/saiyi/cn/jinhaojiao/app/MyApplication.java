package aromatherapy.saiyi.cn.jinhaojiao.app;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import aromatherapy.saiyi.cn.jinhaojiao.bean.User;
import aromatherapy.saiyi.cn.jinhaojiao.util.AppContextUtil;
import aromatherapy.saiyi.cn.jinhaojiao.util.SpUtils;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

public class MyApplication extends Application {
    private static final String TAG = "MyApplication";

    private static MyApplication instance;
    public static List<Activity> activitiesList = new ArrayList<Activity>();    //活动管理集合
    public boolean isLogin = false;


    //保存登录用户信息
    private User user = new User();

    {
        PlatformConfig.setWeixin("wx9b8ccd08509634e2", "e3fd3dbc651c79fdc01ef5b0f057b3a0");
        PlatformConfig.setQQZone("1105725444", "6Q64qSIcWxDnqg3P");
        Config.DEBUG = true;
    }


    public User getUser() {
        SharedPreferences sharedPre = this.getSharedPreferences("user", this.MODE_PRIVATE);
        String phone = sharedPre.getString("phone", "");
        String password = sharedPre.getString("password", "");
        String equipmentID = sharedPre.getString("equipmentID", "");
        String openid = sharedPre.getString("openid", "");
        Log.e(TAG, password + " " + phone + openid);
        if (openid.length() > 0) {
            user.setPhone(phone);
            user.setOpenid(openid);
            user.setEquipmentID(equipmentID);
            return user;
        } else if ((password.equals("") && password.equals(""))) {
            return null;
        } else {
            user.setPhone(phone);
            user.setPassword(password);
            user.setEquipmentID(equipmentID);
            return user;
        }
    }

    public void outLogin() {
        SpUtils.putBoolean("out", false);
        isLogin = false;
      /*  user = null;
        SharedPreferences sharedPre = this.getSharedPreferences("user", this.MODE_PRIVATE);
        //获取Editor对象
        SharedPreferences.Editor editor = sharedPre.edit();
        //设置参数
        editor.putString("phone", "");
        editor.putString("password", "");
        editor.putString("equipmentID", "");
        editor.putString("openid", "");
        //提交
        editor.commit();*/
    }


    public void setUser(User user) {

        this.user = user;
        isLogin = true;
        if (user.getType() == 1) {
            JPushInterface.setAliasAndTags(getApplicationContext(),
                    null,
                    null,
                    mAliasCallback);
        } else {
            JPushInterface.setAliasAndTags(getApplicationContext(),
                    user.getUserID(),
                    null,
                    mAliasCallback);
        }

        //获取SharedPreferences对象
        SharedPreferences sharedPre = this.getSharedPreferences("user", this.MODE_PRIVATE);
        //获取Editor对象
        SharedPreferences.Editor editor = sharedPre.edit();
        //设置参数
        editor.putString("phone", user.getPhone());
        editor.putString("password", user.getPassword());
        editor.putString("equipmentID", user.getEquipmentID());
        editor.putString("openid", user.getOpenid());
        Log.e(TAG, user.getPassword() + " " + user.getPhone() + " " + user.getOpenid() + " " + user.getEquipmentID() + " " + user.getNikename());
        //提交
        editor.commit();

    }

    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    Log.i(TAG, logs);
                    // 建议这里往 SharePreference 里写一个成功设置的状态。成功设置一次后，以后不必再次设置了。
                    break;
                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.i(TAG, logs);
                    // 延迟 60 秒来调用 Handler 设置别名
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
                    break;
                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e(TAG, logs);
            }
            //ExampleUtil.getSingletonToast(logs, getApplicationContext());
        }
    };

    private static final int MSG_SET_ALIAS = 1001;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    Log.d(TAG, "Set alias in handler.");
                    // 调用 JPush 接口来设置别名。
                    if (getUser().getType() == 1) {
                        JPushInterface.setAliasAndTags(getApplicationContext(),
                                null,
                                null,
                                mAliasCallback);
                    } else {
                        JPushInterface.setAliasAndTags(getApplicationContext(),
                                getUser().getUserID(),
                                null,
                                mAliasCallback);
                    }

                    break;
                default:
                    Log.i(TAG, "Unhandled msg - " + msg.what);
            }
        }
    };

    public void jiebang() {
        SharedPreferences sharedPre = this.getSharedPreferences("user", this.MODE_PRIVATE);
        //获取Editor对象
        SharedPreferences.Editor editor = sharedPre.edit();
        editor.putString("equipmentID", "");
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
        instance = this;
        SpUtils.init(this);
        AppContextUtil.init(this);
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

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

    public RequestManager getGlide() {

        return Glide.with(this);


    }
}
