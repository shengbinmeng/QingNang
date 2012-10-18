package pku.shengbin.qingnang;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;

public class Settings extends PreferenceActivity implements OnPreferenceChangeListener,
			OnPreferenceClickListener
{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        
        Preference pref1=findPreference("about_this_app");
        EditTextPreference pref2 = (EditTextPreference) findPreference("custom_server");
        pref1.setOnPreferenceClickListener(this);
        pref2.setOnPreferenceChangeListener(this);
    }

	public boolean onPreferenceChange(Preference arg0, Object arg1) {
		// TODO Auto-generated method stub
		if(arg0.getKey().equals("custom_server")) {
			String server = arg1.toString();
			
			if (!server.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
				MessageBox.show(this, "Invalid Input", "Server should be valid IP address.");
				return false;
			}
			if (!server.equals("www.qingnang.com")) {
				MessageBox.show(this, "Message", "You have changed server to:" + server + ". If anything goes wrong, please change back to default: www.qingnang.com.");
			}
		};
		
		return true; // return true so the preference can be changed, false otherwise
	}

	public boolean onPreferenceClick(Preference arg0) {
		// TODO Auto-generated method stub
		if(arg0.getKey().equals("about_this_app")){
			MessageBox.show(this, "About", "This is QingNang app! \n\r\n\rSuggestions and bug reports may be sent to: shengbinmeng@gmail.com ");
		};
		return false;
	}
}