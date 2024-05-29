package com.manichord.mgit.activities.delegate.actions;

import android.content.DialogInterface;
import com.manichord.mgit.R;
import com.manichord.mgit.activities.RepoDetailActivity;
import com.manichord.mgit.database.models.Repo;
import com.manichord.mgit.repo.tasks.SheimiAsyncTask;
import com.manichord.mgit.repo.tasks.repo.ResetCommitTask;

public class ResetAction extends RepoAction {

    public ResetAction(Repo repo, RepoDetailActivity activity) {
        super(repo, activity);
    }

    @Override
    public void execute() {
        mActivity.showMessageDialog(R.string.dialog_reset_commit_title,
                R.string.dialog_reset_commit_msg, R.string.action_reset,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        reset();
                    }
                });
        mActivity.closeOperationDrawer();
    }

    public void reset() {
        ResetCommitTask resetTask = new ResetCommitTask(mRepo,
                new SheimiAsyncTask.AsyncTaskPostCallback() {
                    @Override
                    public void onPostExecute(Boolean isSuccess) {
                        mActivity.reset();
                    }
                });
        resetTask.executeTask();
    }
}
