package com.appbyme.app272523.login;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.appbyme.app272523.R;
import com.appbyme.app272523.base.util.AppSPUtils;
import com.appbyme.app272523.base.util.ClanUtils;
import com.kit.utils.ZogUtils;
import com.youzu.clan.base.common.ResultCode;

/**s
 * Created by Zhao on 15/6/23.
 */
public class WebLoginActivity extends CallBackWebActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (AppSPUtils.getContentConfig(this).getLoginInfo().getRegSwitch() == 1
                ||AppSPUtils.getConfig(this).getLoginInfo().getRegSwitch()==1) {
            getMenuInflater().inflate(R.menu.menu_login, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_register:
//                Intent intent = new Intent(this, RegisterActivity.class);
//                startActivityForResult(intent, ResultCode.REQUEST_CODE_REGISTER);
                ClanUtils.gotoRegsiter(this, null, ResultCode.REQUEST_CODE_REGISTER, false);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        ZogUtils.printLog(WebLoginActivity.class, "onRestart onRestart onRestart");

        webFragment.getWebView().reload();
    }
}
