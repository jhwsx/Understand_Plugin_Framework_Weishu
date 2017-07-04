package com.test.binder_hook;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            BinderHookHelper.hookClipboardService();
        } catch (Exception e) {
            e.printStackTrace();
        }

        EditText editText = new EditText(this);
        setContentView(editText);
    }
}
