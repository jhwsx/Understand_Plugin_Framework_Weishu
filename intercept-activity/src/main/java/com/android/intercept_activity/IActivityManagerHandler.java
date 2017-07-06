package com.android.intercept_activity;

import android.content.ComponentName;
import android.content.Intent;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by wzc on 2017/7/5.
 *
 */

public class IActivityManagerHandler implements InvocationHandler {
    public static final String TAG = "IActivityManagerHandler";
    private Object mBase;
    private String mPluginPackageName;
    private String mPluginClazzCanonicalName;
    public IActivityManagerHandler(Object base, String pluginPackageName, String pluginClazzCanonicalName) {
        mBase = base;
        mPluginPackageName = pluginPackageName;
        mPluginClazzCanonicalName = pluginClazzCanonicalName;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//        Log.d(TAG, "method: " + method.getName());
//        ActivityManagerService类中的startActivity()方法
//        public final int startActivity(IApplicationThread caller, String callingPackage,
//                Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode,
//        int startFlags, ProfilerInfo profilerInfo, Bundle options)
        if ("startActivity".equals(method.getName())) {
            // 获取intent参数的索引
            int intentArgsIndex = -1;
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Intent) {
                    intentArgsIndex = i;
                    break;
                }
            }
            // 取出rawIntent
            Intent rawIntent = (Intent) args[intentArgsIndex];

            // 构造替换的Intent
            Intent stubIntent = new Intent();
            stubIntent.setComponent(new ComponentName(mPluginPackageName, mPluginClazzCanonicalName));
            stubIntent.putExtra(AMSHookHelper.EXTRA_TARGET_INTENT, rawIntent); // 保存真正要启动的intent

            // 替换intent
            args[intentArgsIndex] = stubIntent;

            return method.invoke(mBase, args);
        }
        return method.invoke(mBase, args);
    }


}
