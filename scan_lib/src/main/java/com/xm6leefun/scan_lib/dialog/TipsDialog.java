package com.xm6leefun.scan_lib.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
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
 * @Author: ljj
 * @CreateDate: 2020/9/24 17:50
 */
public class TipsDialog extends BaseDialogFragment implements View.OnClickListener {
    public static final String TAG = TipsDialog.class.getSimpleName();
    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    private static final String SECOND_CONTENT = "second_content";
    private static final String IS_CENTER_GRAVITY = "is_center_gravity";
    private ClickListener clickListener;
    private Context mContext;
    private TextView title_tv;
    private TextView content_tv;
    private TextView second_content_tv;

    public static TipsDialog getInstance(String title, String content, String secondContent, boolean isCenterGravity, ClickListener clickListener){
        TipsDialog hintDialog = new TipsDialog();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putString(CONTENT, content);
        args.putString(SECOND_CONTENT, secondContent);
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.tips_dialog_layout,null);
        title_tv = view.findViewById(R.id.title_tv);
        content_tv = view.findViewById(R.id.content_tv);
        second_content_tv = view.findViewById(R.id.second_content_tv);
        TextView sure_tv = view.findViewById(R.id.sure_tv);
        sure_tv.setOnClickListener(this);

        if(args != null){
            String title = args.getString(TITLE);
            String content = args.getString(CONTENT);
            String secondContent = args.getString(SECOND_CONTENT);
            boolean isCenterGravity = args.getBoolean(IS_CENTER_GRAVITY,true);
            content_tv.setText(content);
            if(isCenterGravity){
                content_tv.setGravity(Gravity.CENTER);
            }else{
                content_tv.setGravity(Gravity.START);
            }
            if(TextUtils.isEmpty(secondContent)){
                second_content_tv.setVisibility(View.GONE);
            }else{
                second_content_tv.setVisibility(View.VISIBLE);
                second_content_tv.setText(secondContent);
            }
            if (!StrUtils.isEmpty(title)) {
                title_tv.setVisibility(View.VISIBLE);
                title_tv.setText(title);
            } else {
                title_tv.setVisibility(View.GONE);
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
        if (v.getId() == R.id.sure_tv) {
            dismiss();
            if (clickListener != null) {
                clickListener.onSure();
            }
        }
    }

    public static class Builder{
        private String title;
        private String content;
        private String secondContent;
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
        public Builder setSecondContent(String secondContent){
            this.secondContent = secondContent;
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
        public TipsDialog build(){
            return TipsDialog.getInstance(title, content,secondContent,isCenterGravity,clickListener);
        }
    }

    public interface ClickListener{
        void onSure();
    }

}
