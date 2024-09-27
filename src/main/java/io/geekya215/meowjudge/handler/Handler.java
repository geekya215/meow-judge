package io.geekya215.meowjudge.handler;

import io.geekya215.meowjudge.JudgeContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public abstract class Handler {
    protected @Nullable Handler nextHandler;

    public abstract void handle(@NotNull final JudgeContext ctx) throws IOException;

    public void setNextHandler(@NotNull final Handler nextHandler) {
        this.nextHandler = nextHandler;
    }
}
