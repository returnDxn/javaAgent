package com.hone;

import com.hone.command.MemoryCommand;

import java.lang.instrument.Instrumentation;

/**
 * @Author H-one
 * @Date 2024/5/20 13:54
 * @Version 1.0
 */
public class AgentMain {
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("premain执行了。。。。");
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
//        MemoryCommand.printMemory();
        MemoryCommand.heapDump();
    }
}
