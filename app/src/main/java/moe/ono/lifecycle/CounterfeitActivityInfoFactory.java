package moe.ono.lifecycle;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import moe.ono.R;
import moe.ono.util.HostInfo;

public class CounterfeitActivityInfoFactory {

    @Nullable
    public static ActivityInfo makeProxyActivityInfo(@NonNull String className, long flags) {
        try {
            Context ctx = HostInfo.getApplication();
            Class<?> cl = Class.forName(className);
            String[] candidates = new String[]{
                    "com.tencent.mobileqq.activity.QQSettingSettingActivity",
                    "com.tencent.mobileqq.activity.QPublicFragmentActivity"
            };
            PackageManager.NameNotFoundException last = null;
            for (String activityName : candidates) {
                try {
                    // TODO: 2022-02-11 cast flags from long to int loses information
                    ActivityInfo proto = ctx.getPackageManager().getActivityInfo(new ComponentName(
                            ctx.getPackageName(), activityName), (int) flags);
                    // init style here, comment it out if it crashes on Android >= 10
                    proto.theme = R.style.Theme_MaiTungTMDesign_DayNight;
                    return initCommon(proto, className);
                } catch (PackageManager.NameNotFoundException e) {
                    last = e;
                }
            }
            throw new IllegalStateException("QQSettingSettingActivity not found, are we in the host?", last);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private static ActivityInfo initCommon(ActivityInfo ai, String name) {
        ai.targetActivity = null;
        ai.taskAffinity = null;
        ai.descriptionRes = 0;
        ai.name = name;
        ai.splitName = null;
        ai.configChanges |= ActivityInfo.CONFIG_UI_MODE;
        return ai;
    }
}
