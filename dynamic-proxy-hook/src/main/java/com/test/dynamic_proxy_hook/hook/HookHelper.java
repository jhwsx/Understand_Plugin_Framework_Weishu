package com.test.dynamic_proxy_hook.hook;

import android.app.Activity;
import android.app.Instrumentation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by wzc on 2017/6/3.
 */

public class HookHelper {

    public static void attachContext() {
        try {
            // 获取ActivityThread对象
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Method currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread");
            currentActivityThreadMethod.setAccessible(true);
            Object sCurrentActivityThreadObj = currentActivityThreadMethod.invoke(null);
            // 获取mInstrumentation对象
            Field mInstrumentationField = activityThreadClass.getDeclaredField("mInstrumentation");
            mInstrumentationField.setAccessible(true);
            Instrumentation mInstrumentationObj = (Instrumentation) mInstrumentationField.get(sCurrentActivityThreadObj);
            // 替换mInstrumentation
            HookContextInstrumentation hookInstrumentation = new HookContextInstrumentation(mInstrumentationObj);
            mInstrumentationField.set(sCurrentActivityThreadObj, hookInstrumentation);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // this method should be invoked in onCreate(), not in attachBaseContext().
    // if you invoke this method in attachBaseContext(), you'll always get
    // mInstrumentationObj = null.
    public static void attachActivityContext(Activity activity) {
        try {
            // 获取Activity对象
            Class<?> activityClass = Class.forName("android.app.Activity");
            // 获取mInstrumentation对象
            Field mInstrumentationField = activityClass.getDeclaredField("mInstrumentation");
            mInstrumentationField.setAccessible(true);
            Instrumentation mInstrumentationObj = (Instrumentation) mInstrumentationField.get(activity);
            // 替换mInstrumentation
            HookActivityInstrumentation hookActivityInstrumentation = new HookActivityInstrumentation(mInstrumentationObj);
            mInstrumentationField.set(activity,hookActivityInstrumentation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
