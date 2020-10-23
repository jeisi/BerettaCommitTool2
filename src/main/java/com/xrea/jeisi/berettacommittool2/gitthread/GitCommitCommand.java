/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.exception.GitConfigException;
import com.xrea.jeisi.berettacommittool2.gitstatuspane.GitStatusData;
import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoryData;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.AbortedByHookException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoMessageException;

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
    
    public void commit(String message, boolean amend) throws IOException, GitAPIException, GitConfigException, InterruptedException {
        /*
        Git git = Git.open(repository);
        
        Status gitStatus = git.status().call();
        
        if(gitStatus.getChanged().isEmpty() && gitStatus.getAdded().isEmpty() && gitStatus.getRemoved().isEmpty() &&
                gitStatus.getConflicting().isEmpty()) {
            // ステージされていないファイルがなければ何もしない。
            return;
        }
        */
        
        if(message.length() == 0) {
            throw new NoMessageException("Aborting commit due to empty commit message.");
        }
        
        Path tmpPath = Files.createTempFile("commitmsg", "txt");
        try {
            try(BufferedWriter writer = Files.newBufferedWriter(tmpPath, Charset.forName("UTF-8"))) {
                writer.append(message);
            }

            List<String> command = new ArrayList<>();
            command.add("git");
            command.add("commit");
            if(amend) {
                command.add("--amend");
            }
            command.add("-F");
            command.add(tmpPath.toString());
            execProcess(command);
        } finally {
            Files.delete(tmpPath);
        }        
    }
    
    public String readCommitEditMsg() throws GitConfigException, IOException, InterruptedException {
        List<String> lines = execProcessWithOutput("git", "rev-parse", "--git-dir");
        Path messagePath = Paths.get(repository.toString(), lines.get(0), "COMMIT_EDITMSG");
        List<String> commitMessage = Files.readAllLines(messagePath, Charset.forName("UTF-8"));
        return String.join("\n", commitMessage);
    }
}
