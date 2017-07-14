package aromatherapy.saiyi.cn.jinhaojiao.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;


/**
 * @author laoqin
 *         首选项的工具类
 *         boolean String
 */
public final class SpUtils {

    public static final String PREFERENCE_NAME = "saveInfo";
    private static SharedPreferences mSharedPreferences;
    private static SharedPreferences.Editor editor;
    private static SpUtils mPreferencemManager;
    @SuppressLint("CommitPrefEdits")
    private SpUtils(Context cxt) {
        mSharedPreferences = cxt.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();
    }

    public static synchronized void init(Context cxt){
        if(mPreferencemManager == null){
            mPreferencemManager = new SpUtils(cxt);
        }
    }

    /**
     * get instance of PreferenceManager
     *
     * @param
     * @return
     */
    public synchronized static SpUtils getInstance() {
        if (mPreferencemManager == null) {
            throw new RuntimeException("please init first!");
        }
        return mPreferencemManager;
    }

    //存值
    public  static void putBoolean(String key, boolean value) {

        editor.putBoolean(key, value);
        editor.commit();
    }

    public  static void putString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public  static void putInt(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    //获取值
    public  static boolean getBoolean(String key, boolean defValue) {

        return mSharedPreferences.getBoolean(key, defValue);
    }

    public  static String getString(String key, String defValue) {

        return mSharedPreferences.getString(key, defValue);
    }

    public final static int getInt(String key, int defValue) {

        return mSharedPreferences.getInt(key, defValue);
    }


    // 退出登录时要调用
    public static void clean() {
        try {
            if (null != mSharedPreferences) {
                mSharedPreferences.edit().clear().commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
