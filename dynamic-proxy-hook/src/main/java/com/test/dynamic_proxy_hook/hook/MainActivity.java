package com.test.dynamic_proxy_hook.hook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.test.dynamic_proxy_hook.R;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // use for hook_Activity_startActivity
        HookHelper.attachActivityContext(MainActivity.this);

        Log.d("MainActivity", "getApplication():" + getApplication());
        findViewById(R.id.btn_hook_context_startActivity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("https://www.baidu.com/"));
                getApplicationContext().startActivity(intent);
            }
        });
        findViewById(R.id.btn_hook_Activity_startActivity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("https://www.baidu.com/"));
                MainActivity.this.startActivity(intent);
            }
        });
    }

    /**
     * 这个方法中传入了一个base参数，并把这个参数赋值给了mBase对象。而attachBaseContext()方法
     * 其实是由系统来调用的，它会把ContextImpl对象作为参数传递到attachBaseContext()方法当中，
     * 从而赋值给mBase对象，之后ContextWrapper中的所有方法其实都是通过这种委托的机制交由ContextImpl
     * 去具体实现的，所以说ContextImpl是上下文功能的实现类是非常准确的
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        Log.d("MainActivity", "newBase:" + newBase); // MainActivity: newBase:android.app.ContextImpl@9d051ac0
        super.attachBaseContext(newBase);
        // use for hook_context_startActivity
//        HookHelper.attachContext();

    }
}
