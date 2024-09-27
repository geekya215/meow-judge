package io.geekya215.meowjudge;

import org.jetbrains.annotations.NotNull;

public record JudgeRequest(
        @NotNull Long submissionId,
        @NotNull Long problemId,
        @NotNull String code,
        @NotNull Language language
) {
}
