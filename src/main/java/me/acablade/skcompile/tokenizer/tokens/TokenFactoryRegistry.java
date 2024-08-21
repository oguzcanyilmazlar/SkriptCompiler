package me.acablade.skcompile.tokenizer.tokens;

import java.util.HashMap;
import java.util.Map;

public class TokenFactoryRegistry {

    private static final Map<String, ITokenFactory> factoryMap = new HashMap<>();

    public static void register(String token, ITokenFactory factory){
        factoryMap.put(token, factory);
    }

    public static ITokenFactory get(String token){
        return factoryMap.get(token);
    }


}
