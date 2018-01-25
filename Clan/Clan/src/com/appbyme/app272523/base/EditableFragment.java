package com.appbyme.app272523.base;

import com.appbyme.app272523.base.widget.list.OnEditListener;
import com.appbyme.app272523.base.widget.list.RefreshListView;

public abstract class EditableFragment extends BaseFragment implements OnEditListener{
	public abstract RefreshListView getListView();
	
}
