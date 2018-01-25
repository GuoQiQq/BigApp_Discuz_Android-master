package com.kit.extend.ui.web;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.ActionProvider;
import android.view.Display;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;
import android.view.View;
import android.widget.Toast;

import com.kit.extend.R;
import com.kit.utils.ZogUtils;
import com.tencent.mm.opensdk.utils.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MoreActionWebProvider extends ActionProvider {

    //    private Context context;
    private WebActivity activity;
    MoreActionProviderCallback callback;


    private OnMenuItemClickListener mListener;

    public MoreActionWebProvider(Context context) {
        super(context);
    }


    public void setActivity(WebActivity activity) {
        this.activity = activity;

        ZogUtils.printLog(MoreActionWebProvider.class, "activity:" + activity);
    }

    @Override
    public View onCreateActionView() {
        return null;
    }


    @Override
    public void onPrepareSubMenu(SubMenu subMenu) {

        subMenu.clear();

//        subMenu.add(activity.getString(R.string.menu_web_open_in_bowser))
//                .setIcon(R.drawable.ic_bowser_white)
//                .setOnMenuItemClickListener(new OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        ZogUtils.printError(MoreActionWebProvider.class, "activity.webFragment.loadingUrl:" + activity.webFragment.loadingUrl);
//                        if (!StringUtils.isEmptyOrNullOrNullStr(activity.webFragment.loadingUrl))
//                            BrowserUtils.gotoBrowser(activity, activity.webFragment.loadingUrl);
//                        return true;
//                    }
//                });
//
//        subMenu.add(activity.getString(R.string.menu_web_copy_url))
//                .setIcon(R.drawable.abc_ic_menu_copy_mtrl_am_alpha)
//                .setOnMenuItemClickListener(new OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//
//                        ZogUtils.printError(MoreActionWebProvider.class, "activity.webFragment.loadingUrl:" + activity.webFragment.loadingUrl);
//                        if (!StringUtils.isEmptyOrNullOrNullStr(activity.webFragment.loadingUrl)) {
//                            ClipboardUtils.copy(activity, activity.webFragment.loadingUrl);
//                            ToastUtils.mkShortTimeToast(activity, activity.getResources().getString(R.string.copy_ok_default));
//                        }
//                        return true;
//                    }
//                });
//

        subMenu.add(activity.getString(R.string.share))
                .setIcon(R.drawable.ic_share)
                .setOnMenuItemClickListener(new OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Bitmap bitmap = shotActivityNoBar(activity);
                        saveImageToGallery(getContext(), bitmap);
                        return true;
                    }
                });
    }

    public Bitmap shotActivityNoBar(Activity activity) {
        // 获取windows中最顶层的view
        View view = activity.getWindow().getDecorView();
        view.buildDrawingCache();
        // 获取状态栏高度
        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        int statusBarHeights = rect.top;
        Display display = activity.getWindowManager().getDefaultDisplay();
        // 获取屏幕宽和高
        int widths = display.getWidth();
        int heights = display.getHeight();
        // 允许当前窗口保存缓存信息
        view.setDrawingCacheEnabled(true);
        // 去掉状态栏
        Bitmap bmp = Bitmap.createBitmap(view.getDrawingCache(), 0,
                statusBarHeights, widths, heights - statusBarHeights);
        // 销毁缓存信息
        view.destroyDrawingCache();
        return bmp;
    }

    private File appDir;

    public void setAppDir(File appDir) {
        this.appDir = appDir;
    }

    public File getAppDir() {
        return appDir;
    }

    /**
     * 将二维码图片保存到文件夹
     *
     * @param context
     * @param bmp
     */
    public void saveImageToGallery(Context context, Bitmap bmp) {
//        MoreActionWebProvider moreActionWebProvider = new MoreActionWebProvider(context);
        // 首先保存图片
        String externalStorageState = Environment.getExternalStorageState();
        //判断sd卡是否挂载
        if (externalStorageState.equals(Environment.MEDIA_MOUNTED)) {
        /*外部存储可用，则保存到外部存储*/
            //创建一个文件夹
            File appDir = new File(Environment.getExternalStorageDirectory(), "syy");
            //如果文件夹不存在
            if (!appDir.exists()) {
                //则创建这个文件夹
                appDir.mkdir();
            }
            //将bitmap保存
            save(context, bmp, appDir);
//            moreActionWebProvider.setAppDir(appDir);
            getQrAndShare(appDir);
//            Toast.makeText(context, "请22 " + appDir, Toast.LENGTH_SHORT).show();
        } else {
            //外部不可用，将图片保存到内部存储中，获取内部存储文件目录
            File filesDir = context.getFilesDir();
            //保存
            save(context, bmp, filesDir);
//            moreActionWebProvider.setAppDir(filesDir);
            getQrAndShare(filesDir);
//            Toast.makeText(context, "请33 " + filesDir, Toast.LENGTH_SHORT).show();
        }
    }

    private static void save(Context context, Bitmap bmp, File appDir) {
        //命名文件名称
        String fileName = System.currentTimeMillis() + ".jpg";
        //创建图片文件，传入文件夹和文件名
        File imagePath = new File(appDir, fileName);
        try {
            //创建文件输出流，传入图片文件，用于输入bitmap
            FileOutputStream fos = new FileOutputStream(imagePath);
            //将bitmap压缩成png，并保存到相应的文件夹中
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            //冲刷流
            fos.flush();
            //关闭流
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    imagePath.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + imagePath.getAbsolutePath())));
    }


    private void getQrAndShare(File imgFile) {
        String mPath;

        //获取到文件夹中所有的文件
        File[] files = imgFile.listFiles();
        //如果文件夹下没有文件，则提示去生成
        if (files.length == 0) {
            Toast.makeText(getContext(), "分享失败", Toast.LENGTH_SHORT).show();
        } else {
            //创建一个存放图片路径的集合
            List<String> imagePathList = new ArrayList<>();
            // 将所有的文件存入ArrayList中
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                imagePathList.add(file.getPath());
            }
            //获取图片路径,获取的永远是最后一张二维码
            int size = imagePathList.size();
            if (size != 0) {
                //获取最后一张图片的路径
                mPath = imagePathList.get(size - 1);
                //转化为uri
                Uri imageUri = Uri.fromFile(new File(mPath));
                Log.e("share", "uri:" + imageUri);
                //创建Intent设置Action为ACTION_SEND，传入uri，设置type为image/*，开启Intent
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                shareIntent.setType("image/*");
                activity.startActivity(Intent.createChooser(shareIntent, "分享到"));
            } else {
                //由文件得到uri
                Toast.makeText(getContext(), "分享失败", Toast.LENGTH_SHORT).show();
            }
        }

    }


    @Override
    public boolean hasSubMenu() {
        return true;
    }

}