package me.acablade.skcompile.objects;

import org.objectweb.asm.ClassWriter;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public record ListenerData(String className, ClassWriter classWriter) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListenerData that = (ListenerData) o;
        return Objects.equals(className, that.className);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(className);
    }
}
