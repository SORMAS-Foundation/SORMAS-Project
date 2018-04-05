package de.symeda.sormas.app.util;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.File;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.component.ConfirmationDialog;

/**
 * Created by Mate Strysewske on 16.02.2018.
 */
public class AppUpdateController {

    public final static int INSTALL_RESULT =  99;

    private static AppUpdateController instance = null;

    private ProgressDialog progressDialog;
    private ConfirmationDialog displayedDialog;
    private boolean allowDismiss;
    private String appUrl;
    private String fileName;
    private Activity activity;
    private Callback negativeCallback;
    private Tracker tracker;

    private AppUpdateController() { }

    public static AppUpdateController getInstance() {
        if (instance == null) {
            instance = new AppUpdateController();
        }

        return instance;
    }

    /**
     * Displays a confirmation dialog informing the user about the current state of the app update process by displaying one of the following options:
     * - Request to download a new app version
     * - Information that the new app version could not be retrieved
     * - Information that the download has failed
     * - Request to install a new app version (after it has been downloaded)
     * - Information that installing the new version has failed
     * The dialog will automatically be dismissed after the positive or negative buttons are clicked. A possible currently visible dialog created by this
     * class when this method is called will be dismissed and removed.
     *
     * # Testing the app update process
     * This functionality requires manual testing. Let the InfoResource return a custom value in getVersion(), and build an .apk version that manually assigns
     * the same value to appApiVersion in RetroProvider. Put this version on your server, adjust the app.url in sormas.properties, and start the server that returns
     * the fake version. Revert the changes to the appApiVersion assignment and start the app - it should now display the dialog to request downloading the new
     * app version. To test all functionality provided by this class, you can start the server without the fake version return in getVersion() and restart it with
     * the fake return value after logging into the app, and you can move a corrupted file to your phone to test how the app behaves when the .apk file cannot
     * be installed.
     *
     * Please note that the emulator provided by Android Studio seems to have problems with installing a downloaded .apk from within the app's local folders.
     * You might need to use a physical device to successfully use this funcationality.
     *
     * @param activity The activity the update app logic is called from; Needs to override onActivityResult and call AppUpdateController.handleInstallFailure
     *                 in case the result returned is not RESULT_OK or RESULT_CANCELED
     * @param allowDismiss True if the user should be able to dismiss the dialog, e.g. by clicking a "Download later" button; otherwise, the button will
     *                     either be hidden or replaced by a button that closes the app
     * @param negativeCallback Callback that will be called after the logic added in this class has been executed when the user has
     *                         clicked the negative button
     */
    public void updateApp(final Activity activity, final String appUrl, final String version, boolean allowDismiss, final Callback negativeCallback) {
        // retrieve the app version but returns the same value
        if (appUrl == null || appUrl.isEmpty() || version == null) {
            throw new IllegalArgumentException("This method may not be called with appUrl or version set to null or an empty appUrl.");
        }

        // Check if the file is currently being downloaded
        Long currentAppDownloadId = ConfigProvider.getCurrentAppDownloadId();
        boolean fileIsInDownloadQuery = currentAppDownloadId != null && checkIfFileIsInDownloadQuery(activity, currentAppDownloadId);
        // Remove the value of currentAppDownloadId if the file is not in the queue anymore
        if (currentAppDownloadId != null && !fileIsInDownloadQuery) {
            fileIsInDownloadQuery = false;
            ConfigProvider.setCurrentAppDownloadId(null);
        }
        // If download is currently in process, do nothing to avoid duplicate downloads or installation of an incomplete .apk file
        if (fileIsInDownloadQuery) {
            return;
        }

        this.activity = activity;
        this.allowDismiss = allowDismiss;
        this.appUrl = appUrl;
        this.negativeCallback = negativeCallback;
        this.tracker = ((SormasApplication) activity.getApplication()).getDefaultTracker();
        this.fileName = appUrl.substring(appUrl.lastIndexOf("/"), appUrl.lastIndexOf(".")) + "-" + version + ".apk";

        // Check if the required version has already been downloaded
        File file = new File(activity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);
        if (file.exists()) {
            // File is already present, attempt to install it - if the file is broken, delete or rename it and try to re-download it
            displayedDialog = buildInstallAppDialog();
        } else {
            // File is not present, attempt to download it
            displayedDialog = buildDownloadAppDialog();
        }

        displayedDialog.show();
    }

    public void handleInstallFailure() {
        File file = new File(activity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);
        if (!file.exists()) {
            ConfirmationDialog installFailedDialog = buildInstallFailedDialog();
            installFailedDialog.show();
            return;
        }

        boolean deleteOrRenameSuccessful = file.delete() || file.renameTo(new File("", fileName + "-incomplete"));
        if (deleteOrRenameSuccessful) {
            ConfirmationDialog installFailedDialog = buildInstallFailedDialog();
            installFailedDialog.show();
        } else {
            tracker.send(new HitBuilders.EventBuilder()
                    .setCategory("App Update Error")
                    .setAction("File Deletion or Renaming")
                    .setLabel("Failed to delete or rename an incompletely downloaded apk for user: " + ConfigProvider.getUser().getUuid())
                    .build());

            ConfirmationDialog installNotPossibleDialog = buildInstallNotPossibleDialog();
            installNotPossibleDialog.show();
        }
    }

    private ConfirmationDialog buildDownloadAppDialog() {
        dismissExistingDialog();

        Resources resources = activity.getResources();
        String title = resources.getString(R.string.headline_update_app);
        String message = resources.getString(R.string.message_update_app_required);
        String positiveButtonText = resources.getString(R.string.action_download);
        String negativeButtonText = allowDismiss ? resources.getString(R.string.action_download_later) : resources.getString(R.string.action_close_app);

        Callback positiveCallback = new Callback() {
            @Override
            public void call() {
                downloadNewAppVersion();
            }
        };

        return new ConfirmationDialog(activity, title, message, positiveButtonText, negativeButtonText, positiveCallback, negativeCallback);
    }

    private ConfirmationDialog buildDownloadFailedDialog() {
        dismissExistingDialog();

        Resources resources = activity.getResources();
        String title = resources.getString(R.string.headline_download_app_failed);
        String message = resources.getString(R.string.message_download_app_failed);
        String positiveButtonText = resources.getString(R.string.action_try_again);
        String negativeButtonText = allowDismiss ? resources.getString(R.string.action_download_later) : resources.getString(R.string.action_close_app);

        Callback positiveCallback = new Callback() {
            @Override
            public void call() {
                downloadNewAppVersion();
            }
        };

        return new ConfirmationDialog(activity, title, message, positiveButtonText, negativeButtonText, positiveCallback, negativeCallback);
    }

    private ConfirmationDialog buildInstallAppDialog() {
        dismissExistingDialog();

        Resources resources = activity.getResources();
        String title = resources.getString(R.string.headline_install_app);
        String message = resources.getString(R.string.message_install_app);
        String positiveButtonText = resources.getString(R.string.action_install_app);
        String negativeButtonText = allowDismiss ? resources.getString(R.string.action_install_later) : resources.getString(R.string.action_close_app);

        Callback positiveCallback = new Callback() {
            @Override
            public void call() {
                installApp();
            }
        };

        return new ConfirmationDialog(activity, title, message, positiveButtonText, negativeButtonText, positiveCallback, negativeCallback);
    }

    private ConfirmationDialog buildInstallFailedDialog() {
        dismissExistingDialog();

        Resources resources = activity.getResources();
        String title = resources.getString(R.string.headline_install_app_failed);
        String message = resources.getString(R.string.message_install_app_failed);
        String positiveButtonText = resources.getString(R.string.action_redownload_app);
        String negativeButtonText = allowDismiss ? resources.getString(R.string.action_redownload_app_later) : resources.getString(R.string.action_close_app);

        Callback positiveCallback = new Callback() {
            @Override
            public void call() {
                downloadNewAppVersion();
            }
        };

        return new ConfirmationDialog(activity, title, message, positiveButtonText, negativeButtonText, positiveCallback, negativeCallback);
    }

    private ConfirmationDialog buildInstallNotPossibleDialog() {
        dismissExistingDialog();

        Resources resources = activity.getResources();
        String title = resources.getString(R.string.headline_install_app_failed);
        String message = resources.getString(R.string.message_install_app_not_possible);
        String positiveButtonText = allowDismiss ? resources.getString(R.string.action_ok) : resources.getString(R.string.action_close_app);

        return new ConfirmationDialog(activity, title, message, positiveButtonText, null, null, null);
    }

    private void downloadNewAppVersion() {
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        activity.registerReceiver(downloadReceiver, filter);

        DownloadManager downloadManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri downloadUri = Uri.parse(appUrl);
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
        request.setTitle("New SORMAS version");

        File newFile = new File("", fileName);
        // Save the .apk file to the app's local external storage
        request.setDestinationInExternalFilesDir(activity, Environment.DIRECTORY_DOWNLOADS, newFile.getPath());
        request.setMimeType("application/vnd.android.package-archive");
        request.setVisibleInDownloadsUi(false);
        // Retrieve the downloadReference and save it in the Config to be able to receive the onReceive broadcast
        // even if the app is closed and restarted while the download is running
        long downloadReference = downloadManager.enqueue(request);
        ConfigProvider.setCurrentAppDownloadId(downloadReference);

        if (allowDismiss) {
            // Allow users to move the download process to the background; will not automatically open
            // the InstallAppDialog after the download has been finished
            progressDialog = new ProgressDialog(activity);
            progressDialog.setTitle(activity.getString(R.string.headline_app_download));
            progressDialog.setMessage(activity.getString(R.string.hint_app_download));
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, activity.getString(R.string.action_download_background), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
            });
            progressDialog.show();
        } else {
            progressDialog = ProgressDialog.show(activity, activity.getString(R.string.headline_app_download),
                    activity.getString(R.string.hint_app_download), true);
        }
    }

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (ConfigProvider.getCurrentAppDownloadId() == referenceId) {
                context.unregisterReceiver(this);
                ConfigProvider.setCurrentAppDownloadId(null);
                // Check if the download has been successful
                File file = new File(activity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);
                if (!file.exists()) {
                    ConfirmationDialog downloadFailedDialog = buildDownloadFailedDialog();
                    downloadFailedDialog.show();
                    return;
                }
                // If progressDialog is null, the download has been completed in the background
                // and the install prompt should not be shown automatically
                if (progressDialog != null) {
                    progressDialog.dismiss();
                    progressDialog = null;
                    ConfirmationDialog installAppDialog = buildInstallAppDialog();
                    installAppDialog.show();
                }
            }
        }
    };

    private void installApp() {
        File file = new File(activity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);
        if (!file.exists()) {
            ConfirmationDialog installFailedDialog = buildInstallFailedDialog();
            installFailedDialog.show();
            return;
        }

        Intent installIntent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
        installIntent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
        installIntent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
        installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        installIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        installIntent.setDataAndType(FileProvider.getUriForFile(activity, "de.symeda.sormas.fileprovider", file), "application/vnd.android.package-archive");
        activity.startActivityForResult(installIntent, AppUpdateController.INSTALL_RESULT);
    }

    /**
     * Checks whether the current download ID is part of the system's download query, i.e. it is currently being downloaded or scheduled to be downloaded.
     */
    private static boolean checkIfFileIsInDownloadQuery(Activity activity, Long currentAppDownloadId) {
        if (currentAppDownloadId != null) {
            DownloadManager downloadManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(currentAppDownloadId);
            if (downloadManager != null) {
                Cursor c = downloadManager.query(query);
                if (c != null) {
                    if (c.moveToFirst()) {
                        int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                        c.close();

                        switch (status) {
                            case DownloadManager.STATUS_SUCCESSFUL:
                            case DownloadManager.STATUS_FAILED:
                                return false;
                            case DownloadManager.STATUS_PAUSED:
                            case DownloadManager.STATUS_PENDING:
                            case DownloadManager.STATUS_RUNNING:
                                return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private void dismissExistingDialog() {
        if (displayedDialog != null) {
            displayedDialog.dismiss();
            displayedDialog = null;
        }
    }

}