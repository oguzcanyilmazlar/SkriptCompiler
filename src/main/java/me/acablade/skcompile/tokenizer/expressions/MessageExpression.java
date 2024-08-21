package me.acablade.skcompile.tokenizer.expressions;

import me.acablade.skcompile.compiler.IExpressionCompiler;
import me.acablade.skcompile.objects.Context;
import me.acablade.skcompile.objects.PluginData;
import me.acablade.skcompile.tokenizer.tokens.IToken;
import me.acablade.skcompile.tokenizer.tokens.ITokenFactory;
import me.acablade.skcompile.tokenizer.tokens.impl.ConstantToken;
import me.acablade.skcompile.utils.LogManager;
import org.bukkit.ChatColor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.util.Deque;

import static org.objectweb.asm.Opcodes.*;

public record MessageExpression(String msg, String receiver) implements IToken, IExpressionCompiler {

    @Override
    public void compile(Context context, PluginData data, MethodVisitor methodVisitor) {

        if(receiver.equals("all")){
            data.utilOptions().createAnnounceMethod = true;
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLineNumber(20, label1);
            methodVisitor.visitLdcInsn(msg);
            methodVisitor.visitMethodInsn(INVOKESTATIC, data.mainClassCanon(), "SKCompile$$announce", "(Ljava/lang/String;)V", false);
        }else if(receiver.equals("player")){
            if(!context.isPlayerEvent){
                LogManager.err("err@(%d) \"%s\"", context.lineNumber, context.line);
                LogManager.err("\tContext does not contain player. Not compiling method");
                return;
            }
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "org/bukkit/event/player/PlayerEvent", "getPlayer", "()Lorg/bukkit/entity/Player;", false);
            methodVisitor.visitLdcInsn(msg);
            methodVisitor.visitMethodInsn(INVOKEINTERFACE, "org/bukkit/entity/Player", "sendMessage", "(Ljava/lang/String;)V", true);
        } else{
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLdcInsn(receiver);
            methodVisitor.visitMethodInsn(INVOKESTATIC, "org/bukkit/Bukkit", "getPlayer", "(Ljava/lang/String;)Lorg/bukkit/entity/Player;", false);
            methodVisitor.visitLdcInsn(msg);
            methodVisitor.visitMethodInsn(INVOKEINTERFACE, "org/bukkit/entity/Player", "sendMessage", "(Ljava/lang/String;)V", true);
        }

    }

    public static class Factory implements ITokenFactory {

        @Override
        public IToken parse(Deque<ConstantToken> internal) {


            ConstantToken token = internal.pollLast();

            StringBuilder msg = new StringBuilder();

            while(!internal.isEmpty() && !(token.constant().equals("to"))){
                msg.append(token.constant()).append(" ");
                token = internal.pollLast();
            }

            String playerName = internal.pollLast().constant();

            return new MessageExpression(ChatColor.translateAlternateColorCodes('&', msg.toString().trim()), playerName);

        }

    }

}
