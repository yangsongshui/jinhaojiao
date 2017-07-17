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
    @POST("t_user_app/getIdentify?")
    Observable<JSONObject> getIdentify(@QueryMap Map<String, String> map);
    /**
     * 登陆
     * **/
    @POST("t_user_app/login?")
    Observable<JSONObject> login(@QueryMap Map<String, String> map);
    /**
     * 注册
     **/
    @POST("t_user_app/register?")
    Observable<JSONObject> register(@QueryMap Map<String, String> map);
    /**
     * 修改密码
     **/
    @POST("t_user_app/findPwd?")
    Observable<JSONObject> findPwd(@QueryMap Map<String, String> map);

    /**
     * 注册之后填写资料
     **/
    @POST("t_user_app/addtDetaileduser?")
    Observable<JSONObject> addtDetaileduser(@QueryMap Map<String, String> map);
    /**
     * 查询个人资料
     **/
    @POST("t_user_app/findpersonal?")
    Observable<JSONObject> findpersonal(@QueryMap Map<String, String> map);
    /**
     * 修改用户资料
     **/
    @POST("t_user_app/updateUser?")
    Observable<JSONObject> updateUser(@QueryMap Map<String, String> map);
    /**
     * 用户绑定设备
     **/
    @POST("t_user_app/updateequipment?")
    Observable<JSONObject> updateequipment(@QueryMap Map<String, String> map);

    /**
     * 查询首页的心率和计步等数据
     **/
    @POST("t_user_app/findheartratemotion?")
    Observable<JSONObject> findheartratemotion(@QueryMap Map<String, String> map);

    /**
     * 按时间查询步数数据
     **/
    @POST("t_user_app/findmotionbytime?")
    Observable<JSONObject> findmotionbytime(@QueryMap Map<String, String> map);
    /**
     * 按时间查询距离
     **/
    @POST("t_user_app/findjulibytime?")
    Observable<JSONObject> findjulibytime(@QueryMap Map<String, String> map);
    /**
     * 按时间查询速度
     **/
    @POST("t_user_app/findshudubytime?")
    Observable<JSONObject> findshudubytime(@QueryMap Map<String, String> map);

    /**
     * 按时间查询心率
     **/
    @POST("t_user_app/findxingtiaobytime?")
    Observable<JSONObject> findxingtiaobytime(@QueryMap Map<String, String> map);
    /**
     * QQ 微信登陆
     **/
    @POST("t_user_app/qqlogin?")
    Observable<JSONObject> qqlogin(@QueryMap Map<String, String> map);
    /**
     * QQ 微信用户注册
     **/
    @POST("t_user_app/qqregister?")
    Observable<JSONObject> qqregister(@QueryMap Map<String, String> map);
    /**
     * 解绑设备
     **/
    @POST("t_user_app/jiebang?")
    Observable<JSONObject> jiebang(@QueryMap Map<String, String> map);
    /**
     * 获取地理位置
     * **/
    @POST("t_user_app/findposition?")
    Observable<JSONObject> findposition(@QueryMap Map<String, String> map);
    /**
     *我队Tab页数据
     **/
    @POST("t_user_app/find_student?")
    Observable<JSONObject> find_student(@QueryMap Map<String, String> map);
    /**
     * 教练添加学员
     * **/
    @POST("t_user_app/add_Student?")
    Observable<JSONObject> add_Student(@QueryMap Map<String, String> map);
    /**
     * 教练删除学员
     * **/
    @POST("t_user_app/deletestudent?")
    Observable<JSONObject> deletestudent(@QueryMap Map<String, String> map);
    /**
     * 教练备注学员
     * **/
    @POST("t_user_app/updatebeizhu?")
    Observable<JSONObject> updatebeizhu(@QueryMap Map<String, String> map);

    /**
     *我队Tab页数据
     **/
    @POST("JHJInterface/getMyTeamData?")
    Observable<JSONObject> getMyTeamData(@QueryMap Map<String, String> map);
    /**
     *负荷Tab页数据
     **/
    @POST("JHJInterface/getSportLoad?")
    Observable<JSONObject> getSportLoad(@QueryMap Map<String, String> map);
    /**
     *负荷Tab页数据
     **/
    @POST("JHJInterface/getSportStrength?")
    Observable<JSONObject> getSportStrength(@QueryMap Map<String, String> map);
    /**
     *负荷Tab页数据
     **/
    @POST("JHJInterface/getSpeed?")
    Observable<JSONObject> getSpeed(@QueryMap Map<String, String> map);

    /**
     *查询学员首页数据
     **/
    @POST("JHJInterface/getPersonMsg?")
    Observable<JSONObject> getPersonMsg(@QueryMap Map<String, String> map);
    /**
     *查询卡路里历史数据
     **/
    @POST("JHJInterface/getCalorieHistory?")
    Observable<JSONObject> getCalorieHistory(@QueryMap Map<String, String> map);
    /**
     *查询速度历史数据
     **/
    @POST("JHJInterface/getSpeedHistory?")
    Observable<JSONObject> getSpeedHistory(@QueryMap Map<String, String> map);
    /**
     *查询距离历史数据
     **/
    @POST("JHJInterface/getDistanceHistory?")
    Observable<JSONObject> getDistanceHistory(@QueryMap Map<String, String> map);
    /**
     *查询心率历史数据
     **/
    @POST("JHJInterface/getRateHistory?")
    Observable<JSONObject> getRateHistory(@QueryMap Map<String, String> map);
    /**
     *查询设备是否被绑定
     **/
    @POST("JHJInterface/checkBinding?")
    Observable<JSONObject> checkBinding(@QueryMap Map<String, String> map);
}
