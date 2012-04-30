package docketplace.stocktakr.activities;

import docketplace.stocktakr.*;
import docketplace.stocktakr.fragments.*;
import docketplace.stocktakr.components.*;

import com.actionbarsherlock.app.*;
import com.actionbarsherlock.view.*;

import android.support.v4.view.ViewPager;
import android.view.inputmethod.InputMethodManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class PerformStocktake extends SherlockFragmentActivity {
    public static PerformStocktake instance;

    private ViewPager  mViewPager;
    private TabsAdapter mTabsAdapter;

    private MenuItem countMenu;
    
    private InputMethodManager input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	setTheme(R.style.Theme_Sherlock_Light);

        super.onCreate(savedInstanceState);

        instance = this;

        setContentView(R.layout.perform_stocktake);
        
        input = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        ActionBar actionBar = getSupportActionBar();

        //actionBar.setHomeButtonEnabled(true);
        //actionBar.setDisplayHomeAsUpEnabled(true);
        
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mViewPager = (ViewPager)findViewById(R.id.pager);

        mTabsAdapter = new TabsAdapter(this, actionBar, mViewPager);

        mTabsAdapter.addTab(getString(R.string.scan), ScanProducts.class);
        mTabsAdapter.addTab(getString(R.string.recorded), RecordedProducts.class);
        mTabsAdapter.addTab(getString(R.string.submit), SubmitProducts.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //countMenu = menu.add("").setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, Stocktakr.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    public static void close() {
        if (instance != null) {
            instance.finish();
        }
    }
    
    public static void updateStockCount(int count) {
        if ((instance != null) && (instance.countMenu != null)) {
            //instance.countMenu.setTitle(String.valueOf(count));
        }
    }
    
    public static void hideKeyboard() {
		if ((instance != null) && (instance.input != null) && (instance.getCurrentFocus() != null)) {
			instance.input.hideSoftInputFromWindow(instance.getCurrentFocus().getWindowToken(), 0);
		}
    }
}
