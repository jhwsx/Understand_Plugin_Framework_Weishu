package com.android.intercept_activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by wzc on 2017/7/5.
 * 目标Activity,不在AndroidManifest.xml中声明,是我们要启动的Activity
 */

public class TargetActivity extends Activity {
    public static final String TAG = "TargetActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target);
        Log.d(TAG, "onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    // finish()方法的调用过程
    // APP进程 Activity finish()
    // AMS进程 ActivityManagerService  public final boolean finishActivity(IBinder token, int resultCode, Intent resultData,boolean finishTask)
    // AMS进程 ActivityStack final boolean requestFinishActivityLocked(IBinder token, int resultCode,Intent resultData, String reason, boolean oomAdj)
    // AMS进程 ActivityStack final boolean finishActivityLocked(ActivityRecord r, int resultCode, Intent resultData,String reason, boolean oomAdj)
    // AMS进程 ActivityStack final ActivityRecord finishCurrentActivityLocked(ActivityRecord r, int mode, boolean oomAdj)
    // AMS进程 ActivityStack final boolean destroyActivityLocked(ActivityRecord r, boolean removeFromApp, String reason)
    // APP进程 ApplicationThread  public final void scheduleDestroyActivity(IBinder token, boolean finishing,int configChanges)
    // APP进程 ActivityThread private void handleDestroyActivity(IBinder token, boolean finishing,int configChanges, boolean getNonConfigInstance)
    // APP进程 ActivityThread private ActivityClientRecord performDestroyActivity(IBinder token, boolean finishing,int configChanges, boolean getNonConfigInstance)
    // APP进程 Instrumentation public void callActivityOnDestroy(Activity activity)

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}
