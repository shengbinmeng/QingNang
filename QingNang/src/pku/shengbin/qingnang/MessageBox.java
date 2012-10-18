package pku.shengbin.qingnang;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class MessageBox {
	
	public static void showCancelable(Context context, String title, String msg, DialogInterface.OnClickListener listener) {
		new AlertDialog.Builder(context)  
        .setMessage(msg)  
        .setTitle(title)
        .setNegativeButton(android.R.string.cancel, listener) 
        .setPositiveButton(android.R.string.ok, listener)
        .show();
	}
	
	public static void show(Context context, String title, String msg) {
		new AlertDialog.Builder(context)  
        .setMessage(msg)  
        .setTitle(title)  
        .setCancelable(false)  
        .setPositiveButton(android.R.string.ok, null)
        .show();
	}
	
	public static void show(Context context, Exception ex) {
		show(context, "Error", ex.getMessage());
	}

}
