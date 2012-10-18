package pku.shengbin.qingnang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import pku.shengbin.qingnang.QNMedicineManager.QNMedicine;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MedicineDetailActivity extends ListActivity {
	static String[] attributeKeys = {"dosage", "indication", "reaction", "attention", "forbid", "property", "store", "validity", "component"};
	static String[] attributeNames = {"用法用量", "适应症/功能主治", "不良反应", "注意事项", "禁忌", "性状", "贮藏", "有效期", "成分"};

	
	private ArrayList<HashMap<String, Object>>   listItems;
    private SimpleAdapter listItemAdapter;
	private QNMedicine mMedicine;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.medicine_detail);

        String medicineID = this.getIntent().getStringExtra("pku.shengbin.qingnang.medicineID");
        mMedicine = QNMedicineManager.getMedicineByID(medicineID);

		TextView textView = (TextView) findViewById(R.id.title);
		textView.setText(mMedicine.name + " - 说明书");
		textView = (TextView) findViewById(R.id.medicine_name);
		textView.setText(mMedicine.name);
		textView.setGravity(Gravity.CENTER);
		ImageView imageView = (ImageView) findViewById(R.id.medicine_image);
		imageView.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_launcher));
		
		initListView();

    }
	
	private void initListView() {
        listItems = new ArrayList<HashMap<String, Object>>();
        for(int i = 0; i < attributeKeys.length; i++) { 
        	if (mMedicine.attributes.containsKey(attributeKeys[i]) == false) continue;
            HashMap<String, Object> map = new HashMap<String, Object>();  
            map.put("Indicator", android.R.drawable.ic_media_next);   
            map.put("AttributeName", attributeNames[i]); 
            map.put("AttributeKey", attributeKeys[i]); 
            listItems.add(map);
        }
        
        listItemAdapter = new SimpleAdapter(this,listItems,    
                R.layout.medicine_detail_row,
                new String[] {"Indicator", "AttributeName"},        
                new int[ ] {R.id.indicator, R.id.attribute_name}  
        ); 
        this.setListAdapter(listItemAdapter);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		HashMap<String, Object> item = listItems.get(position);
    	MessageBox.show(this, item.get("AttributeName") + "", mMedicine.attributes.get(item.get("AttributeKey")));
	}
}