package com.appbyme.app272523.base.util.view;

import android.content.Context;

import com.appbyme.app272523.base.util.AppSPUtils;
import com.appbyme.app272523.base.util.StringUtils;
import com.youzu.android.framework.JsonUtils;
import com.youzu.clan.base.json.ProfileJson;
import com.youzu.clan.base.json.profile.AdminGroup;
import com.youzu.clan.base.json.profile.Space;

/**
 * Created by Zhao on 15/10/19.
 */
public class ProfileUtils {

    /**
     * 从保存的sharedperference中获取用户信息
     *
     * @param context
     */
    public static ProfileJson getProfile(final Context context) {

        String ps = AppSPUtils.getProfile(context);
        if (!StringUtils.isEmptyOrNullOrNullStr(ps)) {
            ProfileJson profileJson = JsonUtils.parseObject(ps, ProfileJson.class);
            return profileJson;
        }
        return null;
    }


    /**
     * 获取分组
     * @param space
     * @return
     */
    public static String getGroupName(Space space) {
        String groupName = "";
        AdminGroup group = space.getGroup();
        if (group != null) {
            groupName = group.getGrouptitle();
        }
        return StringUtils.get(groupName);
    }

}
