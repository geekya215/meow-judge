package io.geekya215.meowjudge.executor;

import com.github.dockerjava.api.DockerClient;
import io.geekya215.meowjudge.JudgeContext;
import org.jetbrains.annotations.NotNull;

public interface Executable {
    boolean execute(@NotNull final JudgeContext ctx, @NotNull final DockerClient dockerClient);
}
