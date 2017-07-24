package aromatherapy.saiyi.cn.jinhaojiao.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import aromatherapy.saiyi.cn.jinhaojiao.R;
import aromatherapy.saiyi.cn.jinhaojiao.app.MyApplication;
import aromatherapy.saiyi.cn.jinhaojiao.bean.Student;
import aromatherapy.saiyi.cn.jinhaojiao.connector.OnItemCheckListener;
import aromatherapy.saiyi.cn.jinhaojiao.connector.OnItemLongCheckListener;
import aromatherapy.saiyi.cn.jinhaojiao.connector.OnItemPhotoCheckListener;
import de.hdodenhof.circleimageview.CircleImageView;

import static aromatherapy.saiyi.cn.jinhaojiao.util.AppUtil.stringtoBitmap;

/**
 * Created by Administrator on 2016/7/14.
 */
public class CoachAdapter extends RecyclerView.Adapter<CoachAdapter.ViewHolder> {
    private List<Student> data;
    private Context context;
    private int listType;

    private OnItemCheckListener onItemCheckListener;
    private OnItemPhotoCheckListener onItemPhotoCheckListener;
    private OnItemLongCheckListener onItemLongCheckListener;


    public CoachAdapter(List<Student> data, Context context, int listType) {
        this.data = data;
        this.context = context;
        this.listType = listType;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_time, parent, false);
        return new CoachAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Student user = data.get(position);
        holder.setIsRecyclable(false);
        holder.coach_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemCheckListener != null)
                    onItemCheckListener.OnItemCheck(holder, position);
            }
        });
        holder.coach_ll.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onItemLongCheckListener != null) {
                    onItemLongCheckListener.OnItemLong(holder, position);
                }
                return false;

            }
        });
        holder.coach_item_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemPhotoCheckListener != null)
                    onItemPhotoCheckListener.OnPhotoCheck(holder, position);
            }
        });
        if (user.getUsername() != null) {
            holder.coach_item_name.setText(user.getUsername());
        } else {
            holder.coach_item_name.setText(user.getNikename());
        }

        if (user.getSex() != null) {
            if (user.getSex().equals("男")) {
                holder.coach_item_sex.setImageResource(R.drawable.manwhite);
            } else if (user.getSex().equals("女"))
                holder.coach_item_sex.setImageResource(R.drawable.nvxingbai);
        }
        if (user.getHead_pic() != null && user.getHead_pic().length() > 5) {
            if (user.getHead_pic().contains("http:")) {
                MyApplication.newInstance().getGlide().load(user.getHead_pic()).into(holder.coach_item_pic);
            } else
                holder.coach_item_pic.setImageBitmap(stringtoBitmap(user.getHead_pic()));
        }
        double strength = Double.parseDouble(user.getStrength());
        if (strength < 5) {
            holder.state_iv.setImageResource(R.drawable.stop);
        } else if (strength <= 20) {
            holder.state_iv.setImageResource(R.drawable.walk);
        } else if (strength <= 40) {
            holder.state_iv.setImageResource(R.drawable.benpao);
        } else if (strength > 40) {
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
            holder.coach_run_tv.setText(user.getSpeed());
            holder.coach_xinlv_tv.setText(user.getHeartrate());
            holder.coach_step_tv.setText(user.getSteps());
            int min = Integer.parseInt(user.getTime());
            if (min >= 60) {
                int h = (min / 60);
                int m = (min % 60);
                if (h >= 10 && m >= 10) {
                    holder.coach_time_tv.setText((h + ":" + m));
                } else if (h < 10 && m >= 10) {
                    holder.coach_time_tv.setText(("0" + h + ":" + m));
                } else if (h >= 10 && m < 10) {
                    holder.coach_time_tv.setText((h + ":0 +" + m));
                } else if (h < 10 && m < 10) {
                    holder.coach_time_tv.setText(("0" + h + ":0" + m));
                }
                holder.coach_time.setVisibility(View.GONE);
            } else {
                holder.coach_time_tv.setText(min + "");
                holder.coach_time.setVisibility(View.VISIBLE);
            }
        } else {
            holder.coach_info_rl.setVisibility(View.GONE);
            holder.top_tv.setVisibility(View.VISIBLE);
            switch (listType) {
                case 1:
                    holder.top_tv.setText(user.getLoad());
                    break;
                case 2:
                    holder.top_tv.setText(user.getStrength() + "bpm");
                    break;
                case 3:
                    holder.top_tv.setText(user.getSpeed() + "m/min");
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
        private TextView coach_item_name, top_tv, coach_run_tv, coach_xinlv_tv, coach_time_tv, coach_time, coach_step_tv;
        private LinearLayout coach_ll;
        private LinearLayout coach_info_rl;

        public ViewHolder(View itemView) {
            super(itemView);
            top_iv = (ImageView) itemView.findViewById(R.id.top_iv);
            state_iv = (ImageView) itemView.findViewById(R.id.state_iv);
            coach_item_sex = (ImageView) itemView.findViewById(R.id.coach_item_sex);
            coach_item_pic = (CircleImageView) itemView.findViewById(R.id.coach_item_pic);
            coach_item_name = (TextView) itemView.findViewById(R.id.coach_item_name);
            top_tv = (TextView) itemView.findViewById(R.id.top_tv);
            coach_run_tv = (TextView) itemView.findViewById(R.id.coach_run_tv);
            coach_time = (TextView) itemView.findViewById(R.id.coach_time);
            coach_step_tv = (TextView) itemView.findViewById(R.id.coach_step_tv);
            coach_xinlv_tv = (TextView) itemView.findViewById(R.id.coach_xinlv_tv);
            coach_time_tv = (TextView) itemView.findViewById(R.id.coach_time_tv);
            coach_ll = (LinearLayout) itemView.findViewById(R.id.coach_ll);
            coach_info_rl = (LinearLayout) itemView.findViewById(R.id.coach_info_rl);
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

    public void setOnItemLongCheckListener(OnItemLongCheckListener onItemLongCheckListener) {
        this.onItemLongCheckListener = onItemLongCheckListener;
    }

}

