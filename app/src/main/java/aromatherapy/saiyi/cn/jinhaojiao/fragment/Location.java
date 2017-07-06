package aromatherapy.saiyi.cn.jinhaojiao.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.CoordinateConverter;
import com.amap.api.location.DPoint;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapFragment;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;

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
import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

;

public class Location extends BaseFragment implements AMapLocationListener, LocationSource, MsgView {
    @BindView(R.id.map)
    MapView mMapView;
    private final static String TAG = Location.class.getSimpleName();
    private static Location fragment = null;
    private AMap mMap;
    private Map<String, String> map = new HashMap<String, String>();
    private LoadingDialog dialog;
    private Toastor toastor;
    private User user;
    private LocationSource.OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private Handler handler;
    private Runnable myRunnable;
    private FindPositionPresenterImp findPositionPresenterImp;

    public static Location newInstance() {
        if (fragment == null) {
            synchronized (MapFragment.class) {
                if (fragment == null) {
                    fragment = new Location();
                }
            }
        }
        return fragment;
    }

    @Override
    protected void initData(View layout, Bundle savedInstanceState) {
        map = new HashMap<>();
        //获取地图控件引用
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

                } else {
                    init();
                }
            } else {
                init();
            }

        } else {
            init();
        }
    }

    private void zuobiao(String url, final String title){
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

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject dataJson = new JSONObject(str);
                           // toastor.showSingletonToast(dataJson.optString("resMessage"));
                            if (dataJson.optString("status").equals("1")) {
                                JSONObject object = dataJson.optJSONObject("result");
                                try {
                                    String location = object.optString("location");
                                    double latitude = Double.valueOf(location.substring(0, location.indexOf(",")));
                                    double longitude = Double.valueOf(location.substring(location.indexOf(",") + 1, location.length()));

                                    LatLng BEIJING = CoordinateUtil.transformFromWGSToGCJ(new LatLng(longitude, latitude));
                                    mMap.addMarker(new MarkerOptions().
                                            position(BEIJING).
                                            title(title).snippet("佩戴者的位置"));
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(BEIJING, 14));
                                    mMap.invalidate();// 刷新地图
                                    Log.e("-------", "手环位置");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.e("-------", e.getMessage());
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

        });
    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (mMap == null) {
            mMap = mMapView.getMap();
            setUpMap();
        } else {
            mMap.clear();
            mMap.setLocationSource(this);

            mMap.setMyLocationEnabled(true);
            mMap = mMapView.getMap();
            setUpMap();
        }

    }


    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        mMap.setLocationSource(this);// 设置定位监听
        mMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        mMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // aMap.setMyLocationType()
    }


    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点

            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
            }
        }
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            Log.e("-------", "第一次定位");
            mlocationClient = new AMapLocationClient(getActivity());
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位间隔,单位毫秒,默认为2000ms
            mLocationOption.setInterval(2000);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            mlocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */

    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMapView != null)
            mMapView.onDestroy();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        map = null;
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
                //初始化地图变量
                CoordinateConverter converter = new CoordinateConverter(getActivity());
                // CoordType.GPS 待转换坐标类型
                converter.from(CoordinateConverter.CoordType.GPS);
                try {
                    DPoint sourceLatLng = new DPoint();
                    sourceLatLng.setLatitude(object.optDouble("latitude"));//纬度
                    sourceLatLng.setLongitude(object.optDouble("longitude"));//经度
                    // sourceLatLng待转换坐标点 DPoint类型
                    converter.coord(sourceLatLng);
                    // 执行转换操作
                    DPoint desLatLng = converter.convert();
                    LatLng BEIJING = CoordinateUtil.transformFromWGSToGCJ(new LatLng(desLatLng.getLatitude(), desLatLng.getLongitude()));
                    mMap.addMarker(new MarkerOptions().
                            position(BEIJING).
                            title(object.optString("time")).snippet("用户最后一次所在位置"));

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(BEIJING, 14));
                    mMap.invalidate();// 刷新地图
                    Log.e("-------", "手环");

                } catch (Exception e) {
                    e.printStackTrace();
                }
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
}
