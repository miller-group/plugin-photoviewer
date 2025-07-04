package mh.plugins.photo_viewer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Class to Open PhotoViewer with the Required Parameters from Cordova
 * <p>
 * - URL
 * - Title
 */
public class PhotoViewer extends CordovaPlugin {

    public static final int PERMISSION_DENIED_ERROR = 20;

    public static final String WRITE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final String READ = Manifest.permission.READ_EXTERNAL_STORAGE;
    public static final String READ_IMAGES = Manifest.permission.READ_MEDIA_IMAGES;

    public static final int REQ_CODE = 0;

    protected JSONArray args;
    protected CallbackContext callbackContext;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("show")) {
            this.args = args;
            this.callbackContext = callbackContext;
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (cordova.hasPermission(READ_IMAGES)) {
                    this.launchActivity();
                } else {
                    this.getPermission();
                }
            } else {
                if (cordova.hasPermission(READ) && cordova.hasPermission(WRITE)) {
                    this.launchActivity();
                } else {
                    this.getPermission();
                }
            }
            return true;
        }
        return false;
    }

    protected void getPermission() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            cordova.requestPermissions(this, REQ_CODE, new String[] { READ_IMAGES });
        } else {
            cordova.requestPermissions(this, REQ_CODE, new String[] { WRITE, READ });
        }
    }

    //
    protected void launchActivity() throws JSONException {
        Intent i = new Intent(this.cordova.getActivity(), mh.plugins.photo_viewer.PhotoActivity.class);
        PhotoActivity.mArgs = this.args;

        this.cordova.getActivity().startActivity(i);
        this.callbackContext.success("");
    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions,
            int[] grantResults) throws JSONException {
        for (int r : grantResults) {
            if (r == PackageManager.PERMISSION_DENIED) {
                this.callbackContext
                        .sendPluginResult(new PluginResult(PluginResult.Status.ERROR, PERMISSION_DENIED_ERROR));
                return;
            }
        }

        switch (requestCode) {
            case REQ_CODE:
                launchActivity();
                break;
        }

    }

}
