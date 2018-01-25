package com.appbyme.app272523.friends;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.appbyme.app272523.R;
import com.appbyme.app272523.app.constant.Key;
import com.appbyme.app272523.base.BaseActivity;
import com.appbyme.app272523.base.util.StringUtils;
import com.appbyme.app272523.base.widget.list.RefreshListView;
import com.appbyme.app272523.profile.homepage.HomePageActivity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.kit.utils.ZogUtils;
import com.youzu.android.framework.view.annotation.ContentView;
import com.youzu.android.framework.view.annotation.ViewInject;
import com.youzu.clan.base.json.search.User;
import com.youzu.clan.base.net.ClanHttpParams;
import com.youzu.clan.base.util.ClanBaseUtils;

/**
 * Created by tangh on 2015/7/15.
 */
@ContentView(R.layout.activity_friends)
public class NewFriendsActivity extends BaseActivity {

    @ViewInject(value = R.id.searchFriends)
    protected LinearLayout searchFriends;
    @ViewInject(value = R.id.newFriends)
    protected View newFriends;

    @ViewInject(value = R.id.listView)
    protected RefreshListView listView;

    private FriendsAdapter friendsAdapter;


    protected String module = FriendsModule.NEW_FRIENDS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listView.setDefaultMode(PullToRefreshBase.Mode.DISABLED);
        friendsAdapter = new FriendsAdapter(this, module, getClanHttpParams());
        listView.setAdapter(friendsAdapter);
        listView.setDefaultMode(PullToRefreshBase.Mode.DISABLED);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ZogUtils.printError(FriendsActivity.class, "position=" + position);
                String uid = ((User) friendsAdapter.getItem(position)).getUid();
                Intent in = new Intent(NewFriendsActivity.this, HomePageActivity.class);
                in.putExtra(Key.KEY_UID, uid);
                startActivity(in);
            }
        });

        searchFriends.setVisibility(View.GONE);
        newFriends.setVisibility(View.GONE);
    }

    protected ClanHttpParams getClanHttpParams() {
        ClanHttpParams params = new ClanHttpParams(this);
        params.addQueryStringParameter("module", module);
        params.addQueryStringParameter("iyzmobile", "1");
        params.addQueryStringParameter("only_count", "0");

        if (!StringUtils.isEmptyOrNullOrNullStr(ClanBaseUtils.getFormhash(this)))
            params.addQueryStringParameter("formhash", ClanBaseUtils.getFormhash(this));
        return params;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_friends, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_friends:
                Intent intent = new Intent(this, AddFriendsActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
