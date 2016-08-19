package aromatherapy.saiyi.cn.jinhaojiao.activity;

import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.HashMap;
import java.util.Map;

import aromatherapy.saiyi.cn.jinhaojiao.R;
import aromatherapy.saiyi.cn.jinhaojiao.app.MyApplication;
import aromatherapy.saiyi.cn.jinhaojiao.base.BaseActivity;
import aromatherapy.saiyi.cn.jinhaojiao.bean.User;
import aromatherapy.saiyi.cn.jinhaojiao.util.Constant;
import aromatherapy.saiyi.cn.jinhaojiao.util.Log;
import aromatherapy.saiyi.cn.jinhaojiao.util.NormalPostRequest;
import aromatherapy.saiyi.cn.jinhaojiao.util.Toastor;
import aromatherapy.saiyi.cn.jinhaojiao.view.LoadingDialog;


public class AddDevice extends BaseActivity implements Response.ErrorListener {
    private final static String TAG = AddDevice.class.getSimpleName();
    @ViewInject(R.id.device_id_et)
    EditText device_id_et;
    @ViewInject(R.id.device_definite_tv)
    TextView device_definite_tv;
    @ViewInject(R.id.device_id_ll)
    LinearLayout device_id_ll;
    @ViewInject(R.id.device_id_tv)
    TextView device_id_tv;
    private Map<String, String> map = new HashMap<String, String>();
    private LoadingDialog dialog;
    private Toastor toastor;
    private RequestQueue mQueue;
    private User user;
    @Override
    protected int getContentView() {
        return R.layout.activity_add_device;
    }

    @Override
    protected void init() {
        user=MyApplication.newInstance().getUser();
        toastor = new Toastor(this);
        dialog = new LoadingDialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        mQueue = MyApplication.newInstance().getmQueue();
        Log.e(TAG,user.getEquipment());
        if (user.getEquipment()!=null){
            device_definite_tv.setText("解绑");
            device_id_et.setVisibility(View.GONE);
            device_id_ll.setVisibility(View.VISIBLE);
            device_id_tv.setText(user.getEquipment());
        }else {
            device_id_ll.setVisibility(View.GONE);
            device_id_et.setVisibility(View.VISIBLE);
        }
    }
    @Event(R.id.device_definite_tv)
    private void  addDevice(View view){
        if (user.getEquipment()!=null){
            dialog.show();
            map.clear();
            map.put("userID", user.getUserID());
            mQueue.add(normalPostRequest2);
        }else if (device_id_et.getText().toString().length()>0){
            dialog.show();
            map.clear();
            map.put("userID", user.getUserID());
            map.put("equipment",device_id_et.getText().toString());
            mQueue.add(normalPostRequest);
        }else {
            toastor.showToast("设备ID不能为空");
        }
    }
    NormalPostRequest normalPostRequest = new NormalPostRequest(Constant.UPDATEEQUIPMENT, new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            Log.e(TAG, jsonObject.toString());
            dialog.dismiss();
            if (jsonObject.optInt("resCode") == 1) {
                toastor.showToast(jsonObject.optString("resMessage"));
            } else if (jsonObject.optInt("resCode") == 0) {
                toastor.showToast(jsonObject.optString("resMessage"));
                user.setEquipment(device_id_et.getText().toString());
                user.setEquipmentID(jsonObject.optJSONObject("resBody").optString("equipmentID"));
                finish();
            }

        }
    }, this, map);
    NormalPostRequest normalPostRequest2 = new NormalPostRequest(Constant.JIEBANG, new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            Log.e(TAG, jsonObject.toString());
            dialog.dismiss();
            if (jsonObject.optInt("resCode") == 1) {
                toastor.showToast(jsonObject.optString("resMessage"));
            } else if (jsonObject.optInt("resCode") == 0) {
                toastor.showToast(jsonObject.optString("resMessage"));

                finish();
            }

        }
    }, this, map);
    @Override
    public void onErrorResponse(VolleyError volleyError) {
        dialog.dismiss();
        toastor.showToast("服务器异常");
        Log.e(TAG, "服务器异常");
    }
}
