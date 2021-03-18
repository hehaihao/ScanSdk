package com.xm6leefun.scan_lib.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.xm6leefun.scan_lib.R;
import com.xm6leefun.scan_lib.base.BaseDialogFragment;


/**
 * 获取积分弹框（需绑定个人账户）
 * ljj  20201218
 */
public class GetPointsBindDialog extends BaseDialogFragment implements View.OnClickListener {
    public static final String TAG = GetPointsBindDialog.class.getSimpleName();
    private static final String POINTS = "points";
    private ClickListener clickListener;
    private Context mContext;
    private TextView tv_get_points;

    private long points;

    public static GetPointsBindDialog getInstance(long points, ClickListener clickListener){
        GetPointsBindDialog selectNetDialog = new GetPointsBindDialog();
        Bundle args = new Bundle();
        args.putLong(POINTS, points);
        selectNetDialog.setArguments(args);
        selectNetDialog.setClickListener(clickListener);
        return selectNetDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        if(args == null) {
            return super.onCreateDialog(savedInstanceState);
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_get_points_bind_dialog,null);
        tv_get_points = view.findViewById(R.id.tv_get_points);
        ImageView iv_question_mark = view.findViewById(R.id.iv_question_mark);
        TextView tv_no_bind_temp = view.findViewById(R.id.tv_no_bind_temp);
        TextView tv_bind_now = view.findViewById(R.id.tv_bind_now);
        iv_question_mark.setOnClickListener(this);
        tv_no_bind_temp.setOnClickListener(this);
        tv_bind_now.setOnClickListener(this);
        if (args != null) {
            points = args.getLong(POINTS);
            tv_get_points.setText(getResources().getString(R.string.identify_dialog_get_points, String.valueOf(points)));
        }

        Dialog dialog = new Dialog(mContext, R.style.center_dialog_style);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return true;
            }
        });
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if(dialog != null) {
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.gravity = Gravity.CENTER; //将对话框放到布局中间，也就是屏幕中间
            dialog.getWindow().setAttributes((WindowManager.LayoutParams) params);
        }
    }

    public void setClickListener(ClickListener clickListener){
        this.clickListener = clickListener;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_question_mark) {  // 问号，查看活动详情
            if (clickListener != null) {
                clickListener.readDetails();
            }
        } else if (id == R.id.tv_bind_now) {  // 立即绑定
            dismiss();
            if (clickListener != null) {
                clickListener.bindPhone();
            }
        } else if (id == R.id.tv_no_bind_temp) {  // 放弃领取
            dismiss();
            clickListener.showActivity();
        }
    }

    public static class Builder{
        private long points;
        private ClickListener clickListener;

        public Builder setPoints(long points){
            this.points = points;
            return this;
        }

        public Builder setClickListener(ClickListener clickListener){
            this.clickListener = clickListener;
            return this;
        }

        public GetPointsBindDialog build(){
            return GetPointsBindDialog.getInstance(points, clickListener);
        }
    }

    public interface ClickListener{
        void readDetails();
        void bindPhone();
        void showActivity();
    }

}
