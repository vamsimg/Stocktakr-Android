package docketplace.stocktakr.components;

import com.actionbarsherlock.app.*;
import com.actionbarsherlock.app.ActionBar.*;

import android.support.v4.app.*;
import android.support.v4.view.*;
import android.view.inputmethod.*;

import android.os.*;
import android.content.*;

import java.util.*;


public class TabsAdapter extends FragmentPagerAdapter implements TabListener, ViewPager.OnPageChangeListener {
	private final Context mContext;
	
	private final FragmentActivity activity;

	private final ActionBar actionBar;

	private final ViewPager mViewPager;

	private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
	
	InputMethodManager input; 

	static final class TabInfo {
		private final Class<?> clss;
		private final Bundle args;

		TabInfo(Class<?> _class, Bundle _args) {
			clss = _class;
			args = _args;
		}
	}

	public TabsAdapter(FragmentActivity activity, ActionBar actionBar, ViewPager pager) { //TabHost tabHost;
		super(activity.getSupportFragmentManager());
		mContext = activity;
		this.activity = activity;
		this.actionBar = actionBar;
		mViewPager = pager;
		mViewPager.setAdapter(this);
		mViewPager.setOnPageChangeListener(this);
		
		input = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
	}

	public void addTab(String title, Class<?> clss) {
		TabInfo info = new TabInfo(clss, null);
		
		mTabs.add(info);

		ActionBar.Tab tab = actionBar.newTab();
        tab.setText(title);
        tab.setTag(info);
        tab.setTabListener(this);
        actionBar.addTab(tab);

		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mTabs.size();
	}

	@Override
	public Fragment getItem(int position) {
		TabInfo info = (TabInfo)actionBar.getTabAt(position).getTag();

		return Fragment.instantiate(mContext, info.clss.getName(), info.args);
	}

    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction transaction) {
        int position = tab.getPosition();

        mViewPager.setCurrentItem(position);
        
        hideKeyboard();
    }

	
	public void onPageSelected(int position) {
		actionBar.selectTab(actionBar.getTabAt(position));
		
		hideKeyboard();
	}

	private void hideKeyboard() {
		if ((input != null) && (activity.getCurrentFocus() != null)) {
			input.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
		}
	}
	
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction transaction) {}
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction transaction) {}

	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
	public void onPageScrollStateChanged(int state) {}
}
