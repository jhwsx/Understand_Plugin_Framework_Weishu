package com.android.ams_pms_hook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity implements View.OnClickListener {

    private Button hookActivityManager;
    private Button hookPackageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hookActivityManager = (Button) findViewById(R.id.btn_hook_activitymanager);
        hookPackageManager = (Button) findViewById(R.id.btn_hook_packagemanager);
        hookActivityManager.setOnClickListener(this);
        hookPackageManager.setOnClickListener(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        HookHelper.hookActivityManager();
        HookHelper.hookPackageManager(newBase);
        super.attachBaseContext(newBase);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_hook_activitymanager:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.baidu.com/"));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.btn_hook_packagemanager:
                // 测试PMS HOOK (调用其相关方法)
                getPackageManager().getInstalledApplications(0);
                break;
        }
    }
}
