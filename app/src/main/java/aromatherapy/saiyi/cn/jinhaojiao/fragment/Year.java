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

import java.util.ArrayList;
import java.util.Calendar;
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
public class Year extends BaseFragment implements OnChartValueSelectedListener, MsgView {
    private final static String TAG = Year.class.getSimpleName();
    @BindView(R.id.line_chart)
    LineChart mChart;
    @BindView(R.id.textClock)
    TextView textClock;

    @BindView(R.id.year_data)
    TextView year_data;

    @BindView(R.id.year_tiem)
    TextView year_tiem;
    @BindView(R.id.year_kaluli)
    TextView year_kaluli;


    private Map<String, String> map = new HashMap<String, String>();

    List<String> data;
    int index;
    private int TYPE = 0;
    User user;
    GetSpeedHistoryPresenterImp getSpeedHistoryPresenterImp;//速度历史记录
    GetDistancePresenterImp getDistancePresenterImp;//距离
    GetCaloriePresenterImp getCaloriePresenterImp;//卡路里和步数
    GetRatePresenterImp getRatePresenterImp;//心率
    Toastor toastor;
    private LoadingDialog dialog;

    @Override
    protected void initData(View layout, Bundle savedInstanceState) {
        user = MyApplication.newInstance().getUser();
        data = new ArrayList<>();
        initChart();
        toastor = new Toastor(getActivity());
        dialog = new LoadingDialog(getActivity());
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        getSpeedHistoryPresenterImp = new GetSpeedHistoryPresenterImp(this, getActivity());
        getDistancePresenterImp = new GetDistancePresenterImp(this, getActivity());
        getCaloriePresenterImp = new GetCaloriePresenterImp(this, getActivity());
        getRatePresenterImp = new GetRatePresenterImp(this, getActivity());
        TYPE = getActivity().getIntent().getIntExtra("type", -1);
        if (TYPE == 1 || TYPE == 0) {
            year_kaluli.setText("Kcar");
        } else if (TYPE == 2) {
            year_kaluli.setText("bmp");
        } else if (TYPE == 3) {
            year_kaluli.setText("m/min");

        } else if (TYPE == 4) {
            year_kaluli.setText("米");
        }
        index = Calendar.getInstance().get(Calendar.YEAR);
        MonthNext(index + "");
    }


    private void initInfo(JSONArray jsonArray) {
        data.clear();
        if (TYPE == 3) {
            Intent intent2 = new Intent();
            intent2.setAction("VOLOCITY_ACTIVITY_DATA");
            //发送 一个无序广播
            intent2.putExtra("data",jsonArray.optString(12));
            getActivity().sendBroadcast(intent2);
            jsonArray.remove(jsonArray.length());
            jsonArray.remove(12);
        }
        for (int i = 0; i < 12; i++) {
            data.add(jsonArray.optString(i));
        }
        LineData data = getLineData();
        data.setDrawValues(false); //隐藏坐标轴数据
        mChart.setData(data);
        mChart.invalidate();
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_year;
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

    private LineData getLineData() {

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < 12; i++) {
            xVals.add(xx[i]);
        }

        ArrayList<Entry> yVals = new ArrayList<Entry>();
        for (int i = 0; i < data.size(); i++) {
            yVals.add(new Entry(Float.parseFloat(data.get(i)), i));
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
        year_data.setText(data.get(e.getXIndex()));
        year_tiem.setText(e.getXIndex() + 1 + "月");

    }

    @Override
    public void onNothingSelected() {

    }


    @OnClick(value = {R.id.day_next, R.id.day_up})
    public void Click(View view) {
        year_data.setText("");
        year_tiem.setText("0");
        switch (view.getId()) {
            case R.id.day_next:
                index++;
                MonthNext(index + "");
                break;
            case R.id.day_up:
                index--;
                MonthNext(index + "");
                break;
            default:
                break;
        }
    }


    private void MonthNext(String time) {
        textClock.setText(time);
        map.put("startTime", time);
        map.put("type", "2");
        if (user.getType() == 1)
            map.put("userID", user.getUserID());
        else {
            user = (User) getActivity().getIntent().getSerializableExtra("student");
            map.put("userID", user.getUserID());
        }
        getData();
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
        if (jsonObject.optInt("resCode") == 0) {
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
