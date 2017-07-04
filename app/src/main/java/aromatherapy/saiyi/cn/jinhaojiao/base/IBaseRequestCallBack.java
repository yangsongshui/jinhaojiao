package aromatherapy.saiyi.cn.jinhaojiao.base;

/**
 * 描述：请求数据的回调接口
 * Presenter用于接受model获取（加载）数据后的回调
 * 作者：ys
 */
public interface IBaseRequestCallBack<T> {

    /**
     * @descriptoin	请求之前的操作
     * @author	ys
     * @date 2017/6/13 11:34
     */
    void beforeRequest();

    /**
     * @descriptoin	请求异常
     * @author	ys
     * @param throwable 异常类型
     * @date 2017/6/13 11:34
     */
    void requestError(Throwable throwable);

    /**
     * @descriptoin	请求完成
     * @author	ys
     * @date 2017/6/13 11:35
     */
    void requestComplete();

    /**
     * @descriptoin	请求成功
     * @author	ys
     * @param callBack 根据业务返回相应的数据
     * @date 2017/6/13 11:35
     */
    void requestSuccess(T callBack);
}
