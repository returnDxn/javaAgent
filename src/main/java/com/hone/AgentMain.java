package com.hone;

import com.hone.annotation.AgentParam;
import com.hone.command.ClassCommand;
import com.hone.command.MemoryCommand;
import com.hone.command.ThreadCommand;
import com.hone.enhancer.MyAdvice;
import com.hone.enhancer.TimingAdvice;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;
import java.util.Scanner;

/**
 * @Author H-one
 * @Date 2024/5/20 13:54
 * @Version 1.0
 */
public class AgentMain {
    public static void premain(String agentArgs, Instrumentation inst) {
        new AgentBuilder.Default()
                //禁止bytebuddy处理时修改类名
                .disableClassFormatChanges()
                //处理时使用retransform增强
                .with(AgentBuilder.RedefinitionStrategy.REDEFINITION)
                //打印出错误日志
                .with(new AgentBuilder.Listener.WithTransformationsOnly(AgentBuilder.Listener.StreamWriting
                        .toSystemOut()))
                //匹配哪些类
                .type(ElementMatchers.isAnnotatedWith(ElementMatchers.named("org.springframework.web.bind.annotation.RestController"))
                        .or(ElementMatchers.named("org.springframework.web.bind.annotation.Controller")))
                //增强，使用MyAdvice通知，对所有方法都进行增强
                .transform((builder, typeDescription, classLoader, javaModule, protectionDomain) ->
                        builder.visit(Advice.withCustomMapping()
                                .bind(AgentParam.class,agentArgs)
                                .to(TimingAdvice.class).on(ElementMatchers.any())))
                .installOn(inst);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("菜单:\n" +
                    "1、查看内存使用情况\n"
                    + "2、生成堆内存快照\n"
                    + "3、打印栈信息\n"
                    + "4、打印类加载器\n"
                    + "5、打印类源码\n"
                    + "6、打印方法的参数和耗时\n"
                    + "7、退出\n"
            );
            String input = scanner.next();
            switch (input) {
                case "1": {
                    MemoryCommand.printMemory();
                    break;
                }
                case "2": {
                    MemoryCommand.heapDump();
                    break;
                }
                case "3": {
                    ThreadCommand.printThread();
                    break;
                }
                case "4": {
                    ClassCommand.printClassLoader(inst);
                    break;
                }
                case "5": {
                    ClassCommand.printClassSource(inst);
                    break;
                }
                case "6": {
                    ClassCommand.enhanceClassByteBuddy(inst);
                    break;
                }
                case "7": {
                    return;
                }
            }
        }
    }
}
