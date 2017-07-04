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
    // 原始的Service对象,也就是RealObject,需要被代理的类
    private Object mBase;
    public BinderHookHandler(IBinder base,Class<?> stubClass){
        // IXXInterface in = IXXInterface.Stub.asInterface(b); // 转换为Service接口
        try {
            Method asInterfaceMethod = stubClass.getMethod("asInterface", IBinder.class); // 获取asInterface方法
            mBase =  asInterfaceMethod.invoke(null, base);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("hooked failed");
        }
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 把剪切板的内容替换为"you are hooked"
        if ("getPrimaryClip".equals(method.getName())) {
            Log.d(TAG, "hook getPrimaryClip");
            return ClipData.newPlainText(null, "you are hooked.");
        }
        // 欺骗系统,使之认为剪切版上一直有内容
        if ("hasPrimaryClip".equals(method.getName())){
            return true;
        }
        return method.invoke(mBase, args);
    }
}
