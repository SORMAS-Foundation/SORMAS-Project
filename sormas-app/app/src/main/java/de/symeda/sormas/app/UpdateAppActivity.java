package de.symeda.sormas.app;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.File;
import java.util.Date;

import de.symeda.sormas.api.utils.DateHelper;

/**
 * Created by Mate Strysewske on 06.02.2018.
 */

public class UpdateAppActivity extends AppCompatActivity {

    public static final String APP_URL = "appUrl";
    public static final String CALLING_ACTIVITY = "callingActivity";

    private DownloadManager downloadManager;
    private long downloadReference;
    private String fileName;
    private String appUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle params = getIntent().getExtras();
        appUrl = params.getString(APP_URL);

        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadReceiver, filter);

        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Uri downloadUri = Uri.parse(appUrl);
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
        fileName = "sormas-debug.apk";
        request.setTitle(fileName);
//        File filePath = new File(Environment.DIRECTORY_DOWNLOADS, "");
        File newFile = new File("", fileName);
        request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, newFile.getPath());
        downloadReference = downloadManager.enqueue(request);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "Install succeeded!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Install canceled!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Install Failed!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (downloadReference == referenceId) {
                Intent installIntent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
//                File filePath = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "");
                File newFile = new File(Environment.DIRECTORY_DOWNLOADS, fileName);
                installIntent.setData(FileProvider.getUriForFile(context, "de.symeda.sormas.fileprovider", newFile));
                installIntent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                installIntent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                installIntent.putExtra(Intent.EXTRA_INSTALLER_PACKAGE_NAME, getApplicationInfo().packageName);
                installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(installIntent, 1);
                unregisterReceiver(this);
            }
        }
    };

}
