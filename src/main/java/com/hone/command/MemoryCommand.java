package com.hone.command;

import java.lang.management.*;
import java.util.List;

/**
 * @Author H-one
 * @Date 2024/5/21 14:32
 * @Version 1.0
 */
public class MemoryCommand {
    public static void printMemory(){
        List<MemoryPoolMXBean> memoryPoolMXBeans = ManagementFactory.getMemoryPoolMXBeans();
        //堆内存
        System.out.println("堆内存:");
        getMemoryInfo(memoryPoolMXBeans, MemoryType.HEAP);
        //非堆内存
        System.out.println("非堆内存:");
        getMemoryInfo(memoryPoolMXBeans, MemoryType.NON_HEAP);
        //打印nio相关内容
        System.out.println("nio相关内容:");
        printNioMemory();

    }
    private static void printNioMemory() {
        try {
            Class clazz = Class.forName("java.lang.management.BufferPoolMXBean");
            List<BufferPoolMXBean> platformMXBeans = ManagementFactory.getPlatformMXBeans(clazz);
            for (BufferPoolMXBean x : platformMXBeans) {
                StringBuilder sb = new StringBuilder();
                String m = sb.append("名字：")
                        .append(x.getName())
                        .append(" memoryUsed：")
                        .append(x.getMemoryUsed() / 1024 / 1024)
                        .append("m totalCapacity：")
                        .append(x.getTotalCapacity() / 1024 / 1024)
                        .append("m").toString();
                System.out.println(m);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static void getMemoryInfo(List<MemoryPoolMXBean> memoryPoolMXBeans, MemoryType heap) {
        memoryPoolMXBeans.stream().filter(x -> x.getType().equals(heap))
                .forEach(x -> {
                    StringBuilder sb = new StringBuilder();
                    String m = sb.append("名字：")
                            .append(x.getName())
                            .append(" 内存使用量：")
                            .append(x.getUsage().getUsed() / 1024 / 1024)
                            .append("m total：")
                            .append(x.getUsage().getCommitted() / 1024 / 1024)
                            .append("m 最大量：")
                            .append(x.getUsage().getMax() / 1024 / 1024)
                            .append("m").toString();
                    System.out.println(m);
                });
    }
}
