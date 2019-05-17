package com.example.odm.wanandroid.receiver;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

import com.example.odm.wanandroid.base.HandlerManger;

/**
 * Created by ODM on 2019/5/14.
 */

public class InterRecevier extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isAvailable()) {
            HandlerManger.getInstance().sendSuccessMessage();
        } else {
            Dialog(context); //弹出对话框
        }
    }

    /**
     * 对话框的属性
     * @param context
     */
    private void Dialog (final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("提示");
        builder.setMessage("网络中断");
        builder.setCancelable(false);//不可以取消
        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //context.startActivity(new Intent("android.net.wifi.PICK_WIFI_NETWORK")); //跳转到手机WIFI设置
                context.startActivity(new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS)); //跳转到手机流量设置
            }
        });
        builder.setNegativeButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
