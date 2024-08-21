package me.acablade.skcompile.compiler;

import me.acablade.skcompile.objects.ListenerData;
import me.acablade.skcompile.objects.PluginData;
import me.acablade.skcompile.tokenizer.tokens.impl.OnToken;
import me.acablade.skcompile.utils.LogManager;
import org.objectweb.asm.*;


import java.util.Locale;
import java.util.function.Consumer;

import static org.objectweb.asm.Opcodes.*;

public class ClassBuilder {


    public static class Main {

        private static void addListener(MethodVisitor methodVisitor, String mainClassName, String listenerName){
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitMethodInsn(INVOKESTATIC, "org/bukkit/Bukkit", "getServer", "()Lorg/bukkit/Server;", false);
            methodVisitor.visitMethodInsn(INVOKEINTERFACE, "org/bukkit/Server", "getPluginManager", "()Lorg/bukkit/plugin/PluginManager;", true);
            methodVisitor.visitTypeInsn(NEW, listenerName);
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, listenerName, "<init>", "()V", false);
            methodVisitor.visitFieldInsn(GETSTATIC, mainClassName, "INSTANCE", "L"+mainClassName+";");
            methodVisitor.visitMethodInsn(INVOKEINTERFACE, "org/bukkit/plugin/PluginManager", "registerEvents", "(Lorg/bukkit/event/Listener;Lorg/bukkit/plugin/Plugin;)V", true);
        }

        public static byte[] createMainClass(PluginData data){
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);

            String packageName = data.packageName();
            String name = data.mainClassName();

            String className = packageName + "/" + name;

            FieldVisitor fieldVisitor;
            MethodVisitor methodVisitor;
            AnnotationVisitor annotationVisitor0;

            classWriter.visit(V22, ACC_PUBLIC | ACC_SUPER, className, null, "org/bukkit/plugin/java/JavaPlugin", null);

            classWriter.visitSource(name + ".java", null);

            {
                fieldVisitor = classWriter.visitField(ACC_PUBLIC | ACC_STATIC, "INSTANCE", "L"+className+";", null, null);
                fieldVisitor.visitEnd();
            }
            {
                methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
                methodVisitor.visitCode();
                Label label0 = new Label();
                methodVisitor.visitLabel(label0);
                methodVisitor.visitLineNumber(5, label0);
                methodVisitor.visitVarInsn(ALOAD, 0);
                methodVisitor.visitMethodInsn(INVOKESPECIAL, "org/bukkit/plugin/java/JavaPlugin", "<init>", "()V", false);
                methodVisitor.visitInsn(RETURN);
                Label label1 = new Label();
                methodVisitor.visitLabel(label1);
                methodVisitor.visitLocalVariable("this", "L" + className+ ";", null, label0, label1, 0);
                methodVisitor.visitMaxs(20, 20);
                methodVisitor.visitEnd();
            }
            {
                methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "onEnable", "()V", null, null);
                methodVisitor.visitCode();
                Label label0 = new Label();
                methodVisitor.visitLabel(label0);
                methodVisitor.visitVarInsn(ALOAD, 0);
                methodVisitor.visitFieldInsn(PUTSTATIC, className, "INSTANCE", "L"+className+";");
                for (int i = 0; i < data.listener().size(); i++) {
                    addListener(methodVisitor, className, packageName + "/" + data.listener().get(i).className());
                }
                Label label2 = new Label();
                methodVisitor.visitLabel(label2);
                methodVisitor.visitInsn(RETURN);
                Label label3 = new Label();
                methodVisitor.visitLabel(label3);
                methodVisitor.visitLocalVariable("this", "L"+className+";", null, label0, label3, 0);
                methodVisitor.visitMaxs(20, 20);
                methodVisitor.visitEnd();
            }

            if(data.utilOptions().createAnnounceMethod) {
                methodVisitor = classWriter.visitMethod(ACC_PUBLIC | ACC_STATIC, "SKCompile$$announce", "(Ljava/lang/String;)V", null, null);
                methodVisitor.visitCode();
                Label label0 = new Label();
                methodVisitor.visitLabel(label0);
                methodVisitor.visitLineNumber(27, label0);
                methodVisitor.visitMethodInsn(INVOKESTATIC, "org/bukkit/Bukkit", "getOnlinePlayers", "()Ljava/util/Collection;", false);
                methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Collection", "iterator", "()Ljava/util/Iterator;", true);
                methodVisitor.visitVarInsn(ASTORE, 1);
                Label label1 = new Label();
                methodVisitor.visitLabel(label1);
                methodVisitor.visitFrame(Opcodes.F_APPEND, 1, new Object[]{"java/util/Iterator"}, 0, null);
                methodVisitor.visitVarInsn(ALOAD, 1);
                methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z", true);
                Label label2 = new Label();
                methodVisitor.visitJumpInsn(IFEQ, label2);
                methodVisitor.visitVarInsn(ALOAD, 1);
                methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;", true);
                methodVisitor.visitTypeInsn(CHECKCAST, "org/bukkit/entity/Player");
                methodVisitor.visitVarInsn(ASTORE, 2);
                Label label3 = new Label();
                methodVisitor.visitLabel(label3);
                methodVisitor.visitLineNumber(28, label3);
                methodVisitor.visitVarInsn(ALOAD, 2);
                methodVisitor.visitVarInsn(ALOAD, 0);
                methodVisitor.visitMethodInsn(INVOKEINTERFACE, "org/bukkit/entity/Player", "sendMessage", "(Ljava/lang/String;)V", true);
                Label label4 = new Label();
                methodVisitor.visitLabel(label4);
                methodVisitor.visitLineNumber(29, label4);
                methodVisitor.visitJumpInsn(GOTO, label1);
                methodVisitor.visitLabel(label2);
                methodVisitor.visitLineNumber(30, label2);
                methodVisitor.visitFrame(Opcodes.F_CHOP, 1, null, 0, null);
                methodVisitor.visitInsn(RETURN);
                Label label5 = new Label();
                methodVisitor.visitLabel(label5);
                methodVisitor.visitLocalVariable("player", "Lorg/bukkit/entity/Player;", null, label3, label4, 2);
                methodVisitor.visitLocalVariable("msg", "Ljava/lang/String;", null, label0, label5, 0);
                methodVisitor.visitMaxs(20, 20);
                methodVisitor.visitEnd();
            }

            classWriter.visitEnd();

            return classWriter.toByteArray();
        }

    }

    public static class Listener {

        public static ClassWriter generateClass(String packageName, String className){
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            classWriter.visit(V22, ACC_PUBLIC | ACC_SUPER, packageName + "/" + className, null, "java/lang/Object", new String[]{"org/bukkit/event/Listener"});

            classWriter.visitSource(className + ".java", null);
            MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(12, label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            methodVisitor.visitInsn(RETURN);
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLocalVariable("this", "L"+packageName+"/"+className+";", null, label0, label1, 0);
            methodVisitor.visitMaxs(20, 20);
            methodVisitor.visitEnd();

            return classWriter;
        }

        public static void generateListenMethod(ClassWriter classWriter, String className, String event, Consumer<MethodVisitor> consumer){

            if(EventRegistry.get(event) == null){
                LogManager.err("event(%s) is not registered. cant create method");
                return;
            }

            MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "SKCompile$$"+event, "(L"+Type.getInternalName(EventRegistry.get(event))+";)V", null, null);
            {
                AnnotationVisitor annotationVisitor0 = methodVisitor.visitAnnotation("Lorg/bukkit/event/EventHandler;", true);
                annotationVisitor0.visitEnd();
            }
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            consumer.accept(methodVisitor);
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLineNumber(31, label1);
            methodVisitor.visitInsn(RETURN);
            Label label2 = new Label();
            methodVisitor.visitLabel(label2);
            methodVisitor.visitLocalVariable("this", "L"+className+";", null, label0, label2, 0);
            methodVisitor.visitLocalVariable("event", "Lorg/bukkit/event/player/PlayerJoinEvent;", null, label0, label2, 1);
            methodVisitor.visitMaxs(20, 20);
            methodVisitor.visitEnd();
        }
    }



}
