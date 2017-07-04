package aromatherapy.saiyi.cn.jinhaojiao.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aromatherapy.saiyi.cn.jinhaojiao.R;
import aromatherapy.saiyi.cn.jinhaojiao.app.MyApplication;
import aromatherapy.saiyi.cn.jinhaojiao.base.BaseFragment;
import aromatherapy.saiyi.cn.jinhaojiao.bean.User;
import aromatherapy.saiyi.cn.jinhaojiao.util.Constant;
import aromatherapy.saiyi.cn.jinhaojiao.util.DateUtil;
import aromatherapy.saiyi.cn.jinhaojiao.util.NormalPostRequest;
import aromatherapy.saiyi.cn.jinhaojiao.util.Toastor;
import aromatherapy.saiyi.cn.jinhaojiao.widget.LoadingDialog;
import aromatherapy.saiyi.cn.jinhaojiao.widget.MyMarkerView;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/5/28.
 */
public class Day extends BaseFragment implements OnChartValueSelectedListener, Response.ErrorListener {
    private final static String TAG = Time.class.getSimpleName();
    @BindView(R.id.line_chart)
    LineChart mChart;
    @BindView(R.id.textClock)
    TextView textClock;

    @BindView(R.id.day_tiem)
    TextView day_tiem;

    @BindView(R.id.day_data)
    TextView day_data;
    DateUtil util = new DateUtil();
    Date data;
    @BindView(R.id.day_data_tv)
    TextView day_data_tv;
    @BindView(R.id.day_kaluli)
    TextView day_kaluli;
    private int TYPE = 0;
    private String time = "";
    NormalPostRequest normalPostRequest;
    private Map<String, String> map = new HashMap<String, String>();
    private Toastor toastor;
    private RequestQueue mQueue;
    List<String> Calorie = new ArrayList<>();
    List<String> steps = new ArrayList<>();
    String URL = "";
    Handler handler;
    private LoadingDialog dialog;
    User user;

    @Override
    protected void initData(View layout, Bundle savedInstanceState) {
        user = MyApplication.newInstance().getUser();
        mQueue = MyApplication.newInstance().getmQueue();
        toastor = new Toastor(getActivity());
        dialog = new LoadingDialog(getActivity());
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TYPE = getActivity().getIntent().getIntExtra("type", -1);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                initChart();
                map.put("equipmentID", MyApplication.newInstance().getEquipmentID());
                map.put("time", time);
                map.put("type", "2");
                if (user.getType() == 1)
                    map.put("userID", user.getUserID());
                else {
                    user= (User) getActivity().getIntent().getSerializableExtra("student");
                    map.put("userID", user.getUserID());
                }

                Log.e(TAG, time);
                if (MyApplication.newInstance().getEquipmentID() != null) {
                    QueueRequest(URL, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            Log.e(TAG, jsonObject.toString());
                            dialog.dismiss();
                            if (jsonObject.optInt("resCode") == 1) {
                                toastor.getSingletonToast(jsonObject.optString("resMessage"));
                            } else if (jsonObject.optInt("resCode") == 0) {
                                toastor.getSingletonToast(jsonObject.optString("resMessage"));
                                initInfo(jsonObject.optJSONObject("resBody").optJSONArray("list"));
                            }
                        }
                    });
                    mQueue.add(normalPostRequest);
                }
            }
        };
        initChart();
        if (TYPE == 1 || TYPE == 0) {
            day_data_tv.setText("步");
            day_kaluli.setVisibility(View.VISIBLE);
            URL = Constant.FINDMOTIONBYTIME;
        } else if (TYPE == 2) {
            day_kaluli.setVisibility(View.GONE);
            day_data_tv.setText("bmp");
            URL = Constant.FINDXINGTIAOBYTIME;
        } else if (TYPE == 3) {
            day_kaluli.setVisibility(View.GONE);
            day_data_tv.setText("米/min");
            URL = Constant.FINDSHUDUBYTIME;
        } else if (TYPE == 4) {
            day_kaluli.setVisibility(View.GONE);
            day_data_tv.setText("公里");
            URL = Constant.FINDJULIBYTIME;
        }


        init();


        data = new Date();
        textClock.setText(util.dateToString(data, util.LONG_DATE_FORMAT));
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_day;
    }

    private void initChart() {

        /**
         * ====================1.初始化-自由配置===========================
         */
        // 是否在折线图上添加边框
        mChart.setDrawGridBackground(false);
        mChart.setDrawBorders(false);
        // 设置右下角描述
        mChart.setDescription("");
        //设置透明度
        // mChart.setAlpha(0.8f);
        //设置是否可以触摸，如为false，则不能拖动，缩放等
        mChart.setTouchEnabled(true);
        //设置是否可以拖拽
        mChart.setDragEnabled(true);
        //设置是否可以缩放
        mChart.setScaleEnabled(false);
        //设置是否能扩大扩小
        mChart.setPinchZoom(false);
        //设置四个边的间距
        // mChart.setViewPortOffsets(10, 0, 0, 10);
        //隐藏Y轴
        mChart.getAxisRight().setEnabled(false);
        mChart.getAxisLeft().setEnabled(false);
        //隐藏X轴
        mChart.getXAxis().setEnabled(false);
        //隐藏颜色点
        mChart.getLegend().setEnabled(false);
        MyMarkerView myMarkerView = new MyMarkerView(getActivity(), R.layout.custom_marker_view);
        mChart.setMarkerView(myMarkerView);
        mChart.setOnChartValueSelectedListener(this);

    }

    String[] xx = {"2", "4", "6", "8", "10", "12", "14", "16", "18", "14", "16", "18"};
    String[] yy = {"20", "80", "10", "60", "30", "70", "55", "22", "40", "55", "22", "40"};

    private LineData getLineData() {
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < xx.length; i++) {
            xVals.add(xx[i]);
        }
        ArrayList<Entry> yVals = new ArrayList<Entry>();
        for (int i = 0; i < steps.size(); i++) {
            yVals.add(new Entry(Float.parseFloat(steps.get(i)), i));
        }

        LineDataSet set1 = new LineDataSet(yVals, "");
        set1.setCubicIntensity(0.05f);
        set1.setDrawFilled(true);  //设置包括的范围区域填充颜色
        set1.setLineWidth(2f);    //设置线的宽度
        set1.setDrawCircles(false);  //设置有圆点
        set1.setFillDrawable(getResources().getDrawable(R.drawable.jianpian2));
        set1.setCircleColor(Color.argb(80, 209, 209, 209));
        set1.setCircleColorHole(Color.rgb(40, 179, 76));
        set1.setDrawHighlightIndicators(false);
        set1.setColor(Color.rgb(40, 179, 76));    //设置曲线的颜色
        return new LineData(xVals, set1);
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        day_data.setText(steps.get(e.getXIndex()));
        day_tiem.setText(times.get(e.getXIndex()) + "");
        if (Calorie.size() > 0) {
            day_kaluli.setText(Calorie.get(e.getXIndex()) + "千卡");
        }
    }

    @Override
    public void onNothingSelected() {

    }


    @OnClick(value = {R.id.day_next, R.id.day_up})
    public void Click(View view) {
        switch (view.getId()) {
            case R.id.day_next:
                data = util.nextDay(data, 1);
                textClock.setText(util.dateToString(data, util.LONG_DATE_FORMAT));
                gettime(util.dateToString(data, util.LONG_DATE_FORMAT2));
                break;
            case R.id.day_up:
                data = util.nextDay(data, -1);
                textClock.setText(util.dateToString(data, util.LONG_DATE_FORMAT));
                gettime(util.dateToString(data, util.LONG_DATE_FORMAT2));

                break;
            default:
                break;
        }
    }

    private void QueueRequest(final String URL, Response.Listener<JSONObject> listener) {
        normalPostRequest = new NormalPostRequest(URL, listener, this, map);

    }

    List<String> times = new ArrayList<>();

    private void init() {
        times.clear();
        time = "";
        times.add("02:00");
        times.add("04:00");
        times.add("06:00");
        times.add("08:00");
        times.add("10:00");
        times.add("12:00");
        times.add("14:00");
        times.add("16:00");
        times.add("18:00");
        times.add("20:00");
        times.add("22:00");
        times.add("24:00");
        gettime(DateUtil.getCurrDate(DateUtil.LONG_DATE_FORMAT2));
    }

    private void gettime(String string) {
        time = "";
        time += string + "00,";
        time += string + "02,";
        time += string + "04,";
        time += string + "06,";
        time += string + "08,";
        time += string + "10,";
        time += string + "12,";
        time += string + "14,";
        time += string + "16,";
        time += string + "18,";
        time += string + "20,";
        time += string + "22,";
        time += string + "24";
        Message mes = new Message();
        mes.obj = 1;
        handler.sendMessage(mes);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        toastor.getSingletonToast("服务器异常");
    }

    private void initInfo(JSONArray jsonArray) {
        Calorie.clear();
        steps.clear();
        int calorie = 0;
        int step = 0;
        for (int i = 0; i < jsonArray.length(); i++) {
            if (i == 0) {
                if (TYPE == 1 || TYPE == 0)
                    calorie = jsonArray.optJSONObject(i).optInt("Calorie");
                step = jsonArray.optJSONObject(i).optInt("steps");
            } else {
                if (TYPE == 1 || TYPE == 0) {
                    calorie = jsonArray.optJSONObject(i).optInt("Calorie");
                    Calorie.add((jsonArray.optJSONObject(i).optInt("Calorie")) - calorie + "");
                    steps.add((jsonArray.optJSONObject(i).optInt("steps") - step + ""));
                    step = jsonArray.optJSONObject(i).optInt("steps");
                } else if (TYPE == 2) {
                    steps.add((jsonArray.optJSONObject(i).optInt("heartrate")) + "");
                } else if (TYPE == 3) {
                    steps.add((jsonArray.optJSONObject(i).optInt("speed")) + "");
                } else if (TYPE == 4) {
                    steps.add((jsonArray.optJSONObject(i).optInt("distance") - step + ""));
                    step = jsonArray.optJSONObject(i).optInt("distance");
                }


            }
            Log.e("--", steps.size() + " " + jsonArray.length() + " " + i);
        }
        LineData data = getLineData();
        data.setDrawValues(false); //隐藏坐标轴数据
        mChart.setData(data);
        mChart.invalidate();
    }

}
