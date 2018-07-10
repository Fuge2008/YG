package com.femto.ugershop.modle;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.femto.ugershop.application.ListItemAdapter;

public class Test extends ListItemAdapter<String> {

	public Test(Context context, List<String> list) {
		super(context, list);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return null;
	}

}
