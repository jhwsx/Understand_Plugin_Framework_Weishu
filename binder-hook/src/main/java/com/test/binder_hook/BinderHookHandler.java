package com.test.binder_hook;

import android.content.ClipData;
import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by wzc on 2017/7/3.
 * 用代理的方式伪造一个剪切版服务对象
 * 动态代理的方式Hook掉了hasPrimaryClip()，getPrimaryClip()
 */

public class BinderHookHandler implements InvocationHandler {
    private static final String TAG = "BinderHookHandler";
    // 原始的Service对象
    private Object mBase;

    public BinderHookHandler(IBinder base) {
        // IXXInterface in = IXXInterface.Stub.asInterface(b); // 转换为Service接口
        try {
            // 获取IClipboard$Stub的Class文件
            Class<?> stubClass = Class.forName("android.content.IClipboard$Stub");
            Method asInterfaceMethod = stubClass.getMethod("asInterface", IBinder.class); // 获取asInterface方法
            mBase = asInterfaceMethod.invoke(null, base); // TODO 为什么会获取IClipboard$Stub$Proxy对象呢?原因是第一次并没有缓存吗?
            Log.d(TAG, "mBase:" + mBase + ", base:"+base); // mBase:android.content.IClipboard$Stub$Proxy@f0c5e3a, base:android.os.BinderProxy@6d77622
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("hooked failed");
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//        Log.d(TAG, "method:" + method);
        // TODO 这里这样处理方法,为什么不会影响IPC呢?
        // 把剪切板的内容替换为"you are hooked"
        if ("getPrimaryClip".equals(method.getName())) {
            Log.d(TAG, "hook getPrimaryClip");
            return ClipData.newPlainText(null, "you are hooked.");
        }
        // 欺骗系统,使之认为剪切版上一直有内容
        if ("hasPrimaryClip".equals(method.getName())) {
            Log.d(TAG, "proxy:" + proxy); // proxy:android.content.IClipboard$Stub$Proxy@f0c5e3a
            return true;
        }
        return method.invoke(mBase, args);
    }
}
