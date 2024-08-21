package me.acablade.skcompile.compiler;

import me.acablade.skcompile.tokenizer.tokens.ITokenFactory;
import org.bukkit.event.Event;

import java.util.HashMap;
import java.util.Map;

public class EventRegistry {

    private static final Map<String, Class<? extends Event>> eventRegistry = new HashMap<>();

    public static void register(String token, Class<? extends Event> clazz){
        eventRegistry.put(token, clazz);
    }

    public static Class<? extends Event> get(String token){
        return eventRegistry.get(token);
    }



}
