package cx.mobilechecksh.mvideo.picc.ui;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

import cx.mobilechecksh.R;


public class Dialog {

	/**
	 * 只有退出按钮
	 * 
	 * @param context
	 *            　当前上下文
	 */
	public static void negativeDialog(Context context, String mess) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.dialog_title);
		builder.setMessage(mess);
		builder.setNegativeButton(R.string.dialog_positive,
				new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});

		builder.create().show();
	}

	/**
	 * 带输入框的对话框
	 * 
	 * @param context
	 *            　当前上下文
	 * @param mess
	 *            　title提示信息
	 */
	public static void edittextDialog(Context context, String titlemess, EditText editText, AlertDialog.OnClickListener listener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(titlemess);
		builder.setView(editText);
		builder.setNegativeButton(R.string.dialog_negative,
				new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});
		builder.setPositiveButton(R.string.dialog_positive, listener);

		builder.create().show();
	}
	
	/**
	 * 只有取消
	 * @param context
	 * @param mess
	 * @param listener
	 */
	public static void positiveDialog(Context context, String mess, AlertDialog.OnClickListener listener ){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.dialog_title);
		builder.setMessage(mess);
		builder.setPositiveButton(R.string.dialog_positive, listener);
		builder.create().show();
	}
	
	
	/**
	 * 只有退出按钮
	 * 
	 * @param context
	 *            　当前上下文
	 */
	public static void negativeAndPositiveDialog(Context context, String mess , AlertDialog.OnClickListener listener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.dialog_title);
		builder.setMessage(mess);
		builder.setPositiveButton(R.string.dialog_positive, listener);
		builder.setNegativeButton(R.string.dialog_negative,
				new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});
		builder.create().show();
	}
	
	/**
	 * 单选对话框
	 * 
	 * @param context
	 * @param titlemess 提示语
	 * @param checks 选择的内容
	 */
	public static void checkButtonDialog(Context context, String titlemess, String[] checks , int checked, AlertDialog.OnClickListener SingleChoiceItems){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(titlemess);
		builder.setIcon(android.R.drawable.ic_dialog_info);	
		builder.setSingleChoiceItems(checks, checked, SingleChoiceItems);
		builder.setPositiveButton(R.string.dialog_positive, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		builder.create().show();
	}
	
}
