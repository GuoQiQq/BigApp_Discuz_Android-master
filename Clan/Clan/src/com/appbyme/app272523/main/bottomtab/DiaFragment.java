package com.appbyme.app272523.main.bottomtab;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.appbyme.app272523.R;
import com.appbyme.app272523.base.util.view.threadandarticle.ThreadAndArticleUtils;
import com.kit.imagelib.photoselector.PicSelectActivity;


public class DiaFragment extends DialogFragment implements View.OnClickListener {
    ImageView image1;
    ImageView image2;
    ImageView image3;
    ImageView image4;
    ImageView image5;
    private static DiaFragment diaFragment;
    PicSelectActivity picSelectActivity = new PicSelectActivity();

    public static DiaFragment newInstance(String title, String message) {
        DiaFragment diaFragment = new DiaFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("message", message);
        diaFragment.setArguments(bundle);
        return diaFragment;
    }

    public static DiaFragment newInstance() {
        if (diaFragment == null) {
            DiaFragment diaFragment = new DiaFragment();
        }
        return diaFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.aaaa, container, false);
        image1 = (ImageView) view.findViewById(R.id.image1);
        image2 = (ImageView) view.findViewById(R.id.image2);
//        image3 = (ImageView) view.findViewById(R.id.image3);
//        image4 = (ImageView) view.findViewById(R.id.image4);
        image5 = (ImageView) view.findViewById(R.id.image5);

        image1.setOnClickListener(this);
        image2.setOnClickListener(this);
//        image3.setOnClickListener(this);
//        image4.setOnClickListener(this);
        image5.setOnClickListener(this);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(true);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // 不带style的构建的dialog宽度无法铺满屏幕
        //     Dialog dialog = new Dialog(getActivity());
        Dialog dialog = new Dialog(getActivity(), R.style.CustomDatePickerDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.aaaa);
        dialog.setCanceledOnTouchOutside(true);
        //透明背景
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // 设置弹出框布局参数，宽度铺满全屏，底部。
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;

        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);


        return dialog;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            //文字
            case R.id.image1:
                ThreadAndArticleUtils.addThread1(getActivity(),1);
                break;
            //照相
//            case R.id.image2:
//                ThreadAndArticleUtils.addThread1(getActivity(),2);
//
//                break;
            //照片
            case R.id.image2:
                ThreadAndArticleUtils.addThread1(getActivity(),2);
                break;
            //语音
//            case R.id.image4:
//                ThreadAndArticleUtils.addThread1(getActivity(),4);
//                break;
            //返回
            case R.id.image5:
                dismiss();
                break;
        }
    }
}
