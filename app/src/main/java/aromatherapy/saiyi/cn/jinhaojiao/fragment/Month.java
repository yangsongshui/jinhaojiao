package aromatherapy.saiyi.cn.jinhaojiao.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import aromatherapy.saiyi.cn.jinhaojiao.util.Toastor;
import aromatherapy.saiyi.cn.jinhaojiao.view.MsgView;
import aromatherapy.saiyi.cn.jinhaojiao.widget.LoadingDialog;
import aromatherapy.saiyi.cn.jinhaojiao.widget.MyMarkerView;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/5/28.
 */
public class Month extends BaseFragment implements OnChartValueSelectedListener, MsgView {
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
    private Map<String, String> map = new HashMap<String, String>();
    private LoadingDialog dialog;
    private Toastor toastor;
    List<String> data;
    private int TYPE = 0;
    User user;

    GetSpeedHistoryPresenterImp getSpeedHistoryPresenterImp;//速度历史记录
    GetDistancePresenterImp getDistancePresenterImp;//距离
    GetCaloriePresenterImp getCaloriePresenterImp;//卡路里和步数
    GetRatePresenterImp getRatePresenterImp;//心率

    @Override
    public void onResume() {
        super.onResume();
        initChart();
        index = 0;
    }

    @Override
    protected void initData(View layout, Bundle savedInstanceState) {
        data = new ArrayList<>();
        user = MyApplication.newInstance().getUser();
        toastor = new Toastor(getActivity());
        dialog = new LoadingDialog(getActivity());
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TYPE = getActivity().getIntent().getIntExtra("type", -1);
        getSpeedHistoryPresenterImp = new GetSpeedHistoryPresenterImp(this, getActivity());
        getDistancePresenterImp = new GetDistancePresenterImp(this, getActivity());
        getCaloriePresenterImp = new GetCaloriePresenterImp(this, getActivity());
        getRatePresenterImp = new GetRatePresenterImp(this, getActivity());
        if (TYPE == 1 || TYPE == 0) {
            month_data_tv.setText("步");
            month_kaluli.setVisibility(View.VISIBLE);

        } else if (TYPE == 2) {
            month_kaluli.setVisibility(View.GONE);
            month_data_tv.setText("bmp");

        } else if (TYPE == 3) {
            month_kaluli.setVisibility(View.GONE);
            month_data_tv.setText("米/min");

        } else if (TYPE == 4) {
            month_kaluli.setVisibility(View.GONE);
            month_data_tv.setText("公里");
        }
        map.put("startTime", DateUtil.getCurrDate(DateUtil.LONG_DATE_FORMAT2));
        map.put("type", "1");
        if (user.getType() == 1)
            map.put("userID", user.getUserID());
        else {
            user = (User) getActivity().getIntent().getSerializableExtra("student");
            map.put("userID", user.getUserID());
        }
        getData();

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
        for (int i = 0; i <= 30; i++) {
            xVals.add((i + 1) + "");
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
        set1.setDrawValues(false); //隐藏坐标轴数据
        set1.setColor(Color.rgb(40, 179, 76));    //设置曲线的颜色
        return new LineData(xVals, set1);
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        month_data.setText(yy[e.getXIndex()]);
        month_tiem.setText((e.getXIndex() + 1) + "日");
        month_data.setText(data.get(e.getXIndex()));
        month_kaluli.setText(data.get(e.getXIndex()) + "千卡");

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

                break;
            case R.id.month_up:
                mChart.clear();
                mChart.setData(new LineData());
                mChart.invalidate();
                index--;

                break;
            default:
                break;
        }
    }


    private void initInfo(JSONArray jsonArray) {

        data.clear();
        for (int i = 0; i < jsonArray.length(); i++) {
            data.add(jsonArray.optString(i));
            //Log.e("--", jsonArray.optJSONObject(i).optString("steps") + i);
        }
        LineData data = getLineData();
        data.setDrawValues(false); //隐藏坐标轴数据
        mChart.setData(data);
        mChart.invalidate();
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
