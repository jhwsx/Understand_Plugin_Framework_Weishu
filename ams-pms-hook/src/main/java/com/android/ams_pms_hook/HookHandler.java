package com.android.ams_pms_hook;

import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Created by wzc on 2017/7/4.
 */

public class HookHandler implements InvocationHandler {
    private Object mBase;

    public HookHandler(Object base) {
        mBase = base;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Log.d("HookHandler", "method: " + method.getName() + ", args: " + Arrays.toString(args));
        return method.invoke(mBase, args);
    }
}
