package com.manichord.mgit.activities.delegate.actions;

import android.content.DialogInterface;
import com.manichord.mgit.R;
import com.manichord.mgit.activities.RepoDetailActivity;
import com.manichord.mgit.database.models.Repo;

public class DeleteAction extends RepoAction {

    public DeleteAction(Repo repo, RepoDetailActivity activity) {
        super(repo, activity);
    }

    @Override
    public void execute() {
        mActivity.showMessageDialog(R.string.dialog_delete_repo_title,
                R.string.dialog_delete_repo_msg, R.string.label_delete,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mRepo.deleteRepo();
                        mActivity.finish();
                    }
                });
        mActivity.closeOperationDrawer();
    }
}
