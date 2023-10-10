package com.czsy.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.provider.Settings;

import com.czsy.INFCHandler;
import com.czsy.android.R;

public abstract class AbsPVNFC extends AbsPVBase {

    public AbsPVNFC(MainActivity a) {
        super(a);
        boolean use_nfc = this instanceof INFCHandler;
    }

}
