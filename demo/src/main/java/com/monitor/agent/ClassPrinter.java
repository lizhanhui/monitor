package com.monitor.agent;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class ClassPrinter extends ClassVisitor {
    /**
     * Constructs a new {@link ClassVisitor}.
     */
    public ClassPrinter() {
        super(Opcodes.ASM5);
    }

    /**
     * Visits the header of the class.
     *
     * @param version    the class version.
     * @param access     the class's access flags (see {@link Opcodes}). This parameter also indicates if the class is
     *                   deprecated.
     * @param name       the internal name of the class (see {@link Type#getInternalName() getInternalName}).
     * @param signature  the signature of this class. May be <tt>null</tt> if the class is not a generic one, and does
     *                   not extend or implement generic classes or interfaces.
     * @param superName  the internal of name of the super class (see {@link Type#getInternalName() getInternalName}).
     *                   For interfaces, the super class is {@link Object}. May be <tt>null</tt>, but only for the
     *                   {@link Object} class.
     * @param interfaces the internal names of the class's interfaces (see {@link Type#getInternalName()
     *                   getInternalName}). May be
     */
    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);

        StringBuilder stringBuilder = new StringBuilder(1024);
        stringBuilder.append(name).append(" extends ").append(superName);
        if (null != interfaces && interfaces.length > 0) {
            stringBuilder.append(" implements ");
            for (String item : interfaces) {
                stringBuilder.append(item).append(", ");
            }
            stringBuilder.delete(stringBuilder.length() -2, stringBuilder.length());
        }
        stringBuilder.append(" {");

        System.out.println(stringBuilder.toString().replaceAll("/", "."));
    }

    /**
     * Visits a method of the class. This method <i>must</i> return a new {@link MethodVisitor} instance (or
     * <tt>null</tt>) each time it is called, i.e., it should not return a previously returned visitor.
     *
     * @param access     the method's access flags (see {@link Opcodes}). This parameter also indicates if the method is
     *                   synthetic and/or deprecated.
     * @param name       the method's name.
     * @param desc       the method's descriptor (see {@link Type Type}).
     * @param signature  the method's signature. May be <tt>null</tt> if the method parameters, return type and
     *                   exceptions do not use generic types.
     * @param exceptions the internal names of the method's exception classes (see {@link Type#getInternalName()
     *                   getInternalName}). May be <tt>null</tt>.
     * @return an object to visit the byte code of the method, or <tt>null</tt> if this class visitor is not interested
     * in visiting the code of this method.
     */
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

        String returnType = null;

        if (desc.endsWith("V")) {
            returnType = "void ";
        }

        System.out.println("   " + returnType + name + desc.substring(0,  desc.indexOf(")") + 1) + ";");
        return super.visitMethod(access, name, desc, signature, exceptions);
    }

    /**
     * Visits a field of the class.
     *
     * @param access    the field's access flags (see {@link Opcodes}). This parameter also indicates if the field is
     *                  synthetic and/or deprecated.
     * @param name      the field's name.
     * @param desc      the field's descriptor (see {@link Type Type}).
     * @param signature the field's signature. May be <tt>null</tt> if the field's type does not use generic types.
     * @param value     the field's initial value. This parameter, which may be <tt>null</tt> if the field does not have
     *                  an initial value, must be an {@link Integer}, a {@link Float}, a {@link Long}, a {@link Double}
     *                  or a {@link String} (for <tt>int</tt>, <tt>float</tt>, <tt>long</tt> or <tt>String</tt> fields
     *                  respectively). <i>This parameter is only used for static fields</i>. Its value is ignored for
     *                  non static fields, which must be initialized through bytecode instructions in constructors or
     *                  methods.
     * @return a visitor to visit field annotations and attributes, or <tt>null</tt> if this class visitor is not
     * interested in visiting these annotations and attributes.
     */
    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        System.out.println("   " + desc + " " + name);
        return super.visitField(access, name, desc, signature, value);
    }

    /**
     * Visits the end of the class. This method, which is the last one to be called, is used to inform the visitor that
     * all the fields and methods of the class have been visited.
     */
    @Override
    public void visitEnd() {
        System.out.println("}");
        super.visitEnd();
    }
}
