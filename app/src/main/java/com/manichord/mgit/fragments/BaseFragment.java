package com.manichord.mgit.fragments;

import android.content.Context;
import android.content.DialogInterface;
import androidx.fragment.app.Fragment;

import com.manichord.android.activities.SheimiFragmentActivity;
import com.manichord.android.activities.SheimiFragmentActivity.OnBackClickListener;

/**
 * Created by sheimi on 8/7/13.
 */
public abstract class BaseFragment extends Fragment {

    public abstract OnBackClickListener getOnBackClickListener();

    private SheimiFragmentActivity mActivity;

    public abstract void reset();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (SheimiFragmentActivity) context;
    }

    public SheimiFragmentActivity getRawActivity() {
        return mActivity;
    }

    public void showMessageDialog(int title, int msg, int positiveBtn,
            DialogInterface.OnClickListener positiveListener) {
        getRawActivity().showMessageDialog(title, msg, positiveBtn,
                positiveListener);
    }

    public void showMessageDialog(int title, String msg, int positiveBtn,
            DialogInterface.OnClickListener positiveListener) {
        getRawActivity().showMessageDialog(title, msg, positiveBtn,
                positiveListener);
    }

    public void showToastMessage(int resId) {
        getRawActivity().showToastMessage(getString(resId));
    }

    public void showToastMessage(String msg) {
        getRawActivity().showToastMessage(msg);
    }

    // public abstract void search(String query);
}
