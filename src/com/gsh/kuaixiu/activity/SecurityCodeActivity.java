package com.gsh.kuaixiu.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import com.gsh.kuaixiu.R;
import com.gsh.base.viewmodel.SecurityCodeViewModel;
import com.gsh.kuaixiu.Constant;

/**
 * @author Tan Chunmao
 */

public class SecurityCodeActivity extends KuaixiuActivityBase implements OnClickListener{

	private SecurityCodeViewModel securityCodeModel;


	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_security_code_activity);
		setTitleBar("获取短信验证码");

		findViewById(R.id.click_request).setOnClickListener(this);
		findViewById(R.id.click_check).setOnClickListener(this);
		securityCodeModel = new SecurityCodeViewModel(this, Constant.SMS.APPKEY, Constant.SMS.APPSECRET, eventListener);
	}

	protected void onDestroy() {
		securityCodeModel.unRegister();
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.click_request:
				requestCode();
				break;
			case R.id.click_check:
				checkCode();
				break;
		}
	}

	private void requestCode() {
		showProgressDialog();
		String phoneInput = ((EditText)findViewById(R.id.input_phone)).getText().toString();
		securityCodeModel.requestCode(phoneInput);
	}


	private void checkCode() {
		String phoneInput = ((EditText)findViewById(R.id.input_phone)).getText().toString();
		String codeInput = ((EditText)findViewById(R.id.input_code)).getText().toString();
		securityCodeModel.checkCode(phoneInput, codeInput);
	}

	private SecurityCodeViewModel.EventListener eventListener = new SecurityCodeViewModel.EventListener() {
		@Override
		public void onEvent(String s) {
			dismissProgressDialog();
			toast(s);
		}

		@Override
		public void onVerificationThrough() {
			go.name(RegisterActivity.class).go();
		}

		@Override
		public void onResponse() {

		}
	};
}
