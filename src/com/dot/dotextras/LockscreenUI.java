/*
 * Copyright (C) 2018 Project dotOS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dot.dotextras;

import android.app.ActivityManagerNative;
import android.content.Context;
import android.content.ContentResolver;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.support.v7.preference.Preference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManagerGlobal;
import android.view.IWindowManager;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.Locale;
import android.text.TextUtils;
import android.view.View;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.settings.Utils;
import android.hardware.fingerprint.FingerprintManager;

public class LockscreenUI extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String PREF_FINGERPRINT_VIBE = "fingerprint_success_vib";
    private static final String KEY_LOCKSCREEN_CLOCK_SELECTION = "lockscreen_clock_selection";
    private static final String KEY_LOCKSCREEN_DATE_SELECTION = "lockscreen_date_selection";

    private FingerprintManager mFingerprintManager;
    private ListPreference mLockscreenClockSelection;
    private ListPreference mLockscreenDateSelection;
    private SwitchPreference mFingerprintSuccessVib;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.lockscreen_ui);

        final PreferenceScreen prefSet = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();

        mLockscreenClockSelection = (ListPreference) findPreference(KEY_LOCKSCREEN_CLOCK_SELECTION);
        int clockSelection = Settings.System.getIntForUser(resolver,
                Settings.System.LOCKSCREEN_CLOCK_SELECTION, 0, UserHandle.USER_CURRENT);
        mLockscreenClockSelection.setValue(String.valueOf(clockSelection));
        mLockscreenClockSelection.setSummary(mLockscreenClockSelection.getEntry());
        mLockscreenClockSelection.setOnPreferenceChangeListener(this);

        mLockscreenDateSelection = (ListPreference) findPreference(KEY_LOCKSCREEN_DATE_SELECTION);
        int dateSelection = Settings.System.getIntForUser(resolver,
                Settings.System.LOCKSCREEN_DATE_SELECTION, 0, UserHandle.USER_CURRENT);
        mLockscreenDateSelection.setValue(String.valueOf(dateSelection));
        mLockscreenDateSelection.setSummary(mLockscreenDateSelection.getEntry());
        mLockscreenDateSelection.setOnPreferenceChangeListener(this);

        // Remove the fingerprint success vibe switch if the device doesnt support it
        mFingerprintManager = (FingerprintManager) this.getSystemService(
                Context.FINGERPRINT_SERVICE);
        mFingerprintSuccessVib = (SwitchPreference) findPreference(PREF_FINGERPRINT_VIBE);
        if (mFingerprintManager == null) {
            getPreferenceScreen().removePreference(mFingerprintSuccessVib);
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.DOTEXTRAS;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mLockscreenClockSelection) {
            int clockSelection = Integer.valueOf((String) objValue);
            int index = mLockscreenClockSelection.findIndexOfValue((String) objValue);
            Settings.System.putIntForUser(resolver,
                    Settings.System.LOCKSCREEN_CLOCK_SELECTION, clockSelection, UserHandle.USER_CURRENT);
            mLockscreenClockSelection.setSummary(mLockscreenClockSelection.getEntries()[index]);
            return true;

        } else if (preference == mLockscreenDateSelection) {
            int dateSelection = Integer.valueOf((String) objValue);
            int index = mLockscreenDateSelection.findIndexOfValue((String) objValue);
            Settings.System.putIntForUser(resolver,
                    Settings.System.LOCKSCREEN_DATE_SELECTION, dateSelection, UserHandle.USER_CURRENT);
            mLockscreenDateSelection.setSummary(mLockscreenDateSelection.getEntries()[index]);
            return true;
        }
        return false;
    }
}
