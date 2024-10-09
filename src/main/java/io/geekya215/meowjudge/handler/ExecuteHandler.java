package io.geekya215.meowjudge.handler;

import com.github.dockerjava.api.DockerClient;
import io.geekya215.meowjudge.JudgeContext;
import io.geekya215.meowjudge.JudgeRequest;
import io.geekya215.meowjudge.Language;
import io.geekya215.meowjudge.executor.Executable;
import io.geekya215.meowjudge.executor.JavaExecutor;
import io.geekya215.meowjudge.executor.NativeExecutor;
import io.geekya215.meowjudge.executor.PythonExecutor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public final class ExecuteHandler extends Handler {
    private static final Map<Language, Executable> executables = new HashMap<>();

    static {
        executables.put(Language.C, new NativeExecutor());
        executables.put(Language.CPP, new NativeExecutor());
        executables.put(Language.JAVA, new JavaExecutor());
        executables.put(Language.PYTHON, new PythonExecutor());
    }

    private final DockerClient dockerClient;

    public ExecuteHandler(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    @Override
    public void handle(@NotNull final JudgeContext ctx) throws IOException {
        final JudgeRequest req = ctx.getJudgeRequest();
        final Executable executable = executables.get(req.language());
        final boolean result = executable.execute(ctx, dockerClient);
        if (result) {
            if (nextHandler != null) {
                nextHandler.handle(ctx);
            }
        }
    }
}
