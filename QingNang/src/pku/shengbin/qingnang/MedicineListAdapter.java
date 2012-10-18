package pku.shengbin.qingnang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pku.shengbin.qingnang.QNMedicineManager.QNMedicine;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class MedicineListAdapter extends BaseAdapter {
	private List<QNMedicine> 				mMedicines;
	private LayoutInflater 					mInflater;
	private Context 						mContext;
	private Map<Integer, Boolean>			mSelectState;
	private List<Integer>					mSelectedItems;
	private boolean							isSelectMode;

	public MedicineListAdapter(Context context, List<QNMedicine> medicines) {
		mMedicines = medicines;
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mSelectState = new HashMap<Integer, Boolean>();
        mSelectedItems = new ArrayList<Integer>();
        isSelectMode = false;
	}
	
	public int getCount() {
		int n = mMedicines.size();
		return n;
	}

	public Object getItem(int position) {
		return mMedicines.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public void setCheckItem(int position, Boolean isChecked) {
		mSelectState.put(position, isChecked);
		
		if (mSelectedItems.contains((Object)position)) {
			mSelectedItems.remove((Object)position);
		}
		if (isChecked) {
			mSelectedItems.add(position);
		}
	}
	
	public void setSelectMode (boolean b) {
		isSelectMode = b;
		clearSelectState();
	}
	
	public void clearSelectState() {
		mSelectState.clear();
		mSelectedItems.clear();
	}
	
	public Integer[] getSelectPositions() {
		return (Integer[]) mSelectedItems.toArray(new Integer[0]);
	}
	
	private void setRowInfo(MedicineRowViewHolder medicineRow, int position) {
		QNMedicine medicine = mMedicines.get(position);
		medicineRow.medicineImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_launcher));
		medicineRow.medicineNameText.setText(medicine.name);
      	medicineRow.medicineNameText.setGravity(Gravity.CENTER);
      	if (isSelectMode) {
      		medicineRow.selectBox.setVisibility(View.VISIBLE);
      		Boolean isChecked = mSelectState.get(position);
      		if (isChecked == null) isChecked = false;
          	medicineRow.selectBox.setChecked(isChecked);
      	} else {
          	medicineRow.selectBox.setVisibility(View.GONE);
      	}
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		// A MedicineRowViewHolder keeps references to children views to avoid unneccessary calls
        // to findViewById() on each row.
        MedicineRowViewHolder medicineRow;

        // When convertView is not null, we can reuse it directly, there is no need
        // to reinflate it. We only inflate a new View when the convertView supplied
        // by ListView is null.
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.medicine_row, null);
            
            // Creates a MedicineRowViewHolder and store references to the two children views
            // we want to bind data to.
            medicineRow = new MedicineRowViewHolder();
            medicineRow.medicineImage = (ImageView) convertView.findViewById(R.id.medicine_image);
            medicineRow.medicineNameText = (TextView) convertView.findViewById(R.id.medicine_name);
            medicineRow.selectBox = (CheckBox) convertView.findViewById(R.id.item_cb);
   
            convertView.setTag(medicineRow);
        } else {
            // Get the MedicineRowViewHolder back to get fast access to the children views
            medicineRow = (MedicineRowViewHolder) convertView.getTag();
        }

		// Bind the data efficiently with the medicineRow.
		setRowInfo(medicineRow, position);
		
        return convertView;
	}

	private static class MedicineRowViewHolder {
		ImageView medicineImage;
	    TextView medicineNameText;
	    CheckBox selectBox;
	}
}
