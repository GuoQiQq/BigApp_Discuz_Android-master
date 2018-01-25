package com.appbyme.app272523.base.util.view.threadandarticle;

import android.app.Activity;

import com.appbyme.app272523.R;
import com.appbyme.app272523.base.util.AppSPUtils;
import com.appbyme.app272523.base.util.ClanUtils;
import com.appbyme.app272523.base.util.ToastUtils;
import com.appbyme.app272523.login.LoginActivity;
import com.appbyme.app272523.main.base.forumnav.DBForumNavUtils;
import com.appbyme.app272523.thread.ThreadPublishActivity;
import com.appbyme.app272523.thread.ThreadPublishActivity1;
import com.appbyme.app272523.thread.ThreadPublishActivity2;
import com.appbyme.app272523.thread.ThreadPublishActivity3;
import com.kit.utils.ListUtils;
import com.kit.utils.intentutils.BundleData;
import com.kit.utils.intentutils.IntentUtils;
import com.youzu.clan.base.json.forumnav.NavForum;

import java.util.List;

/**
 * 设置文章和帖子列表
 * Created by Zhao on 15/11/5.
 */
public class ThreadAndArticleUtils extends ContentUtils {



    public static void addThread(Activity context) {
        if (ClanUtils.isToLogin(context, null, Activity.RESULT_OK, false)) {
            return;
        }
        List<NavForum> forums = DBForumNavUtils.getAllNavForum(context);
        if (ListUtils.isNullOrContainEmpty(forums)) {
            ToastUtils.mkShortTimeToast(context, context.getString(R.string.wait_a_moment));
        } else
            IntentUtils.gotoNextActivity(context, ThreadPublishActivity.class);
    }
    public static void addThread1(Activity context,Integer num) {
//        if (ClanUtils.isToLogin(context, null, Activity.RESULT_OK, false)) {
//            return;
//        }
        if (!AppSPUtils.isLogined(context)) {
            BundleData bundleData = new BundleData();
            IntentUtils.gotoNextActivity(context, LoginActivity.class, bundleData);
        }
        List<NavForum> forums = DBForumNavUtils.getAllNavForum(context);
        if (ListUtils.isNullOrContainEmpty(forums)) {
            ToastUtils.mkShortTimeToast(context, context.getString(R.string.wait_a_moment));
        }else if (num!=null&&num==1){
            if (!AppSPUtils.isLogined(context)) {
                BundleData bundleData = new BundleData();
                IntentUtils.gotoNextActivity(context, LoginActivity.class, bundleData);
            }else {

                IntentUtils.gotoNextActivity(context, ThreadPublishActivity1.class);
            }
        }else if (num!=null&&num==2){
            if (!AppSPUtils.isLogined(context)) {
                BundleData bundleData = new BundleData();
                IntentUtils.gotoNextActivity(context, LoginActivity.class, bundleData);

            }else {
                IntentUtils.gotoNextActivity(context, ThreadPublishActivity2.class);
            }
        }else if (num!=null&&num==3){
            if (!AppSPUtils.isLogined(context)) {
                BundleData bundleData = new BundleData();
                IntentUtils.gotoNextActivity(context, LoginActivity.class, bundleData);

            }else {
                IntentUtils.gotoNextActivity(context, ThreadPublishActivity3.class);
            }
        }else{
            if (!AppSPUtils.isLogined(context)) {
                BundleData bundleData = new BundleData();
                IntentUtils.gotoNextActivity(context, LoginActivity.class, bundleData);

            }else {
                IntentUtils.gotoNextActivity(context, ThreadPublishActivity.class);
            }
        }
// else if(num == 2){
//            IntentUtils.gotoNextActivity(context, ThreadPublishActivity.class);
//        }else if(num == 3){
//            IntentUtils.gotoNextActivity(context, ThreadPublishActivity.class);
//        }
    }

}
