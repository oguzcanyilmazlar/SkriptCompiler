package me.acablade.skcompile.compiler;

import me.acablade.skcompile.objects.Context;
import me.acablade.skcompile.objects.ListenerData;
import me.acablade.skcompile.objects.PluginData;
import me.acablade.skcompile.objects.UtilOptions;
import me.acablade.skcompile.tokenizer.tokens.IToken;
import me.acablade.skcompile.tokenizer.tokens.impl.ContextToken;
import me.acablade.skcompile.tokenizer.tokens.impl.InScopeToken;
import me.acablade.skcompile.tokenizer.tokens.impl.OnToken;
import me.acablade.skcompile.tokenizer.tokens.impl.OutScopeToken;
import me.acablade.skcompile.utils.LogManager;
import org.bukkit.event.player.PlayerEvent;
import org.objectweb.asm.ClassWriter;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class Compiler {

    public static final Compiler INSTANCE = new Compiler();


    public void compile(String name, Deque<IToken> tokenStack){

        LogManager.debug("------------- COMPILE -------------");

        String packageName = "me/acablade/skcompile/" + name.toLowerCase(Locale.ENGLISH);

        PluginData pluginData = new PluginData(packageName, name+"Plugin", new ArrayList<>(), new ArrayList<>(), new UtilOptions());

        IToken token = tokenStack.pollLast();

        Context context = new Context();

        boolean run = true;

        while (run && !tokenStack.isEmpty()){
            LogManager.debug(token.toString());
            if(token instanceof OnToken onToken){
                // inscope
                // ...
                // outscope
                IToken token1 = tokenStack.pollLast();
                assert token1 instanceof InScopeToken;

                String eventName = onToken.event();

                String listenerClassName = "SKCompileListener$$" + eventName;
                LogManager.debug("listenerClassName: %s", listenerClassName);
                if(pluginData.listener().contains(new ListenerData(listenerClassName, null))){
                    LogManager.err("event named %s is already being used. not creating", listenerClassName);
                    run = false;
                    continue;
                }

                ClassWriter classWriter = ClassBuilder.Listener.generateClass(packageName, listenerClassName);
                ListenerData data = new ListenerData(listenerClassName, classWriter);

                if(EventRegistry.get(eventName) != null && (EventRegistry.get(eventName).getSuperclass() == PlayerEvent.class)){
                    context.isPlayerEvent = true;
                }
                while(!tokenStack.isEmpty() && !(token1 instanceof OutScopeToken)){
                    if(token1 instanceof IExpressionCompiler expr){
                        LogManager.debug("expression compiling");
                        Context finalContext = context;
                        ClassBuilder.Listener.generateListenMethod(classWriter, packageName + "/" + listenerClassName ,eventName, (mv) -> expr.compile(finalContext, pluginData, mv));
                    }
                    token1 = tokenStack.pollLast();
                }
                classWriter.visitEnd();
                pluginData.listener().add(data);
//                tokenStack.pollLast(); // pop outscopetoken
            }else if(token instanceof ContextToken contextToken){
                context = new Context();
                context.line = contextToken.line();
                context.lineNumber = contextToken.lineNum();
            }

            token = tokenStack.pollLast();
        }

        LogManager.debug("size of listeners: %d", pluginData.listener().size());

        List<Map.Entry<File, byte[]>> list = new ArrayList<>();

        list.add(new AbstractMap.SimpleEntry<>(new File(pluginData.mainClassCanon() + ".class"),ClassBuilder.Main.createMainClass(pluginData)));

        pluginData.listener().forEach(listenerData -> {
            list.add(new AbstractMap.SimpleEntry<>(new File(packageName + "/" + listenerData.className() + ".class"), listenerData.classWriter().toByteArray()));
        });

        try {
            createFile(pluginData, list);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public void createFile(PluginData data, List<Map.Entry<File, byte[]>> list) throws IOException {
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        JarOutputStream target = new JarOutputStream(new FileOutputStream("output.jar"), manifest);
        list.forEach(fileEntry -> {
            try {
                add(fileEntry.getKey(), fileEntry.getValue(), target);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        add(new File("plugin.yml"), createPluginYML(data), target);
        target.close();
    }

    private byte[] createPluginYML(PluginData pluginData) throws IOException {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("name", pluginData.mainClassName());
        data.put("version", "1.0.0-SKC");
        data.put("main", pluginData.mainClassCanon().replace('/', '.'));
        data.put("authors", new String[] { "Acablade" });
        data.put("description", pluginData.mainClassName() + " compiled by SKCompiler");

        Yaml yaml = new Yaml();

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(outStream);
        yaml.dump(data, writer);
        return outStream.toByteArray();
    }

    private void add(File source, byte[] bytes, JarOutputStream target) throws IOException {
        String name = source.getPath().replace("\\", "/");
        if (source.isDirectory()) {
            if (!name.endsWith("/")) {
                name += "/";
            }
            JarEntry entry = new JarEntry(name);
            entry.setTime(source.lastModified());
            target.putNextEntry(entry);
            target.closeEntry();
            for (File nestedFile : source.listFiles()) {
                add(nestedFile, bytes, target);
            }
        } else {
            JarEntry entry = new JarEntry(name);
            entry.setTime(source.lastModified());
            target.putNextEntry(entry);
            try (BufferedInputStream in = new BufferedInputStream(new ByteArrayInputStream(bytes))) {
                byte[] buffer = new byte[1024];
                while (true) {
                    int count = in.read(buffer);
                    if (count == -1)
                        break;
                    target.write(buffer, 0, count);
                }
                target.closeEntry();
            }
        }
    }




}
