package aromatherapy.saiyi.cn.jinhaojiao.presenter;

import java.util.Map;

/**
 * 描述：MVP中的P接口定义
 */
public interface MsgPresenter {

    /**
     * @param map<String,String> map
     * @return
     * @descriptoin 请求数据接口
     * @author dc
     * @date 2017/2/17 19:36
     */
    void loadMsg(Map<String, String> map);

    /**
     * @descriptoin 注销subscribe
     * @author dc
     * @date 2017/2/17 19:36
     */
    void unSubscribe();
}
