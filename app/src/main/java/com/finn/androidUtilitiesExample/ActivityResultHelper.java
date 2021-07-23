package com.finn.androidUtilitiesExample;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.finn.androidUtilities.CustomUtility;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ActivityResultHelper extends AppCompatActivity {
    public static final int FILE_SELECT_CODE = 12345;
    public static final int MULTI_FILE_SELECT_CODE = 24680;
    public static final int FOLDER_SELECT_CODE = 6789;
    public static final int PERMISSION_REQUEST_CODE = 13468;
    public static final String EXTRA_UUID = "EXTRA_UUID";

    private static Map<String, Pair<CustomUtility.GenericInterface<AppCompatActivity>, Object>> requestMap = new HashMap<>();
    private String uuid;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uuid = getIntent().getStringExtra(EXTRA_UUID);

        if (!CustomUtility.stringExists(uuid))
            return;

        if (requestMap.containsKey(uuid))
            requestMap.get(uuid).first.runGenericInterface(this);
    }


    //  ------------------------- Generic ------------------------->
    public interface OnGenericRequestResult {
        void runOnGenericRequestResult(int requestCode, int resultCode, Intent data);
    }

    public static void addGenericRequest(AppCompatActivity context, CustomUtility.GenericInterface<AppCompatActivity> startActivity, OnGenericRequestResult result) {
        String uuid = UUID.randomUUID().toString();
        requestMap.put(uuid, Pair.create(startActivity, result));
        context.startActivity(new Intent(context, ActivityResultHelper.class).putExtra(EXTRA_UUID, uuid));
    }
    //  <------------------------- Generic -------------------------


    //  ------------------------- FileChooser ------------------------->
    @SafeVarargs
    public static void addFileChooserRequest(AppCompatActivity context, String mimeType, CustomUtility.GenericInterface<Intent>... onUriResult_Fail) {
        String uuid = UUID.randomUUID().toString();
//        if (onUriResult_Fail.length > 0) {
//            CustomUtility.GenericInterface<Object> oldInterface = onUriResult_Fail[0];
//            onUriResult_Fail[0] = o -> {
//                Intent data = (Intent) o;
//                Uri uri = data.getData();
//                if (uri != null)
//                    context.getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//                oldInterface.runGenericInterface(o);
//            };
//        }
        requestMap.put(uuid, Pair.create(dummyActivity -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT).setType(mimeType).addCategory(Intent.CATEGORY_OPENABLE);
            dummyActivity.startActivityForResult(Intent.createChooser(intent, "Datei Auswählen"), FILE_SELECT_CODE);
        }, onUriResult_Fail));

        context.startActivity(new Intent(context, ActivityResultHelper.class).putExtra(EXTRA_UUID, uuid));
    }

    @SafeVarargs
    public static void addMultiFileChooserRequest(AppCompatActivity context, String mimeType, CustomUtility.GenericInterface<Intent>... onUriResult_Fail) {
        String uuid = UUID.randomUUID().toString();

        requestMap.put(uuid, Pair.create(dummyActivity -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT).setType(mimeType).putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            dummyActivity.startActivityForResult(Intent.createChooser(intent, "Dateien Auswählen"), MULTI_FILE_SELECT_CODE);
        }, onUriResult_Fail));

        context.startActivity(new Intent(context, ActivityResultHelper.class).putExtra(EXTRA_UUID, uuid));
    }
    //  <------------------------- FileChooser -------------------------

    //  ------------------------- FolderChooser ------------------------->
    @SafeVarargs
    public static void addFolderChooserRequest(AppCompatActivity context, CustomUtility.GenericInterface<Intent>... onUriResult_Fail) {
        String uuid = UUID.randomUUID().toString();
//        if (onUriResult_Fail.length > 0) {
//            CustomUtility.GenericInterface<Intent> oldInterface = onUriResult_Fail[0];
//            onUriResult_Fail[0] = o -> {
//                Intent data = o;
//                Uri uri = data.getData();
//                if (uri != null)
//                    context.getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//                oldInterface.runGenericInterface(o);
//            };
//        }
        requestMap.put(uuid, Pair.create(dummyActivity -> {
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            i.addCategory(Intent.CATEGORY_DEFAULT);
            dummyActivity.startActivityForResult(Intent.createChooser(i, "Ordner Auswählen"), FOLDER_SELECT_CODE);
        }, onUriResult_Fail));

        context.startActivity(new Intent(context, ActivityResultHelper.class).putExtra(EXTRA_UUID, uuid));
    }
    //  <------------------------- FolderChooser -------------------------

    //  ------------------------- Permissions ------------------------->
    public interface OnPermissionsRequestResult {
        void runOnPermissionsRequestResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
    }

    public static void addPermissionRequest(AppCompatActivity activity, String[] permissions, OnPermissionsRequestResult requestResult) {
        String uuid = UUID.randomUUID().toString();
        requestMap.put(uuid, Pair.create(dummyActivity -> {
            dummyActivity.requestPermissions(permissions, PERMISSION_REQUEST_CODE);
        }, requestResult));
        activity.startActivity(new Intent(activity, ActivityResultHelper.class).putExtra(EXTRA_UUID, uuid));
    }

    public static boolean hasPermission(Context context, String perm) {
        return (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context, perm));
    }

    // ---------------

    public static void takePersistableUriPermission(Context context, Uri uri) {
        context.getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
    }
    //  <------------------------- Permissions -------------------------

    //  ------------------------- OnResults ------------------------->
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FOLDER_SELECT_CODE:
            case FILE_SELECT_CODE:
            case MULTI_FILE_SELECT_CODE:
                CustomUtility.GenericInterface<Object>[] interfaces = (CustomUtility.GenericInterface<Object>[]) requestMap.get(uuid).second;
                if (resultCode == RESULT_OK) {
                    if (interfaces.length > 0)
                        interfaces[0].runGenericInterface(data);
                } else {
                    if (interfaces.length > 1)
                        interfaces[1].runGenericInterface(null);
                }
                break;
            default:
                ((OnGenericRequestResult) requestMap.get(uuid).second).runOnGenericRequestResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ((OnPermissionsRequestResult) requestMap.get(uuid).second).runOnPermissionsRequestResult(requestCode, permissions, grantResults);

        finish();
//        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
////            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
////            if (VersionControl.hasPermissions(this, true))
////                updateApp();
//        }
    }
    //  <------------------------- OnResults -------------------------


    //  ------------------------- pathFromUri|Intent ------------------------->
    public static String getDocumentPathFromIntent(Context context, Intent intent) {
        return getDocumentPathFromUri(context, intent.getData());
    }

    public static String getDocumentPathFromUri(Context context, Uri uri) {
        Uri docUri = DocumentsContract.buildDocumentUriUsingTree(uri, DocumentsContract.getTreeDocumentId(uri));
        return ActivityResultHelper.getPath(context, docUri);
    }
    //  <------------------------- pathFromUri|Intent -------------------------


    //  ------------------------- GetPath ------------------------->
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + (split.length > 1 ? "/" + split[1] : "");
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
    //  <------------------------- GetPath -------------------------

}
