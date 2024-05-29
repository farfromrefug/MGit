package com.manichord.mgit.activities.delegate.actions;

import android.content.DialogInterface;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.Iterator;

import com.manichord.mgit.R;
import com.manichord.mgit.activities.RepoDetailActivity;
import com.manichord.mgit.database.models.Repo;
import com.manichord.mgit.exception.StopTaskException;
import com.manichord.mgit.repo.tasks.SheimiAsyncTask;
import com.manichord.mgit.repo.tasks.repo.UndoCommitTask;

public class UndoAction extends RepoAction {

    public UndoAction(Repo repo, RepoDetailActivity activity) {
        super(repo, activity);
    }

    @Override
    public void execute() {
        boolean firstCommit = true;
        boolean noCommit = true;
        try {
            Iterator<RevCommit> logIt = mRepo.getGit().log().call().iterator();
            noCommit = !logIt.hasNext();
            if (!noCommit) {
                logIt.next();
                firstCommit = !logIt.hasNext();
            }
        } catch (GitAPIException | StopTaskException e) {
            e.printStackTrace();
        }
        if (noCommit) {
            mActivity.showMessageDialog(R.string.dialog_undo_commit_title, R.string.dialog_undo_no_commit_msg);
        } else if (firstCommit) {
            mActivity.showMessageDialog(R.string.dialog_undo_commit_title, R.string.dialog_undo_first_commit_msg);
        } else {
            mActivity.showMessageDialog(R.string.dialog_undo_commit_title,
                R.string.dialog_undo_commit_msg, R.string.action_undo,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        undo();
                    }
                });
        }
        mActivity.closeOperationDrawer();
    }

    public void undo() {
        UndoCommitTask undoTask = new UndoCommitTask(mRepo,
            new SheimiAsyncTask.AsyncTaskPostCallback() {
                @Override
                public void onPostExecute(Boolean isSuccess) {
                    mActivity.reset();
                }
            });
        undoTask.executeTask();
    }
}
