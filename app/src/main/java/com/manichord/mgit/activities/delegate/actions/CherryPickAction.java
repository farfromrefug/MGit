package com.manichord.mgit.activities.delegate.actions;

import com.manichord.android.activities.SheimiFragmentActivity;
import com.manichord.mgit.R;
import com.manichord.mgit.activities.RepoDetailActivity;
import com.manichord.mgit.database.models.Repo;
import com.manichord.mgit.repo.tasks.SheimiAsyncTask;
import com.manichord.mgit.repo.tasks.repo.CherryPickTask;

public class CherryPickAction extends RepoAction {

    public CherryPickAction(Repo repo, RepoDetailActivity activity) {
        super(repo, activity);
    }

    @Override
    public void execute() {
        mActivity.showEditTextDialog(R.string.dialog_cherrypick_title,
                R.string.dialog_cherrypick_msg_hint,
                R.string.dialog_label_cherrypick,
                new SheimiFragmentActivity.OnEditTextDialogClicked() {
                    @Override
                    public void onClicked(String text) {
                        cherrypick(text);
                    }
                });
        mActivity.closeOperationDrawer();
    }

    public void cherrypick(String commit) {
        CherryPickTask task = new CherryPickTask(mRepo, commit, new SheimiAsyncTask.AsyncTaskPostCallback() {
            @Override
            public void onPostExecute(Boolean isSuccess) {
                mActivity.reset();
            }
        });
        task.executeTask();
    }

}
