package me.acablade.skcompile.tokenizer.tokens;

import me.acablade.skcompile.tokenizer.tokens.impl.ConstantToken;

import java.util.Deque;
import java.util.Stack;

public interface ITokenFactory {

    public IToken parse(Deque<ConstantToken> internal);

}
