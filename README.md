
### 补丁：frameworks/base/core/java/android/app/ActivityThread.java

插件位置：
```
/data/local/tmp/.sskpinehook
```

so文件位置：
```
/data/local/tmp/libpine-enhances.so
/data/local/tmp/libpine.so
```


```

diff --git a/frameworks/base/core/java/android/app/ActivityThread.java b/frameworks/base/core/java/android/app/ActivityThread.java
index 435eb933a6..b617909703 100644
--- a/frameworks/base/core/java/android/app/ActivityThread.java
+++ b/frameworks/base/core/java/android/app/ActivityThread.java
@@ -237,6 +237,10 @@ import java.util.concurrent.Executor;
 import java.util.concurrent.atomic.AtomicBoolean;
 import java.util.function.Consumer;

+import java.io.FileReader;
+import java.io.BufferedReader;
+
+
 /**
  * This manages the execution of the main thread in an
  * application process, scheduling and executing activities,
@@ -557,6 +561,49 @@ public final class ActivityThread extends ClientTransactionHandler

     Bundle mCoreSettings = null;

+
+    private void loadPineHook(AppBindData data) {
+        try {
+            File apkPath = new File("/data/local/tmp/", ".sskpinehook");
+            if (!apkPath.exists()) {
+                Log.i("SSKHOOK", "this dex file does not exist: " + apkPath.getAbsolutePath());
+                return;
+            }
+
+            ClassLoader classLoader = data.info.getClassLoader();
+            DexClassLoader dexClassLoader = new DexClassLoader(apkPath.getAbsolutePath(), data.appInfo.dataDir, "/data/local/tmp", getSystemContext().getClassLoader());
+            Class<?> hookBridgeClass = dexClassLoader.loadClass("com.ssk.pinehook.SSKPineHook");
+            if (hookBridgeClass != null) {
+                hookBridgeClass.getDeclaredMethod("init", ClassLoader.class).invoke(null, classLoader);
+                Log.i("SSKHOOK", "Pine 加载扩展dex成功");
+            } else {
+                Log.i("SSKHOOK", "Pine 加载扩展dex失败");
+            }
+
+
+        } catch (Exception e) {
+            Log.i("SSKHOOK", "Pine load except", e);
+        }
+    }
+

     private void loadDex(AppBindData data) {
         try {
             File apkPath = new File("/data/local/tmp/", data.appInfo.packageName + "_hk");
@@ -6756,7 +6861,10 @@ public final class ActivityThread extends ClientTransactionHandler
         final StrictMode.ThreadPolicy writesAllowedPolicy = StrictMode.getThreadPolicy();
         try {
             // add by ssk
+            loadPineHook(data);

             // If the app is being launched for full backup or restore, bring it up in
             // a restricted environment with the base application class.


```
