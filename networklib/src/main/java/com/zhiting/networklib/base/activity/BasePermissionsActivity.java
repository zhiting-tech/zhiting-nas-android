package com.zhiting.networklib.base.activity;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.zhiting.networklib.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class BasePermissionsActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {

    private boolean isInitFocus = false;  //记录是否已经初始化获得了焦点，PopupWindow必须在Activity获取焦点后才能显示。
    private boolean isDestroyed = false;    //记录activity是否已经被销毁了

    /*权限相关*/
    public final int BASE_PERMISSION_REQUEST = 100;
    public final int WRITE_STORAGE_PERMISSION_REQUEST = 200;
    protected String[] permissions = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_WIFI_STATE,
            //android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroyed = true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            isInitFocus = true;
        }
    }

    @Override
    public boolean isDestroyed() {
        return isDestroyed;
    }

    public boolean isInitFocus() {
        return isInitFocus;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @androidx.annotation.NonNull @NotNull String[] permissions, @androidx.annotation.NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull @NotNull List<String> perms) {
        Log.d("TAG", "onPermissionsGranted:" + requestCode + ":" + perms.size());
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull @NotNull List<String> perms) {
        Log.d("TAG", "onPermissionsDenied:" + requestCode + ":" + perms.size());

        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    public void onRationaleAccepted(int requestCode) {

    }

    @Override
    public void onRationaleDenied(int requestCode) {

    }

    @AfterPermissionGranted(BASE_PERMISSION_REQUEST)
    public void basePermissionTask() {
        if (hasPermissions()) {
            // Have permissions, do the thing!
            hasPermissionTodo();
        } else {
            // Ask for both permissions
            EasyPermissions.requestPermissions(
                    this,
                    getResources().getString(R.string.permission_apply),
                    BASE_PERMISSION_REQUEST,
                    permissions);
        }
    }

    private boolean hasPermissions() {
        return EasyPermissions.hasPermissions(this, permissions);
    }

    protected void hasPermissionTodo() {

    }
}
