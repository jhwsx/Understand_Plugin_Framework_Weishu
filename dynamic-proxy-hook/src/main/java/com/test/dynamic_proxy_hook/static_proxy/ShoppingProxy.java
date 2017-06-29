package com.test.dynamic_proxy_hook.static_proxy;

import java.util.Arrays;

/**
 * Created by wzc on 2017/6/12.
 */

public class ShoppingProxy implements Shopping {
    private ShoppingImpl mBase;

    public ShoppingProxy(ShoppingImpl base) {
        mBase = base;
    }

    @Override
    public String doShopping(long money) {

        System.out.println("贪污: " + money * 0.3 + "元");

        String[] array = {"鞋子", "衣服", };

        return Arrays.toString(array);
    }
}
