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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
public class Month extends BaseFragment implements OnChartValueSelectedListener, Response.ErrorListener {
    private final static String TAG = Month.class.getSimpleName();
    @BindView(R.id.line_chart)
    LineChart mChart;
    @BindView(R.id.textClock)
    TextView textClock;

    @BindView(R.id.month_data)
    TextView month_data;
    @BindView(R.id.month_tiem)
    TextView month_tiem;
    DateUtil util = new DateUtil();

    @BindView(R.id.month_data_tv)
    TextView month_data_tv;
    @BindView(R.id.month_kaluli)
    TextView month_kaluli;
    Handler handler;
    int index;
    int MONTH;
    int YEAY;
    private Map<String, String> map = new HashMap<String, String>();
    private LoadingDialog dialog;
    private Toastor toastor;
    private RequestQueue mQueue;
    List<String> Calorie = new ArrayList<>();
    List<String> steps = new ArrayList<>();
    NormalPostRequest normalPostRequest;
    private int TYPE = 0;
    String URL = "";
    User user;
    private void QueueRequest(final String URL, Response.Listener<JSONObject> listener) {
        normalPostRequest = new NormalPostRequest(URL, listener, this, map);
    }

    @Override
    public void onResume() {
        super.onResume();
        initChart();
        index = 0;
    }

    @Override
    protected void initData(View layout, Bundle savedInstanceState) {
        user = MyApplication.newInstance().getUser();
        toastor = new Toastor(getActivity());
        dialog = new LoadingDialog(getActivity());
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TYPE = getActivity().getIntent().getIntExtra("type", -1);
        if (TYPE == 1 || TYPE == 0) {
            month_data_tv.setText("步");
            month_kaluli.setVisibility(View.VISIBLE);
            URL = Constant.FINDMOTIONBYTIME;
        } else if (TYPE == 2) {
            month_kaluli.setVisibility(View.GONE);
            month_data_tv.setText("bmp");
            URL = Constant.FINDXINGTIAOBYTIME;
        } else if (TYPE == 3) {
            month_kaluli.setVisibility(View.GONE);
            month_data_tv.setText("米/min");
            URL = Constant.FINDSHUDUBYTIME;
        } else if (TYPE == 4) {
            month_kaluli.setVisibility(View.GONE);
            month_data_tv.setText("公里");
            URL = Constant.FINDJULIBYTIME;
        }
        mQueue = MyApplication.newInstance().getmQueue();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                initChart();
                map.put("equipmentID",  MyApplication.newInstance().getEquipmentID());
                //map.put("time", string);
                map.put("time", string);
                map.put("type", "3");
                if (user.getType() == 1)
                    map.put("userID", user.getUserID());
                else {
                    user= (User) getActivity().getIntent().getSerializableExtra("student");
                    map.put("userID", user.getUserID());
                }
                Log.e(TAG,string);
                if ( MyApplication.newInstance().getEquipmentID() != null) {
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


     /*   Calendar cal = Calendar.getInstance();
        YEAY = cal.get(Calendar.YEAR);
        MONTH = cal.get(Calendar.MONTH)+1;
        DAY = util.getDaysOfMonth(YEAY, MONTH);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        textClock.setText(format.format(cal.getTime()));*/
        MonthNext(0);
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_month;
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

    String[] yy = {"20", "80", "10", "60", "30", "70", "55", "30", "70", "55", "30", "70", "55", "30", "70", "55"
            , "20", "80", "10", "60", "30", "70", "55", "30", "70", "55", "30", "70", "55", "30", "70"};

    private LineData getLineData() {
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i <= DAY; i++) {
            xVals.add((i + 1) + "");
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
        set1.setDrawValues(false); //隐藏坐标轴数据
        set1.setColor(Color.rgb(40, 179, 76));    //设置曲线的颜色
        return new LineData(xVals, set1);
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        month_data.setText(yy[e.getXIndex()]);
        month_tiem.setText((e.getXIndex() + 1) + "日");
        month_data.setText(steps.get(e.getXIndex()));
        if (Calorie.size()>0){
            month_kaluli.setText(Calorie.get(e.getXIndex())+"千卡");
        }
    }

    @Override
    public void onNothingSelected() {

    }


    @OnClick(value = {R.id.month_next, R.id.month_up})
    public void Click(View view) {
        switch (view.getId()) {
            case R.id.month_next:
                mChart.clear();
                mChart.setData(new LineData());
                mChart.invalidate();
                index++;
                MonthNext(index);
                break;
            case R.id.month_up:
                mChart.clear();
                mChart.setData(new LineData());
                mChart.invalidate();
                index--;
                MonthNext(index);
                break;
            default:
                break;
        }
    }

    int DAY;
    String string = "";

    private void MonthNext(int i) {
        string = "";
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, i);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyyMM");
        String time = format2.format(cal.getTime());
        textClock.setText(format.format(cal.getTime()));
        YEAY = cal.get(Calendar.YEAR);
        MONTH = cal.get(Calendar.MONTH) + 1;
        DAY = util.getDaysOfMonth(YEAY, MONTH);
        for (int x = 1; x <= DAY; x++) {
            if (x < 10) {
                string += time + "0" + x + ",";
            } else if (x == DAY) {
                string += time + x;
            } else {
                string += time + x + ",";
            }
        }
        Message mes = new Message();
        mes.obj = 1;
        handler.sendMessage(mes);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        dialog.dismiss();
        toastor.getSingletonToast("服务器异常");
    }

    private void initInfo(JSONArray jsonArray) {
        Calorie.clear();
        steps.clear();
        for (int i = 0; i < jsonArray.length(); i++) {
            if (TYPE == 1 || TYPE == 0){
                Calorie.add(jsonArray.optJSONObject(i).optString("Calorie"));
                steps.add( jsonArray.optJSONObject(i).optString("steps"));
            }else if (TYPE==4){
                steps.add( jsonArray.optJSONObject(i).optString("distance"));
            }

            Log.e("--",jsonArray.optJSONObject(i).optString("steps")+i);
        }
        LineData data = getLineData();
        data.setDrawValues(false); //隐藏坐标轴数据
        mChart.setData(data);
        mChart.invalidate();
    }
}
