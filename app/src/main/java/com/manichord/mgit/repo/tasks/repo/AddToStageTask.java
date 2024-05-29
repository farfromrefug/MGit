package com.manichord.mgit.repo.tasks.repo;

import com.manichord.mgit.R;
import com.manichord.mgit.database.models.Repo;
import com.manichord.mgit.exception.StopTaskException;

public class AddToStageTask extends RepoOpTask {

    public String mFilePattern;

    public AddToStageTask(Repo repo, String filepattern) {
        super(repo);
        mFilePattern = filepattern;
        setSuccessMsg(R.string.success_add_to_stage);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        return addToStage();
    }

    protected void onPostExecute(Boolean isSuccess) {
        super.onPostExecute(isSuccess);
    }

    public boolean addToStage() {
        try {
            //   jGit hasn't a cmd direct add modified/new/deleted files, so if want to add
            //   those 3 types changed, need a combined call like below.
            //   check it: https://stackoverflow.com/a/59434085

            //add modified/new files
            mRepo.getGit().add().setUpdate(false).addFilepattern(mFilePattern).call();
            //add modified/deleted files
            mRepo.getGit().add().setUpdate(true).addFilepattern(mFilePattern).call();
        } catch (StopTaskException e) {
            return false;
        } catch (Throwable e) {
            setException(e);
            return false;
        }
        return true;
    }
}
