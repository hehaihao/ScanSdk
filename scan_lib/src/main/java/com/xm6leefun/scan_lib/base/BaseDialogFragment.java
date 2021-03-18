package com.xm6leefun.scan_lib.base;


import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;


/**
 * @Description:
 * @Author: hhh
 * @CreateDate: 2020/9/23 9:23
 */
public class BaseDialogFragment extends DialogFragment {

    @Override
    public void show(FragmentManager manager, String tag) {
//        super.show(manager, tag);
        try{
            Class c = Class.forName("android.app.DialogFragment");
            Constructor con = c.getConstructor();
            Object obj = con.newInstance();
            Field dismissed = c.getDeclaredField("mDismissed");
            dismissed.setAccessible(true);
            dismissed.set(obj, false);
            Field shownByMe = c.getDeclaredField("mShownByMe");
            shownByMe.setAccessible(true);
            shownByMe.set(obj,false);
        }catch (Exception e){
            e.printStackTrace();
        }
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this,tag);
        ft.commitAllowingStateLoss();
    }

    @Override
    public void dismiss() {
//        super.dismiss();
        dismissAllowingStateLoss();
    }
}
