package com.app.toado.activity.chat;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.SensorManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.support.v4.content.FileProvider;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.app.toado.FirebaseChat.AddCaptionChatPhoto;
import com.app.toado.R;
import com.app.toado.activity.chat.utils.BmpUtils;
import com.app.toado.activity.chat.utils.CaptureUtils;
import com.app.toado.activity.chat.view.CamPreview;
import com.app.toado.helper.GetTimeStamp;
import com.app.toado.helper.OpenFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.R.attr.version;

public class CamActivity extends Activity {

    private final int RC_GET_PICTURE = 301;
    private CamPreview mPreview;
    private RelativeLayout previewParent;
    private LinearLayout blackTop;
    private LinearLayout blackBottom;
    private ImageButton ibFlash;
    //    private ImageButton ibGrid;
    final String TAG = " CAMACTIVITY";

    private ImageView ivGridLines;
    private LinearLayout llGallery;
    private TextView textCamera;
//    private SeekBar sbZoom;

    private CaptureUtils captureUtils;

    private boolean flashEnabled = false;

    private int activeCamera = 0;

    private OrientationEventListener orientationListener;
    private int screenOrientation = 90;
    private int THRESHOLD = 30;
    public static boolean isClicked = false;
    private ImageButton ibFlipCamera;
    private String sender;
    private String mykey;
    private String otheruserkey;


    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide status-bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Hide title-bar, must be before setContentView
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_cam);

        sender = getIntent().getStringExtra("username");
        mykey = getIntent().getStringExtra("mykey");
        otheruserkey = getIntent().getStringExtra("otheruserkey");


        InitControls();

    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    void InitControls() {

        previewParent = (RelativeLayout) findViewById(R.id.rlPreview);
        blackTop = (LinearLayout) findViewById(R.id.llBlackTop);
        blackBottom = (LinearLayout) findViewById(R.id.llBlackBottom);
//        llGallery = (LinearLayout) findViewById(R.id.llGallery);

        ibFlash = (ImageButton) findViewById(R.id.ibFlash);
//        ibGrid
// final String TAG = " CAMACTIVITY=;
// (ImageButton) findViewById(R.id.ibGrid)
// final String TAG = " CAMACTIVITY;;

        ivGridLines = (ImageView) findViewById(R.id.ivGridLines);
        ibFlipCamera = (ImageButton) findViewById(R.id.ibFlipCamera);


        if (Camera.getNumberOfCameras() > 1) {
            ibFlipCamera.setVisibility(View.VISIBLE);
        } else {
            ibFlipCamera.setVisibility(View.GONE);
        }
        orientationListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_UI) {
            public void onOrientationChanged(int orientation) {
                if (isOrientation(orientation, 0))
                    screenOrientation = 0;
                else if (isOrientation(orientation, 90))
                    screenOrientation = 90;
                else if (isOrientation(orientation, 180))
                    screenOrientation = 180;
                else if (isOrientation(orientation, 270))
                    screenOrientation = 270;


            }
        };
    }

    protected boolean isOrientation(int orientation, int degree) {
        return (degree - THRESHOLD <= orientation && orientation <= degree + THRESHOLD);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isClicked = false;
//        checkLocationEnabled();
        int currentapiVersion = Build.VERSION.SDK_INT;


        orientationListener.enable();

        setupCamera(activeCamera);

//        if (sbZoom.getVisibility() == View.VISIBLE) {
//            sbZoom.setProgress(0);
//        }

        showLatestPhoto();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
        orientationListener.disable();
    }


    private void setupCamera(final int camera) {
        // Set the second argument by your choice.
        // Usually, 0 for back-facing camera, 1 for front-facing camera.
        // If the OS is pre-gingerbreak, this does not have any effect.
        try {
            mPreview = new CamPreview(this, camera, CamPreview.LayoutMode.NoBlank);// .FitToParent);
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }

        LayoutParams previewLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        // Un-comment below lines to specify the size.

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        int width = outMetrics.widthPixels;
        int height = outMetrics.heightPixels;

        previewLayoutParams.height = height;
        previewLayoutParams.width = width;

        // Un-comment below line to specify the position.
        mPreview.setCenterPosition(width / 2, height / 2);

        previewParent.addView(mPreview, 0, previewLayoutParams);

        // there is changes in calculations
        // camera preview image centered now to have actual image at center of
        // view
        int delta = height - width;
        int btHeight = 0;// blackTop.getHeight();
        int fix = delta - btHeight;
        int fix2 = 0;// fix / 4;

//        FrameLayout.LayoutParams blackBottomParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, fix / 2 + fix2);
//        blackBottomParams.gravity = Gravity.BOTTOM;
//        blackBottom.setLayoutParams(blackBottomParams);
//
//        FrameLayout.LayoutParams blackTopParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, fix / 2 - fix2);
//        blackTopParams.gravity = Gravity.TOP;
//        blackTop.setLayoutParams(blackTopParams);

        captureUtils = new CaptureUtils(mPreview.getCamera());
        if (captureUtils.isCameraFlashAvailable()) {
            captureUtils.toggleFlash(flashEnabled);
            ibFlash.setVisibility(View.VISIBLE);
            ibFlash.setImageResource(flashEnabled ? R.drawable.flash : R.drawable.flash_off);
        } else {
            ibFlash.setVisibility(View.GONE);
        }
        mPreview.setOnZoomCallback(new CamPreview.ZoomCallback() {
            @Override
            public void onZoomChanged(int progress) {
//                sbZoom.setProgress(progress);
            }
        });
    }
//        sbZoom.setVisibility(captureUtils.hasAutofocus() ? View.VISIBLE : View.GONE);
//
//        sbZoom.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//
//                if (mPreview != null) {
//                    mPreview.onProgressChanged(progress);
//                }
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//                if (mPreview != null && seekBar != null) {
//                    mPreview.onStopTrackingTouch(seekBar.getProgress());
//                }
//            }
//        });
//    }

    private void releaseCamera() {
        if (mPreview != null) {
            mPreview.stop();
            previewParent.removeView(mPreview); // This is necessary.
            mPreview = null;
        }
    }

    @SuppressLint("NewApi")
    public void flipClick(View view) {
        if (Build.VERSION.SDK_INT < 9)
            return;

        if (Camera.getNumberOfCameras() > 1) {

            activeCamera = activeCamera == 0 ? 1 : 0;
            releaseCamera();
            setupCamera(activeCamera);
        }
    }

    public void flashClick(View view) {
        if (!captureUtils.isCameraFlashAvailable())
            return;
        flashEnabled = !flashEnabled;
        captureUtils.toggleFlash(flashEnabled);
        ibFlash.setImageResource(flashEnabled ? R.drawable.flash : R.drawable.flash_off);
    }


    public void captureClick(View view) {
        try {
            if (!isClicked) {
                captureUtils.takeShot(jpegCallback);
                isClicked = true;
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void selectFromGallery(View view) {
        Intent iGetAvatar = new Intent(Intent.ACTION_PICK, Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(iGetAvatar, RC_GET_PICTURE);
    }


    PictureCallback jpegCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            FileOutputStream outStream = null;
            try {


                System.gc();
                Bitmap bmp = BmpUtils.getResampledBitmap(data, 800);
                bmp = BmpUtils.cropBitmapToSquare(bmp);

                // Write to file
                File file = OpenFile.createFile(CamActivity.this, GetTimeStamp.timeStamp() + ".jpg", "sent");
                // File file = new
                // File(Environment.getExternalStorageDirectory(), "cam.jpg");
//                outStream = new FileOutputStream(file);

//                String FILENAME = "tmp" + System.currentTimeMillis() + ".jpg";

                outStream = new FileOutputStream(file);

                bmp.compress(Bitmap.CompressFormat.JPEG, 60, outStream);
                // outStream.write(data);
                outStream.close();
                resetCam();

                String path = file.getAbsolutePath();
                int orientation = 0;
                int fix = 1;

                if (activeCamera == 0) {
                    ExifInterface ei = new ExifInterface(path);
                    orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    ei.setAttribute(ExifInterface.TAG_ORIENTATION, "90");
                    ei.saveAttributes();
                } else {
                    ExifInterface ei = new ExifInterface(path);
                    orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    ei.setAttribute(ExifInterface.TAG_ORIENTATION, "0");
                    ei.saveAttributes();
                    orientation = ExifInterface.ORIENTATION_ROTATE_270;
                    fix = -1;
                }

                Log.d(TAG, "orientation" + orientation);

                switch (orientation) {
                    case ExifInterface.ORIENTATION_UNDEFINED:
                        BmpUtils.rotateBitmap(path, normalizeRot(90 + screenOrientation));
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        BmpUtils.rotateBitmap(path, normalizeRot(90 + screenOrientation));
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        BmpUtils.rotateBitmap(path, normalizeRot(180 + screenOrientation));
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        BmpUtils.rotateBitmap(path, normalizeRot(270 + fix * screenOrientation));
                        break;
                    case ExifInterface.ORIENTATION_NORMAL:
                        BmpUtils.rotateBitmap(path, normalizeRot(screenOrientation));
                        break;
                }



                selectCategory(path);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }
        }
    };

    protected void selectCategory(String path) {
        Toast.makeText(this, path, Toast.LENGTH_SHORT).show();

     // String url=  CamActivity.getPath(getApplication(),Uri.parse(path));
        File file = new File(path);

        Uri outputFileUri;
        if(version < 24){

            outputFileUri = Uri.fromFile(file);
        } else {

            outputFileUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider",
                    file);
        }
       // Toast.makeText(this, outputFileUri.toString(), Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, AddCaptionChatPhoto.class);
        intent.putExtra("imageUrl", outputFileUri.toString());
        intent.putExtra("username", sender);
        intent.putExtra("otheruserkey", otheruserkey);
        intent.putExtra("mykey", mykey);
        startActivity(intent);
        finish();
    }

    protected void resetCam() {
        Camera camera = mPreview.getCamera();
        camera.startPreview();
        showLatestPhoto();
    }

    protected int normalizeRot(int rot) {
        if (rot < 0)
            rot += 360;
        if (rot > 360)
            rot -= 360;
        return rot;
    }


    /**
     * Returns how much we have to rotate
     */
    public int rotationForImage(Uri uri) {
        try {
            if (uri.getScheme().equals("content")) {
                // From the media gallery
                String[] projection = {Images.ImageColumns.ORIENTATION};
                Cursor c = getContentResolver().query(uri, projection, null, null, null);
                if (c.moveToFirst()) {
                    return c.getInt(0);
                }
            } else if (uri.getScheme().equals("file")) {
                // From a file saved by the camera
                ExifInterface exif = new ExifInterface(uri.getPath());
                int rotation = (int) exifOrientationToDegrees(exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL));
                return rotation;
            }
            return 0;

        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Get rotation in degrees
     */
    private static int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private void showLatestPhoto() {
        String[] projection = new String[]{Images.ImageColumns._ID, Images.ImageColumns.DATA,
                Images.ImageColumns.BUCKET_DISPLAY_NAME, Images.ImageColumns.DATE_TAKEN, Images.ImageColumns.MIME_TYPE};
        @SuppressWarnings("deprecation")
        final Cursor cursorE = managedQuery(Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, Images.ImageColumns.DATE_TAKEN
                + " DESC");

        @SuppressWarnings("deprecation")
        final Cursor cursorI = managedQuery(Images.Media.INTERNAL_CONTENT_URI, projection, null, null, Images.ImageColumns.DATE_TAKEN
                + " DESC");

        String imageLocation = null;
        long udate = 0;

        if (cursorE.moveToFirst()) {
            udate = cursorE.getLong(3);
            imageLocation = cursorE.getString(1);
        }

        if (cursorI.moveToFirst()) {
            long iudate = cursorI.getLong(3);
            if (iudate > udate)
                imageLocation = cursorI.getString(1);
        }

        if (imageLocation == null)
            return;

////        final ImageView imageView = (ImageView) findViewById(R.id.ivGallery);
//        int rot = rotationForImage(Uri.parse("file://" + imageLocation));
//        File imageFile = new File(imageLocation);
//        if (imageFile.exists()) {
//            Bitmap bm = BmpUtils.getResampledBitmap(imageLocation, 100);
//            if (rot != 0)
//                bm = BmpUtils.rotateBitmap(bm, rot, 100, 100);
//            imageView.setImageBitmap(bm);
//        }
    }



    private static String getPath(Application application, Uri imageUri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(application, imageUri)) {

            // ExternalStorageProvider
            if (isExternalStorageDocument(imageUri)) {
                final String docId = DocumentsContract.getDocumentId(imageUri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(imageUri)) {

                final String id = DocumentsContract.getDocumentId(imageUri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(application, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(imageUri)) {
                final String docId = DocumentsContract.getDocumentId(imageUri);
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
                final String[] selectionArgs = new String[] { split[1] };

                return getDataColumn(application, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(imageUri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(imageUri))
                return imageUri.getLastPathSegment();

            return getDataColumn(application, imageUri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
            return imageUri.getPath();
        }

        return null;
    }



    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
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
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }





    public void gridClick(View v) {


//        boolean visible = ivGridLines.isShown();
//        ivGridLines.setVisibility(visible ? View.GONE : View.VISIBLE);
    }

}
