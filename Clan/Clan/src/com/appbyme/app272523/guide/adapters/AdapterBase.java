package com.appbyme.app272523.guide.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: AdapterBase 
 * @Description: 通用Adapter
 * @author wwj_748
 * @date 2015年6月17日 下午4:03:39 
 * @param <T>
 */
public abstract class AdapterBase <T> extends BaseAdapter{
	protected final Context mContext;
	protected List<T> mData;
	protected final int[] mLayoutResArrays;
	
	public AdapterBase(Context context, int[] layoutResArray) {
		this(context, layoutResArray, null);
	}

	/**
	 * @param context
	 * @param layoutResArray
	 * @param data
	 */
	public AdapterBase(Context context, int[] layoutResArray, List<T> data) {
		this.mData = data == null? new ArrayList<T>() : data;
		this.mContext = context;
		this.mLayoutResArrays = layoutResArray;
	}
	
	
	public void setData(ArrayList<T> data) {
		this.mData = data;
		this.notifyDataSetChanged();
	}
	
	public void addData(ArrayList<T> data) {
		if (data != null) {
			this.mData.addAll(data);
		}
		this.notifyDataSetChanged();
	}
	
	public void addData(T data) {
		this.mData.add(data);
		this.notifyDataSetChanged();
	}
	
	public ArrayList<T> getAllData() {
		return (ArrayList<T>) this.mData;
	}
	
	@Override
	public int getCount() {
		if (this.mData == null) {
			return 0;
		}
		return this.mData.size();
	}
	
	@Override
	public T getItem(int position) {
		if (position > this.mData.size()) {
			return null;
		}
		return mData.get(position);
	}
	
	@Override
	public long getItemId(int position) {
	    return position;
	}
	
	@Override
	public int getViewTypeCount() {
	    return this.mLayoutResArrays.length;
	}

	/**
	 * You should always override this method,to return the 
	 * correct view type for every cell.
	 * 
	 */
	public int getItemViewType(int position){
	    return 0;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolderHelper helper = getAdapterHelper(position, convertView, parent);
		T item = getItem(position);
		covert(helper, item);
		return helper.getView();
	}
	
	
	protected abstract void covert(ViewHolderHelper helper, T item);
	protected abstract ViewHolderHelper getAdapterHelper(int position, View convertView, ViewGroup parent);
}
