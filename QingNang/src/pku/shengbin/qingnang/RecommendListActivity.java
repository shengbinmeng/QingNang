package pku.shengbin.qingnang;

import pku.shengbin.qingnang.QNMedicineManager.QNMedicine;
import pku.shengbin.qingnang.QNUserManager.QNUser;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import java.util.ArrayList;

public class RecommendListActivity extends ListActivity {
	private QNUser mUser;

	private ArrayList<QNMedicine> mRecommendList;
	private MedicineListAdapter mAdapter;
	private LinearLayout mBottom;
	private Button mAddButton, mCancelButton;
	private ListView mListView;
	private boolean inSelectMode = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.medicine_list);
		
		String userID = this.getIntent().getStringExtra("pku.shengbin.qingnang.userID");
        mUser = QNUserManager.getUserById(userID);
		
		initRecommendList();
		initBottomButtons();
        
		mListView = (ListView) this.findViewById(android.R.id.list);
        mListView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
        	public void onCreateContextMenu(ContextMenu menu, View v,
	        	ContextMenuInfo menuInfo) {
		        	menu.setHeaderTitle("操作");
		        	menu.add(0,0,0,"添加至我的药箱");
		        	menu.add(0,1,1,"查看说明书");
	        	}
        	}
        );  
	}
	
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		QNMedicine medicine = mRecommendList.get(info.position);
		int id = item.getItemId();
		if (id == 0) {
			mUser.addMedicine(medicine);
			Toast.makeText(this, "添加药品成功!", Toast.LENGTH_SHORT).show();
		} else if (id == 1) {	
	    	Intent i = new Intent(this, MedicineDetailActivity.class);
	    	i.putExtra("pku.shengbin.qingnang.medicineID", medicine.id);
	    	startActivity(i);
		}
		
		return super.onContextItemSelected(item);
	}
	
	private void initRecommendList() {			
		mRecommendList = QNMedicineManager.getRecommendMedicines();
		if (mRecommendList == null) mRecommendList = new ArrayList<QNMedicine>();
		mAdapter = new MedicineListAdapter(this, mRecommendList);
		this.setListAdapter(mAdapter);
    }
	
	private void initBottomButtons() {
		mAddButton = (Button) this.findViewById(R.id.add);  
		mAddButton.setText("添加至我的药箱");
        mCancelButton = (Button) this.findViewById(R.id.cancel);
        mCancelButton.setText("完成");
        ((Button) this.findViewById(R.id.delete)).setVisibility(View.GONE);
		mBottom = (LinearLayout) this.findViewById(R.id.bottom);
        mBottom.setVisibility(View.GONE);
        
        View.OnClickListener listener = new View.OnClickListener() {	
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// TODO Auto-generated method stub
				if (v == mCancelButton) {
					mBottom.setVisibility(View.GONE);
					mAdapter.setSelectMode(false);
			        mAdapter.notifyDataSetChanged();
			        inSelectMode = false;
			        return ;
				} 
				
				Integer[] positions = mAdapter.getSelectPositions();
				if (positions.length == 0) {
					MessageBox.show(RecommendListActivity.this, "提示", "请先选择操作项!");
				} else {
					if (v == mAddButton) {
						for (int i = 0; i < positions.length; i++) {
							mUser.addMedicine(mRecommendList.get(positions[i]));
						}
						Toast.makeText(RecommendListActivity.this, "添加药品成功!", Toast.LENGTH_SHORT).show();
					}
				}
			}
		};
		
        mAddButton.setOnClickListener(listener);
        mCancelButton.setOnClickListener(listener);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		if (inSelectMode) {
			CheckBox checkBox = (CheckBox) v.findViewById(R.id.item_cb);
			checkBox.toggle();
			mAdapter.setCheckItem(position, checkBox.isChecked());
		} else {
			QNMedicine medicine = mRecommendList.get(position);		
	    	Intent i = new Intent(this, MedicineDetailActivity.class);
	    	i.putExtra("pku.shengbin.qingnang.medicineID", medicine.id);
	    	startActivity(i);
		}
	}
	
	// Menu item Ids
    public static final int MENU_REFRESH = Menu.FIRST;
    public static final int MENU_BATCH = Menu.FIRST + 1;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);     
		menu.add(0, MENU_REFRESH, 1, "更新列表");
		menu.add(0, MENU_BATCH, 1, "批量操作");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
	        case MENU_REFRESH:
	        {
	        	mRecommendList = QNMedicineManager.getRecommendMedicines();
	    		if (mRecommendList == null) mRecommendList = new ArrayList<QNMedicine>();
		        mAdapter.notifyDataSetChanged();
	        	break;	        	
	        }
	        case MENU_BATCH:
	        {
		        mBottom.setVisibility(View.VISIBLE);
		        mAdapter.setSelectMode(true);
		        mAdapter.notifyDataSetChanged();
		        inSelectMode = true;
	        }
        }
        return super.onOptionsItemSelected(item);
    }
    
}