package com.hone.command;


import com.hone.enhancer.AsmEnhancer;
import com.hone.enhancer.MyAdvice;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;
import org.jd.core.v1.ClassFileToJavaSourceDecompiler;
import org.jd.core.v1.api.loader.Loader;
import org.jd.core.v1.api.loader.LoaderException;
import org.jd.core.v1.api.printer.Printer;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.Scanner;
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

    /**
     * 打印类的源码信息
     *
     * @param inst
     */
    public static void printClassSource(Instrumentation inst) {
        System.out.println("请输入类名：");
        Scanner scanner = new Scanner(System.in);
        String next = scanner.next();
        Class[] allLoadedClasses = inst.getAllLoadedClasses();
        for (Class allLoadedClass : allLoadedClasses) {
            if (allLoadedClass.getName().equals(next)) {
                ClassFileTransformer transformer = new ClassFileTransformer() {
                    @Override
                    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                        printSourceCode(classfileBuffer,className);
                        return ClassFileTransformer.super.transform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
                    }
                };

                //1.添加转换器
                inst.addTransformer(transformer, true);
                //2.手动触发转换器
                try {
                    inst.retransformClasses(allLoadedClass);
                } catch (UnmodifiableClassException e) {
                    throw new RuntimeException(e);
                } finally {
                    //3.删除转换器
                    inst.removeTransformer(transformer);
                }
            }
        }
    }

    public static void printSourceCode(byte[] bytes, String className) {
        Loader loader = new Loader() {
            @Override
            public byte[] load(String internalName) throws LoaderException {
                return bytes;
            }

            @Override
            public boolean canLoad(String internalName) {
                return true;
            }
        };

        Printer printer = new Printer() {
            protected static final String TAB = "  ";
            protected static final String NEWLINE = "\n";

            protected int indentationCount = 0;
            protected StringBuilder sb = new StringBuilder();

            @Override
            public String toString() {
                return sb.toString();
            }

            @Override
            public void start(int maxLineNumber, int majorVersion, int minorVersion) {
            }

            @Override
            public void end() {
                System.out.println(sb);
            }

            @Override
            public void printText(String text) {
                sb.append(text);
            }

            @Override
            public void printNumericConstant(String constant) {
                sb.append(constant);
            }

            @Override
            public void printStringConstant(String constant, String ownerInternalName) {
                sb.append(constant);
            }

            @Override
            public void printKeyword(String keyword) {
                sb.append(keyword);
            }

            @Override
            public void printDeclaration(int type, String internalTypeName, String name, String descriptor) {
                sb.append(name);
            }

            @Override
            public void printReference(int type, String internalTypeName, String name, String descriptor, String ownerInternalName) {
                sb.append(name);
            }

            @Override
            public void indent() {
                this.indentationCount++;
            }

            @Override
            public void unindent() {
                this.indentationCount--;
            }

            @Override
            public void startLine(int lineNumber) {
                for (int i = 0; i < indentationCount; i++) sb.append(TAB);
            }

            @Override
            public void endLine() {
                sb.append(NEWLINE);
            }

            @Override
            public void extraLine(int count) {
                while (count-- > 0) sb.append(NEWLINE);
            }

            @Override
            public void startMarker(int type) {
            }

            @Override
            public void endMarker(int type) {
            }
        };

        //通过jd-code打印
        ClassFileToJavaSourceDecompiler decompiler = new ClassFileToJavaSourceDecompiler();
        try {
            decompiler.decompile(loader, printer, className);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void enhanceClass(Instrumentation inst){
        System.out.println("请输入类名：");
        Scanner scanner = new Scanner(System.in);
        String next = scanner.next();
        Class[] allLoadedClasses = inst.getAllLoadedClasses();
        for (Class allLoadedClass : allLoadedClasses) {
            if (allLoadedClass.getName().equals(next)) {
                ClassFileTransformer transformer = new ClassFileTransformer() {
                    @Override
                    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                        //通过asm对类型进行增强,返回字节码
                        return AsmEnhancer.enhancerClass(classfileBuffer);
                    }
                };

                //1.添加转换器
                inst.addTransformer(transformer, true);
                //2.手动触发转换器
                try {
                    inst.retransformClasses(allLoadedClass);
                } catch (UnmodifiableClassException e) {
                    throw new RuntimeException(e);
                } finally {
                    //3.删除转换器
                    inst.removeTransformer(transformer);
                }
            }
        }
    }

    public static void enhanceClassByteBuddy(Instrumentation inst){
        System.out.println("请输入类名：");
        Scanner scanner = new Scanner(System.in);
        String next = scanner.next();
        Class[] allLoadedClasses = inst.getAllLoadedClasses();
        for (Class allLoadedClass : allLoadedClasses) {
            if (allLoadedClass.getName().equals(next)) {
                new AgentBuilder.Default()
                        .disableClassFormatChanges()
                        .with(AgentBuilder.RedefinitionStrategy.REDEFINITION)
                        .with(new AgentBuilder.Listener.WithTransformationsOnly(AgentBuilder.Listener.StreamWriting
                                .toSystemOut()))
                        .type(ElementMatchers.named(next))
                        .transform((builder, typeDescription, classLoader, javaModule, protectionDomain) ->
                                builder.visit(Advice.to(MyAdvice.class).on(ElementMatchers.any())))
                        .installOn(inst);
            }
        }
    }
}
