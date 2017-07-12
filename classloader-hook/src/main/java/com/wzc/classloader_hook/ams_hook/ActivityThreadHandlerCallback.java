package com.wzc.classloader_hook.ams_hook;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by wzc on 2017/7/5.
 * 使用普通的静态代理方式代理Handler.Callback接口
 */

public class ActivityThreadHandlerCallback implements Handler.Callback {

    private Handler mBase;

    public ActivityThreadHandlerCallback(Handler base) {
        mBase = base;
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case 100:
                handleLaunchActivity(msg);
                break;
        }
        mBase.handleMessage(msg);
        return true;
    }

    private void handleLaunchActivity(Message msg) {
        // 源码中对于msg的处理
//        case LAUNCH_ACTIVITY: {
//        Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "activityStart");
//        final ActivityClientRecord r = (ActivityClientRecord) msg.obj;
//
//        r.packageInfo = getPackageInfoNoCheck(
//                r.activityInfo.applicationInfo, r.compatInfo);
//        handleLaunchActivity(r, null);
//        Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);

        try {
            // 把替换的intent还原为rawIntent
            Object obj = msg.obj; // obj是一个ActivityClientRecord对象
            // 获取intent字段的Field对象
            Field intentField = obj.getClass().getDeclaredField("intent");
            // 获取intent字段的值
            intentField.setAccessible(true);
            Intent stubIntent = (Intent) intentField.get(obj);
            // 只有是调取插件apk的入口时才进行还原
//            if (stubIntent.getBooleanExtra("target", false)) {
                // 取出rawIntent
                Intent rawIntent = stubIntent.getParcelableExtra(AMSHookHelper.EXTRA_TARGET_INTENT);
                // TODO 为什么这样弄呢?按照下边的写法行不行呢?
                stubIntent.setComponent(rawIntent.getComponent());
                Log.d("ActivityThreadHandlerCa", "stubIntent:" + stubIntent);
//            // 还原为rawIntent
//            intentField.set(obj, rawIntent);

                Field activityInfoField = obj.getClass().getDeclaredField("activityInfo");
                activityInfoField.setAccessible(true);

                // 根据 getPackageInfo 根据这个 包名获取 LoadedApk的信息; 因此这里我们需要手动填上, 从而能够命中缓存
                ActivityInfo activityInfo = (ActivityInfo) activityInfoField.get(obj);

                activityInfo.applicationInfo.packageName = rawIntent.getPackage() == null ?
                        rawIntent.getComponent().getPackageName() : rawIntent.getPackage();
                // TODO 不增加此方法,却没有出现作者所说的 java.lang.RuntimeException: Unable to start activity ComponentInfo {com.weishu.upf.ams_pms_hook.app/com.weishu.upf.ams_pms_hook.app.MainActivity}: java.lang.RuntimeException: Unable to instantiate application android.app.Application: java.lang.IllegalStateException: Unable to get package info for com.weishu.upf.ams_pms_hook.app; is package not installed?

                hookPackageManager();
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void hookPackageManager() throws Exception {

        // 这一步是因为 initializeJavaContextClassLoader 这个方法内部无意中检查了这个包是否在系统安装
        // 如果没有安装, 直接抛出异常, 这里需要临时Hook掉 PMS, 绕过这个检查.

        Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
        Method currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread");
        currentActivityThreadMethod.setAccessible(true);
        Object currentActivityThread = currentActivityThreadMethod.invoke(null);

        // 获取ActivityThread里面原始的 sPackageManager
        Field sPackageManagerField = activityThreadClass.getDeclaredField("sPackageManager");
        sPackageManagerField.setAccessible(true);
        Object sPackageManager = sPackageManagerField.get(currentActivityThread);

        // 准备好代理对象, 用来替换原始的对象
        Class<?> iPackageManagerInterface = Class.forName("android.content.pm.IPackageManager");
        Object proxy = Proxy.newProxyInstance(iPackageManagerInterface.getClassLoader(),
                new Class<?>[] { iPackageManagerInterface },
                new IPackageManagerHookHandler(sPackageManager));

        // 1. 替换掉ActivityThread里面的 sPackageManager 字段
        sPackageManagerField.set(currentActivityThread, proxy);
    }
}
