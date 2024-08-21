package me.acablade.skcompile.tokenizer.tokens.impl;

import me.acablade.skcompile.tokenizer.tokens.IToken;

public record ConstantToken(String constant) implements IToken {
}
