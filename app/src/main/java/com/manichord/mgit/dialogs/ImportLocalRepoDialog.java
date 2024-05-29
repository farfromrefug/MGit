package com.manichord.mgit.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

import java.io.File;

import com.manichord.android.utils.FsUtils;
import com.manichord.android.views.SheimiDialogFragment;
import com.manichord.mgit.MGitApplication;
import com.manichord.mgit.R;
import com.manichord.mgit.database.RepoContract;
import com.manichord.mgit.database.models.Repo;
import com.manichord.mgit.preference.PreferenceHelper;

/**
 * Created by sheimi on 8/24/13.
 */

public class ImportLocalRepoDialog extends SheimiDialogFragment implements
        View.OnClickListener {

    private File mFile;
    private String mFromPath;
    private Activity mActivity;
    private EditText mLocalPath;
    private CheckBox mImportAsExternal;
    private PreferenceHelper mPrefsHelper;
    public static final String FROM_PATH = "from path";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        mActivity = getActivity();

        mPrefsHelper = ((MGitApplication)mActivity.getApplicationContext()).getPrefenceHelper();

        Bundle args = getArguments();
        if (args != null && args.containsKey(FROM_PATH)) {
            mFromPath = args.getString(FROM_PATH);
        }
        mFile = new File(mFromPath);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(getString(R.string.dialog_import_set_local_repo_title));
        View view = getActivity().getLayoutInflater().inflate(
                R.layout.dialog_import_repo, null);

        builder.setView(view);
        mLocalPath = (EditText) view.findViewById(R.id.localPath);
        mLocalPath.setText(mFile.getName());
        mImportAsExternal = (CheckBox) view.findViewById(R.id.importAsExternal);
        mImportAsExternal
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                            boolean isChecked) {
                        if (isChecked) {
                            mLocalPath.setText(Repo.EXTERNAL_PREFIX
                                    + mFile.getAbsolutePath());
                        } else {
                            mLocalPath.setText(mFile.getName());
                        }
                        mLocalPath.setEnabled(isChecked);
                    }
                });

        // set button listener
        builder.setNegativeButton(R.string.label_cancel,
                new DummyDialogListener());
        builder.setPositiveButton(R.string.label_import,
                new DummyDialogListener());

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog == null)
            return;
        Button positiveButton = (Button) dialog
                .getButton(Dialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        final String localPath = mLocalPath.getText().toString().trim();
        if (!mImportAsExternal.isChecked()) {
            if (localPath.equals("")) {
                showToastMessage(R.string.alert_field_not_empty);
                mLocalPath.setError(getString(R.string.alert_field_not_empty));
                return;
            }

            if (localPath.contains("/")) {
                showToastMessage(R.string.alert_localpath_format);
                mLocalPath.setError(getString(R.string.alert_localpath_format));
                return;
            }

            File file = Repo.getDir(mPrefsHelper, localPath);
            if (file.exists()) {
                showToastMessage(R.string.alert_file_exists);
                mLocalPath.setError(getString(R.string.alert_file_exists));
                return;
            }
        }

        final Repo repo = Repo.importRepo(localPath, getString(R.string.importing));

        if (mImportAsExternal.isChecked()) {
            updateRepoInformation(repo);
            dismiss();
            return;
        }
        final File repoFile = Repo.getDir(mPrefsHelper, localPath);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                FsUtils.copyDirectory(mFile, repoFile);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateRepoInformation(repo);
                    }
                });
            }
        });
        thread.start();
        dismiss();
    }

    private void updateRepoInformation(Repo repo) {
        repo.updateLatestCommitInfo();
        repo.updateRemote();
        repo.updateStatus(RepoContract.REPO_STATUS_NULL);
    }
}
