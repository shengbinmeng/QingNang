package pku.shengbin.qingnang;

import pku.shengbin.qingnang.QNUserManager.QNUser;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

public class MedicineActivity extends TabActivity {	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
    	Resources res = this.getResources(); // Resource object to get Drawables

        String userID = this.getIntent().getStringExtra("pku.shengbin.qingnang.userID");
        QNUser user = QNUserManager.getUserById(userID);
		TextView titleText = (TextView) findViewById(R.id.title);
		titleText.setText(user.name + "的药箱");
        
	    TabHost tabHost = this.getTabHost();  // The activity TabHost
	    TabSpec spec;
	    Intent intent; 
 
	    intent = new Intent(this, MedicineListActivity.class);
    	intent.putExtra("pku.shengbin.qingnang.userID", userID);
	    spec = tabHost.newTabSpec("list_tab")
	    .setIndicator("药品", res.getDrawable(android.R.drawable.ic_menu_gallery))
	    .setContent(intent);
	    tabHost.addTab(spec);
	    
	    intent = new Intent(this, RecommendListActivity.class);
    	intent.putExtra("pku.shengbin.qingnang.userID", userID);
	    spec = tabHost.newTabSpec("recommend_tab")
	    .setIndicator("推荐", res.getDrawable(android.R.drawable.ic_menu_slideshow))
	    .setContent(intent);
	    tabHost.addTab(spec);
	    
	    tabHost.setCurrentTab(0);
    }
    
}