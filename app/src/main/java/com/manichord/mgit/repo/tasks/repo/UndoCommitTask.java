package com.manichord.mgit.repo.tasks.repo;

import org.eclipse.jgit.api.ResetCommand;

import com.manichord.mgit.R;
import com.manichord.mgit.database.models.Repo;
import com.manichord.mgit.exception.StopTaskException;

public class UndoCommitTask extends RepoOpTask {

    private AsyncTaskPostCallback mCallback;

    public UndoCommitTask(Repo repo, AsyncTaskPostCallback callback) {
        super(repo);
        mCallback = callback;
        setSuccessMsg(R.string.success_undo);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        return undo();
    }

    protected void onPostExecute(Boolean isSuccess) {
        super.onPostExecute(isSuccess);
        if (mCallback != null) {
            mCallback.onPostExecute(isSuccess);
        }
    }

    public boolean undo() {
        try {
            mRepo.getGit().getRepository().writeMergeCommitMsg(null);
            mRepo.getGit().getRepository().writeMergeHeads(null);
            mRepo.getGit().reset().setRef("HEAD~").setMode(ResetCommand.ResetType.SOFT).call();
        } catch (StopTaskException e) {
            return false;
        } catch (Throwable e) {
            setException(e);
            return false;
        }
        return true;
    }
}
