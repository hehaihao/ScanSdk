package com.xm6leefun.scan_lib.login;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.xm6leefun.scan_lib.R;
import com.xm6leefun.scan_lib.weight.svprogress.SVProgressHUD;

/**
 *
 */
public class PwdLoginFragment extends Fragment implements View.OnClickListener{

    private EditText ed_phone;
    private EditText ed_pwd;

    private SVProgressHUD svProgressHUD;

    public static PwdLoginFragment getInstance(){
        PwdLoginFragment fragment = new PwdLoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pwd_login, container, false);
        svProgressHUD = new SVProgressHUD(getActivity());
        ed_phone = view.findViewById(R.id.ed_phone);
        ed_pwd = view.findViewById(R.id.ed_pwd);
        return view;
    }

    @Override
    public void onClick(View v){

    }

    protected String getPhone(){
        if(ed_phone != null){
            String phone = ed_phone.getText().toString();
            return phone;
        }
        return null;
    }
    protected String getPwd(){
        if(ed_pwd != null){
            String pwd = ed_pwd.getText().toString();
            return pwd;
        }
        return null;
    }
}
