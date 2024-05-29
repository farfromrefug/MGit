package com.manichord.mgit.repo.tasks.repo;

import java.io.File;

import com.manichord.android.utils.FsUtils;
import com.manichord.mgit.R;
import com.manichord.mgit.database.models.Repo;
import com.manichord.mgit.exception.StopTaskException;

public class DeleteFileFromRepoTask extends RepoOpTask {

    public String mFilePattern;
    public AsyncTaskPostCallback mCallback;
    private DeleteOperationType mOperationType;

    public DeleteFileFromRepoTask(Repo repo, String filepattern,
                                  DeleteOperationType deleteOperationType, AsyncTaskPostCallback callback) {
        super(repo);
        mFilePattern = filepattern;
        mCallback = callback;
        mOperationType = deleteOperationType;
        setSuccessMsg(R.string.success_remove_file);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        return removeFile();
    }

    protected void onPostExecute(Boolean isSuccess) {
        super.onPostExecute(isSuccess);
        if (mCallback != null) {
            mCallback.onPostExecute(isSuccess);
        }
    }

    public boolean removeFile() {
        try {
            switch (mOperationType) {
                case DELETE:
                    File fileToDelete = FsUtils.joinPath(mRepo.getDir(), mFilePattern);
                    FsUtils.deleteFile(fileToDelete);
                    break;
                case REMOVE_CACHED:
                    mRepo.getGit().rm().setCached(true).addFilepattern(mFilePattern).call();
                    break;
                case REMOVE_FORCE:
                    mRepo.getGit().rm().addFilepattern(mFilePattern).call();
                    break;
            }
        } catch (StopTaskException e) {
            return false;
        } catch (Throwable e) {
            setException(e);
            return false;
        }
        return true;
    }

    /**
     * Created by lee on 2015-01-30.
     */
    public static enum DeleteOperationType {
        DELETE,REMOVE_CACHED,REMOVE_FORCE
    }
}
