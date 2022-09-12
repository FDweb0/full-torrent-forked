/*
 * Copyright (C) 2019-2021 Yaroslav Pronin <proninyaroslav@mail.ru>
 *
 * This file is part of Full Torrent.
 *
 * Full Torrent is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Full Torrent is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Full Torrent.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.full.torrent.no.ads.private1.encrypted.ui.createtorrent;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.full.torrent.no.ads.private1.encrypted.core.utils.Utils;
import com.full.torrent.no.ads.private1.encrypted.ui.BaseAlertDialog;
import com.full.torrent.no.ads.private1.encrypted.ui.FragmentCallback;
import com.full.torrent.no.ads.private1.encrypted.ui.PermissionDeniedDialog;
import com.full.torrent.no.ads.private1.encrypted.ui.StoragePermissionManager;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class CreateTorrentActivity extends AppCompatActivity
    implements FragmentCallback
{
    private static final String TAG_CREATE_TORRENT_DIALOG = "create_torrent_dialog";
    private static final String TAG_PERM_DENIED_DIALOG = "perm_denied_dialog";

    private CreateTorrentDialog createTorrentDialog;
    private PermissionDeniedDialog permDeniedDialog;
    private BaseAlertDialog.SharedViewModel dialogViewModel;
    private CompositeDisposable disposable = new CompositeDisposable();
    private StoragePermissionManager permissionManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        setTheme(Utils.getTranslucentAppTheme(getApplicationContext()));
        super.onCreate(savedInstanceState);

        ViewModelProvider provider = new ViewModelProvider(this);
        dialogViewModel = provider.get(BaseAlertDialog.SharedViewModel.class);

        FragmentManager fm = getSupportFragmentManager();
        permissionManager = new StoragePermissionManager(this,
                (isGranted, shouldRequestStoragePermission) -> {
                    if (!isGranted && shouldRequestStoragePermission) {
                        if (fm.findFragmentByTag(TAG_PERM_DENIED_DIALOG) == null) {
                            permDeniedDialog = PermissionDeniedDialog.newInstance();
                            FragmentTransaction ft = fm.beginTransaction();
                            ft.add(permDeniedDialog, TAG_PERM_DENIED_DIALOG);
                            ft.commitAllowingStateLoss();
                        }
                    }
                });

        createTorrentDialog = (CreateTorrentDialog)fm.findFragmentByTag(TAG_CREATE_TORRENT_DIALOG);
        if (createTorrentDialog == null) {
            createTorrentDialog = CreateTorrentDialog.newInstance();
            createTorrentDialog.show(fm, TAG_CREATE_TORRENT_DIALOG);
        }

        if (!permissionManager.checkPermissions() && permDeniedDialog == null) {
            permissionManager.requestPermissions();
        }
        Utils.showManageAllFilesWarningDialog(getApplicationContext(), getSupportFragmentManager());
    }

    @Override
    public void onStart() {
        super.onStart();

        subscribeAlertDialog();
    }

    @Override
    protected void onStop() {
        super.onStop();

        disposable.clear();
    }

    private void subscribeAlertDialog() {
        Disposable d = dialogViewModel.observeEvents().subscribe(event -> {
            if (event.dialogTag == null) {
                return;
            }
            if (event.dialogTag.equals(TAG_PERM_DENIED_DIALOG)) {
                if (event.type != BaseAlertDialog.EventType.DIALOG_SHOWN) {
                    permDeniedDialog.dismiss();
                }
                if (event.type == BaseAlertDialog.EventType.NEGATIVE_BUTTON_CLICKED) {
                    permissionManager.requestPermissions();
                }
                if (event.type == BaseAlertDialog.EventType.POSITIVE_BUTTON_CLICKED) {
                    permissionManager.setDoNotAsk(true);
                }
            }
        });
        disposable.add(d);
    }

    @Override
    public void onFragmentFinished(@NonNull Fragment f, Intent intent, @NonNull ResultCode code)
    {
        finish();
    }

    @Override
    public void onBackPressed()
    {
        createTorrentDialog.onBackPressed();
    }
}