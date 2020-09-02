/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import com.xrea.jeisi.berettacommittool2.gitstatuspane.GitStatusData;
import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoryData;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import org.eclipse.jgit.lib.IndexDiff;

/**
 *
 * @author jeisi
 */
public class GitCommandStatusTest {

    public GitCommandStatusTest() {
    }

    @Test
    // 実際の git status の結果との比較用。
    public void test() throws GitAPIException, IOException {
        Path path = Paths.get("/home/jeisi/test/git/sandbox/work/beretta");
        GitStatusCommand command = new GitStatusCommand(path.toFile());
        command.printStatus();
    }

    @Test
    public void test2() throws GitAPIException, IOException {
        Path path = Paths.get("/home/jeisi/test/git/sandbox/work/beretta/gyp");
        GitStatusCommand command = new GitStatusCommand(path.toFile());
        command.printStatus();

    }

    /*
    @Disabled
    @Test
    public void test3() throws GitAPIException, IOException {
        Path path = Paths.get("/home/jeisi/test/git/sandbox/work/beretta/gyptools");
        GitStatusCommand command = new GitStatusCommand(path.toFile());
        command.printStatus();
    }
     */
    @Test
    public void testStatus_Added() throws IOException {
        MockStatus gitStatus = new MockStatus();
        Set<String> added = new HashSet<>();
        added.add("dummy.cpp");
        added.add("alice.cpp");
        gitStatus.setAdded(added);

        RepositoryData repositoryData = new RepositoryData(true, "", Paths.get("."));
        GitStatusCommand gitCommand = new GitStatusCommand(Paths.get(".").toFile());
        List<GitStatusData> list = gitCommand.status(gitStatus, repositoryData);
        // ファイル名でソートされている。
        assertThat("[{A, , alice.cpp, }, {A, , dummy.cpp, }]").isEqualTo(list.toString());
    }

    @Test
    public void testStatus_UntrackedFiles() throws IOException {
        MockStatus gitStatus = new MockStatus();
        Set<String> added = new HashSet<>();
        added.add("dummy.cpp");
        gitStatus.setAdded(added);
        Set<String> untracked = new HashSet<>();
        untracked.add("carol.cpp");
        gitStatus.setUntracked(untracked);

        RepositoryData repositoryData = new RepositoryData(true, "", Paths.get("."));
        GitStatusCommand gitCommand = new GitStatusCommand(Paths.get(".").toFile());
        List<GitStatusData> list = gitCommand.status(gitStatus, repositoryData);
        // untracked files は最後に追加される。
        assertThat("[{A, , dummy.cpp, }, {?, ?, carol.cpp, }]").isEqualTo(list.toString());
    }

    @Test
    // ?? がディレクトリの場合は最後に / をつける。
    public void testStatus_UntrackedDir() throws IOException, GitAPIException, InterruptedException {
        String userDir = System.getProperty("user.dir");
        Path bashCommand = Paths.get(userDir, "src/test/resources/testPrune.sh");
        ProcessBuilder pb = new ProcessBuilder("bash", bashCommand.toString(), userDir);
        Process process = pb.start();
        int ret = process.waitFor();

        Path path = Paths.get(System.getProperty("user.dir"), "src/test/resources/work/Controls");
        GitStatusCommand command = new GitStatusCommand(path.toFile());
        RepositoryData repositoryData = new RepositoryData(true, "Controls", Paths.get("."));
        List<GitStatusData> gitStatusDatas = command.status(repositoryData);

        assertEquals("[{?, ?, subA/, Controls}]", gitStatusDatas.toString());
    }

    @Test
    public void testStatus_conflicting() throws IOException {
        MockStatus gitStatus = new MockStatus();
        Map<String, IndexDiff.StageState> conflictingStagaState = new HashMap<>();
        conflictingStagaState.put("carol.cpp", IndexDiff.StageState.BOTH_MODIFIED);
        gitStatus.setConflictingStageState(conflictingStagaState);

        RepositoryData repositoryData = new RepositoryData(true, "", Paths.get("."));
        GitStatusCommand gitCommand = new GitStatusCommand(Paths.get(".").toFile());
        List<GitStatusData> list = gitCommand.status(gitStatus, repositoryData);
        assertThat("[{U, U, carol.cpp, }]").isEqualTo(list.toString());
    }

    @Test
    // MM 状態の表示
    public void testStatus_UncommittedAndModified() throws IOException {
        MockStatus gitStatus = new MockStatus();

        Set<String> changed = new HashSet<>();
        changed.add("update.sh");
        gitStatus.setChanged(changed);

        Set<String> modified = new HashSet<>();
        modified.add("update.sh");
        gitStatus.setModified(modified);

        RepositoryData repositoryData = new RepositoryData(true, "", Paths.get("."));
        GitStatusCommand gitCommand = new GitStatusCommand(Paths.get(".").toFile());
        List<GitStatusData> list = gitCommand.status(gitStatus, repositoryData);
        // ファイル名でソートされている。
        assertThat("[{M, M, update.sh, }]").isEqualTo(list.toString());
    }

    @Test
    // 特定のファイルのみの git status を表示するテスト
    public void testStatusAddPath() throws IOException, InterruptedException, GitAPIException {
        System.out.println("GitCommandTest.testAdd()");
        String userDir = System.getProperty("user.dir");
        Path bashCommand = Paths.get(userDir, "src/test/resources/testAdd.sh");

        ProcessBuilder pb = new ProcessBuilder("bash", bashCommand.toString(), userDir);
        Process process = pb.start();
        int ret = process.waitFor();

        // １ファイルのみ指定した場合。
        RepositoryData repositoryData = new RepositoryData(true, ".", Paths.get("."));
        GitStatusCommand command = new GitStatusCommand(Paths.get(userDir, "src/test/resources/work/beretta").toFile());
        List<GitStatusData> list = command.status(repositoryData, "b.txt");
        assertEquals("[{?, ?, b.txt, .}]", list.toString());

        // 複数ファイル指定した場合。
        list = command.status(repositoryData, "b.txt", "c.txt");
        assertEquals("[{?, ?, b.txt, .}, {?, ?, c.txt, .}]", list.toString());
    }
}
