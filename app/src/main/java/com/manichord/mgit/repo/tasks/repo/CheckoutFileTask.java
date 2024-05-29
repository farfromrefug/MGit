package com.manichord.mgit.repo.tasks.repo;

import com.manichord.mgit.R;
import com.manichord.mgit.database.models.Repo;
import com.manichord.mgit.exception.StopTaskException;
import com.manichord.mgit.repo.tasks.SheimiAsyncTask;

public class CheckoutFileTask extends RepoOpTask {

    private SheimiAsyncTask.AsyncTaskPostCallback mCallback;
    private String mPath;

    public CheckoutFileTask(Repo repo, String path,
                            SheimiAsyncTask.AsyncTaskPostCallback callback) {
        super(repo);
        mCallback = callback;
        mPath = path;
        setSuccessMsg(R.string.success_checkout_file);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        return checkout();
    }

    protected void onPostExecute(Boolean isSuccess) {
        super.onPostExecute(isSuccess);
        if (mCallback != null) {
            mCallback.onPostExecute(isSuccess);
        }
    }

    private boolean checkout() {
        try {
            mRepo.getGit().checkout().addPath(mPath).call();
        } catch (StopTaskException e) {
            return false;
        } catch (Throwable e) {
            setException(e);
            return false;
        }
        return true;
    }

}
