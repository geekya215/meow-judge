package io.geekya215.meowjudge.compiler;

import com.github.dockerjava.api.DockerClient;
import io.geekya215.meowjudge.JudgeContext;
import org.jetbrains.annotations.NotNull;

public interface Compilable {
    boolean compile(@NotNull final JudgeContext ctx, @NotNull final DockerClient dockerClient);
}
