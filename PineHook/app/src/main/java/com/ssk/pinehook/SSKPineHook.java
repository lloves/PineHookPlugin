package com.ssk.pinehook;

import android.os.Bundle;
import android.util.Log;

import java.lang.reflect.Method;

import de.robv.android.xposed.XposedHelpers;
import top.canyie.pine.Pine;
import top.canyie.pine.PineConfig;
import top.canyie.pine.callback.MethodHook;
import top.canyie.pine.enhances.PineEnhances;

public class SSKPineHook {
    private static String TAG = "SSKHOOK";

    private static ClassLoader mClassLoader;

    public static void init(ClassLoader classLoader) {
        mClassLoader = classLoader;

        PineConfig.debug = true;
        PineConfig.debuggable = BuildConfig.DEBUG;
        Pine.ensureInitialized();
        PineEnhances.enableDelayHook();

        HOOK();

    }

    public static void HOOK() {
        Log.d(TAG, "Enter hook main function.");
        Class mainActivity = XposedHelpers.findClass("com.ssk.pine.hook.demo.MainActivity", mClassLoader);
        try {
            if (mainActivity != null) {
                Method mid = mainActivity.getDeclaredMethod("onCreate", Bundle.class);
                if (mid != null) {
                    MethodHook.Unhook unhook = Pine.hook(mid, new MethodHook() {
                        @Override
                        public void beforeCall(Pine.CallFrame callFrame) throws Throwable {
                            super.beforeCall(callFrame);
                            // int a = (int) callFrame.args[0];
                            // int b = (int) callFrame.args[1];
                            Log.d(TAG, "hook MainActivity onCreate method");
                        }

                        @Override
                        public void afterCall(Pine.CallFrame callFrame) throws Throwable {
                            super.afterCall(callFrame);
                            callFrame.setResult(1000);
                        }
                    });
                    Log.d(TAG, "hook MainActivity success");

                } else {
                    Log.d(TAG, "Find hook main function null.");
                }
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
