package com.android.ams_pms_hook;

import android.content.Context;
import android.content.pm.PackageManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by wzc on 2017/7/4.
 */

public class HookHelper {
    /**
     * Hook点是Singleton单例里保存的IActivityManager对象
     */
    public static void hookActivityManager() {
        try {
            // 获取ActivityManagerNative类的Class文件
            Class<?> activityManagerNativeClass = Class.forName("android.app.ActivityManagerNative");
            // 获取gDefault字段的Field对象
            Field gDefaultField = activityManagerNativeClass.getDeclaredField("gDefault");
            gDefaultField.setAccessible(true);
            // 获取gDefault字段的值
            Object gDefault = gDefaultField.get(null);
            // 获取Singleton类的Class文件
            Class<?> singletonClass = Class.forName("android.util.Singleton");
            // 获取mInstance字段的Field对象
            Field mInstanceField = singletonClass.getDeclaredField("mInstance");
            mInstanceField.setAccessible(true);
            // 获取原始的IActivityManager对象
            Object rawIActivityManager = mInstanceField.get(gDefault);
            // 创建代理IActivityManager
            Object hookIActivityManager = Proxy.newProxyInstance(rawIActivityManager.getClass().getClassLoader(),
                    new Class[]{Class.forName("android.app.IActivityManager")},
                    new HookHandler(rawIActivityManager));
            // 把原始的IActivityManager对象替换为代理IActivityManager
            mInstanceField.set(gDefault, hookIActivityManager);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hookPackageManager(Context context) {
        try {
            // 获取ActivityThread对象
            // 获取android.app.ActivityThread类的Class文件
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            // 获取currentActivityThread的Method对象
            Method currentActivityThreadMethod = activityThreadClass.getMethod("currentActivityThread");
            // 获取sCurrentActivityThread对象
            Object sCurrentActivityThreadObj = currentActivityThreadMethod.invoke(null);

            // 获取IPackageManager的原始对象 也可以通过反射getPackageManager()方法来获取
            Field sPackageManagerField = activityThreadClass.getDeclaredField("sPackageManager");
            sPackageManagerField.setAccessible(true);
            Object sPackageManagerObj = sPackageManagerField.get(sCurrentActivityThreadObj); // sPackageManagerObj是android.content.pm.IPackageManager$Stub$Proxy对象

//            // 获取getPackageManager()的Method对象
//            Method getPacakageManagerMethod = activityThreadClass.getMethod("getPackageManager");
//            Object sPackageManagerObj =  getPacakageManagerMethod.invoke(null);
            // 获取IPackageManager的代理对象
            Object hookIPackageManager = Proxy.newProxyInstance(sPackageManagerObj.getClass().getClassLoader(),
                    new Class[]{Class.forName("android.content.pm.IPackageManager")},
                    new HookHandler(sPackageManagerObj));

            // 替换掉ActivityThread类的sPackageManager,这样就保证了ContextImpl类中getPackageManager()方法中的局部变量IPackageManager pm也是被替换掉的
            sPackageManagerField.set(sCurrentActivityThreadObj,hookIPackageManager);

            // 替换掉ApplicationPackageManager类中的mPM字段 ApplicationPackageManager实际上是包装了一个IPackageManager.Stub.Proxy的对象
            PackageManager pm = context.getPackageManager(); // 这里返回的是ApplicationPackageManager对象
            Field mPmField = pm.getClass().getDeclaredField("mPM");
            mPmField.setAccessible(true);
            mPmField.set(pm, hookIPackageManager);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
