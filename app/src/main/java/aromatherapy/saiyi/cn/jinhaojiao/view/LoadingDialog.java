package aromatherapy.saiyi.cn.jinhaojiao.view;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import aromatherapy.saiyi.cn.jinhaojiao.R;


/**
 * 加载中Dialog
 * 
 * @author lexyhp
 */
public class LoadingDialog extends AlertDialog {

	private TextView tips_loading_msg;
	private int layoutResId;
	private String message = null;

	/**
	 * 构造方法
	 * 
	 * @param context
	 *            上下文
	 */
	public LoadingDialog(Context context, String msg) {
		super(context);
		this.layoutResId = R.layout.view_tips_loading;
		message = msg;
	}

	public LoadingDialog(Context context) {
		super(context);
		this.layoutResId = R.layout.view_tips_loading;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(layoutResId);
		if (message!=null){
			tips_loading_msg = (TextView) findViewById(R.id.tips_loading_msg);
			tips_loading_msg.setText(this.message);
			tips_loading_msg.setVisibility(View.VISIBLE);
		}

	}

}
