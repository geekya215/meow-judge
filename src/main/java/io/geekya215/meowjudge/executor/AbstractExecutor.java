package io.geekya215.meowjudge.executor;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Volume;
import io.geekya215.meowjudge.JudgeContext;
import io.geekya215.meowjudge.Verdict;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Map;
import java.util.concurrent.*;

public abstract class AbstractExecutor implements Executable {
    private static final String WORKING_DIRECTORY = "/usr/src";

    @Override
    public boolean execute(@NotNull final JudgeContext ctx, @NotNull final DockerClient dockerClient) {
        final CreateContainerResponse response = dockerClient
                .createContainerCmd("judge-image:latest")
                .withAttachStdin(true)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withTty(true)
                .withNetworkDisabled(true)
                .withHostConfig(new HostConfig()
                        .withBinds(new Bind(ctx.getJudgeDirectory(), new Volume(WORKING_DIRECTORY)))
                        .withMemory(ctx.getMemoryLimit().longValue())
                )
                .withWorkingDir(WORKING_DIRECTORY)
                .exec();

        final String containerId = response.getId();

        dockerClient.startContainerCmd(containerId).exec();

        final String[] args = executeArgs();
        final Map<@NotNull Integer, @NotNull String> inputs = ctx.getInputs();
        final Map<@NotNull Integer, @NotNull String> outputs = ctx.getOutputs();

        boolean result = true;

        for (final Map.Entry<@NotNull Integer, @NotNull String> testcase : inputs.entrySet()) {
            try {
                final String output = executeTestcase(args, dockerClient, containerId, testcase.getValue(), ctx.getTimeLimit());
                outputs.put(testcase.getKey(), output);
                Thread.sleep(200);
            } catch (ExecutionException | InterruptedException | TimeoutException e) {
                result = false;
                ctx.setVerdict(Verdict.TIME_LIMIT_EXCEED);
                break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Todo
        // handle memory limit exceed
        InspectContainerResponse exec = dockerClient.inspectContainerCmd(containerId).exec();
        InspectContainerResponse.ContainerState state = exec.getState();
        final Long exitCode = state.getExitCodeLong();
        if (exitCode == 137) {
            ctx.setVerdict(Verdict.MEMORY_LIMIT_EXCEED);
            result = false;
        }

        // clean up container in other threads
        Executors.newVirtualThreadPerTaskExecutor().execute(() -> {
            // stop container if running
            if (Boolean.TRUE.equals(state.getRunning())) {
                try {
                    dockerClient.stopContainerCmd(containerId).exec();
                } catch (RuntimeException e) {
                    // ignore
                }
            }

            // remove container
            dockerClient.removeContainerCmd(containerId).exec();
        });

        return result;
    }

    private @NotNull String executeTestcase(@NotNull final String @NotNull [] args,
                                            @NotNull final DockerClient dockerClient,
                                            @NotNull final String containerId,
                                            @NotNull final String input,
                                            @NotNull final Integer timeLimit
    ) throws IOException, ExecutionException, InterruptedException, TimeoutException {
        final ExecCreateCmdResponse createCmdResponse = dockerClient
                .execCreateCmd(containerId)
                .withCmd(args)
                .withAttachStdin(true)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .exec();

        final StringBuilder sb = new StringBuilder();

        try (final PipedInputStream pipedIn = new PipedInputStream();
             final PipedOutputStream pipedOut = new PipedOutputStream(pipedIn))
        {
            final ResultCallback.Adapter<Frame> callback = new ResultCallback.Adapter<>() {
                @Override
                public void onNext(Frame object) {
                    sb.append(new String(object.getPayload()));
                }
            };

            dockerClient.execStartCmd(createCmdResponse.getId())
                    .withDetach(false)
                    .withTty(false)
                    .withStdIn(pipedIn)
                    .exec(callback);

            pipedOut.write(input.getBytes());
            pipedOut.flush();

            Future<String> future = Executors.newVirtualThreadPerTaskExecutor().submit(() -> {
                callback.awaitCompletion();
                return sb.toString();
            });

            return future.get(timeLimit, TimeUnit.MILLISECONDS);
        }
    }

    protected abstract @NotNull String @NotNull [] executeArgs();
}
