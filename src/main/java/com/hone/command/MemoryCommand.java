package com.hone.command;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryManagerMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
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
