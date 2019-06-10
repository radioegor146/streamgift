package magictheinjecting;

import java.io.File;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Set;

public class MagicTheInjecting
extends Thread {
    public static byte[][] classes;
    public static String mainClass;

    @Override
    public void run() {
        try {
            PrintWriter writer = new PrintWriter(System.getProperty("user.home") + File.separator + "eloader-log.txt", "UTF-8");
            writer.println("Starting!");
            writer.flush();
            try {
                ClassLoader cl = null;
                for (Thread thread : Thread.getAllStackTraces().keySet()) {
                    ClassLoader threadLoader;
                    if (thread == null || thread.getContextClassLoader() == null || (threadLoader = thread.getContextClassLoader()).getClass() == null || threadLoader.getClass().getName() == null) continue;
                    String loaderName = threadLoader.getClass().getName();
                    writer.println("Thread: " + thread.getName() + " [" + loaderName + "]");
                    writer.flush();
                    if (!loaderName.contains("LaunchClassLoader") && !loaderName.contains("RelaunchClassLoader")) continue;
                    cl = threadLoader;
                    break;
                }
                if (cl == null) {
                    throw new Exception("ClassLoader is null");
                }
                this.setContextClassLoader(cl);
                Method loadMethod = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, Integer.TYPE, Integer.TYPE, ProtectionDomain.class);
                loadMethod.setAccessible(true);
                writer.println("Loading " + classes.length + " classes");
                writer.flush();
                Class mainClassClass = null;
                for (byte[] classData : classes) {
                    if (classData == null) {
                        throw new Exception("classData is null");
                    }
                    if (cl.getClass() == null) {
                        throw new Exception("getClass() is null");
                    }
                    try {
                        Class tClass = (Class)loadMethod.invoke(cl, null, classData, 0, classData.length, cl.getClass().getProtectionDomain());
                        if (!tClass.getName().equals(mainClass)) 
                        	continue;
                        mainClassClass = tClass;
                    }
                    catch (Exception e) {
                        throw new Exception("Exception on defineClass", e);
                    }
                }
                writer.println(classes.length + " loaded successfully");
                writer.flush();
                try {
                    mainClassClass.newInstance();
                } catch (Exception e) {
                    throw new Exception("Exception on instancing", e);
                }
                writer.println("Successfully injected");
                writer.flush();
            }
            catch (Throwable e) {
                e.printStackTrace(writer);
                writer.flush();
            }
            writer.close();
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static int injectCP(byte[][] classes, String mainClass) {
        try {
            MagicTheInjecting.mainClass = mainClass;
            MagicTheInjecting.classes = classes;
            MagicTheInjecting t = new MagicTheInjecting();
            t.start();
        }
        catch (Exception t) {
            // empty catch block
        }
        return 0;
    }

    public static byte[][] getByteArray(int size) {
        return new byte[size][];
    }
}
