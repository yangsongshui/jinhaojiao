package aromatherapy.saiyi.cn.jinhaojiao.fragment;

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
import aromatherapy.saiyi.cn.jinhaojiao.presenter.FindStepPresenterImp;
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
public class Week extends BaseFragment implements OnChartValueSelectedListener, MsgView{
    private final static String TAG = Week.class.getSimpleName();
    @BindView(R.id.line_chart)
    LineChart mChart;
    @BindView(R.id.textClock)
    TextView textClock;

    @BindView(R.id.week_data)
    TextView week_data;
    @BindView(R.id.tweek_kaluli)
    TextView week_kaluli;
    @BindView(R.id.week_tiem)
    TextView week_tiem;
    DateUtil util = new DateUtil();
    Date data;
    SimpleDateFormat format;
    SimpleDateFormat format2;
    private Map<String, String> map = new HashMap<String, String>();
    private LoadingDialog dialog;
    private Toastor toastor;

    List<String> Calorie = new ArrayList<>();
    List<String> steps = new ArrayList<>();
    FindStepPresenterImp findStepPresenterImp;

    User user;
    @Override
    protected void initData(View layout, Bundle savedInstanceState) {
        user = MyApplication.newInstance().getUser();
        toastor = new Toastor(getActivity());
        dialog = new LoadingDialog(getActivity());
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        findStepPresenterImp = new FindStepPresenterImp(this, getActivity());
        initChart();
        data = new Date();
        format = new SimpleDateFormat("MM.dd");
        format2 = new SimpleDateFormat("yyyyMMdd,");
        data = util.getDateOfMondayInWeek();
        textClock.setText(format.format(data) + "-" + format.format(util.nextDay(data, 6)));
        if (MyApplication.newInstance().getEquipmentID() != null) {
            gettiem();
            map.put("equipmentID",MyApplication.newInstance().getEquipmentID());
            //map.put("time", string);
            map.put("time", string);
            map.put("type", "3");
            if (user.getType() == 1)
                map.put("userID", user.getUserID());
            else {
                user= (User) getActivity().getIntent().getSerializableExtra("student");
                map.put("userID", user.getUserID());
            }

            findStepPresenterImp.loadMsg(map);
        }
    }



    @Override
    protected int getContentView() {
        return R.layout.fragment_week;
    }

    private void initInfo(JSONArray jsonArray) {
        Calorie.clear();
        steps.clear();
        for (int i = 0; i < jsonArray.length(); i++) {
            Calorie.add(jsonArray.optJSONObject(i).optString("Calorie"));
            steps.add(jsonArray.optJSONObject(i).optString("steps"));
            android.util.Log.e("--", steps.get(i) + " " + jsonArray.length() + " " + i);
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

    String[] xx = {"1", "2", "3", "4", "5", "6", "7"};
    String[] yy = {"20", "80", "10", "60", "30", "70", "55"};

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
        week_tiem.setText(util.dayNames[e.getXIndex()]);
        week_data.setText(steps.get(e.getXIndex()));
        if (Calorie.size() > 0) {
            week_kaluli.setText(Calorie.get(e.getXIndex()));
        }
    }

    @Override
    public void onNothingSelected() {

    }


    @OnClick(value = {R.id.week_next, R.id.week_up})
    public void Click(View view) {
        switch (view.getId()) {
            case R.id.week_next:
                gettiem();
                textClock.setText(format.format(data) + "-" + format.format(util.nextDay(data, 6)));
                data = util.nextDay(data, 7);
                break;
            case R.id.week_up:
                data = util.nextDay(data, -7);
                textClock.setText(format.format(util.nextDay(data, -7)) + "-" + format.format(util.nextDay(data, -1)));
                gettiem2();
                break;
            default:
                break;
        }
    }

    String string = "";

    private void gettiem() {
        string = format2.format(data);
        for (int i = 1; i < 7; i++) {
            string += format2.format(util.nextDay(data, i));
            Log.e("----", string);
        }
        string = string.substring(0, string.length() - 1);
        Log.e("----", string);
        if ( MyApplication.newInstance().getEquipmentID() != null) {
            map.clear();
            map.put("equipmentID",  MyApplication.newInstance().getEquipmentID());
            map.put("time", string);
            map.put("type", "3");

            findStepPresenterImp.loadMsg(map);
        }
    }

    private void gettiem2() {
        string = "";
        for (int i = -7; i < -1; i++) {
            string += format2.format(util.nextDay(data, i));
        }
        string += format2.format(util.nextDay(data, -1));
        string = string.substring(0, string.length() - 1);
        Log.e("----", string);
        if ( MyApplication.newInstance().getEquipmentID() != null) {
            map.clear();
            map.put("equipmentID",MyApplication.newInstance().getEquipmentID());
            map.put("time", string);
            map.put("type", "3");
            findStepPresenterImp.loadMsg(map);
        }
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
            initInfo(jsonObject.optJSONObject("resBody").optJSONArray("list"));
        }
    }


    @Override
    public void loadDataError(Throwable throwable) {
        Log.e(TAG, throwable.getLocalizedMessage());
        toastor.showSingletonToast("服务器连接失败");
    }
}
