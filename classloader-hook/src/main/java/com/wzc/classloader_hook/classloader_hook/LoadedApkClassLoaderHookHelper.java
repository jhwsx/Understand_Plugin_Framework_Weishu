package com.wzc.classloader_hook.classloader_hook;

import android.content.pm.ApplicationInfo;

import com.wzc.classloader_hook.Utils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wzc on 2017/7/11.
 *
 */

public class LoadedApkClassLoaderHookHelper {

    public static Map<String, Object> sLoadedApk = new HashMap<String, Object>();

    public static void hookLoadedApkInActivityThread(File packageFile) {
        try {

            // 获取ActivityThread类的Class文件
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            // 获取currentActivityThread方法的Method对象
            Method currentActivityThreadMethod = activityThreadClass.getMethod("currentActivityThread");
            // 获取currentActivityThread方法的返回值sCurrentActivityThread对象
            Object sCurrentActivityThreadObj = currentActivityThreadMethod.invoke(null);

            // 获取mPackages对象
            // 获取mPackage字段的Field对象
            Field mPackagesField = activityThreadClass.getDeclaredField("mPackages");
            mPackagesField.setAccessible(true);
            // 获取mPackage字段的值,强转成一个Map类型的,这里缓存了dex包的信息
            // 源码信息: final ArrayMap<String, WeakReference<LoadedApk>> mPackages
            // = new ArrayMap<String, WeakReference<LoadedApk>>();
            // 思路:把插件的包名和插件的信息(LoadedApk对象)放进这个map里面,这样系统在查找缓存时,会
            // 直接命中缓存,进而使用我们添加进去的LoadedApk的ClassLoader来加载这个特定的Activity类！
            // 这样我们就能接管我们自己插件类的加载过程了！
            // 获取到了这个map后,插件的包名是有的,所以现在的任务是创建一个插件信息的LoadedApk对象
            Map mPackagesObj = (Map) mPackagesField.get(sCurrentActivityThreadObj);

            Object loadedApkObj = getLoadedApk(packageFile, activityThreadClass, sCurrentActivityThreadObj);

            // 构造插件的ClassLoader
            String odexPath = Utils.getPluginOptDexDir(getApplicationInfo(packageFile).packageName).getPath();
            String libPath = Utils.getPluginLibDir(getApplicationInfo(packageFile).packageName).getPath();
            CustomClassLoader classLoader = new CustomClassLoader(packageFile.getPath(), odexPath, libPath, ClassLoader.getSystemClassLoader());
            // 把loadedApk的mClassLoader替换为插件的ClassLoader
            Class<?> loadedApkClass = Class.forName("android.app.LoadedApk");
            Field mClassLoaderField = loadedApkClass.getDeclaredField("mClassLoader");
            mClassLoaderField.setAccessible(true);
            mClassLoaderField.set(loadedApkObj,classLoader);

            // 由于是弱引用, 因此我们必须在某个地方存一份, 不然容易被GC; 那么就前功尽弃了.
            sLoadedApk.put(getApplicationInfo(packageFile).packageName, loadedApkObj);

            WeakReference weakReference = new WeakReference(loadedApkObj);
            mPackagesObj.put(getApplicationInfo(packageFile).packageName, weakReference);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Object getLoadedApk(File packageFile, Class<?> activityThreadClass, Object sCurrentActivityThreadObj) throws Exception {
        // 创建一个插件信息的LoadedApk对象

        // 使用ActivityThread类的 public final LoadedApk getPackageInfoNoCheck(ApplicationInfo ai,CompatibilityInfo compatInfo)
        // 方法来获取一个LoadedApk对象,为什么选用这个方法呢,自己清楚不清楚?
        // 获取getPackageInfoNoCheck方法的Method对象
        Class<?> compatibilityInfoClass = Class.forName("android.content.res.CompatibilityInfo");
        Method getPackageInfoNoCheckMethod = activityThreadClass.getMethod("getPackageInfoNoCheck", ApplicationInfo.class,
                compatibilityInfoClass);
        // 构造反射getPackageInfoNoCheck()方法需要的两个参数
        // 构造CompatibilityInfo compatInfo参数
        Field default_compatibility_infoField = compatibilityInfoClass.getField("DEFAULT_COMPATIBILITY_INFO");
        Object compatibilityInfoObj = default_compatibility_infoField.get(null);

        // 构造ApplicationInfo ai参数
        // 通过反射generateApplicationInfo()方法获取ai参数
        // 源码信息:PackageParser类中的public static ApplicationInfo generateApplicationInfo(Package p, int flags, PackageUserState state)
        // 使用PackageParser类,需要考虑版本兼容性,因为这个类随版本会有变化
        ApplicationInfo applicationInfo = getApplicationInfo(packageFile);

        // 反射调用 getPackageInfoNoCheck()方法,获取LoadedApk对象
        Object loadedApkObj = getPackageInfoNoCheckMethod.invoke(sCurrentActivityThreadObj, applicationInfo, compatibilityInfoObj);
        return loadedApkObj;
    }

    private static ApplicationInfo getApplicationInfo(File packageFile) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        // 获取generateApplicationInfo方法的Method对象
        // 获取PackageParser类的Class文件
        Class<?> packageParserClass = Class.forName("android.content.pm.PackageParser");
        // 获取generateApplicationInfo方法的Method对象
        Method generateApplicationInfoMethod
                = packageParserClass.getMethod("generateApplicationInfo",
                Class.forName("android.content.pm.PackageParser$Package"),
                int.class,
                Class.forName("android.content.pm.PackageUserState"));
        // 构造Package p, int flags, PackageUserState state这三个参数
        // 获取Package的实例
        // 通过反射PackageParser类的public Package parsePackage(File packageFile, int flags)方法
        Method parsePackageMethod = packageParserClass.getMethod("parsePackage", File.class, int.class);
        // 创建一个PackageParser对象使用
        Object packageParserObj = packageParserClass.newInstance();
        // 反射调用parsePackage()方法
        Object packageObj = parsePackageMethod.invoke(packageParserObj, packageFile, 0);
        // 获取PackageUserState的实例,使用默认的构造方法构造出一个实例
        Object packageUserStateObj = Class.forName("android.content.pm.PackageUserState").newInstance();
        // 反射调用generateApplicationInfo()方法,获取ApplicationInfo对象
        ApplicationInfo applicationInfo
                = (ApplicationInfo) generateApplicationInfoMethod.invoke(null, packageObj, 0, packageUserStateObj);// 第二个参数取0表示解析全部信息
        // 在返回之前我们需要做一点小小的修改：使用系统系统的这个方法解析得到的ApplicationInfo对象中并没有apk文件本身的信息，所以我们把解析的apk文件的路径设置一下（ClassLoader依赖dex文件以及apk的路径）
        // TODO 这里是为什么?怎么知道了没有这些信息,没有了又能怎么样呢?
        applicationInfo.sourceDir = packageFile.getPath();
        applicationInfo.publicSourceDir = packageFile.getPath();
        return applicationInfo;
    }
}
