package aromatherapy.saiyi.cn.jinhaojiao.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.amap.api.location.CoordinateConverter;
import com.amap.api.location.DPoint;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import aromatherapy.saiyi.cn.jinhaojiao.R;
import aromatherapy.saiyi.cn.jinhaojiao.app.MyApplication;
import aromatherapy.saiyi.cn.jinhaojiao.base.BaseFragment;
import aromatherapy.saiyi.cn.jinhaojiao.bean.User;
import aromatherapy.saiyi.cn.jinhaojiao.presenter.FindPositionPresenterImp;
import aromatherapy.saiyi.cn.jinhaojiao.util.CoordinateUtil;
import aromatherapy.saiyi.cn.jinhaojiao.util.Log;
import aromatherapy.saiyi.cn.jinhaojiao.util.Toastor;
import aromatherapy.saiyi.cn.jinhaojiao.view.MsgView;
import aromatherapy.saiyi.cn.jinhaojiao.widget.LoadingDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

;

public class Location extends BaseFragment implements MsgView {

    MapView mMapView;
    private final static String TAG = Location.class.getSimpleName();
    private static Location fragment = null;
    private AMap mMap;
    private Map<String, String> map = new HashMap<String, String>();
    private LoadingDialog dialog;
    private Toastor toastor;
    private User user;
    private Handler handler;
    private Runnable myRunnable;
    private FindPositionPresenterImp findPositionPresenterImp;
    MyLocationStyle myLocationStyle;
    Marker marker;


    @Override
    protected void initData(View layout, Bundle savedInstanceState) {
        map = new HashMap<>();
        //获取地图控件引用
        mMapView = (MapView) layout.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        user = MyApplication.newInstance().getUser();
        toastor = new Toastor(getActivity());
        dialog = new LoadingDialog(getActivity());
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        findPositionPresenterImp = new FindPositionPresenterImp(this, getActivity());
        handler = new Handler();
        myRunnable = new Runnable() {
            @Override
            public void run() {
                findPositionPresenterImp.loadMsg(map);

            }
        };

    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_location;
    }


    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，实现地图生命周期管理
        mMapView.onResume();
        if (user != null) {
            if (user.getType() == 1) {
                if (mMap == null) {
                    mMap = mMapView.getMap();
                }
                if (user.getEquipmentID() != null && user.getEquipmentID().length() > 0) {
                    Log.e("----", user.getEquipmentID() + " ");
                    map.clear();
                    map.put("equipmentID", user.getEquipmentID());
                    findPositionPresenterImp.loadMsg(map);
                    return;
                }
            } else {
                init();
            }
        }
    }

    private void zuobiao(String url, final String title) {
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        toastor.showSingletonToast("服务器连接失败");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String str = response.body().string();
                Log.i("wangshu", str);
                if (mMap != null && marker != null) {
                    marker.destroy();
                    mMap.clear();
                }

                try {
                    JSONObject dataJson = new JSONObject(str);

                    if (dataJson.optString("status").equals("1")) {
                        final JSONObject object = dataJson.optJSONObject("result");

                       getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setMarker(object, 2);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        });
    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (mMap == null) {
            mMap = mMapView.getMap();

        } else {
            mMap.clear();
            mMap.setMyLocationEnabled(true);
            mMap = mMapView.getMap();

        }
        setUpMap();
    }


    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        Log.e("setMarker", "定位");
        mMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        myLocationStyle = new MyLocationStyle();
        //初始化定位蓝点样式类
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));
        myLocationStyle.interval(10000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.strokeColor(Color.argb(00,255,255,255));//设置定位蓝点精度圆圈的边框颜色的方法。
        myLocationStyle.radiusFillColor(Color.argb(00,255,255,255));//设置定位蓝点精度圆圈的填充颜色的方法。
        mMap.moveCamera(CameraUpdateFactory.zoomBy(3));
        mMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style。
        mMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(myRunnable);
        if (mMapView != null)
            mMapView.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        if (mMapView != null)
            mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void showProgress() {
        if (dialog != null && !dialog.isShowing()) {
            //dialog.show();
        }

    }

    @Override
    public void disimissProgress() {
        if (dialog != null && dialog.isShowing()) {
            // dialog.dismiss();
        }
    }


    @Override
    public void loadDataSuccess(JSONObject jsonObject) {
        Log.e(TAG, jsonObject.toString());
        if (jsonObject.optInt("resCode") == 1) {
            toastor.showSingletonToast(jsonObject.optString("resMessage"));
            init();
        } else if (jsonObject.optInt("resCode") == 0) {
            toastor.showSingletonToast(jsonObject.optString("resMessage"));
            JSONObject object = jsonObject.optJSONObject("resBody");
            if (jsonObject.optInt("type") == 1) {
                //GPS坐标
                setMarker(object, 1);
            } else {
                String url = object.optString("http");
                Log.e("--------", url);
                //基站
                zuobiao(url, object.optString("time"));
            }
            handler.postDelayed(myRunnable, 30000);
        }
    }


    @Override
    public void loadDataError(Throwable throwable) {
        Log.e(TAG, throwable.getLocalizedMessage());
        toastor.showSingletonToast("服务器连接失败");
    }

    private void setMarker(JSONObject object, int type) {
        LatLng latLng = null;
        if (type == 1) {
            Log.e("setMarker", "GPS");
            //初始化地图变量
            CoordinateConverter converter = new CoordinateConverter(getActivity());
            converter.from(CoordinateConverter.CoordType.GPS);
            try {
                DPoint sourceLatLng = new DPoint();
                sourceLatLng.setLatitude(object.optDouble("latitude"));//纬度
                sourceLatLng.setLongitude(object.optDouble("longitude"));//经度
                // sourceLatLng待转换坐标点 DPoint类型
                converter.coord(sourceLatLng);
                // 执行转换操作
                DPoint desLatLng = converter.convert();
                latLng = CoordinateUtil.transformFromWGSToGCJ(new LatLng(desLatLng.getLatitude(), desLatLng.getLongitude()));

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e("setMarker", "基站定位");
            try {
                String location = object.optString("location");
                double latitude = Double.valueOf(location.substring(0, location.indexOf(",")));
                double longitude = Double.valueOf(location.substring(location.indexOf(",") + 1, location.length()));
                latLng =new LatLng(longitude, latitude);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("setMarker", e.getMessage());
            }
        }

        Log.e("setMarker", "用户位置:latitude" + latLng.latitude + " longitude:" + latLng.longitude);
        mMap.clear();
        mMap = mMapView.getMap();
        mMap.addMarker(new MarkerOptions().
                position(latLng).
                draggable(true)
                .title(object.optString("time")).snippet("用户最后一次所在位置"));
        mMap.invalidate();// 刷新地图
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLng.latitude, latLng.longitude), 13));

    }

}
