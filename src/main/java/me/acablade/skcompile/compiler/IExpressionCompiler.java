package me.acablade.skcompile.compiler;

import me.acablade.skcompile.objects.Context;
import me.acablade.skcompile.objects.PluginData;
import org.objectweb.asm.MethodVisitor;

public interface IExpressionCompiler {

    public void compile(Context context, PluginData data, MethodVisitor visitor);

}
