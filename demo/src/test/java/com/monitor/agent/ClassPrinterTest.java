package com.monitor.agent;

import org.junit.Test;
import org.objectweb.asm.ClassReader;

public class ClassPrinterTest {

    @Test
    public void testClassPrinter() throws Exception {
        ClassPrinter classPrinter = new ClassPrinter();
        ClassReader classReader = new ClassReader("java.lang.Runnable");
        classReader.accept(classPrinter, 0);
    }

}