package com.hone.enhancer;

import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.io.File;
import java.io.IOException;

import static org.objectweb.asm.Opcodes.ASM7;
import static org.objectweb.asm.Opcodes.ICONST_0;

/**
 * @Author H-one
 * @Date 2024/5/29 10:27
 * @Version 1.0
 */
public class AsmDemo {
    public static void main(String[] args) throws IOException {
        //1.从本地读取一个字节码文件
        byte[] bytes = FileUtils.readFileToByteArray(new File("D:\\devtools\\workspace\\javaagent\\target\\classes\\com\\hone\\AttachMain.class"));
        //2.通过ASM修改字节码文件
        //将二进制数据转换成可以解析的内容
        ClassReader classReader = new ClassReader(bytes);
        ClassWriter classWriter = new ClassWriter(0);
        ClassVisitor classVisitor = new ClassVisitor(ASM7,classWriter){
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
                //返回自定义的methodvisit
                MethodVisitor result = new MethodVisitor(this.api,methodVisitor){
                    @Override
                    public void visitCode() {
                        //插入一行字节码指令 ICONST_0
                        visitInsn(ICONST_0);
                    }
                };
                return result;
            }
        };
        classReader.accept(classVisitor,0);
        //3.将修改完的字节码信息写入文件中，进行替换
        FileUtils.writeByteArrayToFile(new File("D:\\devtools\\workspace\\javaagent\\target\\classes\\com\\hone\\AttachMain.class"),classWriter.toByteArray());
    }
}
