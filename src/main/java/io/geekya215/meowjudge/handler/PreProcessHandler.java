package io.geekya215.meowjudge.handler;

import io.geekya215.meowjudge.JudgeContext;
import io.geekya215.meowjudge.JudgeRequest;
import io.geekya215.meowjudge.Language;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public final class PreProcessHandler extends Handler {
    private static final String BASE_JUDGE_DIRECTORY = "/tmp";
    private static final Map<Language, String> sourcecodeFileNames = new HashMap<>();
    private static final Map<Language, String> executableFileNames = new HashMap<>();

    static {
        sourcecodeFileNames.put(Language.C, "main.c");
        sourcecodeFileNames.put(Language.CPP, "main.cpp");
        sourcecodeFileNames.put(Language.JAVA, "Main.java");
        sourcecodeFileNames.put(Language.GO, "main.go");
        sourcecodeFileNames.put(Language.PYTHON, "main.py");
        sourcecodeFileNames.put(Language.JAVASCRIPT, "main.js");

        executableFileNames.put(Language.C, "main");
        executableFileNames.put(Language.CPP, "main");
        executableFileNames.put(Language.JAVA, "Main");
        executableFileNames.put(Language.GO, "main.go");
        executableFileNames.put(Language.PYTHON, "main.py");
        executableFileNames.put(Language.JAVASCRIPT, "main.js");
    }

    @Override
    public void handle(@NotNull final JudgeContext ctx) throws IOException {
        final JudgeRequest req = ctx.getJudgeRequest();
        final Long submissionId = req.submissionId();

        final String judgeDirectory = Paths.get(BASE_JUDGE_DIRECTORY, submissionId.toString()).toString();
        final String sourcecodePath = Paths.get(judgeDirectory, sourcecodeFileNames.get(req.language())).toString();
        final String executablePath = Paths.get(judgeDirectory, executableFileNames.get(req.language())).toString();

        writeCodeToFile(judgeDirectory, sourcecodePath, req.code());

        ctx.setJudgeDirectory(judgeDirectory);
        ctx.setSourcecodePath(sourcecodePath);
        ctx.setExecutablePath(executablePath);

        if (nextHandler != null) {
            nextHandler.handle(ctx);
        }
    }

    private void writeCodeToFile(@NotNull final String judgeDirectory,
                                 @NotNull final String sourcecodePath,
                                 @NotNull final String code) throws IOException {

        final File file = new File(judgeDirectory);
        if (!file.exists()) {
            final boolean created = file.mkdirs();
            if (!created) {
                throw new IOException("Failed to create judge directory: " + file);
            }
        }

        Files.writeString(Paths.get(sourcecodePath), code);
    }
}
