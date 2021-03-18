package com.xm6leefun.scan_lib.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.xm6leefun.scan_lib.R;
import com.xm6leefun.scan_lib.base.BaseDialogFragment;
import com.xm6leefun.scan_lib.utils.SizeUtils;


/**
 * @Description:非本平台芯片弹框
 * @Author: hehaihao
 * @CreateDate: 2020/9/24 17:50
 */
public class NotPlatformTipsDialog extends BaseDialogFragment implements View.OnClickListener {
    public static final String TAG = NotPlatformTipsDialog.class.getSimpleName();
    private Context mContext;
    private Button sure_tv;

    public static NotPlatformTipsDialog getInstance(){
        NotPlatformTipsDialog hintDialog = new NotPlatformTipsDialog();
        Bundle args = new Bundle();
        hintDialog.setArguments(args);
        return hintDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.not_platform_tips_dialog_layout,null);
        sure_tv = view.findViewById(R.id.sure_tv);
        sure_tv.setOnClickListener(this);
        Dialog dialog = new Dialog(mContext,R.style.bottom_dialog_style_nopadding);
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
            params.width = SizeUtils.dip2px(mContext,250);
            params.gravity = Gravity.CENTER; //将对话框放到布局中间，也就是屏幕中间
            dialog.getWindow().setAttributes(params);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sure_tv) {
            dismiss();
        }
    }

    public static class Builder{
        public NotPlatformTipsDialog build(){
            return NotPlatformTipsDialog.getInstance();
        }
    }

}
