package com.hone.command;

import java.lang.instrument.Instrumentation;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * @Author H-one
 * @Date 2024/5/21 17:19
 * @Version 1.0
 */
public class ClassCommand {
    public static void printClassLoader(Instrumentation inst) {
        HashSet<ClassLoader> classLoaders = new HashSet<>();
        //获取所有类
        Class[] allLoadedClasses = inst.getAllLoadedClasses();
        for (Class allLoadedClass : allLoadedClasses) {
            ClassLoader classLoader = allLoadedClass.getClassLoader();
            classLoaders.add(classLoader);
        }
        //打印类加载器
        String str = classLoaders.stream().map(x -> {
            if (x == null) {
                return "BootStrapClassLoader";
            } else {
                return x.getName();
            }
        }).filter(x -> x != null).distinct().sorted(String::compareTo).collect(Collectors.joining(","));
        System.out.println(str);
    }
}
