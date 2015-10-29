package library.neetoffice.com.neetcardbagview;

import android.content.Context;
import android.content.pm.ApplicationInfo;

/**
 * Created by Deo on 2015/10/29.
 */
public class BugConfig {
    public static boolean isPrintLog(Context context) {
        return (context.getApplicationInfo().flags & ApplicationInfo.FLAG_ALLOW_BACKUP) != 0;
    }
}
