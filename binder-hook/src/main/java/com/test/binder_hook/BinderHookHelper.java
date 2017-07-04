package com.test.binder_hook;

import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * Created by wzc on 2017/7/3.
 */

public class BinderHookHelper {
    public static void hookClipboardService() throws Exception {
        final String CLIPBOARD_SERVICE = "clipboard";
        // 获取ServiceManager类的Class文件
        Class<?> serviceManagerClass = Class.forName("android.os.ServiceManager");
        // 获取getService的Method对象
        Method getServiceMethod = serviceManagerClass.getMethod("getService", String.class);
        // 获取ServiceManager里管理的clipboard binder对象 IBinder b = ServiceManager.getService("service_name"); // 获取原始的IBinder对象
        IBinder binderProxy = (IBinder) getServiceMethod.invoke(null, CLIPBOARD_SERVICE);
        Log.d("BinderHookHelper", "binderProxy:" + binderProxy);
        // Hook 掉这个Binder代理对象的 queryLocalInterface 方法
        // 然后在 queryLocalInterface 返回一个IInterface对象, hook掉我们感兴趣的方法即可.
        IBinder hookedBinder = (IBinder) Proxy.newProxyInstance(serviceManagerClass.getClassLoader(),
                new Class<?>[] { IBinder.class },
                new BinderProxyHookHandler(binderProxy));
        Log.d("BinderHookHelper", "hookedBinder:" + hookedBinder); // hookedBinder:android.os.BinderProxy@6d77622
        // 把这个hook过的Binder代理对象放进ServiceManager的cache里面
        // 以后查询的时候 会优先查询缓存里面的Binder, 这样就会使用被我们修改过的Binder了
        Field cacheField = serviceManagerClass.getDeclaredField("sCache");
        cacheField.setAccessible(true);
        Map<String, IBinder> cache = (Map) cacheField.get(null);
        cache.put(CLIPBOARD_SERVICE, hookedBinder);
    }
}
