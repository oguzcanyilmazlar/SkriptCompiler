package me.acablade.skcompile.objects;

import java.util.List;
import java.util.Set;

public record PluginData(String packageName, String mainClassName, List<ListenerData> listener, List<CommandData> command, UtilOptions utilOptions) {

    public String mainClassCanon(){
        return packageName + "/" + mainClassName;
    }

}
