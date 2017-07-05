package aromatherapy.saiyi.cn.jinhaojiao.model;

import android.content.Context;

import org.json.JSONObject;

import java.util.Map;

import aromatherapy.saiyi.cn.jinhaojiao.api.ServiceApi;
import aromatherapy.saiyi.cn.jinhaojiao.base.BaseModel;
import aromatherapy.saiyi.cn.jinhaojiao.base.IBaseRequestCallBack;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


/**
 * 描述：MVP中的M实现类，处理业务逻辑数据
 */
public class FindsuduModelImp extends BaseModel implements MsgModel<JSONObject> {

    private Context context = null;
    private ServiceApi serviceApi;
    private CompositeSubscription mCompositeSubscription;

    public FindsuduModelImp(Context mContext) {
        super();
        context = mContext;
        serviceApi = retrofitManager.getService();
        mCompositeSubscription = new CompositeSubscription();

    }

    @Override
    public void loadMsg(Map<String, String> map, final IBaseRequestCallBack<JSONObject> iBaseRequestCallBack) {
        mCompositeSubscription.add(serviceApi.findshudubytime(map)
                .observeOn(AndroidSchedulers.mainThread())//指定事件消费线程
                .subscribeOn(Schedulers.io())   //指定 subscribe() 发生在 IO 线程
                .subscribe(new Subscriber<JSONObject>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        //onStart它总是在 subscribe 所发生的线程被调用 ,如果你的subscribe不是主线程，则会出错，则需要指定线程;
                        iBaseRequestCallBack.beforeRequest();
                    }

                    @Override
                    public void onCompleted() {
                        //回调接口：请求已完成，可以隐藏progress
                        iBaseRequestCallBack.requestComplete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        //回调接口：请求异常
                        iBaseRequestCallBack.requestError(e);
                    }

                    @Override
                    public void onNext(JSONObject jsonObject) {
                        iBaseRequestCallBack.requestSuccess(jsonObject);

                    }

                })
        );
    }

    @Override
    public void onUnsubscribe() {
        //判断状态
        if (mCompositeSubscription.isUnsubscribed()) {
            mCompositeSubscription.clear();  //注销
            mCompositeSubscription.unsubscribe();
        }
    }
}
