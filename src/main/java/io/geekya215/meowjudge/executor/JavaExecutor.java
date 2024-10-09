package io.geekya215.meowjudge.executor;

import org.jetbrains.annotations.NotNull;

public final class JavaExecutor extends AbstractExecutor {
    @Override
    protected @NotNull String @NotNull [] executeArgs() {
        return new String[] {"java", "Main"};
    }
}
