package aromatherapy.saiyi.cn.jinhaojiao.fragment;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import aromatherapy.saiyi.cn.jinhaojiao.R;
import aromatherapy.saiyi.cn.jinhaojiao.base.BaseFragment;
import aromatherapy.saiyi.cn.jinhaojiao.util.DateUtil;
import aromatherapy.saiyi.cn.jinhaojiao.view.MyMarkerView;

/**
 * Created by Administrator on 2016/5/28.
 */
public class Month extends BaseFragment implements OnChartValueSelectedListener {
    @ViewInject(R.id.line_chart)
    LineChart mChart;
    @ViewInject(R.id.textClock)
    TextView textClock;

    @ViewInject(R.id.month_data)
    TextView month_data;
    @ViewInject(R.id.month_tiem)
    TextView month_tiem;
    DateUtil util = new DateUtil();

    @ViewInject(R.id.month_data_tv)
    TextView month_data_tv;
    @ViewInject(R.id.month_kaluli)
    TextView month_kaluli;
    Handler handler;
    int index;
    int MONTH;
    int YEAY;
    private int TYPE = 0;
    @Override
    protected void initData(View layout) {
        TYPE = getActivity().getIntent().getIntExtra("type", -1);
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




        Calendar cal = Calendar.getInstance();
        YEAY = cal.get(Calendar.YEAR);
        MONTH = cal.get(Calendar.MONTH)+1;
        DAY = util.getDaysOfMonth(YEAY, MONTH);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        textClock.setText(format.format(cal.getTime()));
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                initChart();
            }
        };

    }

    @Override
    public void onResume() {
        super.onResume();
        initChart();
        index = 0;
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
        LineData data = getLineData();
        mChart.setOnChartValueSelectedListener(this);

        mChart.setData(data);
        mChart.invalidate();
    }

    String[] yy = {"20", "80", "10", "60", "30", "70", "55", "30", "70", "55", "30", "70", "55", "30", "70", "55"
            , "20", "80", "10", "60", "30", "70", "55", "30", "70", "55", "30", "70", "55", "30", "70"};

    private LineData getLineData() {


        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i <= DAY; i++) {
            xVals.add((i + 1) + "");
        }

        ArrayList<Entry> yVals = new ArrayList<Entry>();
        for (int x = 0; x < DAY; x++) {
            yVals.add(new Entry(Float.parseFloat(yy[x]), x));
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

    }

    @Override
    public void onNothingSelected() {

    }


    @Event(value = {R.id.month_next, R.id.month_up})
    private void Click(View view) {
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

    private void MonthNext(int i) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, i);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        textClock.setText(format.format(cal.getTime()));
        YEAY = cal.get(Calendar.YEAR);
        MONTH = cal.get(Calendar.MONTH)+1;
        DAY = util.getDaysOfMonth(YEAY, MONTH);
        Message mes = new Message();
        mes.obj = 1;
        handler.sendMessage(mes);
    }
}
