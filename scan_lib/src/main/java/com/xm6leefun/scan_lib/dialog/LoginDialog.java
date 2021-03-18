package com.xm6leefun.scan_lib.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xm6leefun.scan_lib.R;
import com.xm6leefun.scan_lib.base.BaseDialogFragment;
import com.xm6leefun.scan_lib.utils.StrUtils;


/**
 * 登录领取积分弹框
 * ljj  20201221
 */
public class LoginDialog extends BaseDialogFragment implements View.OnClickListener {
    public static final String TAG = LoginDialog.class.getSimpleName();
    private ClickListener clickListener;
    private Context mContext;
    private EditText et_phone, et_code;
    private TextView tv_get_code,tv_Title;

    public static LoginDialog getInstance(ClickListener clickListener, String title){
        LoginDialog loginDialog = new LoginDialog();
        Bundle args = new Bundle();
        args.putString("title",title);
        loginDialog.setArguments(args);
        loginDialog.setClickListener(clickListener);
        return loginDialog;
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_bind_for_points,null);
        tv_Title = view.findViewById(R.id.tv_Title);
        et_phone = view.findViewById(R.id.et_phone);
        et_code = view.findViewById(R.id.et_code);
        tv_get_code = view.findViewById(R.id.tv_get_code);
        TextView tv_save = view.findViewById(R.id.tv_save);
        ImageView iv_close = view.findViewById(R.id.iv_close);
        iv_close.setOnClickListener(this);
        tv_get_code.setOnClickListener(this);
        tv_save.setOnClickListener(this);
        String title = args.getString("title","");
        if(!TextUtils.isEmpty(title))tv_Title.setText(title);
        Dialog dialog = new Dialog(mContext, R.style.center_dialog_style);
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
            params.gravity = Gravity.BOTTOM; //将对话框放到布局底部，也就是屏幕底部
            params.windowAnimations = R.style.bottomDialogAnimStyle;
            dialog.getWindow().setAttributes((WindowManager.LayoutParams) params);
        }
    }

    public void setClickListener(ClickListener clickListener){
        this.clickListener = clickListener;
    }

    private CountDownTimer timer =new CountDownTimer(60000, 1000) {
        @Override
        public void onTick(long l) {
            tv_get_code.setText((l / 1000) + "s");
            tv_get_code.setClickable(false);
            tv_get_code.setTextColor(getResources().getColor(R.color.grayb3));
//            tv_get_code.setBackgroundResource(R.drawable.btn_stroke_nor_b5);
        }
        @Override
        public void onFinish() {
            tv_get_code.setEnabled(true);
            tv_get_code.setClickable(true);

            tv_get_code.setText(getResources().getString(R.string.personal_setting_send_code));
            tv_get_code.setTextColor(getResources().getColor(R.color.blue1e));
//            tv_get_code.setBackgroundResource(R.drawable.btn_stroke_sel);
        }
    };

    @Override
    public void onClick(View v) {
        String phone = et_phone.getText().toString().trim();
        String code = et_code.getText().toString().trim();
        int id = v.getId();
        if (id == R.id.iv_close) {  // 关闭
            closeSoftKeyboard(v);//隐藏键盘
            dismiss();
        } else if (id == R.id.tv_get_code) {  // 获取验证码
            if (StrUtils.isEmpty(phone)) {
                Toast.makeText(mContext.getApplicationContext(),getResources().getString(R.string.personal_setting_input_phone),Toast.LENGTH_SHORT).show();
                return;
            }
            if (clickListener != null)
                clickListener.sendSms(phone, new SendSmsCallBack() {
                    @Override
                    public void sendSmsSuccess() {
                        timer.start();//开启定时器
                    }
                });
        } else if (id == R.id.tv_save) {
            if (clickListener != null) {
                if (StrUtils.isEmpty(phone)) {
                    Toast.makeText(mContext.getApplicationContext(),getResources().getString(R.string.personal_setting_input_phone),Toast.LENGTH_SHORT).show();
                    return;
                }
                if (StrUtils.isEmpty(code)) {
                    Toast.makeText(mContext.getApplicationContext(),getResources().getString(R.string.personal_setting_input_code),Toast.LENGTH_SHORT).show();
                    return;
                }
                closeSoftKeyboard(v);//隐藏键盘
                clickListener.onSure(phone, code);
                dismiss();
            }
        }
    }

    private void closeSoftKeyboard(View view) {
        InputMethodManager inputManger = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManger != null) {
            inputManger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static class Builder{
        private ClickListener clickListener;
        private String title;

        public Builder setTitle(String title){
            this.title = title;
            return this;
        }
        public Builder setClickListener(ClickListener clickListener){
            this.clickListener = clickListener;
            return this;
        }
        public LoginDialog build(){
            return LoginDialog.getInstance(clickListener,title);
        }
    }

    public interface ClickListener{
        void onSure(String phone, String code);
        void sendSms(String phone, SendSmsCallBack callBack);
    }

    public interface SendSmsCallBack{
        void sendSmsSuccess();
    }

    @Override
    public void dismiss() {
        if(timer != null)timer.cancel();
        super.dismiss();
    }
}
