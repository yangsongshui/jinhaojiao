package aromatherapy.saiyi.cn.jinhaojiao.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aromatherapy.saiyi.cn.jinhaojiao.R;
import aromatherapy.saiyi.cn.jinhaojiao.app.MyApplication;
import aromatherapy.saiyi.cn.jinhaojiao.base.BaseFragment;
import aromatherapy.saiyi.cn.jinhaojiao.bean.User;
import aromatherapy.saiyi.cn.jinhaojiao.presenter.GetCaloriePresenterImp;
import aromatherapy.saiyi.cn.jinhaojiao.presenter.GetDistancePresenterImp;
import aromatherapy.saiyi.cn.jinhaojiao.presenter.GetRatePresenterImp;
import aromatherapy.saiyi.cn.jinhaojiao.presenter.GetSpeedHistoryPresenterImp;
import aromatherapy.saiyi.cn.jinhaojiao.util.DateUtil;
import aromatherapy.saiyi.cn.jinhaojiao.util.Log;
import aromatherapy.saiyi.cn.jinhaojiao.util.Toastor;
import aromatherapy.saiyi.cn.jinhaojiao.view.MsgView;
import aromatherapy.saiyi.cn.jinhaojiao.widget.LoadingDialog;
import aromatherapy.saiyi.cn.jinhaojiao.widget.MyMarkerView;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/5/28.
 */
public class Week extends BaseFragment implements OnChartValueSelectedListener, MsgView {
    private final static String TAG = Week.class.getSimpleName();
    @BindView(R.id.line_chart)
    LineChart mChart;
    @BindView(R.id.textClock)
    TextView textClock;

    @BindView(R.id.week_data)
    TextView week_data;
    @BindView(R.id.week_kaluli)
    TextView week_kaluli;
    @BindView(R.id.week_tiem)
    TextView week_tiem;
    DateUtil util = new DateUtil();
    Date date;
    SimpleDateFormat format;
    SimpleDateFormat format2;
    private Map<String, String> map = new HashMap<String, String>();
    private LoadingDialog dialog;
    private Toastor toastor;

    List<String> data;
    List<String> week;
    GetSpeedHistoryPresenterImp getSpeedHistoryPresenterImp;//速度历史记录
    GetDistancePresenterImp getDistancePresenterImp;//距离
    GetCaloriePresenterImp getCaloriePresenterImp;//卡路里和步数
    GetRatePresenterImp getRatePresenterImp;//心率
    User user;
    private int TYPE = 0;


    @Override
    protected void initData(View layout, Bundle savedInstanceState) {
        user = MyApplication.newInstance().getUser();
        data = new ArrayList<>();
        week = new ArrayList<>();
        toastor = new Toastor(getActivity());
        dialog = new LoadingDialog(getActivity());
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TYPE = getActivity().getIntent().getIntExtra("type", -1);
        getSpeedHistoryPresenterImp = new GetSpeedHistoryPresenterImp(this, getActivity());
        getDistancePresenterImp = new GetDistancePresenterImp(this, getActivity());
        getCaloriePresenterImp = new GetCaloriePresenterImp(this, getActivity());
        getRatePresenterImp = new GetRatePresenterImp(this, getActivity());
        initChart();
        date = new Date();
        format = new SimpleDateFormat("MM.dd");
        format2 = new SimpleDateFormat("yyyyMMdd");
        textClock.setText(format.format(util.nextDay(date, -6)) + "-" + format.format(date));
        map.put("startTime", DateUtil.getCurrDate(DateUtil.LONG_DATE_FORMAT2));
        map.put("type", "0");
        if (user.getType() == 1)
            map.put("userID", user.getUserID());
        else {
            user = (User) getActivity().getIntent().getSerializableExtra("student");
            map.put("userID", user.getUserID());
        }

        if (TYPE == 1 || TYPE == 0) {
            week_kaluli.setText("Kcar");
        } else if (TYPE == 2) {
            week_kaluli.setText("bmp");
        } else if (TYPE == 3) {
            week_kaluli.setText("m/min");
        } else if (TYPE == 4) {
            week_kaluli.setText("米");
        }
        getData();
    }


    @Override
    protected int getContentView() {
        return R.layout.fragment_week;
    }

    private void initInfo(JSONArray jsonArray) {
        data.clear();
        if (TYPE == 3) {
            Intent intent2 = new Intent();
            intent2.setAction("VOLOCITY_ACTIVITY_DATA");
            //发送 一个无序广播
            intent2.putExtra("data",jsonArray.optString(7));
            getActivity().sendBroadcast(intent2);
            jsonArray.remove(jsonArray.length());
        }

        for (int i = 0; i < 7; i++) {
            data.add(jsonArray.optString(i));

        }
        LineData data = getLineData();
        data.setDrawValues(false); //隐藏坐标轴数据
        mChart.setData(data);
        mChart.invalidate();
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

    private LineData getLineData() {
        String[] xx = {"1", "2", "3", "4", "5", "6", "7"};
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < xx.length; i++) {
            xVals.add(xx[i]);
        }

        ArrayList<Entry> yVals = new ArrayList<Entry>();
        for (int i = (data.size() - 1), j = 0; i >= 0; i--, j++) {
            yVals.add(new Entry(Float.parseFloat(data.get(i)), j));
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
        week_tiem.setText(week.get(e.getXIndex()));
        week_data.setText(e.getVal() + "");
    }

    @Override
    public void onNothingSelected() {

    }


    @OnClick(value = {R.id.week_next, R.id.week_up})
    public void Click(View view) {
        week_tiem.setText("");
        week_data.setText("0");
        switch (view.getId()) {

            case R.id.week_next:
                textClock.setText(format.format(date) + "-" + format.format(util.nextDay(date, 6)));
                date = util.nextDay(date, 7);
                gettiem();
                break;
            case R.id.week_up:
                date = util.nextDay(date, -7);
                textClock.setText(format.format(util.nextDay(date, -6)) + "-" + format.format(util.nextDay(date, 0)));
                gettiem2();
                break;
            default:
                break;
        }
    }


    private void gettiem() {

        map.put("startTime", format2.format(date));
        getData();
        initWeek();

    }

    private void initWeek() {
        week.clear();
        String string = "";
        SimpleDateFormat format2 = new SimpleDateFormat("EEEE");
        for (int i = 0; i < 7; i++) {
            string = format2.format(DateUtil.nextDay(date, -i));
            week.add(0, string);

        }
    }

    private void gettiem2() {
        map.put("startTime", format2.format(date));
        getData();
        initWeek();

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
            data.clear();
            toastor.showSingletonToast(jsonObject.optString("resMessage"));
            if (TYPE == 1 || TYPE == 0) {
                initInfo(jsonObject.optJSONObject("resBody").optJSONArray("calorieList"));
            } else if (TYPE == 2) {
                initInfo(jsonObject.optJSONObject("resBody").optJSONArray("rateList"));
            } else if (TYPE == 3) {
                initInfo(jsonObject.optJSONObject("resBody").optJSONArray("speedList"));
            } else if (TYPE == 4) {
                initInfo(jsonObject.optJSONObject("resBody").optJSONArray("distanceList"));

            }

        }
    }


    @Override
    public void loadDataError(Throwable throwable) {
        Log.e(TAG, throwable.getLocalizedMessage());
        toastor.showSingletonToast("服务器连接失败");
    }

    private void getData() {
        initWeek();
        if (TYPE == 1 || TYPE == 0) {
            getCaloriePresenterImp.loadMsg(map);
        } else if (TYPE == 2) {
            getRatePresenterImp.loadMsg(map);
        } else if (TYPE == 3) {
            getSpeedHistoryPresenterImp.loadMsg(map);
        } else if (TYPE == 4) {
            getDistancePresenterImp.loadMsg(map);
        }
    }
}
