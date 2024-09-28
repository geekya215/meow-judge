package io.geekya215.meowjudge.compiler;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CppCompiler extends AbstractCompiler {
    @Override
    protected @NotNull List<@NotNull String> compileArgs() {
        return List.of("g++", "main.cpp", "-o", "main");
    }
}
