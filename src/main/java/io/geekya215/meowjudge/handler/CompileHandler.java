package io.geekya215.meowjudge.handler;

import com.github.dockerjava.api.DockerClient;
import io.geekya215.meowjudge.JudgeContext;
import io.geekya215.meowjudge.JudgeRequest;
import io.geekya215.meowjudge.Language;
import io.geekya215.meowjudge.compiler.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public final class CompileHandler extends Handler {
    private static final Map<Language, Compilable> compilers = new HashMap<>();
    private final DockerClient dockerClient;

    static {
        compilers.put(Language.C, new CCompiler());
        compilers.put(Language.CPP, new CppCompiler());
        compilers.put(Language.JAVA, new JavaCompiler());
        compilers.put(Language.PYTHON, new PythonCompiler());
    }

    public CompileHandler(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    @Override
    public void handle(@NotNull final JudgeContext ctx) throws IOException {
        final JudgeRequest req = ctx.getJudgeRequest();
        final Compilable compilable = compilers.get(req.language());
        final boolean result = compilable.compile(ctx, dockerClient);
        if (result) {
            if (nextHandler != null) {
                nextHandler.handle(ctx);
            }
        }
    }
}
