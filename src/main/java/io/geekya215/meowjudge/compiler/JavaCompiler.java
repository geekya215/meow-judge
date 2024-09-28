package io.geekya215.meowjudge.compiler;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class JavaCompiler extends AbstractCompiler {
    @Override
    protected @NotNull List<@NotNull String> compileArgs() {
        return List.of("javac", "Main.java");
    }
}
