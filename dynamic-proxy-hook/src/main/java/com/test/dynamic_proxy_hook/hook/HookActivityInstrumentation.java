package com.test.dynamic_proxy_hook.hook;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * Created by wzc on 2017/6/3.
 */

public class HookActivityInstrumentation extends Instrumentation {
    private Instrumentation mBase;

    public HookActivityInstrumentation(Instrumentation base) {
        mBase = base;
    }

    public ActivityResult execStartActivity(
            Context who, IBinder contextThread, IBinder token, Activity target,
            Intent intent, int requestCode, Bundle options) {
        // 调用Instrumentation类中的execStartActivity()方法
        try {
            // 在这里插入一条log
            Log.d("HookActivityInstrumen", "执行了execStartActivity()方法");
            Method execStartActivityMethod = Instrumentation.class.getDeclaredMethod(
                    "execStartActivity", Context.class, IBinder.class,
                    IBinder.class, Activity.class, Intent.class, int.class, Bundle.class);
            execStartActivityMethod.setAccessible(true);
            return (ActivityResult) execStartActivityMethod.invoke(mBase, who, contextThread, token, target,
                    intent, requestCode, options);
        } catch (Exception e) {
            throw new RuntimeException("do not support!!! pls adapt it");
        }
    }
}
