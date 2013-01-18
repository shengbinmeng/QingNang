package pku.shengbin.qingnang;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import pku.shengbin.qingnang.QNMedicineManager.QNMedicine;
import pku.shengbin.qingnang.QNUserManager.QNUser;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ResultParser;

import com.zijunlin.Zxing.Demo.CaptureActivity;
import com.zijunlin.Zxing.Demo.decoding.InactivityTimer;
import com.zijunlin.Zxing.Demo.view.ViewfinderView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;

import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MedicineAddActivity extends CaptureActivity implements SurfaceHolder.Callback {

	private static final Set<ResultMetadataType> DISPLAYABLE_METADATA_TYPES =
			EnumSet.of(ResultMetadataType.ISSUE_NUMBER,
					ResultMetadataType.SUGGESTED_PRICE,
					ResultMetadataType.ERROR_CORRECTION_LEVEL,
					ResultMetadataType.POSSIBLE_COUNTRY);
	
	private ViewfinderView viewfinderView;
	private InactivityTimer inactivityTimer;
	
	private TextView statusView;
	private View resultView;
	private Button manuallyButton;

	private QNUser mUser;
	private QNMedicine mMedicine;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.capture);
		
		String userID = this.getIntent().getStringExtra("pku.shengbin.qingnang.userID");
        mUser = QNUserManager.getUserById(userID);
        
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
	    resultView = findViewById(R.id.result_view);
	    statusView = (TextView) findViewById(R.id.status_view);
	    manuallyButton = (Button) findViewById(R.id.manually_button);
		inactivityTimer = new InactivityTimer(this);
		
		manuallyButton.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				final ArrayList<String> autoNames = new ArrayList<String>();
	        	autoNames.add("Medicine");
	        	final AutoCompleteTextView nameEdit = new AutoCompleteTextView(MedicineAddActivity.this);
	        	ArrayAdapter<String> adapter = new ArrayAdapter<String>(MedicineAddActivity.this,
	                    android.R.layout.simple_dropdown_item_1line, autoNames.toArray(new String[0]));
	        	nameEdit.setAdapter(adapter);
	        	nameEdit.setThreshold(1);

	        	DialogInterface.OnClickListener ok_listener = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						String input = nameEdit.getText().toString();
						if (input.equals("")) {
							MessageBox.show(MedicineAddActivity.this, "提示", "请输入内容!");
							return ;
						}
						
						QNMedicine med = QNMedicineManager.getMedicineByID(input);
						if(med != null) {
							mUser.medicines.add(med);
							mMedicine = med;
							Toast.makeText(MedicineAddActivity.this, "添加药品成功!", Toast.LENGTH_LONG).show();
							MedicineAddActivity.this.finish();
						} else {
							MessageBox.show(MedicineAddActivity.this, "出错了", "未成功添加药品!");
						}
					}
	        	};

	        	new AlertDialog.Builder(MedicineAddActivity.this)
	        	.setTitle("输入药品条形码值:")
	        	.setIcon(android.R.drawable.ic_input_get)
	        	.setView(nameEdit)
	        	.setPositiveButton("确定", ok_listener)
	        	.setNegativeButton("取消", null)
	        	.show();
			}
			
		});
	}


	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();
	}
	
	public void handleDecode(Result rawResult, Bitmap barcode) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		viewfinderView.drawResultBitmap(barcode);
	    drawResultPoints(barcode, rawResult);
	
	    ParsedResult result = ResultParser.parseResult(rawResult);
	    handleDecodeInternally(rawResult, result, barcode);
	}
	
	/**
	 * Superimpose a line for 1D or dots for 2D to highlight the key features of the barcode.
	 *
	 * @param barcode   A bitmap of the captured image.
	 * @param rawResult The decoded results which contains the points to draw.
	 */
	private void drawResultPoints(Bitmap barcode, Result rawResult) {
		ResultPoint[] points = rawResult.getResultPoints();
		if (points != null && points.length > 0) {
			Canvas canvas = new Canvas(barcode);
			Paint paint = new Paint();
			paint.setColor(getResources().getColor(R.color.result_image_border));
			paint.setStrokeWidth(3.0f);
			paint.setStyle(Paint.Style.STROKE);
			Rect border = new Rect(2, 2, barcode.getWidth() - 2, barcode.getHeight() - 2);
			canvas.drawRect(border, paint);

			paint.setColor(getResources().getColor(R.color.result_points));
			if (points.length == 2) {
				paint.setStrokeWidth(4.0f);
				drawLine(canvas, paint, points[0], points[1]);
			} else if (points.length == 4 &&
					(rawResult.getBarcodeFormat() == BarcodeFormat.UPC_A ||
					rawResult.getBarcodeFormat() == BarcodeFormat.EAN_13)) {
				// Hacky special case -- draw two lines, for the barcode and metadata
				drawLine(canvas, paint, points[0], points[1]);
				drawLine(canvas, paint, points[2], points[3]);
			} else {
				paint.setStrokeWidth(10.0f);
				for (ResultPoint point : points) {
					canvas.drawPoint(point.getX(), point.getY(), paint);
				}
			}
		}
	}

	private static void drawLine(Canvas canvas, Paint paint, ResultPoint a, ResultPoint b) {
		canvas.drawLine(a.getX(), a.getY(), b.getX(), b.getY(), paint);
	}

	// Put up our own UI for how to handle the decoded contents.
	private void handleDecodeInternally(Result rawResult, ParsedResult parsedResult, Bitmap barcode) {
		statusView.setVisibility(View.GONE);
		manuallyButton.setVisibility(View.GONE);
		viewfinderView.setVisibility(View.GONE);
		resultView.setVisibility(View.VISIBLE);

		ImageView barcodeImageView = (ImageView) findViewById(R.id.barcode_image_view);
		if (barcode == null) {
			barcodeImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(),
					R.drawable.ic_launcher));
		} else {
			barcodeImageView.setImageBitmap(barcode);
		}

		TextView formatTextView = (TextView) findViewById(R.id.format_text_view);
		formatTextView.setText(rawResult.getBarcodeFormat().toString());

		TextView typeTextView = (TextView) findViewById(R.id.type_text_view);
		typeTextView.setText(parsedResult.getType().toString());

		DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
		String formattedTime = formatter.format(new Date(rawResult.getTimestamp()));
		TextView timeTextView = (TextView) findViewById(R.id.time_text_view);
		timeTextView.setText(formattedTime);

		TextView metaTextView = (TextView) findViewById(R.id.meta_text_view);
		View metaTextViewLabel = findViewById(R.id.meta_text_view_label);
		metaTextView.setVisibility(View.GONE);
		metaTextViewLabel.setVisibility(View.GONE);
		Map<ResultMetadataType,Object> metadata = rawResult.getResultMetadata();
		if (metadata != null) {
			StringBuilder metadataText = new StringBuilder(20);
			for (Map.Entry<ResultMetadataType,Object> entry : metadata.entrySet()) {
				if (DISPLAYABLE_METADATA_TYPES.contains(entry.getKey())) {
					metadataText.append(entry.getValue()).append('\n');
				}
			}
			if (metadataText.length() > 0) {
				metadataText.setLength(metadataText.length() - 1);
				metaTextView.setText(metadataText);
				metaTextView.setVisibility(View.VISIBLE);
				metaTextViewLabel.setVisibility(View.VISIBLE);
			}
		}

		TextView contentsTextView = (TextView) findViewById(R.id.contents_text_view);
	    String contents = parsedResult.getDisplayResult();
		CharSequence displayContents = contents.replace("\r", "");
		contentsTextView.setText(displayContents);
		// Crudely scale betweeen 22 and 32 -- bigger font for shorter text
		int scaledSize = Math.max(22, 32 - displayContents.length() / 4);
		contentsTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, scaledSize);

		boolean isMedicineID = true;
		switch (parsedResult.getType()) {
	    case ADDRESSBOOK:
	    case EMAIL_ADDRESS:
	    case URI:
	    case WIFI:
	    case GEO:
	    case TEL:
	    case SMS:
	    case CALENDAR:
	    case ISBN:
	    	isMedicineID = false;
	    case PRODUCT:
	    default:
	    }
		
		String message = "";
		boolean addSuccess = true;
		if (isMedicineID) {
			QNMedicine med = QNMedicineManager.getMedicineByID(contents);
			if(med != null) {
				mUser.medicines.add(med);
				mMedicine = med;
				message = "成功添加药品： " + contents + "的名称";
			} else {
				addSuccess = false;
				message = "未成功添加药品";
			}
		} else {
			message = "扫描到的不是药品条形码!";
		}
		
		TextView supplementTextView = (TextView) findViewById(R.id.contents_supplement_text_view);
		supplementTextView.setText(message);
		supplementTextView.setOnClickListener(null);
		
		int buttonCount = 3;
		if (addSuccess == false) buttonCount = 2; 
		ViewGroup buttonView = (ViewGroup) findViewById(R.id.result_button_view);
		buttonView.requestFocus();
		// can set at most 4 buttons 
		for (int x = 0; x < 4; x++) {
			Button button = (Button) buttonView.getChildAt(x);
			if (x < buttonCount) {
				button.setVisibility(View.VISIBLE);
				button.setOnClickListener(new ResultButtonListener(button, x));
			} else {
				button.setVisibility(View.GONE);
			}
			
			if (x == 0) {
				button.setText("返回药箱");
			} else if (x == 1) {
				button.setText("继续添加");
			} else if (x == 2) {
				button.setText("设置提醒");
			}
		}
	}
	
	public final class ResultButtonListener implements Button.OnClickListener {
		private final int index;
		public ResultButtonListener(View view, int index) {
			this.index = index;
		}

		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (index == 0) {
				MedicineAddActivity.this.finish();
			} else if (index == 1) {
				restartScan();
			} else if (index == 2) {
		    	Intent i = new Intent(MedicineAddActivity.this, ReminderEditActivity.class);
		    	i.putExtra("pku.shengbin.qingnang.medicineID", mMedicine.id);
		    	i.putExtra("pku.shengbin.qingnang.userID", mUser.id);
		    	startActivity(i);
			}
		}
	}
	
	private void restartScan () {
		
		statusView.setVisibility(View.VISIBLE);
		manuallyButton.setVisibility(View.VISIBLE);
		viewfinderView.setVisibility(View.VISIBLE);
		resultView.setVisibility(View.GONE);
		super.handler = null;
		super.onResume();
	}

	/*
	private void restartActivity() {
		Intent intent = getIntent();
	    overridePendingTransition(0, 0);
	    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
	    finish();
	    overridePendingTransition(0, 0);
	    startActivity(intent);
	}
	*/
}