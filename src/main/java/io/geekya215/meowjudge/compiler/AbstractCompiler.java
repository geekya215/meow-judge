package io.geekya215.meowjudge.compiler;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.InvocationBuilder;
import io.geekya215.meowjudge.JudgeContext;
import io.geekya215.meowjudge.Verdict;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class AbstractCompiler implements Compilable {
    private static final String WORKING_DIRECTORY = "/usr/src";

    @Override
    public boolean compile(@NotNull final JudgeContext ctx, @NotNull final DockerClient dockerClient) {
        final List<@NotNull String> args = compileArgs();

        // do not need to compile
        if (args.isEmpty()) {
            return true;
        }

        final CreateContainerResponse response = dockerClient
                .createContainerCmd("judge-image:latest")
                .withCmd(args)
                .withNetworkDisabled(true)
                .withHostConfig(new HostConfig()
                        .withBinds(new Bind(ctx.getJudgeDirectory(), new Volume(WORKING_DIRECTORY)))
                        .withAutoRemove(true))
                .withWorkingDir(WORKING_DIRECTORY)
                .exec();

        final String containerId = response.getId();

        // Todo
        // use Ref
        final boolean[] compileResult = {true};

        try {
            dockerClient
                   .logContainerCmd(containerId)
                   .withStdOut(true)
                   .withStdErr(true)
                   .withFollowStream(true)
                   .exec(new InvocationBuilder.AsyncResultCallback<>() {
                       @Override
                       public void onNext(Frame object) {
                           if (object.getStreamType() == StreamType.STDERR) {
                               System.out.println(object);
                               compileResult[0] = false;
                           }
                       }
                   })
                   .awaitCompletion();

            dockerClient.startContainerCmd(containerId).exec();
        } catch (InterruptedException e) {
            compileResult[0] = false;
        }
        if (!compileResult[0]) {
            ctx.setVerdict(Verdict.COMPILE_ERROR);
        }
        return compileResult[0];
    }

    protected abstract @NotNull List<@NotNull String> compileArgs();
}
