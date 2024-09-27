package io.geekya215.meowjudge;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class JudgeContext {
    private @NotNull final JudgeRequest judgeRequest;
    private @NotNull final Integer timeLimit;
    private @NotNull final Integer memoryLimit;
    private @NotNull Verdict verdict;
    private @Nullable String judgeDirectory;
    private @Nullable String sourcecodePath;
    private @Nullable String executablePath;

    public JudgeContext(@NotNull final JudgeRequest judgeRequest,
                        @NotNull final Integer timeLimit,
                        @NotNull final Integer memoryLimit,
                        @NotNull Verdict verdict) {
        this.judgeRequest = judgeRequest;
        this.timeLimit = timeLimit;
        this.memoryLimit = memoryLimit;
        this.verdict = verdict;
    }

    public JudgeContext(@NotNull JudgeRequest judgeRequest,
                        @NotNull Integer timeLimit,
                        @NotNull Integer memoryLimit) {
        this(judgeRequest, timeLimit, memoryLimit, Verdict.WAITING);
    }

    public @NotNull JudgeRequest getJudgeRequest() {
        return judgeRequest;
    }

    public @NotNull Integer getTimeLimit() {
        return timeLimit;
    }

    public @NotNull Integer getMemoryLimit() {
        return memoryLimit;
    }

    public @NotNull Verdict getVerdict() {
        return verdict;
    }

    public void setVerdict(@NotNull final Verdict verdict) {
        this.verdict = verdict;
    }

    public @Nullable String getJudgeDirectory() {
        return judgeDirectory;
    }

    public void setJudgeDirectory(@NotNull String judgeDirectory) {
        this.judgeDirectory = judgeDirectory;
    }

    public @Nullable String getSourcecodePath() {
        return sourcecodePath;
    }

    public void setSourcecodePath(@NotNull String sourcecodePath) {
        this.sourcecodePath = sourcecodePath;
    }

    public @Nullable String getExecutablePath() {
        return executablePath;
    }

    public void setExecutablePath(@NotNull String executablePath) {
        this.executablePath = executablePath;
    }
}
