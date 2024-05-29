package com.manichord.mgit.repo.tasks.repo;

import android.content.Context;

import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.UnmergedPathsException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.lib.StoredConfig;

import com.manichord.android.utils.Profile;
import com.manichord.mgit.MGitApplication;
import com.manichord.mgit.R;
import com.manichord.mgit.database.models.Repo;
import com.manichord.mgit.exception.StopTaskException;
import com.manichord.mgit.repo.tasks.SheimiAsyncTask;

public class CommitChangesTask extends RepoOpTask {

    private SheimiAsyncTask.AsyncTaskPostCallback mCallback;
    private String mCommitMsg;
    private String mAuthorName;
    private String mAuthorEmail;
    private boolean mIsAmend;
    private boolean mStageAll;

    public CommitChangesTask(Repo repo, String commitMsg, boolean isAmend,
                             boolean stageAll, String authorName, String authorEmail,
                             SheimiAsyncTask.AsyncTaskPostCallback callback) {
        super(repo);
        mCallback = callback;
        mCommitMsg = commitMsg;
        mIsAmend = isAmend;
        mStageAll = stageAll;
        mAuthorName = authorName;
        mAuthorEmail = authorEmail;
        setSuccessMsg(R.string.success_commit);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        return commit();
    }

    protected void onPostExecute(Boolean isSuccess) {
        super.onPostExecute(isSuccess);
        if (mCallback != null) {
            mCallback.onPostExecute(isSuccess);
        }
    }

    public boolean commit() {
        try {
            commit(mRepo, mStageAll, mIsAmend, mCommitMsg, mAuthorName, mAuthorEmail);
        } catch (StopTaskException e) {
            return false;
        } catch (GitAPIException e) {
            setException(e);
            return false;
        } catch (Throwable e) {
            setException(e);
            return false;
        }
        mRepo.updateLatestCommitInfo();
        return true;
    }

    public static void commit(Repo repo, boolean stageAll, boolean isAmend,
            String msg, String authorName, String authorEmail) throws Exception, NoHeadException, NoMessageException,
            UnmergedPathsException, ConcurrentRefUpdateException,
            WrongRepositoryStateException, GitAPIException, StopTaskException {
        Context context = MGitApplication.getContext();
        StoredConfig config = repo.getGit().getRepository().getConfig();
        String committerEmail = config.getString("user", null, "email");
        String committerName = config.getString("user", null, "name");

        if (committerName == null || committerName.equals("")) {
            committerName = Profile.getUsername(context);
        }
        if (committerEmail == null || committerEmail.equals("")) {
            committerEmail = Profile.getEmail(context);
        }
        if (committerName.isEmpty() || committerEmail.isEmpty()) {
            throw new Exception("Please set your name and email");
        }
        if (msg.isEmpty()) {
            throw new Exception("Please include a commit message");
        }
        CommitCommand cc = repo.getGit().commit()
                .setCommitter(committerName, committerEmail).setAll(stageAll)
                .setAmend(isAmend).setMessage(msg);
        if (authorName != null && authorEmail != null) {
            cc.setAuthor(authorName, authorEmail);
        }
        cc.call();
        repo.updateLatestCommitInfo();
    }
}
