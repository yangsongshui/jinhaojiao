package aromatherapy.saiyi.cn.jinhaojiao.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import aromatherapy.saiyi.cn.jinhaojiao.R;
import aromatherapy.saiyi.cn.jinhaojiao.bean.Student;
import aromatherapy.saiyi.cn.jinhaojiao.connector.OnItemCheckListener;
import aromatherapy.saiyi.cn.jinhaojiao.connector.OnItemPhotoCheckListener;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2016/7/14.
 */
public class CoachAdapter extends RecyclerView.Adapter<CoachAdapter.ViewHolder> {
    private List<Student> data;
    private Context context;
    private int listType;

    private OnItemCheckListener onItemCheckListener;
    private OnItemPhotoCheckListener onItemPhotoCheckListener;



    public CoachAdapter(List<Student> data, Context context, int listType) {
        this.data = data;
        this.context = context;
        this.listType = listType;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.coach_item, parent, false);
        return new CoachAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Student user = data.get(position);
        holder.coach_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemCheckListener != null)
                    onItemCheckListener.OnItemCheck(holder, position);
            }
        });
        holder.coach_item_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemPhotoCheckListener != null)
                    onItemPhotoCheckListener.OnPhotoCheck(holder, position);
            }
        });
        holder.coach_item_name.setText(user.getNikename());
        if (user.getSex() != null) {
            if (user.getSex().equals("男")) {
                holder.coach_item_sex.setImageResource(R.drawable.manwhite);
            } else if (user.getSex().equals("女"))
                holder.coach_item_sex.setImageResource(R.drawable.nvxingbai);
        }
        if (user.getBitmap() != null) {
            holder.coach_item_pic.setImageBitmap(user.getBitmap());
        } else {
            holder.coach_item_pic.setImageResource(R.mipmap.logo);
        }
        int speed = Integer.parseInt(user.getSpeed());
        if (speed < 5) {
            holder.state_iv.setImageResource(R.drawable.stop);
        } else if (speed <= 20) {
            holder.state_iv.setImageResource(R.drawable.walk);
        } else if (speed <= 40) {
            holder.state_iv.setImageResource(R.drawable.benpao);
        } else if (speed > 40) {
            holder.state_iv.setImageResource(R.drawable.fastrun);
        }

        if (position == 0) {
            holder.top_iv.setImageResource(R.drawable.huanguanyellow);
        } else if (position == 1) {
            holder.top_iv.setImageResource(R.drawable.huangguangreen);
        } else if (position == 2) {
            holder.top_iv.setImageResource(R.drawable.jinhuangguan);
        } else {
            holder.top_iv.setVisibility(View.INVISIBLE);
        }
        if (listType == 0) {
            holder.coach_info_rl.setVisibility(View.VISIBLE);
            holder.top_tv.setVisibility(View.GONE);
            holder.coach_run_tv.setText(user.getDistance() + "km");
            holder.coach_xinlv_tv.setText(user.getHeartrate() + "bpm");
            holder.coach_time_tv.setText(user.getSpeed() + "m");
        } else {
            holder.coach_info_rl.setVisibility(View.GONE);
            holder.top_tv.setVisibility(View.VISIBLE);
            switch (listType) {
                case 1:
                    holder.top_tv.setText(user.getStrength());
                    break;
                case 2:
                    holder.top_tv.setText(user.getLoad() + "bpm");
                    break;
                case 3:
                    holder.top_tv.setText(user.getSpeed() + "/km");
                    break;
            }

        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView top_iv, state_iv, coach_item_sex;
        private CircleImageView coach_item_pic;
        private TextView coach_item_name, top_tv, coach_run_tv, coach_xinlv_tv, coach_time_tv;
        private LinearLayout coach_ll;
        private RelativeLayout coach_info_rl;

        public ViewHolder(View itemView) {
            super(itemView);
            top_iv = (ImageView) itemView.findViewById(R.id.top_iv);
            state_iv = (ImageView) itemView.findViewById(R.id.state_iv);
            coach_item_sex = (ImageView) itemView.findViewById(R.id.coach_item_sex);
            coach_item_pic = (CircleImageView) itemView.findViewById(R.id.coach_item_pic);
            coach_item_name = (TextView) itemView.findViewById(R.id.coach_item_name);
            top_tv = (TextView) itemView.findViewById(R.id.top_tv);
            coach_run_tv = (TextView) itemView.findViewById(R.id.coach_run_tv);
            coach_xinlv_tv = (TextView) itemView.findViewById(R.id.coach_xinlv_tv);
            coach_time_tv = (TextView) itemView.findViewById(R.id.coach_time_tv);
            coach_ll = (LinearLayout) itemView.findViewById(R.id.coach_ll);
            coach_info_rl = (RelativeLayout) itemView.findViewById(R.id.coach_info_rl);
        }
    }

    public void setItems(List<Student> data, int listType) {
        this.data = data;
        this.listType = listType;
        this.notifyDataSetChanged();
    }

    public void setOnItemCheckListener(OnItemCheckListener onItemCheckListener) {
        this.onItemCheckListener = onItemCheckListener;
    }
    public void setOnItemPhotoCheckListener(OnItemPhotoCheckListener onItemPhotoCheckListener) {
        this.onItemPhotoCheckListener = onItemPhotoCheckListener;
    }
}
