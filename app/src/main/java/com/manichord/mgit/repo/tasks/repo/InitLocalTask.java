package com.manichord.mgit.repo.tasks.repo;

import com.manichord.mgit.database.RepoContract;
import com.manichord.mgit.database.models.Repo;

import org.eclipse.jgit.api.Git;

public class InitLocalTask extends RepoOpTask {

    public InitLocalTask(Repo repo) {
        super(repo);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean result = init();
        if (!result) {
            mRepo.deleteRepoSync();
            return false;
        }
        return true;
    }

    protected void onPostExecute(Boolean isSuccess) {
        super.onPostExecute(isSuccess);
        if (isSuccess) {
            mRepo.updateLatestCommitInfo();
            mRepo.updateStatus(RepoContract.REPO_STATUS_NULL);
        }
    }

    public boolean init() {
        try {
            Git.init().setDirectory(mRepo.getDir()).call();
        } catch (Throwable e) {
            setException(e);
            return false;
        }
        return true;
    }
}
