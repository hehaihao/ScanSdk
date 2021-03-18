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
 * 获取积分弹框
 * ljj  20201218
 */
public class GetPointsDialog extends BaseDialogFragment implements View.OnClickListener {
    public static final String TAG = GetPointsDialog.class.getSimpleName();
    public static final String TITLE = "title";
    private ClickListener clickListener;
    private Context mContext;

    public static GetPointsDialog getInstance(String title, ClickListener clickListener){
        GetPointsDialog selectNetDialog = new GetPointsDialog();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_get_points_dialog,null);
        TextView tv_get_points = view.findViewById(R.id.tv_get_points);
        ImageView iv_question_mark = view.findViewById(R.id.iv_question_mark);
        TextView tv_read_details = view.findViewById(R.id.tv_read_details);
        TextView tv_close = view.findViewById(R.id.tv_close);
        iv_question_mark.setOnClickListener(this);
        tv_read_details.setOnClickListener(this);
        tv_close.setOnClickListener(this);
        if (args != null) {
            String title = args.getString(TITLE);
            tv_get_points.setText(title);
        }

        Dialog dialog = new Dialog(mContext, R.style.center_dialog_style_margin30);
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
//                dismiss();
            if (clickListener != null) {
                clickListener.readActivityDetails();
            }
        } else if (id == R.id.tv_read_details) {  // 查看积分详情
            dismiss();
            if (clickListener != null) {

                clickListener.readPointsDetails();
            }
        } else if (id == R.id.tv_close) {  // 关闭
            dismiss();
        }
    }

    public static class Builder{
        private String title;
        private ClickListener clickListener;

        public Builder setTitle(String title){
            this.title = title;
            return this;
        }

        public Builder setClickListener(ClickListener clickListener){
            this.clickListener = clickListener;
            return this;
        }

        public GetPointsDialog build(){
            return GetPointsDialog.getInstance(title, clickListener);
        }
    }

    public interface ClickListener{
        void readPointsDetails();
        void readActivityDetails();
    }

}
