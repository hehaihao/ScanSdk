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
 * 积分活动弹框
 * ljj  20201218
 */
public class PointsActivityDialog extends BaseDialogFragment implements View.OnClickListener {
    public static final String TAG = PointsActivityDialog.class.getSimpleName();
    public static final String ACTIVITY_STATE = "activateState";
    public static final String GOODS_NAME = "goodsName";
    public static final String GOODS_LOGO = "goodsLogo";
    private ClickListener clickListener;
    private Context mContext;
    private TextView tv_name;

    public static PointsActivityDialog getInstance(int activateState, String goodsName, String goodsLogo, ClickListener clickListener){
        PointsActivityDialog selectNetDialog = new PointsActivityDialog();
        Bundle args = new Bundle();
        args.putInt(ACTIVITY_STATE, activateState);
        args.putString(GOODS_NAME, goodsName);
        args.putString(GOODS_LOGO, goodsLogo);
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_points_activity_dialog,null);
        tv_name = view.findViewById(R.id.tv_name);
        TextView tv_send_points = view.findViewById(R.id.tv_send_points);
        TextView tv_read_details = view.findViewById(R.id.tv_read_details);
        ImageView iv_close = view.findViewById(R.id.iv_close);
        tv_send_points.setOnClickListener(this);
        tv_read_details.setOnClickListener(this);
        iv_close.setOnClickListener(this);
        ImageView iv_activity_over = view.findViewById(R.id.iv_activity_status);
        if (args != null) {
            int activateState = args.getInt(ACTIVITY_STATE,0);
            iv_activity_over.setVisibility((activateState == 3 || activateState == 4) ? View.VISIBLE : View.GONE);  // 积分活动状态：1活动未开始; 2活动进行中；3活动已结束；4活动已关闭
            switch (activateState) {
                case 3:  // 活动已结束
                    iv_activity_over.setImageResource(R.mipmap.activity_over);
                    break;
                case 4: // 活动已关闭
                    iv_activity_over.setImageResource(R.mipmap.activity_closed);
                    break;
            }

            String name = args.getString(GOODS_NAME,"");
            String headUrl = args.getString(GOODS_LOGO,"");
            tv_name.setText(name);
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
        int id = v.getId();// 查看详情
        if (id == R.id.tv_send_points || id == R.id.tv_read_details) {  // “卟噔”送积分
            dismiss();
            if (clickListener != null) {
                clickListener.onReadDetails();
            }
        } else if (id == R.id.iv_close) {  // 关闭
            dismiss();
            if (clickListener != null) {
                clickListener.onShow();
            }
        }
    }

    public static class Builder{
        private int activateState;
        private String goodsName;
        private String goodsLogo;
        private ClickListener clickListener;

        public Builder setActivateState(int activateState) {
            this.activateState = activateState;
            return this;
        }

        public Builder setGoodsName(String goodsName) {
            this.goodsName = goodsName;
            return this;
        }

        public Builder setGoodsLogo(String goodsLogo) {
            this.goodsLogo = goodsLogo;
            return this;
        }

        public Builder setClickListener(ClickListener clickListener){
            this.clickListener = clickListener;
            return this;
        }

        public PointsActivityDialog build(){
            return PointsActivityDialog.getInstance(activateState,goodsName,goodsLogo, clickListener);
        }
    }

    public interface ClickListener{
        void onShow();
        void onReadDetails();
    }
}
