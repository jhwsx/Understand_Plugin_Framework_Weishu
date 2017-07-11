package com.wzc.classloader_hook.classloader_hook;

import dalvik.system.DexClassLoader;

/**
 * Created by wzc on 2017/7/11.
 * 直接继承了DexClassLoader,重新创建一个类是为了更有区分度；以后也可以通过修改这个类实现对于类加载的控制
 */

public class CustomClassLoader extends DexClassLoader {
    public CustomClassLoader(String dexPath, String optimizedDirectory, String librarySearchPath, ClassLoader parent) {
        super(dexPath, optimizedDirectory, librarySearchPath, parent);
    }
}
