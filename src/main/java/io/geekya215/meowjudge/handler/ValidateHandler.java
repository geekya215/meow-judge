package io.geekya215.meowjudge.handler;

import io.geekya215.meowjudge.JudgeContext;
import io.geekya215.meowjudge.Verdict;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;

public final class ValidateHandler extends Handler {
    private final Map<@NotNull Integer, @NotNull String> answers;

    public ValidateHandler(final Map<@NotNull Integer, @NotNull String> answers) {
        this.answers = answers;
    }

    @Override
    public void handle(@NotNull JudgeContext ctx) throws IOException {
        final Map<@NotNull Integer, @NotNull String> outputs = ctx.getOutputs();

        boolean valid = answers.keySet().stream().allMatch(a -> answers.get(a).equals(outputs.get(a)));

        if (valid) {
            ctx.setVerdict(Verdict.ACCEPT);
        } else {
            ctx.setVerdict(Verdict.WRONG_ANSWER);
        }
    }
}
