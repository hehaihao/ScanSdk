package com.xm6leefun.scan_lib.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.xm6leefun.scan_lib.R;
import com.xm6leefun.scan_lib.base.BaseDialogFragment;
import com.xm6leefun.scan_lib.utils.StrUtils;


/**
 * @Description:弹框
 * @Author: hhh
 * @CreateDate: 2020/9/24 17:50
 */
public class TwoButtonTipsDialog extends BaseDialogFragment implements View.OnClickListener {
    public static final String TAG = TwoButtonTipsDialog.class.getSimpleName();
    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    private static final String SURE_TEXT = "sure_text";
    private static final String IS_CENTER_GRAVITY = "is_center_gravity";
    private ClickListener clickListener;
    private Context mContext;
    private TextView title_tv;
    private TextView content_tv;

    public static TwoButtonTipsDialog getInstance(String title, String content, String sureText, boolean isCenterGravity, ClickListener clickListener){
        TwoButtonTipsDialog hintDialog = new TwoButtonTipsDialog();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putString(CONTENT, content);
        args.putString(SURE_TEXT, sureText);
        args.putBoolean(IS_CENTER_GRAVITY, isCenterGravity);
        hintDialog.setArguments(args);
        hintDialog.setClickListener(clickListener);
        return hintDialog;
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.two_button_tips_dialog_layout,null);
        title_tv = view.findViewById(R.id.title_tv);
        content_tv = view.findViewById(R.id.content_tv);
        TextView sure_tv = view.findViewById(R.id.sure_tv);
        TextView cancel_tv = view.findViewById(R.id.cancel_tv);
        sure_tv.setOnClickListener(this);
        cancel_tv.setOnClickListener(this);

        if(args != null){
            String title = args.getString(TITLE);
            String content = args.getString(CONTENT);
            String sureText = args.getString(SURE_TEXT);
            boolean isCenterGravity = args.getBoolean(IS_CENTER_GRAVITY,true);
            content_tv.setText(content);
            if(isCenterGravity){
                content_tv.setGravity(Gravity.CENTER);
            }else{
                content_tv.setGravity(Gravity.START);
            }
            if (!StrUtils.isEmpty(title)) {
                title_tv.setVisibility(View.VISIBLE);
                title_tv.setText(title);
            } else {
                title_tv.setVisibility(View.GONE);
            }
            if (!StrUtils.isEmpty(sureText)) {
                sure_tv.setText(sureText);
            } else {
                sure_tv.setText(getString(R.string.app_base_confirm));
            }
        }
        Dialog dialog = new Dialog(mContext,R.style.bottom_dialog_style);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
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

    public void setClickListener(ClickListener clickRightListener){
        this.clickListener = clickRightListener;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.sure_tv) {
            dismiss();
            if (clickListener != null) {
                clickListener.onSure();
            }
        } else if (id == R.id.cancel_tv) {
            dismiss();
        }
    }

    public static class Builder{
        private String title;
        private String content;
        private String sureText;
        private boolean isCenterGravity = true;
        private ClickListener clickListener;

        public Builder setTitle(String title){
            this.title = title;
            return this;
        }
        public Builder setContent(String content){
            this.content = content;
            return this;
        }
        public Builder setSureText(String sureText){
            this.sureText = sureText;
            return this;
        }
        public Builder setIsCenterGravity(boolean isCenterGravity){
            this.isCenterGravity = isCenterGravity;
            return this;
        }
        public Builder setClickListener(ClickListener clickListener){
            this.clickListener = clickListener;
            return this;
        }
        public TwoButtonTipsDialog build(){
            return TwoButtonTipsDialog.getInstance(title, content,sureText,isCenterGravity,clickListener);
        }
    }

    public interface ClickListener{
        void onSure();
    }

}
