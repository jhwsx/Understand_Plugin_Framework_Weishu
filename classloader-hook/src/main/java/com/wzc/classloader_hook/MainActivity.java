package com.wzc.classloader_hook;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.wzc.classloader_hook.ams_hook.AMSHookHelper;
import com.wzc.classloader_hook.classloader_hook.LoadedApkClassLoaderHookHelper;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("target", true);
                intent.setComponent(new ComponentName("com.android.ams_pms_hook",
                        "com.android.ams_pms_hook.MainActivity"));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {

        AMSHookHelper.hookActivityManagerNative();
        AMSHookHelper.hookActivityThreadHandler();
        super.attachBaseContext(newBase);
        Utils.extractAssets(newBase, "ams-pms-hook.apk");

        try {
            LoadedApkClassLoaderHookHelper.hookLoadedApkInActivityThread(getFileStreamPath("ams-pms-hook.apk"));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
