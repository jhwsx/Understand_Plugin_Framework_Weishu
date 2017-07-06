package com.android.intercept_activity;


import android.os.Handler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by wzc on 2017/7/5.
 */

public class AMSHookHelper {

    public static final String EXTRA_TARGET_INTENT = "extra_target_intent";

    public static void hookActivityManager(String pluginPackageName, String pluginClazzCanonicalName) {
        try {
            // 获取ActivityManagerNative类的Class文件
            Class<?> activityManagerNativeClass = Class.forName("android.app.ActivityManagerNative");
            // 获取gDefault字段的Field对象
            Field gDefaultField = activityManagerNativeClass.getDeclaredField("gDefault");
            // 获取gDefault字段的值
            gDefaultField.setAccessible(true);
            Object gDefaultObj = gDefaultField.get(null);

            // 获取Singleton类的Class文件
            Class<?> singletonClass = Class.forName("android.util.Singleton");
            // 获取mInstance字段的Field对象
            Field mInstanceField = singletonClass.getDeclaredField("mInstance");
            // 获取mInstance字段的值
            mInstanceField.setAccessible(true);
            Object rawIActivityManager = mInstanceField.get(gDefaultObj);

            // 获取代理IActivityManager
            Object hookIActivityManager = Proxy.newProxyInstance(rawIActivityManager.getClass().getClassLoader(),
                    new Class[]{Class.forName("android.app.IActivityManager")},
                    new IActivityManagerHandler(rawIActivityManager, pluginPackageName, pluginClazzCanonicalName));

            // 把rawIActivityManager替换为代理IActivityManager
            mInstanceField.set(gDefaultObj, hookIActivityManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hookActivityThreadHandler() {
        // 给ActivityThread类的mH字段设置我们自己定义的callback

        try {
            // 获取ActivityThread类的Class文件
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            // 获取currentActivityThread方法的Method对象
            Method currentActivityThreadMethod = activityThreadClass.getMethod("currentActivityThread");
            // 获取sCurrentActivityThread字段的值
            Object sCurrentActivityThreadObj = currentActivityThreadMethod.invoke(null);

            // 获取mH字段的Field对象
            Field mHField = activityThreadClass.getDeclaredField("mH");
            mHField.setAccessible(true);
            // 获取mH字段的值
            Handler mHObj = (Handler) mHField.get(sCurrentActivityThreadObj);

            // 获取Handler类的mCallback字段的Field对象
            Field mCallbackField = Handler.class.getDeclaredField("mCallback");
            mCallbackField.setAccessible(true);
            // 获取自己定义的callback
            ActivityThreadHandlerCallback activityThreadHandlerCallback = new ActivityThreadHandlerCallback(mHObj);
            // 替换成自己的callback
            mCallbackField.set(mHObj,activityThreadHandlerCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
