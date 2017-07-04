package aromatherapy.saiyi.cn.jinhaojiao.api;


import org.json.JSONObject;

import java.util.Map;

import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import rx.Observable;


/**
 * 描述：retrofit的接口service定义
 */
public interface ServiceApi {

    /**
     * 获取验证码
     * **/
    @POST("getIdentify?")
    Observable<JSONObject> getIdentify(@QueryMap Map<String, String> map);
    /**
     * 登陆
     * **/
    @POST("login?")
    Observable<JSONObject> login(@QueryMap Map<String, String> map);
    /**
     * 注册
     **/
    @POST("register?")
    Observable<JSONObject> register(@QueryMap Map<String, String> map);
    /**
     * 修改密码
     **/
    @POST("findPwd?")
    Observable<JSONObject> findPwd(@QueryMap Map<String, String> map);

    /**
     * 注册之后填写资料
     **/
    @POST("addtDetaileduser?")
    Observable<JSONObject> addtDetaileduser(@QueryMap Map<String, String> map);
    /**
     * 查询个人资料
     **/
    @POST("findpersonal?")
    Observable<JSONObject> findpersonal(@QueryMap Map<String, String> map);
    /**
     * 修改用户资料
     **/
    @POST("updateUser?")
    Observable<JSONObject> updateUser(@QueryMap Map<String, String> map);
    /**
     * 用户绑定设备
     **/
    @POST("updateequipment?")
    Observable<JSONObject> updateequipment(@QueryMap Map<String, String> map);

    /**
     * 查询首页的心率和计步等数据
     **/
    @POST("findheartratemotion?")
    Observable<JSONObject> findheartratemotion(@QueryMap Map<String, String> map);

    /**
     * 按时间查询步数数据
     **/
    @POST("findmotionbytime?")
    Observable<JSONObject> findmotionbytime(@QueryMap Map<String, String> map);
    /**
     * 按时间查询距离
     **/
    @POST("findjulibytime?")
    Observable<JSONObject> findjulibytime(@QueryMap Map<String, String> map);
    /**
     * 按时间查询速度
     **/
    @POST("findshudubytime?")
    Observable<JSONObject> findshudubytime(@QueryMap Map<String, String> map);

    /**
     * 按时间查询心率
     **/
    @POST("findxingtiaobytime?")
    Observable<JSONObject> findxingtiaobytime(@QueryMap Map<String, String> map);
    /**
     * QQ 微信登陆
     **/
    @POST("qqlogin?")
    Observable<JSONObject> qqlogin(@QueryMap Map<String, String> map);
    /**
     * QQ 微信用户注册
     **/
    @POST("qqregister?")
    Observable<JSONObject> qqregister(@QueryMap Map<String, String> map);
    /**
     * 解绑设备
     **/
    @POST("jiebang?")
    Observable<JSONObject> jiebang(@QueryMap Map<String, String> map);
    /**
     * 通过用户ID查询设备
     **/
    @POST("find_shebei?")
    Observable<JSONObject> find_shebei(@QueryMap Map<String, String> map);
    /**
     * 获取地理位置
     * **/
    @POST("findposition?")
    Observable<JSONObject> findposition(@QueryMap Map<String, String> map);
    /**
     *教练首页
     **/
    @POST("find_student?")
    Observable<JSONObject> find_student(@QueryMap Map<String, String> map);
    /**
     * 教练添加学员
     * **/
    @POST("add_Student?")
    Observable<JSONObject> add_Student(@QueryMap Map<String, String> map);
    /**
     * 教练删除学员
     * **/
    @POST("deletestudent?")
    Observable<JSONObject> deletestudent(@QueryMap Map<String, String> map);
    /**
     * 教练备注学员
     * **/
    @POST("updatebeizhu?")
    Observable<JSONObject> updatebeizhu(@QueryMap Map<String, String> map);

}
