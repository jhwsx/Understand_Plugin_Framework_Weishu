package com.test.dynamic_proxy_hook.static_proxy;

/**
 * Created by wzc on 2017/6/12.
 */

public class Test {
    public static void main(String[] args){
        ShoppingImpl shopping = new ShoppingImpl();
        String result = shopping.doShopping(100);
        System.out.println(result);

        ShoppingProxy shoppingProxy = new ShoppingProxy(shopping);
        String result2 = shoppingProxy.doShopping(100);
        System.out.println(result2);
    }
}
