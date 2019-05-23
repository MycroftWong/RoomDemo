package com.mycroft.roomdemo.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;
import chihane.jdaddressselector.AddressProvider;
import chihane.jdaddressselector.AddressSelector;
import chihane.jdaddressselector.OnAddressSelectedListener;
import com.mycroft.roomdemo.R;
import mlxy.utils.Dev;

public class BottomDialog extends Dialog {
    private AddressSelector selector;

    public BottomDialog(Context context) {
        super(context, R.style.bottom_dialog);
        this.init(context);
    }

    public BottomDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.init(context);
    }

    public BottomDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.init(context);
    }

    private void init(Context context) {
        this.selector = new AddressSelector(context);
        this.setContentView(this.selector.getView());
        Window window = this.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = -1;
        params.height = Dev.dp2px(context, 256.0F);
        window.setAttributes(params);
        window.setGravity(80);
    }

    public void setOnAddressSelectedListener(OnAddressSelectedListener listener) {
        this.selector.setOnAddressSelectedListener(listener);
    }

    public static BottomDialog show(Context context) {
        return show(context, null);
    }

    public static BottomDialog show(Context context, OnAddressSelectedListener listener) {
        return show(context, listener, null);
    }


    public static BottomDialog show(Context context, OnAddressSelectedListener listener, AddressProvider addressProvider) {
        BottomDialog dialog = new BottomDialog(context, R.style.bottom_dialog);
        dialog.selector.setAddressProvider(addressProvider);
        dialog.selector.setOnAddressSelectedListener(listener);
        dialog.show();
        return dialog;
    }
}

