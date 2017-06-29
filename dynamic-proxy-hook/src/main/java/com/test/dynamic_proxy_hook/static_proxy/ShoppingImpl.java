package com.test.dynamic_proxy_hook.static_proxy;

import java.util.Arrays;

/**
 * Created by wzc on 2017/6/12.
 */

public class ShoppingImpl implements Shopping {
    @Override
    public String doShopping(long money) {
        System.out.println("买了:" + money + "元");
        String[] array = {"鞋子", "衣服", "帽子"};
        return Arrays.toString(array);
    }
}
