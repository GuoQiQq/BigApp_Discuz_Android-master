package com.appbyme.app272523.message.pm;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.appbyme.app272523.R;
import com.appbyme.app272523.base.BaseFragment;
import com.appbyme.app272523.base.util.LoadImageUtils;
import com.appbyme.app272523.base.util.StringUtils;
import com.appbyme.app272523.base.widget.ViewHolder;
import com.appbyme.app272523.base.widget.list.BaseRefreshAdapter;
import com.keyboard.utils.DefEmoticons;
import com.youzu.clan.base.json.MypmJson;
import com.youzu.clan.base.json.mypm.Mypm;
import com.youzu.clan.base.net.ClanHttpParams;

public class MyPMAdatper extends BaseRefreshAdapter<MypmJson> {

    private Context context;
    private BaseFragment fragment;

    public MyPMAdatper(Context context, ClanHttpParams params) {
        super(params);
        this.context = context;
    }


    public MyPMAdatper(Context context, BaseFragment fragment, ClanHttpParams params) {
        super(params);
        this.context = context;
        this.fragment = fragment;
    }

    @Override
    protected void loadSuccess(int page, MypmJson result) {
        super.loadSuccess(page, result);
        if (fragment != null && fragment instanceof MyPMFragment) {
            ((MyPMFragment) fragment).initMainMenu();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_mypm, null);
        }

        Mypm mypm = (Mypm) getItem(position);

        CheckBox checkbox = ViewHolder.get(convertView, R.id.checkbox);
        checkbox.setVisibility(isEditable() ? View.VISIBLE : View.GONE);

        ImageView photoImage = ViewHolder.get(convertView, R.id.icon);
        TextView nameText = ViewHolder.get(convertView, R.id.name);
        TextView contentText = ViewHolder.get(convertView, R.id.content);
        LoadImageUtils.display(context, photoImage, mypm.getMsgtoidAvatar());
        nameText.setText(StringUtils.get(mypm.getTousername()));
        contentText.setText(DefEmoticons.replaceUnicodeByEmoji(context, StringUtils.get(mypm.getMessage())));


        View newNotify = ViewHolder.get(convertView, R.id.newNotify);

        if (mypm.getIsnew().equals("1")) {
            newNotify.setVisibility(View.VISIBLE);
        } else {
            newNotify.setVisibility(View.GONE);
        }

        return convertView;
    }

}
