/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import com.xrea.jeisi.berettacommittool2.JUtility;
import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.exception.GitCommandException;
import com.xrea.jeisi.berettacommittool2.exception.GitConfigException;
import com.xrea.jeisi.berettacommittool2.exception.RepositoryNotFoundException;
import com.xrea.jeisi.berettacommittool2.gitstatuspane.GitStatusData;
import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoryData;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author jeisi
 */
public class GitStatusCommand extends BaseSingleGitCommand {

    private final static List<GitStatusData> emptyData = new ArrayList<>();
    private boolean ignored = false;

    public GitStatusCommand(Path repository, ConfigInfo configInfo) {
        super(repository, configInfo);
    }

    public void setIgnored(boolean ignored) {
        this.ignored = ignored;
    }

    public List<GitStatusData> status(RepositoryData repositoryData) throws GitCommandException, GitConfigException, IOException, InterruptedException {
        return status(repositoryData, emptyData);
    }

    public List<GitStatusData> status(RepositoryData repositoryData, List<GitStatusData> datas) throws IOException, GitCommandException, GitConfigException, InterruptedException {
        List<String> command = getStatusCommand(datas);
        List<String> displayCommand = getStatusCommand(datas);
        String[] lines;
        try {
            lines = execProcess(command, displayCommand);
            checkMerging(repositoryData);
        } catch (GitCommandException e) {
            if (e.getStdErr().stream().anyMatch(l -> l.contains("not a git repository"))) {
                throw new RepositoryNotFoundException(repositoryData.getPath(), e);
            }
            throw e;
        }
        return getStatusDatas(lines, repositoryData);
    }

    public List<GitStatusData> status(RepositoryData repositoryData, GitStatusData data) throws IOException, GitCommandException, GitConfigException, InterruptedException {
        return status(repositoryData, Arrays.asList(data));
    }

    private List<String> getStatusCommand(List<GitStatusData> datas) /*throws GitCommandException, GitConfigException*/ {
        ArrayList<String> command = new ArrayList<>();
        command.add("git");
        command.add("status");
        command.add("-s");
        command.add("--untracked-files=all");
        if (ignored) {
            command.add("--ignored");
        }
        datas.forEach(d -> command.add(d.getFileName()));
        return command;
    }

    private static List<GitStatusData> getStatusDatas(String[] lines, RepositoryData repositoryData) throws IOException {
        Pattern pattern = Pattern.compile("^(.)(.) (.+)( -> (.+))?");
        List<GitStatusData> list = new ArrayList<>();
        for (var line : lines) {
            Matcher m = pattern.matcher(line);
            if (!m.matches()) {
                throw new AssertionError(line + " が正規表現にマッチしませんでした。");
            }

            String index = m.group(1);
            if (index.equals(" ")) {
                index = "";
            }

            String workTree = m.group(2);
            if (workTree.equals(" ")) {
                workTree = "";
            }

            String fileName;
            if (m.group(4) == null) {
                fileName = m.group(3);
            } else {
                fileName = m.group(5);
            }
            list.add(new GitStatusData(index, workTree, fileName, repositoryData));
        }
        return list;
    }

    private void checkMerging(RepositoryData repositoryData) throws GitConfigException, IOException, InterruptedException {
        String[] lines = execProcess("git", "rev-parse", "--git-dir");
        Path revertHeadPath = JUtility.expandPath(repository.toString(), lines[0], "REVERT_HEAD");
        boolean isReverting = Files.exists(revertHeadPath);
        repositoryData.setReverting(isReverting);

        if (isReverting) {
            repositoryData.setMerging(false);            
        } else {
            Path messagePath = JUtility.expandPath(repository.toString(), lines[0], "MERGE_MSG");
            boolean merging = Files.exists(messagePath);
            repositoryData.setMerging(merging);
        }
    }
}
