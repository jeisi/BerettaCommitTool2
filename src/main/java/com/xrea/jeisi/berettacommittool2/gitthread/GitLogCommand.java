/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import java.io.File;
import java.io.IOException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;

/**
 *
 * @author jeisi
 */
public class GitLogCommand {

    private final File repository;

    public GitLogCommand(File repository) {
        this.repository = repository;
    }

    public Iterable<RevCommit> getLog() throws IOException, GitAPIException {
        Git git = gitOpen();
        ObjectId head = git.getRepository().resolve(Constants.HEAD);
        return git.log().add(head).setMaxCount(10).call();
    }

    private Git gitOpen() throws IOException {
        return Git.open(repository);
    }

}
