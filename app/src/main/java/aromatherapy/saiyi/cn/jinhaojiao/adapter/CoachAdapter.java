package aromatherapy.saiyi.cn.jinhaojiao.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import aromatherapy.saiyi.cn.jinhaojiao.R;
import aromatherapy.saiyi.cn.jinhaojiao.base.MyBaseAdapter;
import aromatherapy.saiyi.cn.jinhaojiao.bean.User;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2016/7/14.
 */
public class CoachAdapter extends MyBaseAdapter<User> {
    Context mContext;
    List<User> list = new ArrayList<>();

    public CoachAdapter(List<User> list, Context context) {
        super(list);
        this.mContext = context;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mContext,
                    R.layout.coach_item, null);
            new ViewHolder(convertView);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.link_name_tv.setText(list.get(position).getNikename());
        if (list.get(position).getHead_pic()!=null){
           holder.coach_item_pic.setImageBitmap(stringtoBitmap(list.get(position).getHead_pic()));
        }else {
            holder.coach_item_pic.setImageResource(R.drawable.movie_2);
        }
        if (list.get(position).isLine())
            holder.coach_ONOFF_pic.setVisibility(View.GONE);
        else
            holder.coach_ONOFF_pic.setVisibility(View.VISIBLE);

        return convertView;
    }

    class ViewHolder {
        TextView link_name_tv;
        CircleImageView coach_item_pic;
        CircleImageView coach_ONOFF_pic;

        public ViewHolder(View view) {
            link_name_tv = (TextView) view.findViewById(R.id.coach_item_name);
            coach_item_pic = (CircleImageView) view.findViewById(R.id.coach_item_pic);
            coach_ONOFF_pic = (CircleImageView) view.findViewById(R.id.coach_ONOFF_pic);
            view.setTag(this);
        }
    }

    public Bitmap stringtoBitmap(String string) {
        //将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }
}
