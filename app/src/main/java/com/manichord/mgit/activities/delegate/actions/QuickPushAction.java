package com.manichord.mgit.activities.delegate.actions;

import java.util.Set;

import com.manichord.android.utils.Profile;
import com.manichord.mgit.R;
import com.manichord.mgit.activities.RepoDetailActivity;
import com.manichord.mgit.database.models.Repo;
import com.manichord.mgit.repo.tasks.repo.AddToStageTask;
import com.manichord.mgit.repo.tasks.repo.CommitChangesTask;
import com.manichord.mgit.repo.tasks.repo.PushTask;

public class QuickPushAction extends RepoAction {

    public QuickPushAction(Repo repo, RepoDetailActivity activity) {
        super(repo, activity);
    }

    @Override
    public void execute() {
        Set<String> remotes = mRepo.getRemotes();
        if (remotes == null || remotes.isEmpty()) {
            mActivity.showToastMessage(R.string.alert_please_add_a_remote);
            return;
        }

        String quickPushMsg = Profile.getQuickPushMsg(mActivity.getApplicationContext());
        if(quickPushMsg==null || quickPushMsg.isEmpty()) {
            mActivity.showToastMessage(R.string.alert_plese_set_commit_msg_for_quick_push);
            return;
        }

        mActivity.closeOperationDrawer();

        // stageAll(include new file), commit, push
        AddToStageTask addTask = new AddToStageTask(mRepo, ".") {
            @Override
            protected void onPostExecute(Boolean isSuccess) {
                super.onPostExecute(isSuccess);
                //commit
                CommitChangesTask commitTask = new CommitChangesTask(mRepo,
                    quickPushMsg, false, false,
                    Profile.getUsername(mActivity.getApplicationContext()),
                    Profile.getEmail(mActivity.getApplicationContext()),
                    new AsyncTaskPostCallback() {
                        @Override
                        public void onPostExecute(Boolean isSuccess) {
                            //   mActivity.reset() is copy from existed code when new CommitChangeTask(),
                            // idk this line work for what, but it work bad on here, so comment.
                            // mActivity.reset();

                            PushTask pushTask = new PushTask(mRepo,remotes.toArray()[0].toString(),
                                false,false,
                                mActivity.new ProgressCallback(R.string.push_msg_init));
                            pushTask.executeTask();
                        }
                    });
                commitTask.executeTask();
            }
        };

        addTask.executeTask();
    }

}
