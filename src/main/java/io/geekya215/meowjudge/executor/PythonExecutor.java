package io.geekya215.meowjudge.executor;

import org.jetbrains.annotations.NotNull;

public final class PythonExecutor extends AbstractExecutor {
    @Override
    protected @NotNull String @NotNull [] executeArgs() {
        return new String[] {"python3", "main.py"};
    }
}
