package com.test.dynamic_proxy_hook.dynamic_proxy;

import com.test.dynamic_proxy_hook.static_proxy.Shopping;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by wzc on 2017/6/12.
 */

public class ShoppingHandler implements InvocationHandler {
    private Shopping mBase;

    public ShoppingHandler(Shopping base) {
        mBase = base;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("doShopping".equals(method.getName())){
            Long originalMoney = (Long) args[0];
            System.out.println("原来给的钱: " + originalMoney);
            System.out.println("贪污的钱: " + originalMoney*0.3);
            Object invoke = method.invoke(mBase, originalMoney);
            return invoke.toString();
        }
        return null;
    }
}
