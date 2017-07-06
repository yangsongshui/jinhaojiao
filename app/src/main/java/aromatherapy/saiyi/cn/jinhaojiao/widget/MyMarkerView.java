package aromatherapy.saiyi.cn.jinhaojiao.widget;

import android.content.Context;
import android.widget.ImageView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

import aromatherapy.saiyi.cn.jinhaojiao.R;

/**
 * Created by Administrator on 2016/5/28.
 */
public class MyMarkerView extends MarkerView{
    private ImageView iv;
    public MyMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        iv = (ImageView) findViewById(R.id.iv);
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        if (e instanceof CandleEntry) {

            CandleEntry ce = (CandleEntry) e;

            //tvContent.setText("" + Utils.formatNumber(ce.getHigh(), 0, true));
        } else {

            //tvContent.setText("" + Utils.formatNumber(e.getVal(), 0, true));
        }
    }

    @Override
    public int getXOffset(float xpos) {
        // this will center the marker-view horizontally
        return -(getWidth() / 2);
    }

    @Override
    public int getYOffset(float ypos) {
        // this will cause the marker-view to be above the selected value
        return -getHeight()/2;
    }
}
