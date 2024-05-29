package com.manichord.mgit.repo.tasks.repo;

import com.manichord.mgit.R;
import com.manichord.mgit.database.models.Repo;
import com.manichord.mgit.exception.StopTaskException;
import com.manichord.mgit.repo.tasks.SheimiAsyncTask;

public class RebaseTask extends RepoOpTask {

    public String mUpstream;
    private SheimiAsyncTask.AsyncTaskPostCallback mCallback;

    public RebaseTask(Repo repo, String upstream, SheimiAsyncTask.AsyncTaskPostCallback callback) {
        super(repo);
        mUpstream = upstream;
        mCallback = callback;
        setSuccessMsg(R.string.success_rebase);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        return rebase();
    }

    protected void onPostExecute(Boolean isSuccess) {
        super.onPostExecute(isSuccess);
        if (mCallback != null) {
            mCallback.onPostExecute(isSuccess);
        }
    }

    public boolean rebase() {
        try {
            mRepo.getGit().rebase().setUpstream(mUpstream).call();
        } catch (StopTaskException e) {
            return false;
        } catch (Throwable e) {
            setException(e);
            return false;
        }
        return true;
    }
}
