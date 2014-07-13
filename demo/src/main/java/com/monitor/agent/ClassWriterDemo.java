package com.monitor.agent;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ClassWriterDemo {

    public static void main(String[] args) {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES|ClassWriter.COMPUTE_MAXS);

        classWriter.visit(Opcodes.V1_7, Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT + Opcodes.ACC_INTERFACE, "Test",
                null, "java/lang/Object", new String[] {"java/lang/Runnable"});

        classWriter.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC, "VERSION", "I",
                null, 1).visitEnd();

        classWriter.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT, "method", "()V", null, null).visitEnd();

        classWriter.visitEnd();

        byte[] classData = classWriter.toByteArray();

        MyClassLoader classLoader = new MyClassLoader();
        Class testClass = classLoader.defineClass("Test", classData);

        Field[] fields = testClass.getFields();
        try {
            for (Field field : fields) {
                System.out.println(field.getName());
                System.out.println(field.get(null));
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        Method[] methods = testClass.getDeclaredMethods();
        for (Method method : methods) {
            System.out.println(method.getName());
        }

    }


    static class MyClassLoader extends ClassLoader {

        public Class defineClass(String name, byte[] data) {
            return defineClass(name, data, 0, data.length);
        }

    }

}
