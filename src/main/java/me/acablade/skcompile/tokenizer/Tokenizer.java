package me.acablade.skcompile.tokenizer;

import me.acablade.skcompile.tokenizer.tokens.IToken;
import me.acablade.skcompile.tokenizer.tokens.ITokenFactory;
import me.acablade.skcompile.tokenizer.tokens.TokenFactoryRegistry;
import me.acablade.skcompile.tokenizer.tokens.impl.ConstantToken;
import me.acablade.skcompile.tokenizer.tokens.impl.OnToken;
import me.acablade.skcompile.utils.LogManager;
import org.checkerframework.checker.units.qual.C;

import java.util.*;

public class Tokenizer {

    public static final Tokenizer INSTANCE = new Tokenizer();

    private Tokenizer(){}


    public Deque<IToken> tokenizeCode(List<String> string){
        Deque<IToken> stack = new ArrayDeque<>();

        for (int i = 0; i < string.size(); i++) {
            String line = string.get(i);

            this.tokenizeLine(stack, new TokenParseInfo(line, i+1));

        }

        return stack;
    }

    private boolean inScope = false;

    private void tokenizeLine(Deque<IToken> stack, TokenParseInfo parseInfo) {

        Deque<ConstantToken> internalStack = new ArrayDeque<>();

        internalStack.push(new ConstantToken("$$context$$"));
        internalStack.push(new ConstantToken(parseInfo.lineNumber() + ""));
        internalStack.push(new ConstantToken(parseInfo.line()));

        String line = parseInfo.line();

        if(line.startsWith("\t") && !inScope){
            inScope = true;
            internalStack.push(new ConstantToken("$$inscope$$"));
            LogManager.debug("in scope");
        }else if(!line.startsWith("\t") && inScope){
            inScope = false;
            internalStack.push(new ConstantToken("$$outscope$$"));
            LogManager.debug("out scope");
        }

        String[] params = line.split(" ");

        LogManager.debug(params.length + "");
//        System.out.println(params.length);



        for (int i = 0; i < params.length; i++) {
            String param = params[i];



            param = param.trim();

            if (param.isEmpty()) {
                String highlight = highlightError(params, i);
                LogManager.warn("Extra space found on line skipping token %d(%s)", parseInfo.lineNumber(), highlight);
                continue;
            }
            LogManager.debug("adding(%s)", param);
            internalStack.push(new ConstantToken(param));

        }

        LogManager.debug(internalStack.toString());


        LogManager.debug(internalStack.peekFirst().constant());

        ConstantToken curr;
        while(!internalStack.isEmpty()){
            curr = internalStack.pollLast();
            LogManager.debug(curr.toString());
            ITokenFactory factory = TokenFactoryRegistry.get(curr.constant());
            if(factory != null){
                IToken token = factory.parse(internalStack);
                stack.push(token);
            }
            else stack.push(curr);
        }

        LogManager.debug(stack.toString());


    }


    private String highlightError(String[] line, int i){
        StringBuilder s = new StringBuilder();
        for (int j = 0; j < line.length; j++) {
            if(j==i) s.append(LogManager.ANSI_RED_UNDERLINE);
            else s.append(LogManager.ANSI_RESET);
            s.append(line[j]);
            s.append(" ");
        }
        return s.toString();
    }



}
