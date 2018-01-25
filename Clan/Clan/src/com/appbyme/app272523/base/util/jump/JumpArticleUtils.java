package com.appbyme.app272523.base.util.jump;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.kit.utils.intentutils.IntentUtils;
import com.appbyme.app272523.article.ArticleDetailActivity;
import com.appbyme.app272523.article.ArticleListActivity;
import com.appbyme.app272523.base.util.view.threadandarticle.ThreadAndArticleItemUtils;

/**
 * Created by Zhao on 15/9/9.
 */
public class JumpArticleUtils {
    /**
     * 跳转到帖子详情
     *
     * @param context
     */
    public static void gotoArticleDetail(Context context, String aid) {
        if (TextUtils.isEmpty(aid)) {
            return;
        }
        ThreadAndArticleItemUtils.saveReadAid(context, aid);
        Bundle bundle = new Bundle();
        bundle.putString("aid", aid);
        IntentUtils.gotoNextActivity(context, ArticleDetailActivity.class, bundle);
    }

    /**
     * 帖子详情Intent
     *
     * @param context
     */
    public static Intent getArticleIntent(Context context, String aid) {
        Intent intent = new Intent(context, ArticleDetailActivity.class);
        intent.putExtra("aid", aid);
        return intent;
    }

    /**
     * 跳转到帖子列表
     *
     * @param context
     */
    public static void gotoArticleList(Context context, String catid,String title) {
        if (TextUtils.isEmpty(catid)) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("catid", catid);
        bundle.putString("title",title);
        IntentUtils.gotoNextActivity(context, ArticleListActivity.class, bundle);
    }


}
