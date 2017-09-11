package exr.at.com.prayertimesetting.util;

import android.app.Activity;
import android.content.pm.ActivityInfo;

/**
 * Created by Ali on 12/07/2015.
 */
public class ScreenUtils {
  /**
   * Locks the device window in actual screen mode.
   */
  public static void lockOrientation(Activity activity) {
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
  }

  /**
   * Unlocks the device window in user defined screen mode.
   */
  public static void unlockOrientation(Activity activity) {
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
  }

}
