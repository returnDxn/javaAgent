package com.hone;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 * @Author H-one
 * @Date 2024/5/20 17:27
 * @Version 1.0
 */
public class AttachMain {
    public static void main(String[] args) throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
        //获取所有进程id
        Runtime runtime = Runtime.getRuntime();
        Process jps = runtime.exec("jps");
        InputStream inputStream = jps.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (bufferedReader != null){
                bufferedReader.close();
            }
        }
        //输入
        Scanner scanner = new Scanner(System.in);
        String pid = scanner.next();
        VirtualMachine attach = VirtualMachine.attach(pid);
        attach.loadAgent("javaagent-1.0-SNAPSHOT-jar-with-dependencies.jar");
    }
}
