package me.acablade.skcompile;

import me.acablade.skcompile.compiler.Compiler;
import me.acablade.skcompile.compiler.EventRegistry;
import me.acablade.skcompile.objects.ListenerData;
import me.acablade.skcompile.objects.PluginData;
import me.acablade.skcompile.objects.UtilOptions;
import me.acablade.skcompile.tokenizer.Tokenizer;
import me.acablade.skcompile.tokenizer.expressions.MessageExpression;
import me.acablade.skcompile.tokenizer.tokens.IToken;
import me.acablade.skcompile.tokenizer.tokens.TokenFactoryRegistry;
import me.acablade.skcompile.tokenizer.tokens.impl.ContextToken;
import me.acablade.skcompile.tokenizer.tokens.impl.InScopeToken;
import me.acablade.skcompile.tokenizer.tokens.impl.OnToken;
import me.acablade.skcompile.tokenizer.tokens.impl.OutScopeToken;
import me.acablade.skcompile.utils.LogManager;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    public static void main(String[] args) {

        LogManager.debugMode = true;

        TokenFactoryRegistry.register("on", new OnToken.Factory());
        TokenFactoryRegistry.register("$$inscope$$", new InScopeToken.Factory());
        TokenFactoryRegistry.register("$$outscope$$", new OutScopeToken.Factory());
        TokenFactoryRegistry.register("$$context$$", new ContextToken.Factory());
        TokenFactoryRegistry.register("message", new MessageExpression.Factory());

        // use reflection to register all events maybe???
        EventRegistry.register("join", PlayerJoinEvent.class);
        EventRegistry.register("quit", PlayerQuitEvent.class);

        // get sk
        Path path = Paths.get(args[0]);



        try {
//            List<String> lines = List.of("on join:", "\tmessage hello world to player", "on quit:", "\tmessage {player::getName()} has quit to all", "");

            List<String> lines = Files.readAllLines(path);

            lines = new ArrayList<>(lines.stream().map(s -> s.replaceAll(" {4}", "\t")).toList());

            Deque<IToken> stack = Tokenizer.INSTANCE.tokenizeCode(lines);
            stack.push(new OutScopeToken());


            Compiler.INSTANCE.compile("testing", stack);

            lines.forEach(System.out::println);

        } catch (Throwable e) {
            e.printStackTrace();
        }


    }
}