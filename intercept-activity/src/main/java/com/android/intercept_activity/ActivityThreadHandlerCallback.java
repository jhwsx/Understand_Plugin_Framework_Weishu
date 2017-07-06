package com.android.intercept_activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.lang.reflect.Field;

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
            // 取出rawIntent
            Intent rawIntent = stubIntent.getParcelableExtra(AMSHookHelper.EXTRA_TARGET_INTENT);
            // TODO 为什么这样弄呢?按照下边的写法行不行呢?
            stubIntent.setComponent(rawIntent.getComponent());
            Log.d("ActivityThreadHandlerCa", "stubIntent:" + stubIntent);
//            // 还原为rawIntent
//            intentField.set(obj, rawIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
