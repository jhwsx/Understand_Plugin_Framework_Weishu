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
 * Instrumentation是一个类,采用手写代理的方法
 */

public class HookContextInstrumentation extends Instrumentation {
    private Instrumentation mBase;

    public HookContextInstrumentation(Instrumentation base) {
        mBase = base;
    }

    public ActivityResult execStartActivity(
            Context who, IBinder contextThread, IBinder token, Activity target,
            Intent intent, int requestCode, Bundle options) {
        // 调用Instrumentation类中的execStartActivity()方法
        try {
            // 在这里插入一条log
            Log.d("HookContextInstrumentation", "执行了HookInstrumentation的execStartActivity()方法");
            // 反射调用原始的方法,因为这个方法是隐藏的.如果不调用的话,所有的startActivity都会失效了.
            Method execStartActivityMethod = Instrumentation.class.getDeclaredMethod(
                    "execStartActivity", Context.class, IBinder.class,
                    IBinder.class, Activity.class, Intent.class, int.class, Bundle.class);
            execStartActivityMethod.setAccessible(true);
            return (ActivityResult) execStartActivityMethod.invoke(mBase, who, contextThread, token, target,
                    intent, requestCode, options);
            // 不调用的话,就返回null试一下,结果是跳转Activity没有任何的效果
//            return null;
        } catch (Exception e) {
            throw new RuntimeException("do not support!!! pls adapt it");
        }
    }

}
