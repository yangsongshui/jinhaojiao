package aromatherapy.saiyi.cn.jinhaojiao.base;


import aromatherapy.saiyi.cn.jinhaojiao.helper.RetrofitManager;

/**
 * 描述：业务对象的基类
 */
public class BaseModel {
    //retrofit请求数据的管理类
    public RetrofitManager retrofitManager;

    public BaseModel() {
        retrofitManager = RetrofitManager.builder();
    }
}
