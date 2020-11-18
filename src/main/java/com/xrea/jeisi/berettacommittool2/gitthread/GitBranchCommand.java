/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.exception.GitConfigException;
import com.xrea.jeisi.berettacommittool2.gitbranchpane.GitBranchData;
import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoryData;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author jeisi
 */
public class GitBranchCommand extends BaseSingleGitCommand {
    
    public GitBranchCommand(Path repository, ConfigInfo configInfo) {
        super(repository, configInfo);
    }
    
    

    public GitBranchData exec(RepositoryData repositoryData) throws IOException, InterruptedException, GitConfigException {
        //XmlWriter.writeStartMethod("GitBranchCommand.exec()");
        List<String> command = getBranchCommand();
        List<String> displayCommand = getBranchCommand();
        String[] lines = execProcess(command, displayCommand);
        
        GitBranchData data = new GitBranchData(repositoryData);
        for(String line : lines) {
            String branchName = line.substring(2);
            //XmlWriter.writeObject("branchName", branchName);
            if(line.charAt(0) == '*') {
                data.currentBranchProperty().set(branchName);
            } else if(branchName.startsWith("remotes/")) {
                data.getRemoteBranches().add(new SimpleStringProperty(branchName));
            } else {
                data.getOtherBranches().add(new SimpleStringProperty(branchName));
            }            
        }
        //XmlWriter.writeEndMethod();
        return data;
    }

    private List<String> getBranchCommand() {
        ArrayList<String> command = new ArrayList<>();
        command.add("git");
        command.add("branch");
        command.add("-a");
        return command;
    }
}
