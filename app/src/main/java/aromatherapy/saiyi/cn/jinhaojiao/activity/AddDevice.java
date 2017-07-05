package aromatherapy.saiyi.cn.jinhaojiao.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import aromatherapy.saiyi.cn.jinhaojiao.R;
import aromatherapy.saiyi.cn.jinhaojiao.app.MyApplication;
import aromatherapy.saiyi.cn.jinhaojiao.base.BaseActivity;
import aromatherapy.saiyi.cn.jinhaojiao.bean.User;
import aromatherapy.saiyi.cn.jinhaojiao.presenter.JieBangPresenterImp;
import aromatherapy.saiyi.cn.jinhaojiao.presenter.UpdateEquipmentPresenterImp;
import aromatherapy.saiyi.cn.jinhaojiao.util.Log;
import aromatherapy.saiyi.cn.jinhaojiao.util.Toastor;
import aromatherapy.saiyi.cn.jinhaojiao.view.MsgView;
import aromatherapy.saiyi.cn.jinhaojiao.widget.LoadingDialog;
import butterknife.BindView;
import butterknife.OnClick;


public class AddDevice extends BaseActivity implements Response.ErrorListener, MsgView {
    private final static String TAG = AddDevice.class.getSimpleName();
    @BindView(R.id.device_id_et)
    EditText device_id_et;
    @BindView(R.id.device_definite_tv)
    TextView device_definite_tv;
    @BindView(R.id.device_id_ll)
    LinearLayout device_id_ll;
    @BindView(R.id.device_id_tv)
    TextView device_id_tv;
    private Map<String, String> map = new HashMap<String, String>();
    private LoadingDialog dialog;
    private Toastor toastor;
    private RequestQueue mQueue;
    private User user;
    private UpdateEquipmentPresenterImp updateEquipmentPresenterImp;
    private JieBangPresenterImp jieBangPresenterImp;
    @Override
    protected int getContentView() {
        return R.layout.activity_add_device;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        user = MyApplication.newInstance().getUser();
        toastor = new Toastor(this);
        dialog = new LoadingDialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        updateEquipmentPresenterImp = new UpdateEquipmentPresenterImp(this, this);
        Log.e(TAG, user.getEquipment());
        if (user.getEquipment() != null && user.getEquipment().length() > 0) {
            device_definite_tv.setText("解绑");
            device_id_et.setVisibility(View.GONE);
            device_id_ll.setVisibility(View.VISIBLE);
            device_id_tv.setText(user.getEquipment());
        } else {
            device_id_ll.setVisibility(View.GONE);
            device_id_et.setVisibility(View.VISIBLE);
        }
        jieBangPresenterImp = new JieBangPresenterImp(new MsgView() {
            @Override
            public void showProgress() {
                if (dialog != null && !dialog.isShowing()) {
                    dialog.show();
                }

            }

            @Override
            public void disimissProgress() {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }

            @Override
            public void loadDataSuccess(JSONObject jsonObject) {
                Log.e(TAG, jsonObject.toString());

                toastor.showSingletonToast(jsonObject.optString("resMessage"));
                if (jsonObject.optInt("resCode") == 1) {
                } else if (jsonObject.optInt("resCode") == 0) {
                    MyApplication.newInstance().getUser().setEquipment("");
                    MyApplication.newInstance().getUser().setEquipmentID("");
                    MyApplication.newInstance().jiebang();
                    finish();
                }
            }


            @Override
            public void loadDataError(Throwable throwable) {
                Log.e(TAG, throwable.getLocalizedMessage());
                toastor.showSingletonToast("服务器连接失败");
            }
        }, this);
    }

    @OnClick({R.id.device_definite_tv, R.id.device_back_iv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.device_back_iv:
                finish();
                break;
            case R.id.device_definite_tv:
                if (user.getEquipment() != null && user.getEquipment().length() > 0) {
                    map.clear();
                    map.put("userID", user.getUserID());
                    jieBangPresenterImp.loadMsg(map);
                } else if (device_id_et.getText().toString().length() > 0) {
                    map.clear();
                    map.put("userID", user.getUserID());
                    map.put("equipment", device_id_et.getText().toString());
                    updateEquipmentPresenterImp.loadMsg(map);
                } else {
                    toastor.showSingletonToast("设备ID不能为空");
                }
                break;
        }

    }
    @Override
    public void onErrorResponse(VolleyError volleyError) {
        dialog.dismiss();
        toastor.showSingletonToast("服务器异常");
        Log.e(TAG, "服务器异常");
    }

    @Override
    public void showProgress() {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }

    }

    @Override
    public void disimissProgress() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    public void loadDataSuccess(JSONObject jsonObject) {
        Log.e(TAG, jsonObject.toString());
        toastor.showSingletonToast(jsonObject.optString("resMessage"));
        if (jsonObject.optInt("resCode") == 0) {
            user.setEquipment(device_id_et.getText().toString());
            user.setEquipmentID(jsonObject.optJSONObject("resBody").optString("equipmentID"));
            MyApplication.newInstance().setUser(user);
            finish();
        }
    }

    @Override
    public void loadDataError(Throwable throwable) {
        Log.e(TAG, throwable.getLocalizedMessage());
        toastor.showSingletonToast("服务器连接失败");
    }
}
