package com.monitor.agent.transformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.List;

public class TestTransformer implements ClassFileTransformer {

    private static final List<String> CLASS_TO_TRANSFORM = Arrays.asList("Test");


    /**
     * The implementation of this method may transform the supplied class file and return a new replacement class file.
     *
     * <P> There are two kinds of transformers, determined by the <code>canRetransform</code> parameter of {@link
     * Instrumentation#addTransformer(ClassFileTransformer, boolean)}: <ul> <li><i>retransformation capable</i>
     * transformers that were added with <code>canRetransform</code> as true </li> <li><i>retransformation incapable</i>
     * transformers that were added with <code>canRetransform</code> as false or where added with {@link
     * Instrumentation#addTransformer(ClassFileTransformer)} </li> </ul>
     *
     * <P> Once a transformer has been registered with {@link Instrumentation#addTransformer(ClassFileTransformer, boolean)
     * addTransformer}, the transformer will be called for every new class definition and every class redefinition.
     * Retransformation capable transformers will also be called on every class retransformation. The request for a new
     * class definition is made with {@link ClassLoader#defineClass ClassLoader.defineClass} or its native equivalents.
     * The request for a class redefinition is made with {@link Instrumentation#redefineClasses
     * Instrumentation.redefineClasses} or its native equivalents. The request for a class retransformation is made with
     * {@link Instrumentation#retransformClasses Instrumentation.retransformClasses} or its native equivalents. The
     * transformer is called during the processing of the request, before the class file bytes have been verified or
     * applied. When there are multiple transformers, transformations are composed by chaining the
     * <code>transform</code> calls. That is, the byte array returned by one call to <code>transform</code> becomes the
     * input (via the <code>classFileBuffer</code> parameter) to the next call.
     *
     * <P> Transformations are applied in the following order: <ul> <li>Retransformation incapable transformers </li>
     * <li>Retransformation incapable native transformers </li> <li>Retransformation capable transformers </li>
     * <li>Retransformation capable native transformers </li> </ul>
     *
     * <P> For retransformations, the retransformation incapable transformers are not called, instead the result of the
     * previous transformation is reused. In all other cases, this method is called. Within each of these groupings,
     * transformers are called in the order registered. Native transformers are provided by the
     * <code>ClassFileLoadHook</code> event in the Java Virtual Machine Tool Interface).
     *
     * <P> The input (via the <code>classFileBuffer</code> parameter) to the first transformer is: <ul> <li>for new
     * class definition, the bytes passed to <code>ClassLoader.defineClass</code> </li> <li>for class redefinition,
     * <code>definitions.getDefinitionClassFile()</code> where <code>definitions</code> is the parameter to {@link
     * Instrumentation#redefineClasses Instrumentation.redefineClasses} </li> <li>for class retransformation, the bytes
     * passed to the new class definition or, if redefined, the last redefinition, with all transformations made by
     * retransformation incapable transformers reapplied automatically and unaltered; for details see {@link
     * Instrumentation#retransformClasses Instrumentation.retransformClasses} </li> </ul>
     *
     * <P> If the implementing method determines that no transformations are needed, it should return <code>null</code>.
     * Otherwise, it should create a new <code>byte[]</code> array, copy the input <code>classFileBuffer</code> into it,
     * along with all desired transformations, and return the new array. The input <code>classFileBuffer</code> must not
     * be modified.
     *
     * <P> In the retransform and redefine cases, the transformer must support the redefinition semantics: if a class
     * that the transformer changed during initial definition is later retransformed or redefined, the transformer must
     * insure that the second class output class file is a legal redefinition of the first output class file.
     *
     * <P> If the transformer throws an exception (which it doesn't catch), subsequent transformers will still be called
     * and the load, redefine or retransform will still be attempted. Thus, throwing an exception has the same effect as
     * returning <code>null</code>. To prevent unexpected behavior when unchecked exceptions are generated in
     * transformer code, a transformer can catch <code>Throwable</code>. If the transformer believes the
     * <code>classFileBuffer</code> does not represent a validly formatted class file, it should throw an
     * <code>IllegalClassFormatException</code>; while this has the same effect as returning null. it facilitates the
     * logging or debugging of format corruptions.
     *
     * @param loader              the defining loader of the class to be transformed, may be <code>null</code> if the
     *                            bootstrap loader
     * @param className           the name of the class in the internal form of fully qualified class and interface
     *                            names as defined in <i>The Java Virtual Machine Specification</i>. For example,
     *                            <code>"java/util/List"</code>.
     * @param classBeingRedefined if this is triggered by a redefine or retransform, the class being redefined or
     *                            retransformed; if this is a class load, <code>null</code>
     * @param protectionDomain    the protection domain of the class being defined or redefined
     * @param classFileBuffer     the input byte buffer in class file format - must not be modified
     * @return a well-formed class file buffer (the result of the transform), or <code>null</code> if no transform is
     * performed.
     * @throws IllegalClassFormatException if the input does not represent a well-formed class file
     * @see Instrumentation#redefineClasses
     */
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classFileBuffer)
            throws IllegalClassFormatException {
        if (CLASS_TO_TRANSFORM.contains(className)) {
            ClassReader classReader = new ClassReader(classFileBuffer);
            ClassNode classNode = new ClassNode();
            classReader.accept(classNode, 0);
            for (Object obj : classNode.methods) {
                MethodNode md = (MethodNode) obj;
                if ("<init>".endsWith(md.name) || "<clinit>".equals(md.name)) {
                    continue;
                }
                InsnList insnList = md.instructions;
                InsnList il = new InsnList();
                il.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System",
                        "out", "Ljava/io/PrintStream;"));
                il.add(new LdcInsnNode("Enter method-> " + classNode.name+"."+md.name));
                il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
                        "java/io/PrintStream", "println", "(Ljava/lang/String;)V"));
                insnList.insert(il);
                md.maxStack += 3;
            }
            ClassWriter cw = new ClassWriter(0);
            classNode.accept(cw);
            return cw.toByteArray();
        } else {
            return classFileBuffer;
        }
    }
}
