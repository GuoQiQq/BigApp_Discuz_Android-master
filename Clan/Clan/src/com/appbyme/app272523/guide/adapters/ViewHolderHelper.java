package com.appbyme.app272523.guide.adapters;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * @ClassName: ViewHolderHelper
 * @Description: 打造通用的ViewHolder
 * @author wwj_748
 * @date 2015年6月17日 下午3:55:18
 */
public class ViewHolderHelper {
	private SparseArray<View> mViews;
	private Context mContext;
	private int position;
	private View mConvertView;

	private ViewHolderHelper(Context context, ViewGroup parent, int layoutId,
			int position) {
		this.mContext = context;
		this.position = position;
		this.mViews = new SparseArray<View>();
		this.mConvertView = LayoutInflater.from(mContext).inflate(layoutId,
				parent, false);

		mConvertView.setTag(this);
	}

	/**
	 * 复用ViewHolder
	 * 
	 * @param context
	 * @param convertView
	 * @param parent
	 * @param layoutId
	 * @param position
	 * @return
	 */
	public static ViewHolderHelper get(Context context, View convertView,
			ViewGroup parent, int layoutId, int position) {
		if (convertView == null) {
			return new ViewHolderHelper(context, parent, layoutId, position);
		}
		ViewHolderHelper existingHelper = (ViewHolderHelper) convertView
				.getTag();
		existingHelper.position = position;
		return existingHelper;
	}

	/**
	 * 通过viewId找到控件
	 * 
	 * @param viewId
	 * @return
	 */
	private <T extends View> T findViewById(int viewId) {
		View view = mViews.get(viewId);
		if (view == null) {
			view = mConvertView.findViewById(viewId);
			mViews.put(viewId, view);
		}
		return (T) view;
	}

	/**
	 * 设置TextView文本
	 * 
	 * @param viewId
	 * @param value
	 * @return
	 */
	public ViewHolderHelper setText(int viewId, String value) {
		TextView view = findViewById(viewId);
		view.setText(value);
		return this;
	}

	/**
	 * 设置控件背景
	 * 
	 * @param viewId
	 * @param resId
	 * @return
	 */
	public ViewHolderHelper setBackground(int viewId, int resId) {
		View view = findViewById(viewId);
		view.setBackgroundResource(resId);
		return this;
	}

	/**
	 * 设置圆角图片
	 * 
	 * @param viewId
	 * @param res
	 * @param resId
	 * @return
	 */
	public ViewHolderHelper setRoundImageBitmap(int viewId, Resources res,
			int resId, int roundPiexl) {
		ImageView imageView = findViewById(viewId);
		Bitmap bitmap = BitmapFactory.decodeResource(res, resId);
		imageView.setImageBitmap(getRoundedCornerBitmap(bitmap, roundPiexl));
		return this;
	}


	public void setViewVisiblity(int viewId, int visibility) {
		View view = findViewById(viewId);
		view.setVisibility(visibility);
	}

	/**
	 * 设置控件点击事件
	 * 
	 * @param viewId
	 * @param listener
	 * @return
	 */
	public ViewHolderHelper setClickListener(int viewId,
			View.OnClickListener listener) {
		View view = findViewById(viewId);
		view.setOnClickListener(listener);
		return this;
	}

	/**
	 * 返回convertView
	 * 
	 * @return
	 */
	public View getView() {
		return mConvertView;
	}


	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int roundPiexl) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPiexl, roundPiexl, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

}
