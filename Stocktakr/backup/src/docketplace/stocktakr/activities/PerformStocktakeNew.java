package docketplace.stocktakr.activities;

import java.util.Vector;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.TabHost;

import com.actionbarsherlock.app.*;
import com.actionbarsherlock.app.ActionBar.Tab;

import docketplace.stocktakr.R;
import docketplace.stocktakr.data.DatabaseHelper;
import docketplace.stocktakr.fragments.*;


//

public class PerformStocktakeNew extends SherlockFragmentActivity {
    TabHost mTabHost;
    ViewPager  mViewPager;
    TabsAdapter mTabsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	setTheme(R.style.Theme_Sherlock_Light);
    	
        super.onCreate(savedInstanceState);

        setContentView(R.layout.perform_stocktake);
        mTabHost = (TabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup();

        mViewPager = (ViewPager)findViewById(R.id.pager);

        mTabsAdapter = new TabsAdapter(this, mTabHost, mViewPager);

        mTabsAdapter.addTab(mTabHost.newTabSpec("scan").setIndicator("Scan"), ScanProducts.class, null);
        mTabsAdapter.addTab(mTabHost.newTabSpec("recorded").setIndicator("Recorded"), RecordedProducts.class, null);
        mTabsAdapter.addTab(mTabHost.newTabSpec("submit").setIndicator("Submit"), SubmitProducts.class, null);
        //mTabsAdapter.addTab(mTabHost.newTabSpec("scan2").setIndicator("Scan2"), ScanProducts.class, null);
        //mTabsAdapter.addTab(mTabHost.newTabSpec("scan3").setIndicator("Scan3"), ScanProducts.class, null);
        /*mTabsAdapter.addTab(mTabHost.newTabSpec("contacts").setIndicator("Contacts"),
                LoaderCursorSupport.CursorLoaderListFragment.class, null);
        mTabsAdapter.addTab(mTabHost.newTabSpec("custom").setIndicator("Custom"),
                LoaderCustomSupport.AppListFragment.class, null);
        mTabsAdapter.addTab(mTabHost.newTabSpec("throttle").setIndicator("Throttle"),
                LoaderThrottleSupport.ThrottledLoaderListFragment.class, null);*/
    }
}
