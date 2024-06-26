package com.manichord.mgit.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.manichord.mgit.tasks.repo.UpdateIndexTask;

import com.manichord.android.views.SheimiDialogFragment;
import com.manichord.mgit.R;
import com.manichord.mgit.activities.RepoDetailActivity;

import static com.manichord.mgit.repo.tasks.repo.DeleteFileFromRepoTask.DeleteOperationType;

/**
 * Created by sheimi on 8/16/13.
 */
public class RepoFileOperationDialog extends SheimiDialogFragment {

    private RepoDetailActivity mActivity;
    private static final int ADD_TO_STAGE = 0;
    private static final int CHECKOUT_FILE = 1;
    private static final int DELETE = 2;
    private static final int REMOVE_CACHED = 3;
    private static final int REMOVE_FORCE = 4;
    private static final int MAKE_EXECUTABLE = 5;
    private static final int MAKE_NOT_EXECUTABLE = 6;
    public static final String FILE_PATH = "file path";
    private static String mFilePath;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        Bundle args = getArguments();
        if (args != null && args.containsKey(FILE_PATH)) {
            mFilePath = args.getString(FILE_PATH);
        }

        mActivity = (RepoDetailActivity) getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        builder.setTitle(R.string.dialog_title_you_want_to).setItems(
            R.array.repo_file_operations,
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case ADD_TO_STAGE: // Add to stage
                            mActivity.getRepoDelegate().addToStage(
                                mFilePath);
                            break;
                        case CHECKOUT_FILE:
                            mActivity.getRepoDelegate().checkoutFile(mFilePath);
                            break;
                        case DELETE:
                            showRemoveFileMessageDialog(R.string.dialog_file_delete,
                                R.string.dialog_file_delete_msg,
                                R.string.label_delete,
                                DeleteOperationType.DELETE);
                            break;
                        case REMOVE_CACHED:
                            showRemoveFileMessageDialog(R.string.dialog_file_remove_cached,
                                R.string.dialog_file_remove_cached_msg,
                                R.string.label_delete,
                                DeleteOperationType.REMOVE_CACHED);
                            break;
                        case REMOVE_FORCE:
                            showRemoveFileMessageDialog(R.string.dialog_file_remove_force,
                                R.string.dialog_file_remove_force_msg,
                                R.string.label_delete,
                                DeleteOperationType.REMOVE_FORCE);
                            break;
                        case MAKE_EXECUTABLE:
                        case MAKE_NOT_EXECUTABLE:
                            final boolean newExecutableState = which == MAKE_EXECUTABLE;
                            mActivity.getRepoDelegate().updateIndex(mFilePath, UpdateIndexTask.Companion.calculateNewMode(newExecutableState));
                            break;
                    }
                }
            });

        return builder.create();
    }

    private void showRemoveFileMessageDialog(int dialog_title, int dialog_msg, int dialog_positive_button, final DeleteOperationType deleteOperationType) {
        showMessageDialog(dialog_title,
                dialog_msg,
                dialog_positive_button,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(
                            DialogInterface dialogInterface,
                            int i) {

                        mActivity.getRepoDelegate()
                                .deleteFileFromRepo(
                                        mFilePath, deleteOperationType);
                    }
                });
    }
}
