package com.rnfstudio.ytdl;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by roger_huang on 2018/2/7.
 */

public class ExtractService extends Service {
    // ------------------------------------------------------------------------
    // STATIC FIELDS
    // ------------------------------------------------------------------------
    private static final String TAG = "ExtractService";
    private static final boolean DEBUG = true;

    // asynchronous worker thread and handler
    private static final String sWorkerDisplayName = "Extract worker";
    private static final HandlerThread sWorkerThread;
    private static final Handler sWorker;

    // ------------------------------------------------------------------------
    // STATIC INITIALIZERS
    // ------------------------------------------------------------------------
    static {
        sWorkerThread = new HandlerThread(sWorkerDisplayName,
                android.os.Process.THREAD_PRIORITY_BACKGROUND);
        sWorkerThread.start();
        sWorker = new Handler(sWorkerThread.getLooper());
    }

    // ------------------------------------------------------------------------
    // STATIC METHODS
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // METHODS
    // ------------------------------------------------------------------------
    @Override
    public IBinder onBind(Intent intent) {
        if (DEBUG) Log.v(TAG, "[onBind] called");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (DEBUG) Log.v(TAG, "[onStartCommand] called");

        return START_NOT_STICKY;
    }
}
