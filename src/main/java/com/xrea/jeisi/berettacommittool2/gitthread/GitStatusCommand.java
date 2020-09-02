/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import com.xrea.jeisi.berettacommittool2.gitstatuspane.GitStatusData;
import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoryData;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.IndexDiff.StageState;

/**
 *
 * @author jeisi
 */
public class GitStatusCommand {

    private final File repository;

    public GitStatusCommand(File repoDir) {
        this.repository = repoDir;
    }

    public List<GitStatusData> status(RepositoryData repositoryData) throws IOException, GitAPIException {
        Git git = Git.open(repository);
        Status gitStatus = git.status().call();

        return status(gitStatus, repositoryData);
    }

    public List<GitStatusData> status(RepositoryData repositoryData, String ... paths) throws IOException, GitAPIException {
        Git git = Git.open(repository);
        var statusCommand = git.status();
        for(var path : paths) {
            statusCommand.addPath(path);
        }
        Status gitStatus = statusCommand.call();
        return status(gitStatus, repositoryData);
    }

    List<GitStatusData> status(Status gitStatus, RepositoryData repositoryData) {
        //String workDir = repositoryData.nameProperty().get();
        //Path fullPath = repositoryData.getPath();
        
        Map<String, GitStatusData> map = new TreeMap<>();
        gitStatus.getRemoved().forEach(file -> map.put(file, new GitStatusData("D", "", file, repositoryData)));
        gitStatus.getAdded().forEach(file -> map.put(file, new GitStatusData("A", "", file, repositoryData)));
        gitStatus.getChanged().forEach(file -> map.put(file, new GitStatusData("M", "", file, repositoryData)));

        gitStatus.getModified().forEach(file -> setModifiedStatusData(map, file, repositoryData));
        gitStatus.getMissing().forEach(file -> map.put(file, new GitStatusData("", "D", file, repositoryData)));
        gitStatus.getConflictingStageState().forEach((file, state) -> map.put(file, getConflictingStatusData(state, file, repositoryData)));

        Map<String, GitStatusData> untrackedFiles = new TreeMap<>();
        gitStatus.getUntracked().forEach(file -> {
            if (Files.isDirectory(Paths.get(repository.toString(), file))) {
                untrackedFiles.put(file, new GitStatusData("?", "?", file + "/", repositoryData));
            } else {
                untrackedFiles.put(file, new GitStatusData("?", "?", file, repositoryData));
            }
        });

        List<GitStatusData> list = new ArrayList<>();
        list.addAll(map.values());
        list.addAll(untrackedFiles.values());
        return list;
    }

    private void setModifiedStatusData(Map<String, GitStatusData> map, String file, RepositoryData repositoryData) {
        GitStatusData preStatus = map.get(file);
        if(preStatus == null) {
            map.put(file, new GitStatusData("", "M", file, repositoryData));
        } else {
            preStatus.setWorkTreeStatus("M");
        }
    }
    
    private static GitStatusData getConflictingStatusData(StageState state, String file, RepositoryData repositoryData) {
        switch (state) {
            case BOTH_MODIFIED:
                return new GitStatusData("U", "U", file, repositoryData);
            case BOTH_ADDED:
                return new GitStatusData("A", "A", file, repositoryData);
            case BOTH_DELETED:
                return new GitStatusData("D", "D", file, repositoryData);
            case DELETED_BY_THEM:
                return new GitStatusData("U", "D", file, repositoryData);
            case DELETED_BY_US:
                return new GitStatusData("D", "U", file, repositoryData);
            case ADDED_BY_US:
                return new GitStatusData("A", "U", file, repositoryData);
            case ADDED_BY_THEM:
                return new GitStatusData("U", "A", file, repositoryData);
            default:
                throw new RuntimeException("case 文漏れです。");
        }
    }

    public void printStatus() throws GitAPIException, IOException {
        Git git = Git.open(repository);
        Status status = git.status().call();

        System.out.println("[" + repository.toString() + "]");
        System.out.println("getAdded(): " + status.getAdded());
        System.out.println("getChanged(): " + status.getChanged());
        System.out.println("getMissing(): " + status.getMissing());
        System.out.println("getModified(): " + status.getModified());
        System.out.println("getRemoved(): " + status.getRemoved());
        System.out.println("getUncommittedChanges(): " + status.getUncommittedChanges());
        System.out.println("getUntracked(): " + status.getUntracked());
        System.out.println("getUntrackedFolders(): " + status.getUntrackedFolders());
        System.out.println("getConflicting(): " + status.getConflicting());
        System.out.println("getConflictingStageState(): " + status.getConflictingStageState());
    }

}
