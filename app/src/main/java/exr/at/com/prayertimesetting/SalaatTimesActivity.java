package exr.at.com.prayertimesetting;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import exr.at.com.prayertimesetting.fragments.InitialConfigFragment;
import exr.at.com.prayertimesetting.fragments.LocationHelper;
import exr.at.com.prayertimesetting.fragments.SalaatTimesFragment;
import exr.at.com.prayertimesetting.util.AppSettings;
import exr.at.com.prayertimesetting.util.ScreenUtils;
import exr.at.com.prayertimesetting.widget.FragmentStatePagerAdapter;
import exr.at.com.prayertimesetting.widget.SlidingTabLayout;


public class SalaatTimesActivity extends AppCompatActivity implements Constants,
    InitialConfigFragment.OnOptionSelectedListener, ViewPager.OnPageChangeListener,
    LocationHelper.LocationCallback {

  private LocationHelper mLocationHelper;
  private Location mLastLocation = null;

  private ViewPager mPager;
  private ScreenSlidePagerAdapter mAdapter;
  private SlidingTabLayout mTabs;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    AppSettings settings = AppSettings.getInstance(this);
    //INIT APP
    if (!settings.getBoolean(AppSettings.Key.IS_INIT)) {
      settings.set(settings.getKeyFor(AppSettings.Key.IS_ALARM_SET,         0), true);
      settings.set(settings.getKeyFor(AppSettings.Key.IS_FAJR_ALARM_SET,    0), true);
      settings.set(settings.getKeyFor(AppSettings.Key.IS_DHUHR_ALARM_SET,   0), true);
      settings.set(settings.getKeyFor(AppSettings.Key.IS_ASR_ALARM_SET,     0), true);
      settings.set(settings.getKeyFor(AppSettings.Key.IS_MAGHRIB_ALARM_SET, 0), true);
      settings.set(settings.getKeyFor(AppSettings.Key.IS_ISHA_ALARM_SET,    0), true);
      settings.set(AppSettings.Key.USE_ADHAN, true);
      settings.set(AppSettings.Key.IS_INIT, true);
    }

    setContentView(R.layout.activity_salaat_times);
    ScreenUtils.lockOrientation(this);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    mLocationHelper = (LocationHelper) getFragmentManager().findFragmentByTag(LOCATION_FRAGMENT);

    // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
    mAdapter = new ScreenSlidePagerAdapter(getFragmentManager(),0);

    // Assigning ViewPager View and setting the adapter
    mPager = (ViewPager) findViewById(R.id.pager);
    mPager.setAdapter(mAdapter);
    mPager.addOnPageChangeListener(this);

    // Assiging the Sliding Tab Layout View
    mTabs = (SlidingTabLayout) findViewById(R.id.tabs);
    mTabs.setDistributeEvenly(true);
    // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

    // Setting Custom Color for the Scroll bar indicator of the Tab View
    mTabs.setSelectedIndicatorColors(getResources().getColor(android.R.color.primary_text_dark));
    mTabs.setTextColor(android.R.color.primary_text_dark);

    // Setting the ViewPager For the SlidingTabsLayout
    mTabs.setViewPager(mPager);

    if(mLocationHelper == null) {
      mLocationHelper = LocationHelper.newInstance();
      getFragmentManager().beginTransaction().add(mLocationHelper, LOCATION_FRAGMENT).commit();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();

    if (mLastLocation == null) {
      fetchLocation();
    }
  }

  @Override
  protected void onDestroy() {
    //Just to be sure memory is cleaned up.
    mPager.removeOnPageChangeListener(this);
    mPager = null;
    mAdapter = null;
    mTabs = null;
    mLastLocation = null;

    super.onDestroy();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_salaat_times, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    if (id == R.id.action_settings) {
      startOnboardingFor(0);
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  private void startOnboardingFor(int index) {
    Intent intent = new Intent(getApplicationContext(), OnboardingActivity.class);
    intent.putExtra(OnboardingActivity.EXTRA_CARD_INDEX, index);
    startActivityForResult(intent, REQUEST_ONBOARDING);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_CHECK_SETTINGS) {
      switch (resultCode) {
        case Activity.RESULT_OK:
          // All required changes were successfully made
          fetchLocation();
          break;
        case Activity.RESULT_CANCELED:
          // The user was asked to change settings, but chose not to
          onLocationSettingsFailed();
          break;
        default:
          onLocationSettingsFailed();
          break;
      }
    } else if (requestCode == REQUEST_ONBOARDING) {
      if (resultCode == RESULT_OK) {
        onUseDefaultSelected();
      }
    } else if (requestCode == REQUEST_TNC) {
      if (resultCode == RESULT_CANCELED) {
        finish();
      } else {
        AppSettings settings = AppSettings.getInstance(this);
        settings.set(AppSettings.Key.IS_TNC_ACCEPTED, true);
      }
    } else {
      super.onActivityResult(requestCode, resultCode, data);
    }
  }

  /**
   * Callback received when a permissions request has been completed.
   */
  private void fetchLocation() {
    if (mLocationHelper != null) {
      mLocationHelper.checkLocationPermissions();
    }
  }

  @Override
  public void onLocationSettingsFailed() {

  }

  @Override
  public void onLocationChanged(Location location) {
    mLastLocation = location;
    // NOT THE BEST SOLUTION, THINK OF SOMETHING ELSE
    mAdapter = new ScreenSlidePagerAdapter(getFragmentManager(), 0);
    mPager.setAdapter(mAdapter);
  }

  @Override
  public void onConfigNowSelected(int num) {
    startOnboardingFor(num);
  }

  @Override
  public void onUseDefaultSelected() {
    if (mLastLocation != null) {
      // NOT THE BEST SOLUTION, THINK OF SOMETHING ELSE
      mAdapter = new ScreenSlidePagerAdapter(getFragmentManager(),0);
      mPager.setAdapter(mAdapter);
    }
  }

  @Override
  public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
  }

  @Override
  public void onPageSelected(int position) {

  }

  @Override
  public void onPageScrollStateChanged(int state) {

  }

  private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
    private int mCardIndex;

    public ScreenSlidePagerAdapter(FragmentManager fm, int index) {
      super(fm);
      mCardIndex = index;
    }

    @Override
    public Fragment getItem(int position) {
      switch (position) {
        case 0:
          if (AppSettings.getInstance(getApplicationContext()).isDefaultSet()) {
            return SalaatTimesFragment.newInstance(mCardIndex, mLastLocation);
          } else {
            return InitialConfigFragment.newInstance();
          }
      }
      return null;
    }

    @Override
    public int getCount() {
      return 1;
    }
  }
}
