/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import com.xrea.jeisi.berettacommittool2.JUtility;
import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.exception.GitCommandException;
import com.xrea.jeisi.berettacommittool2.exception.GitCommitNoMessageException;
import com.xrea.jeisi.berettacommittool2.exception.GitCommitNothingAddedException;
import com.xrea.jeisi.berettacommittool2.exception.GitCommitUnmergedFilesException;
import com.xrea.jeisi.berettacommittool2.exception.GitConfigException;
import com.xrea.jeisi.berettacommittool2.gitstatuspane.GitStatusData;
import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoryData;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 *
 * @author jeisi
 */
public class GitCommitCommand extends BaseSingleGitCommand {

    private RepositoryData repositoryData;
    private Predicate<GitStatusData> predicate = new Predicate<GitStatusData>() {
        @Override
        public boolean test(GitStatusData t) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };

    public GitCommitCommand(Path repository, ConfigInfo configInfo) {
        super(repository, configInfo);
    }

    public void commit(String message, boolean amend) throws IOException, GitConfigException, InterruptedException {
        if (message.length() == 0) {
            throw new GitCommitNoMessageException("Aborting commit due to empty commit message.");
        }

        Path tmpPath = Files.createTempFile("commitmsg", "txt");
        try {
            try (BufferedWriter writer = Files.newBufferedWriter(tmpPath, Charset.forName("UTF-8"))) {
                writer.append(message);
            }

            List<String> command = new ArrayList<>();
            command.add("git");
            command.add("commit");
            if (amend) {
                command.add("--amend");
            }
            command.add("-F");
            command.add(tmpPath.toString());
            List<String> displayCommand = new ArrayList<>();
            displayCommand.add("git");
            displayCommand.add("commit");
            if (amend) {
                displayCommand.add("--amend");
            }
            execProcess(command, displayCommand);
        } catch (GitCommandException ex) {
            if(ex.getStdOut().contains("nothing added to commit but untracked files present")) {
                throw new GitCommitNothingAddedException(ex);
            }
            if(ex.getStdErr().contains("error: Committing is not possible because you have unmerged files.")) {
                throw new GitCommitUnmergedFilesException(ex);
            }
            throw ex;
        } finally {
            Files.delete(tmpPath);
        }
    }

    public String readCommitEditMsg() throws GitConfigException, IOException, InterruptedException {
        String[] lines = execProcessWithOutput("git", "rev-parse", "--git-dir");
        Path messagePath = JUtility.expandPath(repository.toString(), lines[0], "COMMIT_EDITMSG");
        if(!Files.exists(messagePath)) {
            //return "";
            return null;
        }
        //List<String> commitMessage = Files.readAllLines(messagePath, Charset.forName("UTF-8"));
        //return String.join("\n", commitMessage);
        return Files.readString(messagePath, Charset.forName("UTF-8"));
    }
}
