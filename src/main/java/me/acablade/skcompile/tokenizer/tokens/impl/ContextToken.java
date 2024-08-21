package me.acablade.skcompile.tokenizer.tokens.impl;

import me.acablade.skcompile.tokenizer.tokens.IToken;
import me.acablade.skcompile.tokenizer.tokens.ITokenFactory;

import java.util.Deque;

public record ContextToken(int lineNum, String line) implements IToken {

    public static class Factory implements ITokenFactory {

        @Override
        public IToken parse(Deque<ConstantToken> internal) {

            int lineNum = Integer.parseInt(internal.pollLast().constant());
            String line = internal.pollLast().constant();

            return new ContextToken(lineNum, line);

        }

    }

}
