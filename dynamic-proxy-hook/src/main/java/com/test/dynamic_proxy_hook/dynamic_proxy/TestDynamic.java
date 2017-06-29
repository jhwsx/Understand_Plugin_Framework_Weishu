package com.test.dynamic_proxy_hook.dynamic_proxy;


import com.test.dynamic_proxy_hook.static_proxy.Shopping;
import com.test.dynamic_proxy_hook.static_proxy.ShoppingImpl;

import java.lang.reflect.Proxy;

/**
 * Created by wzc on 2017/6/12.
 */

public class TestDynamic {
    public static void main(String[] args){
        ShoppingImpl shopping = new ShoppingImpl();
        String result = shopping.doShopping(100);
        System.out.println(result);

        Shopping o = (Shopping) Proxy.newProxyInstance(Shopping.class.getClassLoader(),
                shopping.getClass().getInterfaces(),
                new ShoppingHandler(shopping));
        System.out.println(o.doShopping(100));
    }
}
