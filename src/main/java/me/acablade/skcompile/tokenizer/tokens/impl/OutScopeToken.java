package me.acablade.skcompile.tokenizer.tokens.impl;

import me.acablade.skcompile.tokenizer.tokens.IToken;
import me.acablade.skcompile.tokenizer.tokens.ITokenFactory;

import java.util.Deque;

public record OutScopeToken() implements IToken {


    public static class Factory implements ITokenFactory {

        @Override
        public IToken parse(Deque<ConstantToken> internal) {

            return new OutScopeToken();

        }

    }

}
