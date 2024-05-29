package com.manichord.mgit.activities.delegate.actions;

import com.manichord.android.activities.SheimiFragmentActivity;
import com.manichord.mgit.R;
import com.manichord.mgit.activities.RepoDetailActivity;
import com.manichord.mgit.database.models.Repo;

public class NewDirAction extends RepoAction {

    public NewDirAction(Repo repo, RepoDetailActivity activity) {
        super(repo, activity);
    }

    @Override
    public void execute() {
        mActivity.showEditTextDialog(R.string.dialog_create_dir_title,
                R.string.dialog_create_dir_hint, R.string.label_create,
                new SheimiFragmentActivity.OnEditTextDialogClicked() {
                    @Override
                    public void onClicked(String text) {
                        mActivity.getFilesFragment().newDir(text);
                    }
                });
        mActivity.closeOperationDrawer();
    }
}
