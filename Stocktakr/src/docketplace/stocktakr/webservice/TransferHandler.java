package docketplace.stocktakr.webservice;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class TransferHandler extends Handler {
	public static final int START = 0;
	public static final int START_INDETERMINATE = 1;
	public static final int BEGIN = 2;
	public static final int TRANSFER = 3;
	public static final int COMPLETE = 4;
	public static final int ERROR = 5;
	public static final int LOGIN_SUCCESS = 6;
	public static final int INCORRECT_STOREID = 7;
	public static final int INCORRECT_PASSWORD = 8;
	public static final int FINISH = 9;
	
	
	private ProgressDialog progress;
	
	private Activity activity;
	
	private String title, completedMessage, errorMessage;
	
	public TransferHandler(Activity activity, String title, String completedMessage, String errorMessage) {
		progress = new ProgressDialog(activity);
		
		this.activity         = activity;
		this.title            = title;
		this.completedMessage = completedMessage;
		this.errorMessage     = errorMessage;
	}
	
	@Override
	public void handleMessage(Message msg) {
	    int type=msg.what;
	    int arg=msg.arg1;
	    
	    switch (type) {
		    case START_INDETERMINATE:
	    		Log.d("PROGRESS", "starting indeterminate: " + title);
	    		progress.setIndeterminate(true);
	    		progress.setTitle("Stocktakr");
	    		progress.setMessage(title);
	    		progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	    		progress.setCanceledOnTouchOutside(false);
	    		progress.setCancelable(false);
	    		progress.show();
	    		break;
	    
	    	case START:
	    		Log.d("PROGRESS", "starting: " + title);
	    		progress.setProgress(0);
	    		progress.setIndeterminate(false);
	    		//progress.setIndeterminate(true);
	    		progress.setTitle("Stocktakr");
	    		progress.setMessage(title);
	    		progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	    		//progress.setProgressNumberFormat(null);
	    		//progress.setProgressPercentFormat(null);
	    		progress.setCanceledOnTouchOutside(false);
	    		progress.setCancelable(false);
	    		progress.show();
	    		break;
	    			    
	    	case BEGIN:
    			Log.d("PROGRESS", "begin: " + arg);
    			//progress.setIndeterminate(false);
    			//progress.setProgressPercentFormat(NumberFormat.getPercentInstance());
    			
    			progress.setMax(arg);
	    		progress.setProgress(0);
	    		
	    		break;
	    		
	    	case TRANSFER:
	    		Log.d("PROGRESS", "transfer: " + arg);
	    		progress.setProgress(arg);
	    		
	    		break;
	    		
	    	case COMPLETE:
	    		Log.d("PROGRESS", "complete");;

	    		progress.dismiss();
	    		
	    		Toast.makeText(activity, completedMessage, Toast.LENGTH_SHORT).show();	    		
	    		break;
	    		
	    	case ERROR:
	    		Log.d("PROGRESS", "error");
	    		progress.dismiss();
	    		
	    		Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show();
	    		
	    		break;

	    	case LOGIN_SUCCESS:
	    		Log.d("PROGRESS", "login success");
	    		
	    		progress.dismiss();
	    		
	    		showAlert("Login Success", "Store ID and Password are correct");
	    		
	    		break;
	    		
	    	case INCORRECT_STOREID:
	    		Log.d("PROGRESS", "incorrect store id");
	    		
	    		progress.dismiss();
	    		
	    		showAlert("Login Error", "Incorrect Store ID");
	    		
	    		break;
	    		
	    	case INCORRECT_PASSWORD:
	    		Log.d("PROGRESS", "incorrect password");
	    		
	    		progress.dismiss();
	    		
	    		showAlert("Login Error", "Incorrect Password");
	    		
	    		break;
	    		
	    	case FINISH:
	    		Log.d("PROGRESS", "finish");

	    		progress.dismiss();
	    		
	    		break;
	    		
	    	default:
	    }
	}

	private void showAlert(String title, String message) {
		final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
		alertDialog.setTitle(title);
		alertDialog.setMessage(message);

		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface di6alog, int which) {
					alertDialog.cancel();
				}
		});

		alertDialog.show();
	}
	
}
