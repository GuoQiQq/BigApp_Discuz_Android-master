package com.youzu.clan.base.json.profile;

import com.youzu.android.framework.json.annotation.JSONField;

import java.io.Serializable;

public class Wsq implements Serializable {
	private static final long serialVersionUID = 94779530797099354L;
	private String wsqApicredit;
	public String getWsqApicredit() {
		return wsqApicredit;
	}
	@JSONField(name="wsq_apicredit") 
	public void setWsqApicredit(String wsqApicredit) {
		this.wsqApicredit = wsqApicredit;
	}
	
}
