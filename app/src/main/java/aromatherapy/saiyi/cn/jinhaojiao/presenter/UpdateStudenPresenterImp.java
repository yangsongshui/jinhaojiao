package aromatherapy.saiyi.cn.jinhaojiao.presenter;

import android.content.Context;

import org.json.JSONObject;

import java.util.Map;

import aromatherapy.saiyi.cn.jinhaojiao.base.BasePresenterImp;
import aromatherapy.saiyi.cn.jinhaojiao.model.AddStudenModelImp;
import aromatherapy.saiyi.cn.jinhaojiao.view.MsgView;


/**
 * 描述：MVP中的P实现类
 * 作者：ys on 2017/2/16 15:17
 * 邮箱：yang3384046@126.com
 */
public class UpdateStudenPresenterImp extends BasePresenterImp<MsgView,JSONObject> implements MsgPresenter {
    //传入泛型V和T分别为WeatherView、WeatherInfoBean表示建立这两者之间的桥梁
    private Context context = null;
    private AddStudenModelImp addStudenModelImp = null;

    /**
     * @param view 具体业务的视图接口对象
     * @descriptoin 构造方法

     */
    public UpdateStudenPresenterImp(MsgView view, Context context) {
        super(view);
        this.context = context;
        this.addStudenModelImp = new AddStudenModelImp(context);

    }

    @Override
    public void loadMsg(Map<String, String> map) {

        addStudenModelImp.loadMsg(map, this);
    }

    @Override
    public void unSubscribe() {

    }
}
