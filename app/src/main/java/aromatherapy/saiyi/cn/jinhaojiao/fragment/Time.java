package aromatherapy.saiyi.cn.jinhaojiao.fragment;

import android.graphics.Color;
import android.os.Bundle;
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
import aromatherapy.saiyi.cn.jinhaojiao.presenter.FindStepPresenterImp;
import aromatherapy.saiyi.cn.jinhaojiao.presenter.FindsuduPresenterImp;
import aromatherapy.saiyi.cn.jinhaojiao.presenter.FindxinlvPresenterImp;
import aromatherapy.saiyi.cn.jinhaojiao.util.Toastor;
import aromatherapy.saiyi.cn.jinhaojiao.view.MsgView;
import aromatherapy.saiyi.cn.jinhaojiao.widget.LoadingDialog;
import aromatherapy.saiyi.cn.jinhaojiao.widget.MyMarkerView;
import butterknife.BindView;

/**
 * Created by Administrator on 2016/5/28.
 */
public class Time extends BaseFragment implements OnChartValueSelectedListener, MsgView{
    private final static String TAG = Time.class.getSimpleName();
    @BindView(R.id.line_chart)
    LineChart mChart;
    @BindView(R.id.time_data)
    TextView time_data;
    @BindView(R.id.itme_data_tv)
    TextView itme_data_tv;

    @BindView(R.id.time_kaluli)
    TextView time_kaluli;
    @BindView(R.id.time_tiem)
    TextView time_tiem;
    private int TYPE = 0;
    private Map<String, String> map = new HashMap<String, String>();
    private Toastor toastor;
    List<String> Calorie = new ArrayList<>();
    List<String> steps = new ArrayList<>();
    FindxinlvPresenterImp findxinlvPresenterImp;
    FindsuduPresenterImp findsuduPresenterImp;
    FindStepPresenterImp findStepPresenterImp;
    User user;
    private LoadingDialog dialog;
    @Override
    protected void initData(View layout, Bundle savedInstanceState) {
        user = MyApplication.newInstance().getUser();
        TYPE = getActivity().getIntent().getIntExtra("type", -1);
        initChart();
        dialog = new LoadingDialog(getActivity());
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        toastor = new Toastor(getActivity());
        findxinlvPresenterImp = new FindxinlvPresenterImp(this, getActivity());
        findsuduPresenterImp = new FindsuduPresenterImp(this, getActivity());
        findStepPresenterImp = new FindStepPresenterImp(this, getActivity());
        init();

            map.put("time", string);
            map.put("type", "1");
            if (user.getType() == 1)
                map.put("userID", user.getUserID());
            else {
                user= (User) getActivity().getIntent().getSerializableExtra("student");
                map.put("userID", user.getUserID());

        }
        Log.e(TAG, string);
        if (TYPE == 1 || TYPE == 0) {
            itme_data_tv.setText("步");
            time_kaluli.setVisibility(View.VISIBLE);
            findStepPresenterImp.loadMsg(map);
        } else if (TYPE == 2) {
            time_kaluli.setVisibility(View.GONE);
            itme_data_tv.setText("bmp");
            findxinlvPresenterImp.loadMsg(map);

        } else if (TYPE == 3) {
            time_kaluli.setVisibility(View.GONE);
            itme_data_tv.setText("米/min");
            findsuduPresenterImp.loadMsg(map);
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_time;
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

    String[] xx = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
    String[] yy = {"43", "80", "10", "60", "30", "120", "55", "22", "40", "55", "22", "12"};

    private LineData getLineData() {
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < xx.length - 1; i++) {
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
        Log.e("---------", (e.getXIndex() + 1) + "");
        time_data.setText(steps.get(e.getXIndex()));
        time_tiem.setText(times.get(e.getXIndex()+1) + "");
        if (Calorie.size() > 0) {
            time_kaluli.setText(Calorie.get(e.getXIndex()) + "千卡");
        }
    }

    @Override
    public void onNothingSelected() {

    }

    List<String> times = new ArrayList<>();
    String string = "";

    private void init() {
        times.clear();
        string = "";
        Calendar cal = Calendar.getInstance();//使用默认时区和语言环境获得一个日历。
        for (int i = 1; i <= xx.length; i++) {
            cal.add(Calendar.MINUTE, -10);
            //通过格式化输出日期
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMddHHmm,");
            times.add(0, format.format(cal.getTime()));
            string += format2.format(cal.getTime());

        }
        string = string.substring(0, string.length() - 1);
    }



    private void initInfo(JSONArray jsonArray, String string) {
        Calorie.clear();
        steps.clear();
        int calorie = 0;
        int step = 0;
        for (int i = 0; i < jsonArray.length(); i++) {
            if (i == 0) {
                if (TYPE == 1 || TYPE == 0)
                    calorie = jsonArray.optJSONObject(i).optInt("Calorie");
                step = jsonArray.optJSONObject(i).optInt(string);
            } else {
                if (TYPE == 1 || TYPE == 0){
                    Calorie.add(0, (calorie - jsonArray.optJSONObject(i).optInt("Calorie")) + "");
                    calorie = jsonArray.optJSONObject(i).optInt("Calorie");
                    steps.add(0, (step - jsonArray.optJSONObject(i).optInt(string)) + "");
                    step = jsonArray.optJSONObject(i).optInt(string);
                }else if (TYPE==2){
                    steps.add(0, (jsonArray.optJSONObject(i).optInt(string)) + "");
                }else if (TYPE == 3){
                    steps.add(0, (step - jsonArray.optJSONObject(i).optInt(string)) + "");
                    step = jsonArray.optJSONObject(i).optInt(string);
                }/*else if (TYPE == 4){
                    steps.add(0, (step - jsonArray.optJSONObject(i).optInt(string)) + "");
                    step = jsonArray.optJSONObject(i).optInt(string);
                }*/
            }
            Log.e("--", steps.size() + " " + jsonArray.length() + " " + i);
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
            toastor.showSingletonToast(jsonObject.optString("resMessage"));
            if (TYPE == 1 || TYPE == 0) {
                initInfo(jsonObject.optJSONObject("resBody").optJSONArray("list"), "steps");
            } else if (TYPE == 2) {
                initInfo(jsonObject.optJSONObject("resBody").optJSONArray("list"), "heartrate");
            } else if (TYPE == 3) {
                initInfo(jsonObject.optJSONObject("resBody").optJSONArray("list"), "speed");
            }

        }
    }


    @Override
    public void loadDataError(Throwable throwable) {
        Log.e(TAG, throwable.getLocalizedMessage());
        toastor.showSingletonToast("服务器连接失败");
    }
}
