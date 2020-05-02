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
import java.util.List;
import org.eclipse.jgit.api.errors.GitAPIException;

/**
 *
 * @author jeisi
 */
public class MockGitStatusCommand extends GitStatusCommand {

    private MockStatus mockStatus;

    public MockGitStatusCommand(File repoDir) {
        super(repoDir);
    }

    public void setMockStatus(MockStatus mockStatus) {
        this.mockStatus = mockStatus;
    }

    @Override
    public List<GitStatusData> status(RepositoryData repositoryData) throws IOException, GitAPIException {
        return status(mockStatus, repositoryData);
    }

    @Override
    public List<GitStatusData> status(RepositoryData repositoryData, String... paths) throws IOException, GitAPIException {
        return status(mockStatus, repositoryData);
    }

}
