package com.android.intercept_activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
// TODO 为什么继承了AppCompatActivity时,却不能启动TargetActivity?
// TODO 为什么启动成功了TargetActivity,退出程序再次启动,却打开的是StubActivity?
// TODO 文章中写的一切的秘密在token里面,怎么理解呢?
public class MainActivity extends Activity implements View.OnClickListener {

    private Button startTargetActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startTargetActivity = (Button) findViewById(R.id.btn_start_target_activity);
        startTargetActivity.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_start_target_activity:
                startActivity(new Intent(MainActivity.this,TargetActivity.class));
                break;
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        AMSHookHelper.hookActivityManager("com.android.intercept_activity", "com.android.intercept_activity.StubActivity");
        AMSHookHelper.hookActivityThreadHandler();
        super.attachBaseContext(newBase);
    }
}
