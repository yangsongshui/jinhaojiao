package aromatherapy.saiyi.cn.jinhaojiao.presenter;

import android.content.Context;

import org.json.JSONObject;

import java.util.Map;

import aromatherapy.saiyi.cn.jinhaojiao.base.BasePresenterImp;
import aromatherapy.saiyi.cn.jinhaojiao.model.UpdateEquipmentModelImp;
import aromatherapy.saiyi.cn.jinhaojiao.view.MsgView;


/**
 * 描述：MVP中的P实现类
 * 作者：ys on 2017/2/16 15:17
 * 邮箱：yang3384046@126.com
 */
public class UpdateEquipmentPresenterImp extends BasePresenterImp<MsgView,JSONObject> implements MsgPresenter {
    //传入泛型V和T分别为WeatherView、WeatherInfoBean表示建立这两者之间的桥梁
    private Context context = null;
    private UpdateEquipmentModelImp updateEquipmentModelImp = null;

    /**
     * @param view 具体业务的视图接口对象
     * @descriptoin 构造方法

     */
    public UpdateEquipmentPresenterImp(MsgView view, Context context) {
        super(view);
        this.context = context;
        this.updateEquipmentModelImp = new UpdateEquipmentModelImp(context);

    }

    @Override
    public void loadMsg(Map<String, String> map) {

        updateEquipmentModelImp.loadMsg(map, this);
    }

    @Override
    public void unSubscribe() {

    }
}
