package com.rnfstudio.ytdl;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;

/**
 * Created by roger_huang on 2018/2/7.
 */

public class Permission {

    public static final String[] PERMISSIONS_DOWNLOAD = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    public static final int REQUEST_CODE_DOWNLOAD = 1;

    // ------------------------------------------------------------------------
    // STATIC METHODS
    // ------------------------------------------------------------------------
    public static boolean check(Context context, ArrayList<String> permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkAndRequest(Activity activity, ArrayList<String> permissions, int requestCode) {
        ArrayList<String> notGrantedPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                notGrantedPermissions.add(permission);
            }
        }

        if (notGrantedPermissions.size() > 0) {
            String[] permissionArray = notGrantedPermissions.toArray(new String[notGrantedPermissions.size()]);
            ActivityCompat.requestPermissions(activity, permissionArray, requestCode);
            return false;
        }
        return true;

    }
}
