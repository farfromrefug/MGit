package com.manichord.mgit.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.annotation.StringRes
import android.widget.Button
import com.manichord.mgit.R
import com.manichord.mgit.databinding.DialogErrorBinding
import com.manichord.android.views.SheimiDialogFragment

import timber.log.BuildConfig
import timber.log.Timber

class ErrorDialog : SheimiDialogFragment() {

    private var mThrowable: Throwable? = null
    @StringRes
    private var mErrorRes: Int = 0
    @StringRes
    var errorTitleRes: Int = 0
        get() = if (field != 0) field else R.string.dialog_error_title

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        val builder = AlertDialog.Builder(rawActivity)
        val inflater = rawActivity.layoutInflater
        val binding = DialogErrorBinding.inflate(inflater)
        val details = when (mThrowable) {
            is Exception -> {
                (mThrowable as Exception).message
            }
            else -> ""
        }
        binding.errorMessage.text = getString(mErrorRes) + "\n" + details

        builder.setView(binding.root)

        // set button listener
        builder.setTitle(errorTitleRes)
        builder.setPositiveButton(
            getString(R.string.label_ok),
            DummyDialogListener()
        )
        return builder.create()
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as AlertDialog
        val positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE) as Button
        positiveButton.setOnClickListener {
            if (BuildConfig.DEBUG) {
                // when debugging just log the exception
                if (mThrowable != null) {
                    Timber.e(mThrowable);
                } else {
                    Timber.e(if (mErrorRes != 0) getString(mErrorRes) else "")
                }
            }
            dismiss()
        }
    }

    fun setThrowable(throwable: Throwable?) {
        mThrowable = throwable
    }

    fun setErrorRes(@StringRes errorRes: Int) {
        mErrorRes = errorRes
    }
}
