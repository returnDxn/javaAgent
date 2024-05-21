package com.hone.command;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

/**
 * @Author H-one
 * @Date 2024/5/21 17:04
 * @Version 1.0
 */
public class ThreadCommand {
    public static void printThread(){
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(threadMXBean.isObjectMonitorUsageSupported(),
                threadMXBean.isSynchronizerUsageSupported());
        for (ThreadInfo threadInfo : threadInfos) {
            StringBuilder sb = new StringBuilder();
            sb.append("名字：")
                    .append(threadInfo.getThreadName())
                    .append(" threadId:")
                    .append(threadInfo.getThreadId())
                    .append(" threadState:")
                    .append(threadInfo.getThreadState());
            System.out.println(sb);
            //打印栈信息
            StackTraceElement[] stackTrace = threadInfo.getStackTrace();
            for (StackTraceElement stackTraceElement : stackTrace) {
                System.out.println(stackTraceElement);
            }
        }
    }
}
