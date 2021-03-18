package com.xm6leefun.scan_lib.login;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xm6leefun.scan_lib.R;
import com.xm6leefun.scan_lib.utils.StrUtils;
import com.xm6leefun.scan_lib.weight.svprogress.SVProgressHUD;

/**
 *
 */
public class CodeLoginFragment extends Fragment implements View.OnClickListener{

    private EditText ed_phone;
    private EditText ed_code;
    private TextView tv_send_code;

    private OnContentListener onContentListener;
    private SVProgressHUD svProgressHUD;

    public static CodeLoginFragment getInstance(){
        CodeLoginFragment fragment = new CodeLoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_code_login, container, false);
        svProgressHUD = new SVProgressHUD(getActivity());
        ed_phone = view.findViewById(R.id.ed_phone);
        ed_code = view.findViewById(R.id.ed_code);
        ed_phone.addTextChangedListener(phoneWatcher);
        tv_send_code = view.findViewById(R.id.tv_send_code);
        tv_send_code.setOnClickListener(this);
        return view;
    }

    private TextWatcher phoneWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String phone = editable.toString().trim();
            if (!StrUtils.isEmpty(phone) && phone.length() == 11) {
                tv_send_code.setEnabled(true);
            } else {
                tv_send_code.setEnabled(false);
            }
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnContentListener){
            onContentListener = (OnContentListener) context;
        }else{
            throw new RuntimeException("LoginActivity must implements OnContentListener");
        }
    }

    @Override
    public void onClick(View v){
        if (v.getId() == R.id.tv_send_code) {// 发送验证码
            initSendSms();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(timer != null){
            timer.cancel();
        }
    }

    private CountDownTimer timer = new CountDownTimer(60000, 1000) {
        @Override
        public void onTick(long l) {
            tv_send_code.setText(getString(R.string.get_code_count,l / 1000));
            tv_send_code.setEnabled(false);
        }

        @Override
        public void onFinish() {
            tv_send_code.setEnabled(true);
            tv_send_code.setText(getResources().getString(R.string.get_code));
        }
    };

    private void initSendSms() {
        String phone = ed_phone.getText().toString().trim();
        if (StrUtils.isEmpty(phone) || phone.length() != 11) {
            tv_send_code.setEnabled(false);
            return;
        }
        if (StrUtils.isEmpty(phone)) {
            Toast.makeText(getActivity().getApplicationContext(),getString(R.string.phone_is_null),Toast.LENGTH_SHORT).show();
            return;
        }
        //发送验证码
        onContentListener.sendSms(phone, new SendSmsCallBack() {
            @Override
            public void onSuccess() {
                timer.start();
            }
        });
    }

    protected String getPhone(){
        if(ed_phone != null){
            String phone = ed_phone.getText().toString();
            return phone;
        }
        return null;
    }
    protected String getCode(){
        if(ed_code != null){
            String code = ed_code.getText().toString();
            return code;
        }
        return null;
    }

    interface OnContentListener {
        void sendSms(String phone, SendSmsCallBack callBack);
    }
    public interface SendSmsCallBack{
        void onSuccess();
    }
}
