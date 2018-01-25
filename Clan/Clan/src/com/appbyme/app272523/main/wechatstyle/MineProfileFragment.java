package com.appbyme.app272523.main.wechatstyle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.appbyme.app272523.R;
import com.appbyme.app272523.app.WebActivity;
import com.appbyme.app272523.app.config.AppConfig;
import com.appbyme.app272523.app.constant.Key;
import com.appbyme.app272523.base.BaseFragment;
import com.appbyme.app272523.base.net.DoSignIn;
import com.appbyme.app272523.base.util.AppSPUtils;
import com.appbyme.app272523.base.util.AppUSPUtils;
import com.appbyme.app272523.base.util.AvatalUtils;
import com.appbyme.app272523.base.util.ClanUtils;
import com.appbyme.app272523.base.util.LoadImageUtils;
import com.appbyme.app272523.base.util.StringUtils;
import com.appbyme.app272523.base.util.jump.JumpWebUtils;
import com.appbyme.app272523.base.util.view.MainTopUtils;
import com.appbyme.app272523.base.util.view.MenuJumpTopUtils;
import com.appbyme.app272523.base.util.view.ProfileUtils;
import com.appbyme.app272523.base.util.view.ViewDisplayUtils;
import com.appbyme.app272523.friends.FriendsActivity;
import com.appbyme.app272523.login.LoginActivity;
import com.appbyme.app272523.main.bottomtab.BottomTabMainActivity;
import com.appbyme.app272523.main.bottomtab.MenuJumpActivity;
import com.appbyme.app272523.message.pm.MyPMActivity;
import com.appbyme.app272523.myfav.MyFavActivity;
import com.appbyme.app272523.profile.homepage.HomePageActivity;
import com.appbyme.app272523.profile.thread.OwnerThreadActivity;
import com.appbyme.app272523.setting.SettingsActivity;
import com.kit.app.core.task.DoSomeThing;
import com.kit.imagelib.entity.ImageLibRequestResultCode;
import com.kit.utils.GsonUtils;
import com.kit.utils.HtmlUtils;
import com.kit.utils.ZogUtils;
import com.kit.utils.intentutils.BundleData;
import com.kit.utils.intentutils.IntentUtils;
import com.kit.widget.textview.WithTitleTextView;
import com.youzu.android.framework.JsonUtils;
import com.youzu.android.framework.ViewUtils;
import com.youzu.android.framework.view.annotation.ViewInject;
import com.youzu.android.framework.view.annotation.event.OnClick;
import com.youzu.clan.base.callback.HttpCallback;
import com.youzu.clan.base.common.Constants;
import com.youzu.clan.base.common.ResultCode;
import com.youzu.clan.base.json.ProfileJson;
import com.youzu.clan.base.json.profile.ProfileVariables;
import com.youzu.clan.base.json.profile.Space;
import com.youzu.clan.base.net.ClanHttp;

import cn.sharesdk.feedback.model.UserInfo;

public class MineProfileFragment extends BaseFragment {

    WebView webView;
    Context context;
    private ImageButton ibSignIn;


    @ViewInject(R.id.photo)
    private ImageView mPhotoImage;
    @ViewInject(R.id.sex)
    private ImageView mSexImage;
    @ViewInject(R.id.name)
    private TextView mUserName;
    @ViewInject(R.id.red_dot)
    private TextView mRedDotText;
    @ViewInject(value = R.id.resource)
    TextView mResourceText;
    //用户称号（group_title）
    @ViewInject(value = R.id.group_id)
    TextView mGroup_id;


    @ViewInject(R.id.wttvGroup)
    WithTitleTextView wttvGroup;

    @ViewInject(R.id.wttvRegisterDate)
    WithTitleTextView wttvRegisterDate;


    @ViewInject(R.id.groupAndRegisterDate)
    View groupAndRegisterDate;


    private ProfileVariables mProfileVariables;
    private SharedPreferences mSharedPreferences;
    private UserInfo userInfo;


    public MineProfileFragment() {
    }

    private OnSharedPreferenceChangeListener mPreferenceListener = new OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (!key.equals(Key.KEY_NEW_MESSAGE)) {
                return;
            }
            final int messageCount = sharedPreferences.getInt(key, 0);
            mRedDotText.setVisibility(messageCount > 0 ? View.VISIBLE : View.GONE);
            mRedDotText.setText(String.valueOf(messageCount));
            Log.e("TAG", "messageCount" + messageCount);
        }
    };

    private void setRedDot(int count) {
        mRedDotText.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
        mRedDotText.setText(String.valueOf(count));
        ViewDisplayUtils.setBadgeCount(getActivity(), count);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine_profile, null);
        ViewUtils.inject(this, view);
        shuaxin();
        mSharedPreferences = getActivity().getSharedPreferences(Key.FILE_PREFERENCES, Context.MODE_PRIVATE);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(mPreferenceListener);
//        photoLayout.setBackgroundColor(ThemeUtils.getThemeColor(getActivity()));
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

        setIbSignIn();

//        ProfileJson profileJson = ProfileUtils.getProfile(getActivity());
//        updateUi(profileJson);

        if (AppSPUtils.isLogined(getActivity())) {
            updateProfile();
        } else {
            clearProfile();
        }


    }

    public void setIbSignIn() {
        if (getActivity() instanceof MenuJumpActivity) {
            ibSignIn = MenuJumpTopUtils.ibSignIn;
        } else {
            ibSignIn = MainTopUtils.ibSignIn;
        }

        ibSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        initMainMenu();
    }

    /**
     * 签到
     */
    public void signIn() {
        if (ClanUtils.isToLogin(getActivity(), null, Activity.RESULT_OK, false)) {
            return;
        }
        DoSignIn.checkSignIn(getActivity(), ibSignIn, DoSignIn.SIGN_IN, new DoSomeThing() {
            @Override
            public void execute(Object... object) {
                updateProfile();
            }
        });
    }

    @OnClick(R.id.photo)
    public void photo(View view) {
        if (!AppSPUtils.isLogined(getActivity())) {
            IntentUtils.gotoNextActivity(getActivity(),LoginActivity.class);

        }else {
            AvatalUtils.changeAvatal(getActivity(), this);
        }
    }

    private void shuaxin(){
        setIbSignIn();

        ProfileJson profileJson = ProfileUtils.getProfile(getActivity());
        updateUi(profileJson);

        if (AppSPUtils.isLogined(getActivity())) {
            updateProfile();
        } else {
            clearProfile();
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(mPreferenceListener);
    }

    public void jump(Class clz) {
        if (!AppSPUtils.isLogined(getActivity())) {
            ClanUtils.gotoLogin(getActivity(), null, 1, false);
            return;
        }
        Intent intent = new Intent(getActivity(), clz);
        startActivity(intent);
    }

    public void jump(Intent intent) {
        if (!AppSPUtils.isLogined(getActivity())) {
//            ClanUtils.gotoLogin(getActivity(), null, 1, false);
            IntentUtils.gotoNextActivity(getActivity(),LoginActivity.class);
        }else {
            startActivityForResult(intent, 2);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ZogUtils.printError(MineProfileFragment.class, "onActivityResult requestCode:" + requestCode + " resultCode:" + resultCode + " data:" + JsonUtils.toJSONString(data));

        switch (resultCode) {
            case ResultCode.RESULT_CODE_EXIT:
                clearProfile();
                break;
            case ResultCode.RESULT_CODE_LOGIN:
                if (data != null && data.getBooleanExtra(Key.KEY_LOGINED, false)) {
                    ProfileJson json = (ProfileJson) data.getSerializableExtra(Key.KEY_LOGIN_RESULT);

                    ZogUtils.printError(MineProfileFragment.class, "ProfileJson:" + GsonUtils.toJson(json));
                    if (json == null) {
                        updateProfile();
                    } else {
                        updateUi(json);
                    }
                }
                break;
            case Activity.RESULT_OK:
                if (requestCode == ImageLibRequestResultCode.REQUEST_SELECT_PIC) {
                    AvatalUtils.uploadAvatal(getActivity(), data, mPhotoImage);
                }
                break;
        }
    }


    /**
     * 更新用户信息
     */
    private void updateProfile() {

        ClanHttp.getProfile(getActivity(), "", new HttpCallback<ProfileJson>() {

            @Override
            public void onSuccess(Context ctx, ProfileJson t) {
                updateUi(t);
            }

            @Override
            public void onFailed(Context cxt, int errorCode, String errorMsg) {
            }
        });
    }

    private void clearProfile() {
        ZogUtils.printError(MineProfileFragment.class, "clearProfile clearProfile clearProfile");
        if (isAdded() && isVisible()) {
            mProfileVariables = null;
            mPhotoImage.setImageResource(R.drawable.ic_profile_nologin_default);
            mSexImage.setVisibility(View.GONE);
            mUserName.setText(getString(R.string.click_to_login));
            mResourceText.setText(R.string.to_login_get_more);
            ibSignIn.setEnabled(true);
        }
    }

    /**
     * 更新界面
     *
     * @param t
     */
    public void updateUi(ProfileJson t) {
        if (t == null || t.getVariables() == null) {
            ZogUtils.printError(MineProfileFragment.class, "ProfileJson is null ");
            clearProfile();
            return;
        }

        if (!isAdded() || isHidden() || isDetached())
            return;

        mProfileVariables = t.getVariables();
        Space space = mProfileVariables.getSpace();


        if (space == null) {
            return;
        }
        final String avatar = space.getAvatar();

        LoadImageUtils.displayMineAvatar(getActivity(), mPhotoImage, avatar);
        mResourceText.setText(ClanUtils.getLevelStr(getActivity(), space));


        final String gender = space.getGender();
        if (Constants.SEX_MAN.equals(gender)) {
            mSexImage.setVisibility(View.VISIBLE);
            mSexImage.setImageResource(R.drawable.ic_man);
        } else if (Constants.SEX_WOMAN.equals(gender)) {
            mSexImage.setVisibility(View.VISIBLE);
            mSexImage.setImageResource(R.drawable.ic_woman);
        } else {
            mSexImage.setVisibility(View.GONE);
        }
        //用户名
        mUserName.setText(StringUtils.get(mProfileVariables.getMemberUsername()));
        //用户称号
        mGroup_id.setText(StringUtils.get(space.getGroup().getGrouptitle()));
        mGroup_id.setTextColor(Integer.valueOf(R.color.text_orange));

        if (AppUSPUtils.isUShowGroupAndRegisterDate(getActivity())) {
            groupAndRegisterDate.setVisibility(View.VISIBLE);
            wttvGroup.setContent(HtmlUtils.delHTMLTag(ProfileUtils.getGroupName(space)));
            wttvRegisterDate.setContent(StringUtils.get(space.getRegdate()));
        }
        // ClanUtils.initSignIn(getActivity(), ibSignIn);

        initMainMenu();

        DoSignIn.checkSignIn(getActivity(), ibSignIn, DoSignIn.CHECK, null);


        ibSignIn.setEnabled(false);

        int dotCount = AppSPUtils.getNewMessage(getActivity());
        setRedDot(dotCount);
    }


    @OnClick(R.id.user_settings)
    public void protrait(View view) {
        Intent intent = new Intent(getActivity(), HomePageActivity.class);
        intent.putExtra(Key.KEY_UID, AppSPUtils.getUid(getActivity()));

        if (mProfileVariables != null) {
            intent.putExtra(Key.KEY_PROFILE, mProfileVariables);
        }
        jump(intent);
    }


    /**
     * 我的收藏
     *
     * @param view
     */
    @OnClick(R.id.llMyFav)
    public void llThreadFav(View view) {
            String[] name = getResources().getStringArray(R.array.my_favorites);
            Intent intent = new Intent(getActivity(), MyFavActivity.class);
            intent.putExtra("name", name[0]);
            intent.putExtra(Key.KEY_UID, AppSPUtils.getUid(getActivity()));
            jump(intent);
    }

    /**
     * 签到有奖
     *
     * @param view
     */
    @OnClick(R.id.sign_in)
    public void sign_in(View view) {
        JumpWebUtils.gotoWeb(getActivity(), "", "http://app.sqys.com/plugin.php?id=wq_sign");
    }

    /**
     * 我的好友
     *
     * @param view
     */
    @OnClick(R.id.myFriend)
    public void myFriend(View view) {
            Intent intent = new Intent(getActivity(), FriendsActivity.class);
            intent.putExtra(Key.KEY_UID, AppSPUtils.getUid(getActivity()));
            jump(intent);
    }

    /**
     * 我的帖子
     *
     * @param view
     */
    @OnClick(R.id.llMyThread)
    public void llMyPost(View view) {
            String[] name = getResources().getStringArray(R.array.my_thread);
            Intent intent = new Intent(getActivity(), OwnerThreadActivity.class);
            intent.putExtra("name", name[0]);
            intent.putExtra(Key.KEY_UID, AppSPUtils.getUid(getActivity()));
            jump(intent);
    }


    /**
     * 站内消息
     *
     * @param view
     */
    @OnClick(R.id.my_message)
    public void myMessage(View view) {
        jump(MyPMActivity.class);
    }


    /**
     * 我的主页
     *
     * @param view
     */
    @OnClick(R.id.my_home_page)
    public void myHomePage(View view) {
        Intent intent = new Intent(getActivity(), HomePageActivity.class);
        intent.putExtra(Key.KEY_UID, AppSPUtils.getUid(getActivity()));

        if (mProfileVariables != null) {
            intent.putExtra(Key.KEY_PROFILE, mProfileVariables);
        }
        jump(intent);
    }

    /**
     * 设置
     *
     * @param view
     */
    @OnClick(R.id.settings)
    public void settings(View view) {
        BundleData bundleData = new BundleData();
        bundleData.put("ProfileVariables", mProfileVariables);
        IntentUtils.gotoNextActivity(getActivity(), SettingsActivity.class, bundleData);
    }

    /**
     * 关于
     *
     * @param view
     */
    @OnClick(R.id.about)
    public void about(View view) {

        BundleData bundleData = new BundleData();
        bundleData.put("title", getString(R.string.about));
        bundleData.put("content", AppConfig.ABOUT);

        IntentUtils.gotoNextActivity(getActivity(), WebActivity.class, bundleData);
    }

    /**
     * 充值医师币
     *
     * @param view
     */
    @OnClick(R.id.recharge)
    public void recharge(View view) {
        JumpWebUtils.gotoWeb(getActivity(), "", "http://app.sqys.com/plugin.php?id=xigua_c");

    }

    /**
     * 我的星数
     *
     * @param view
     */
    @OnClick(R.id.number_stars)
    public void star(View view) {
        JumpWebUtils.gotoWeb(getActivity(), "我的星数", "http://app.sqys.com/plugin.php?id=tpgao_medal:m");
    }

    /**
     * 完善信息
     *
     * @param view
     */
    @OnClick(R.id.My_info)
    public void My_info(View view) {
        JumpWebUtils.gotoWeb(getActivity(), "", "http://app.sqys.com/plugin.php?id=hwh_member");
//        ProfileJson profileJson = ProfileUtils.getProfile(getActivity());
//        updateUi(profileJson);
    }


    /**
     * 意见反馈
     *
     * @param view
     */
    @OnClick(R.id.feedback)
    public void fellback(View view) {
        JumpWebUtils.gotoWeb(getActivity(), "", "http://app.sqys.com/plugin.php?id=hejin_forms&formid=6");
    }

    /**
     * 我的视频
     *
     * @param view
     */
    @OnClick(R.id.my_void)
    public void my_void(View view) {
        JumpWebUtils.gotoWeb(getActivity(), "", "http://app.sqys.com/plugin.php?id=tpgao_medu:favlist");
    }

    /**
     * 积分兑换
     *
     * @param view
     */
    @OnClick(R.id.integral_club)
    public void integral_club(View view) {
        JumpWebUtils.gotoWeb(getActivity(), "", "http://app.sqys.com/plugin.php?id=xj_exchange");
    }

    /**
     * 积分俱乐部
     *
     * @param view
     */
    @OnClick(R.id.integral_club2)
    public void integral_club2(View view) {
        JumpWebUtils.gotoWeb(getActivity(), "", "http://app.sqys.com/plugin.php?id=xj_mall:items_list&classid=7");
    }

    /**
     * 积分规则
     *
     * @param view
     */
    @OnClick(R.id.integral_club4)
    public void integral_club4(View view) {
        JumpWebUtils.gotoWeb(getActivity(), "", "http://app.sqys.com/forum.php?mod=viewthread&tid=2385");
    }

    /**
     * 邀请有奖
     *
     * @param view
     */
    @OnClick(R.id.invita)
    public void invita(View view) {
        JumpWebUtils.gotoWeb(getActivity(), "", "http://app.sqys.com/plugin.php?id=cack_app_litebl&cmod=code");
    }

    public void initMainMenu() {
        if (!this.isAdded()) {
            return;
        }

        boolean isShowCfg = false;


        if (ClanUtils.isUseSignIn(getActivity())) {
            isShowCfg = true;
        }

        Fragment showingFragment = null;
        try {
            showingFragment = BottomTabMainActivity.getFragments().get(BottomTabMainActivity.NOW_POSITION_IN_VIEWPAGER);
        } catch (Exception e) {
        }


        boolean isShowSignIn = false;
        if (showingFragment != null && (showingFragment instanceof MineProfileFragment) && isShowCfg) {
            isShowSignIn = true;
        }
        if ((getActivity() instanceof MenuJumpActivity) && isShowCfg) {
            isShowSignIn = true;
        }

        ZogUtils.printError(MineProfileFragment.class, "isShowCfg:" + isShowCfg + " isShowSignIn:" + isShowSignIn + " ibSignIn:" + ibSignIn);

        if (isShowSignIn) {
            ibSignIn.setVisibility(View.VISIBLE);
        } else {
            ibSignIn.setVisibility(View.GONE);
        }
    }


}
