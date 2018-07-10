package com.femto.ugershop.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.EMValueCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chatuidemo.activity.ChatAllHistoryFragment;
import com.easemob.chatuidemo.activity.ContactlistFragment;
import com.easemob.chatuidemo.activity.EMBaseActivity;
import com.easemob.util.EMLog;
import com.easemob.util.HanziToPinyin;
import com.femto.ugershop.R;
import com.femto.ugershop.application.Constant;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.easemob.applib.controller.HXSDKHelper;
import com.femto.ugershop.easemob.applib.db.UserDao;
import com.femto.ugershop.easemob.applib.domain.User;
import com.femto.ugershop.fragment.Fragment_MyTongxunlu;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Activity_Contacts extends EMBaseActivity implements EMEventListener, OnClickListener {
	private RelativeLayout rl_back_contacts;
	private ContactlistFragment contactListFragment;
	// private ChatHistoryFragment chatHistoryFragment;
	private ChatAllHistoryFragment chatHistoryFragment;
	private Fragment[] fragments;
	private FragmentTransaction transaction;

	private Fragment_MyTongxunlu fmtxl;
	// private Button btn_tongxunluc, btn_lianxiren;
	private MyConnectionListener connectionListener = null;
	private RelativeLayout rl_newgoodsc, rl_designerc;
	private TextView tv_lasecon, tv_txl;
	private boolean isback = false;

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("我的通讯录");
	}

	

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_contacts);
		MyApplication.addActivity(this);
		initView();
		Control();
		connectionListener = new MyConnectionListener();
		EMChatManager.getInstance().addConnectionListener(connectionListener);
		chatHistoryFragment = new ChatAllHistoryFragment();
		contactListFragment = new ContactlistFragment();
		fmtxl = new Fragment_MyTongxunlu(MyApplication.userId, 1);
		fragments = new Fragment[] { chatHistoryFragment, fmtxl };
		// 添加显示第一个fragment
		getSupportFragmentManager().beginTransaction().add(R.id.fl_contain_xunlu, chatHistoryFragment)
				.add(R.id.fl_contain_xunlu, fmtxl).hide(fmtxl).show(chatHistoryFragment).commit();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("我的通讯录");
		EMChatManager.getInstance().registerEventListener(
				this,
				new EMNotifierEvent.Event[] { EMNotifierEvent.Event.EventNewMessage, EMNotifierEvent.Event.EventOfflineMessage,
						EMNotifierEvent.Event.EventConversationListChanged });
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.rl_back_contacts:
			isback = true;
			finish();
			break;

		case R.id.rl_designerc:
			showText(tv_txl, tv_lasecon);
			FragmentTransaction trx1 = getSupportFragmentManager().beginTransaction();
			trx1.hide(chatHistoryFragment);
			trx1.show(fmtxl);
			trx1.commit();
			break;

		case R.id.rl_newgoodsc:
			showText(tv_lasecon, tv_txl);
			FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
			trx.hide(fmtxl);
			trx.show(chatHistoryFragment);
			trx.commit();
			break;

		default:
			break;
		}
	}

	private void showText(TextView tshow, TextView tnoshow1) {
		tshow.setVisibility(View.VISIBLE);
		tnoshow1.setVisibility(View.INVISIBLE);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			isback = true;
			finish();
		}
		return true;
	}

	public void initView() {
		// TODO Auto-generated method stub
		rl_back_contacts = (RelativeLayout) findViewById(R.id.rl_back_contacts);
		rl_newgoodsc = (RelativeLayout) findViewById(R.id.rl_newgoodsc);
		rl_designerc = (RelativeLayout) findViewById(R.id.rl_designerc);
		tv_lasecon = (TextView) findViewById(R.id.tv_lasecon);
		tv_txl = (TextView) findViewById(R.id.tv_txl);
		// btn_lianxiren = (Button) findViewById(R.id.btn_lianxiren);
		// btn_tongxunluc = (Button) findViewById(R.id.btn_tongxunluc);
		// private RelativeLayout rl_newgoodsc, rl_designerc;
		// private ImageView im_leftc, im_rightc;
	}

	public void Control() {
		// TODO Auto-generated method stub
		rl_back_contacts.setOnClickListener(this);
		// btn_tongxunluc.setOnClickListener(this);
		// btn_lianxiren.setOnClickListener(this);
		rl_newgoodsc.setOnClickListener(this);
		rl_designerc.setOnClickListener(this);
	}

	@Override
	public void onEvent(EMNotifierEvent arg0) {
		System.out.println("zuo---EMNotifierEvent--");
		if (!isback) {
			Intent intent = new Intent();
			intent.setAction("com.hx.refresh");
			sendBroadcast(intent);
		}

	}

	/**
	 * 连接监听listener
	 * 
	 */
	public class MyConnectionListener implements EMConnectionListener {

		@Override
		public void onConnected() {
			boolean groupSynced = HXSDKHelper.getInstance().isGroupsSyncedWithServer();
			boolean contactSynced = HXSDKHelper.getInstance().isContactsSyncedWithServer();

			// in case group and contact were already synced, we supposed to
			// notify sdk we are ready to receive the events
			if (groupSynced && contactSynced) {
				new Thread() {
					@Override
					public void run() {
						HXSDKHelper.getInstance().notifyForRecevingEvents();
					}
				}.start();
			} else {
				if (!groupSynced) {
					asyncFetchGroupsFromServer();
				}

				if (!contactSynced) {
					asyncFetchContactsFromServer();
				}

				if (!HXSDKHelper.getInstance().isBlackListSyncedWithServer()) {
					asyncFetchBlackListFromServer();
				}
			}

			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// chatHistoryFragment.errorItem.setVisibility(View.GONE);
				}

			});
		}

		void asyncFetchGroupsFromServer() {
			HXSDKHelper.getInstance().asyncFetchGroupsFromServer(new EMCallBack() {
				@Override
				public void onSuccess() {
					HXSDKHelper.getInstance().noitifyGroupSyncListeners(true);

					if (HXSDKHelper.getInstance().isContactsSyncedWithServer()) {
						HXSDKHelper.getInstance().notifyForRecevingEvents();
					}
				}

				@Override
				public void onError(int code, String message) {
					HXSDKHelper.getInstance().noitifyGroupSyncListeners(false);
				}

				@Override
				public void onProgress(int progress, String status) {

				}

			});
		}

		void asyncFetchContactsFromServer() {
			HXSDKHelper.getInstance().asyncFetchContactsFromServer(new EMValueCallBack<List<String>>() {

				@Override
				public void onSuccess(List<String> usernames) {
					Context context = HXSDKHelper.getInstance().getAppContext();

					System.out.println("----------------" + usernames.toString());
					EMLog.d("roster", "contacts size: " + usernames.size());
					Map<String, User> userlist = new HashMap<String, User>();
					for (String username : usernames) {
						User user = new User();
						user.setUsername(username);
						setUserHearder(username, user);
						userlist.put(username, user);
					}
					// // 添加user"申请与通知"
					// User newFriends = new User();
					// newFriends
					// .setUsername(Constant.NEW_FRIENDS_USERNAME);
					// String strChat = context
					// .getString(R.string.Application_and_notify);
					// newFriends.setNick(strChat);
					//
					// userlist.put(Constant.NEW_FRIENDS_USERNAME,
					// newFriends);
					// // 添加"群聊"
					// User groupUser = new User();
					// String strGroup = context
					// .getString(R.string.group_chat);
					// groupUser.setUsername(Constant.GROUP_USERNAME);
					// groupUser.setNick(strGroup);
					// groupUser.setHeader("");
					// userlist.put(Constant.GROUP_USERNAME, groupUser);
					//
					// // 添加"聊天室"
					// User chatRoomItem = new User();
					// String strChatRoom = context
					// .getString(R.string.chat_room);
					// chatRoomItem.setUsername(Constant.CHAT_ROOM);
					// chatRoomItem.setNick(strChatRoom);
					// chatRoomItem.setHeader("");
					// userlist.put(Constant.CHAT_ROOM, chatRoomItem);
					//
					// // 添加"Robot"
					// User robotUser = new User();
					// String strRobot = context
					// .getString(R.string.robot_chat);
					// robotUser.setUsername(Constant.CHAT_ROBOT);
					// robotUser.setNick(strRobot);
					// robotUser.setHeader("");
					// userlist.put(Constant.CHAT_ROBOT, robotUser);

					// 存入内存
					MyApplication.getInstance().setContactList(userlist);
					// 存入db
					UserDao dao = new UserDao(context);
					List<User> users = new ArrayList<User>(userlist.values());
					dao.saveContactList(users);

					HXSDKHelper.getInstance().notifyContactsSyncListener(true);

					if (HXSDKHelper.getInstance().isGroupsSyncedWithServer()) {
						HXSDKHelper.getInstance().notifyForRecevingEvents();
					}

				}

				@Override
				public void onError(int error, String errorMsg) {
					HXSDKHelper.getInstance().notifyContactsSyncListener(false);
				}

			});
		}

		void asyncFetchBlackListFromServer() {
			HXSDKHelper.getInstance().asyncFetchBlackListFromServer(new EMValueCallBack<List<String>>() {

				@Override
				public void onSuccess(List<String> value) {
					EMContactManager.getInstance().saveBlackList(value);
					HXSDKHelper.getInstance().notifyBlackListSyncListener(true);
				}

				@Override
				public void onError(int error, String errorMsg) {
					HXSDKHelper.getInstance().notifyBlackListSyncListener(false);
				}

			});
		}

		@Override
		public void onDisconnected(final int error) {
			final String st1 = getResources().getString(R.string.can_not_connect_chat_server_connection);
			final String st2 = getResources().getString(R.string.the_current_network);
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (error == EMError.USER_REMOVED) {
						// 显示帐号已经被移除
						// showAccountRemovedDialog();
					} else if (error == EMError.CONNECTION_CONFLICT) {
						// 显示帐号在其他设备登陆dialog
						// showConflictDialog();
					} else {

						// chatHistoryFragment.errorItem
						// .setVisibility(View.VISIBLE);
						// if (NetUtils.hasNetwork(MainActivity.this))
						// chatHistoryFragment.errorText.setText(st1);
						// else
						// chatHistoryFragment.errorText.setText(st2);

					}
				}

			});
		}
	}

	/**
	 * 设置hearder属性，方便通讯中对联系人按header分类显示，以及通过右侧ABCD...字母栏快速定位联系人
	 * 
	 * @param username
	 * @param user
	 */
	private static void setUserHearder(String username, User user) {
		String headerName = null;
		if (!TextUtils.isEmpty(user.getNick())) {
			headerName = user.getNick();
		} else {
			headerName = user.getUsername();
		}
		if (username.equals(Constant.NEW_FRIENDS_USERNAME)) {
			user.setHeader("");
		} else if (Character.isDigit(headerName.charAt(0))) {
			user.setHeader("#");
		} else {
			user.setHeader(HanziToPinyin.getInstance().get(headerName.substring(0, 1)).get(0).target.substring(0, 1)
					.toUpperCase());
			char header = user.getHeader().toLowerCase().charAt(0);
			if (header < 'a' || header > 'z') {
				user.setHeader("#");
			}
		}
	}

}
