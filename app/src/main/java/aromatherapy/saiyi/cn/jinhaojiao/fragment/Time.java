package aromatherapy.saiyi.cn.jinhaojiao.fragment;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.xutils.view.annotation.ViewInject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import aromatherapy.saiyi.cn.jinhaojiao.R;
import aromatherapy.saiyi.cn.jinhaojiao.base.BaseFragment;
import aromatherapy.saiyi.cn.jinhaojiao.view.MyMarkerView;

/**
 * Created by Administrator on 2016/5/28.
 */
public class Time extends BaseFragment implements OnChartValueSelectedListener {
    @ViewInject(R.id.line_chart)
    LineChart mChart;
    @ViewInject(R.id.time_data)
    TextView time_data;
    @ViewInject(R.id.itme_data_tv)
    TextView itme_data_tv;

    @ViewInject(R.id.time_kaluli)
    TextView time_kaluli;
    @ViewInject(R.id.time_tiem)
    TextView time_tiem;
    private int TYPE = 0;

    @Override
    protected void initData(View layout) {
        TYPE = getActivity().getIntent().getIntExtra("type", -1);
        initChart();
        init();
        if (TYPE == 1 || TYPE == 0) {
            itme_data_tv.setText("步");
            time_kaluli.setVisibility(View.VISIBLE);
        } else if (TYPE == 2) {
            time_kaluli.setVisibility(View.GONE);
            itme_data_tv.setText("bmp");
        }else if (TYPE == 3){
            time_kaluli.setVisibility(View.GONE);
            itme_data_tv.setText("米/min");
        }else if (TYPE == 4){
            time_kaluli.setVisibility(View.GONE);
            itme_data_tv.setText("公里");
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
        LineData data = getLineData();
        mChart.setOnChartValueSelectedListener(this);
        data.setDrawValues(false); //隐藏坐标轴数据
        mChart.setData(data);
    }

    String[] xx = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
    String[] yy = {"43", "80", "10", "60", "30", "120", "55", "22", "40", "55", "22", "12"};

    private LineData getLineData() {


        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < xx.length; i++) {
            xVals.add(xx[i]);
        }

        ArrayList<Entry> yVals = new ArrayList<Entry>();
        for (int i = 0; i < yy.length; i++) {
            yVals.add(new Entry(Float.parseFloat(yy[i]), i));
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
        time_data.setText(yy[e.getXIndex()]);
        time_tiem.setText(times.get(e.getXIndex()) + "");
    }

    @Override
    public void onNothingSelected() {

    }

    List<String> times = new ArrayList<>();

    private void init() {
        times.clear();
        Calendar cal = Calendar.getInstance();//使用默认时区和语言环境获得一个日历。
        for (int i = 1; i <= xx.length; i++) {
            cal.add(Calendar.MINUTE, -10);//取当前日期的前一天.
            //通过格式化输出日期
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            times.add(0, format.format(cal.getTime()));
        }


    }
}
