package com.wzc.classloader_hook.ams_hook;

import android.content.ComponentName;
import android.content.Intent;

import com.wzc.classloader_hook.StubActivity;
import com.wzc.classloader_hook.UPFApplication;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by wzc on 2017/7/5.
 *
 */

public class IActivityManagerHandler implements InvocationHandler {
    public static final String TAG = "IActivityManagerHandler";
    private Object mBase;
    public IActivityManagerHandler(Object base) {
        mBase = base;
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
            // 只有是调取插件apk的入口时才进行替换
            if (rawIntent.getBooleanExtra("target", false)) {
                // 构造替换的Intent
                Intent stubIntent = new Intent();
                String stubPackage = UPFApplication.getContext().getPackageName();
                stubIntent.setComponent(new ComponentName(stubPackage, StubActivity.class.getName()));
                stubIntent.putExtra("target", true);
                stubIntent.putExtra(AMSHookHelper.EXTRA_TARGET_INTENT, rawIntent); // 保存真正要启动的intent

                // 替换intent
                args[intentArgsIndex] = stubIntent;
            }

            return method.invoke(mBase, args);
        }
        return method.invoke(mBase, args);
    }


}
