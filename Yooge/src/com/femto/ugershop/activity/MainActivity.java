package com.femto.ugershop.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMGroupChangeListener;
import com.easemob.EMNotifierEvent;
import com.easemob.EMValueCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMConversation.EMConversationType;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Type;
import com.easemob.chat.TextMessageBody;
import com.easemob.chatuidemo.activity.ChatActivity;
import com.easemob.chatuidemo.activity.ChatAllHistoryFragment;
import com.easemob.chatuidemo.activity.ContactlistFragment;
import com.easemob.chatuidemo.activity.GroupsActivity;
import com.easemob.chatuidemo.activity.LoginActivity;
import com.easemob.chatuidemo.activity.SettingsFragment;
import com.easemob.util.EMLog;
import com.easemob.util.HanziToPinyin;
import com.femto.hx.utils.CommonUtils;
import com.femto.ugershop.R;
import com.femto.ugershop.application.Constant;
import com.femto.ugershop.application.DemoHXSDKHelper;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.easemob.applib.controller.HXSDKHelper;
import com.femto.ugershop.easemob.applib.db.InviteMessgeDao;
import com.femto.ugershop.easemob.applib.db.UserDao;
import com.femto.ugershop.easemob.applib.domain.InviteMessage;
import com.femto.ugershop.easemob.applib.domain.InviteMessage.InviteMesageStatus;
import com.femto.ugershop.easemob.applib.domain.User;
import com.femto.ugershop.entity.Flags;
import com.femto.ugershop.fragment.Fragment_Community;
import com.femto.ugershop.fragment.Fragment_Customization;
import com.femto.ugershop.fragment.Fragment_Designer;
import com.femto.ugershop.fragment.Fragment_MGC;
import com.femto.ugershop.fragment.Fragment_Me;
import com.femto.ugershop.fragment.Fragment_Me_Custom;
import com.femto.ugershop.fragment.Fragment_Message;
import com.femto.ugershop.fragment.Fragment_NewCustomize;
import com.femto.ugershop.fragment.Fragment_Store;
import com.femto.ugershop.interfac.MyInterface;
import com.femto.ugershop.interfac.MyInterface.OnWindowsListener;
import com.femto.ugershop.selepic.ImgFileListActivity;
import com.femto.ugershop.service.DownLoadService;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

public class MainActivity extends MainBaseActivity implements EMEventListener {

	private FragmentTransaction transaction;
	private Fragment_Community fcommenity;
	private Fragment_MGC fcustomization;
	private Fragment_Message fstore;
	private Fragment_Me fme;
	private Fragment_Designer fde;
	private RadioButton rb_store, rb_customization, rb_community, rb_me, rb_design_tab;
	private boolean islogin;
	private int userid = 0;
	private int type;
	private Fragment_Me_Custom fmec;
	private long firstClickTime;
	protected static final String TAG = "MainActivity";
	// // 未读消息textview
	// private TextView unreadLabel;
	// // 未读通讯录textview
	// private TextView unreadAddressLable;
	private Button[] mTabs;
	private ContactlistFragment contactListFragment;
	// private ChatHistoryFragment chatHistoryFragment;
	private ChatAllHistoryFragment chatHistoryFragment;
	private SettingsFragment settingFragment;
	private Fragment[] fragments;
	private int index;
	// 当前fragment的index
	private int currentTabIndex;
	// 账号在别处登录
	public boolean isConflict = false;
	// 账号被移除
	private boolean isCurrentAccountRemoved = false;

	private MyConnectionListener connectionListener = null;
	private MyGroupChangeListener groupChangeListener = null;
	private InviteMessgeDao inviteMessgeDao;
	private UserDao userDao;
	private ImageView im_mflags, im_newmflags;

	@Override
	public void initView() {
		MyApplication.addActivity(this);
		rb_store = (RadioButton) findViewById(R.id.rb_store);
		rb_customization = (RadioButton) findViewById(R.id.rb_customization);
		rb_community = (RadioButton) findViewById(R.id.rb_community1);
		rb_me = (RadioButton) findViewById(R.id.rb_me);
		rb_design_tab = (RadioButton) findViewById(R.id.rb_design_tab);
		im_mflags = (ImageView) findViewById(R.id.im_mflags);
		im_newmflags = (ImageView) findViewById(R.id.im_newmflags);

		im_newmflags.setOnClickListener(this);
	}

	private android.app.AlertDialog.Builder conflictBuilder;
	private android.app.AlertDialog.Builder accountRemovedBuilder;
	private boolean isConflictDialogShow;
	private boolean isAccountRemovedDialogShow;
	private BroadcastReceiver internalDebugReceiver;
	private Toast toast;
	private MBC mbc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.update(this);
		UmengUpdateAgent.setDeltaUpdate(false);
		registMBC();
		if (savedInstanceState != null && savedInstanceState.getBoolean(Constant.ACCOUNT_REMOVED, false)) {
			// 防止被移除后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
			// 三个fragment里加的判断同理
			MyApplication.getInstance().logout(null);
			finish();
			startActivity(new Intent(this, LoginActivity.class));
			return;
		} else if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false)) {
			// 防止被T后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
			// 三个fragment里加的判断同理
			finish();
			startActivity(new Intent(this, LoginActivity.class));
			return;
		}

		if (getIntent().getBooleanExtra("conflict", false) && !isConflictDialogShow) {
			showConflictDialog();
		} else if (getIntent().getBooleanExtra(Constant.ACCOUNT_REMOVED, false) && !isAccountRemovedDialogShow) {
			showAccountRemovedDialog();
		}

		inviteMessgeDao = new InviteMessgeDao(this);
		userDao = new UserDao(this);

		toast = new Toast(this);
		toast.setView(View.inflate(this, R.layout.toa, null));
		toast.setDuration(500);
		init();
	}

	private void registMBC() {
		// TODO Auto-generated method stub
		mbc = new MBC();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.hx.refresh.hah");
		registerReceiver(mbc, filter);
	}

	class MBC extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (action.equals("com.hx.refresh.hah")) {
				System.out.println("zuo===刷新");
				refreshUI();
			}

		}
	}

	private void init() {
		// setContactListener监听联系人的变化等
		EMContactManager.getInstance().setContactListener(new MyContactListener());
		// 注册一个监听连接状态的listener

		connectionListener = new MyConnectionListener();
		EMChatManager.getInstance().addConnectionListener(connectionListener);

		groupChangeListener = new MyGroupChangeListener();
		// 注册群聊相关的listener
		EMGroupManager.getInstance().addGroupChangeListener(groupChangeListener);

		// // 内部测试方法，请忽略
		// registerInternalDebugReceiver();
		// 开启下载服务
		Intent intent_service = new Intent(this, DownLoadService.class);
		startService(intent_service);
	}

	/**
	 * MyGroupChangeListener
	 */
	public class MyGroupChangeListener implements EMGroupChangeListener {

		@Override
		public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {

			boolean hasGroup = false;
			for (EMGroup group : EMGroupManager.getInstance().getAllGroups()) {
				if (group.getGroupId().equals(groupId)) {
					hasGroup = true;
					break;
				}
			}
			if (!hasGroup)
				return;

			// 被邀请
			String st3 = getResources().getString(R.string.Invite_you_to_join_a_group_chat);
			EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
			msg.setChatType(ChatType.GroupChat);
			msg.setFrom(inviter);
			msg.setTo(groupId);
			msg.setMsgId(UUID.randomUUID().toString());
			msg.addBody(new TextMessageBody(inviter + " " + st3));
			// 保存邀请消息
			EMChatManager.getInstance().saveMessage(msg);
			// 提醒新消息
			HXSDKHelper.getInstance().getNotifier().viberateAndPlayTone(msg);

			runOnUiThread(new Runnable() {
				public void run() {
					updateUnreadLabel();
					// 刷新ui
					if (currentTabIndex == 0) {
						Intent intent = new Intent();
						intent.setAction("com.hx.refresh");
						sendBroadcast(intent);
					}
					if (CommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName())) {
						GroupsActivity.instance.onResume();
					}
				}
			});

		}

		@Override
		public void onInvitationAccpted(String groupId, String inviter, String reason) {

		}

		@Override
		public void onInvitationDeclined(String groupId, String invitee, String reason) {

		}

		@Override
		public void onUserRemoved(String groupId, String groupName) {

			// 提示用户被T了，demo省略此步骤
			// 刷新ui
			runOnUiThread(new Runnable() {
				public void run() {
					try {
						updateUnreadLabel();
						if (currentTabIndex == 0) {

							Intent intent = new Intent();
							intent.setAction("com.hx.refresh");
							sendBroadcast(intent);

						}
						if (CommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName())) {
							GroupsActivity.instance.onResume();
						}
					} catch (Exception e) {
						EMLog.e(TAG, "refresh exception " + e.getMessage());
					}
				}
			});
		}

		@Override
		public void onGroupDestroy(String groupId, String groupName) {

			// 群被解散
			// 提示用户群被解散,demo省略
			// 刷新ui
			runOnUiThread(new Runnable() {
				public void run() {
					updateUnreadLabel();
					if (currentTabIndex == 0) {
						Intent intent = new Intent();
						intent.setAction("com.hx.refresh");
						sendBroadcast(intent);

					}
					if (CommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName())) {
						GroupsActivity.instance.onResume();
					}
				}
			});

		}

		@Override
		public void onApplicationReceived(String groupId, String groupName, String applyer, String reason) {

			// 用户申请加入群聊
			InviteMessage msg = new InviteMessage();
			msg.setFrom(applyer);
			msg.setTime(System.currentTimeMillis());
			msg.setGroupId(groupId);
			msg.setGroupName(groupName);
			msg.setReason(reason);
			Log.d(TAG, applyer + " 申请加入群聊：" + groupName);
			msg.setStatus(InviteMesageStatus.BEAPPLYED);
			notifyNewIviteMessage(msg);
		}

		@Override
		public void onApplicationAccept(String groupId, String groupName, String accepter) {

			String st4 = getResources().getString(R.string.Agreed_to_your_group_chat_application);
			// 加群申请被同意
			EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
			msg.setChatType(ChatType.GroupChat);
			msg.setFrom(accepter);
			msg.setTo(groupId);
			msg.setMsgId(UUID.randomUUID().toString());
			msg.addBody(new TextMessageBody(accepter + " " + st4));
			// 保存同意消息
			EMChatManager.getInstance().saveMessage(msg);
			// 提醒新消息
			HXSDKHelper.getInstance().getNotifier().viberateAndPlayTone(msg);

			runOnUiThread(new Runnable() {
				public void run() {
					updateUnreadLabel();
					// 刷新ui
					if (currentTabIndex == 0) {
						Intent intent = new Intent();
						intent.setAction("com.hx.refresh");
						sendBroadcast(intent);
					}
					if (CommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName())) {
						GroupsActivity.instance.onResume();
					}
				}
			});
		}

		@Override
		public void onApplicationDeclined(String groupId, String groupName, String decliner, String reason) {
			// 加群申请被拒绝，demo未实现
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
						showAccountRemovedDialog();
					} else if (error == EMError.CONNECTION_CONFLICT) {
						// 显示帐号在其他设备登陆dialog
						showConflictDialog();
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
	 * set head
	 * 
	 * @param username
	 * @return
	 */
	User setUserHead(String username) {
		User user = new User();
		user.setUsername(username);
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
		return user;
	}

	/**
	 * 保存邀请等msg
	 * 
	 * @param msg
	 */
	private void saveInviteMsg(InviteMessage msg) {
		// 保存msg
		inviteMessgeDao.saveMessage(msg);
		// 未读数加1
		User user = MyApplication.getInstance().getContactList().get(Constant.NEW_FRIENDS_USERNAME);
		if (user.getUnreadMsgCount() == 0)
			user.setUnreadMsgCount(user.getUnreadMsgCount() + 1);
	}

	/**
	 * 保存提示新消息
	 * 
	 * @param msg
	 */
	private void notifyNewIviteMessage(InviteMessage msg) {
		saveInviteMsg(msg);
		// 提示有新消息
		HXSDKHelper.getInstance().getNotifier().viberateAndPlayTone(null);

		// 刷新bottom bar消息未读数
		updateUnreadAddressLable();
		// 刷新好友页面ui
		if (currentTabIndex == 1) {
			Intent intent = new Intent();
			intent.setAction("com.hx.refresh");
			sendBroadcast(intent);
		}
	}

	/***
	 * 好友变化listener
	 * 
	 */
	public class MyContactListener implements EMContactListener {

		@Override
		public void onContactAdded(List<String> usernameList) {
			// 保存增加的联系人
			Map<String, User> localUsers = MyApplication.getInstance().getContactList();
			Map<String, User> toAddUsers = new HashMap<String, User>();
			for (String username : usernameList) {
				User user = setUserHead(username);
				// 添加好友时可能会回调added方法两次
				if (!localUsers.containsKey(username)) {
					userDao.saveContact(user);
				}
				toAddUsers.put(username, user);
			}
			localUsers.putAll(toAddUsers);
			// 刷新ui
			if (currentTabIndex == 1) {
				Intent intent = new Intent();
				intent.setAction("com.hx.refresh");
				sendBroadcast(intent);
				System.out.println("zuo==好友变化=");
			}

		}

		@Override
		public void onContactDeleted(final List<String> usernameList) {
			// 被删除
			Map<String, User> localUsers = MyApplication.getInstance().getContactList();
			for (String username : usernameList) {
				localUsers.remove(username);
				userDao.deleteContact(username);
				inviteMessgeDao.deleteMessage(username);
			}
			runOnUiThread(new Runnable() {
				public void run() {
					// 如果正在与此用户的聊天页面
					String st10 = getResources().getString(R.string.have_you_removed);
					if (ChatActivity.activityInstance != null
							&& usernameList.contains(ChatActivity.activityInstance.getToChatUsername())) {
						Toast.makeText(MainActivity.this, ChatActivity.activityInstance.getToChatUsername() + st10, 1).show();
						ChatActivity.activityInstance.finish();
					}
					updateUnreadLabel();
					// 刷新ui
					Intent intent = new Intent();
					intent.setAction("com.hx.refresh");
					sendBroadcast(intent);
					System.out.println("zuo==被删除=");
					// contactListFragment.refresh();
					// chatHistoryFragment.refresh();
				}
			});

		}

		@Override
		public void onContactInvited(String username, String reason) {

			// 接到邀请的消息，如果不处理(同意或拒绝)，掉线后，服务器会自动再发过来，所以客户端不需要重复提醒
			List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();

			for (InviteMessage inviteMessage : msgs) {
				if (inviteMessage.getGroupId() == null && inviteMessage.getFrom().equals(username)) {
					inviteMessgeDao.deleteMessage(username);
				}
			}
			// 自己封装的javabean
			InviteMessage msg = new InviteMessage();
			msg.setFrom(username);
			msg.setTime(System.currentTimeMillis());
			msg.setReason(reason);
			Log.d(TAG, username + "请求加你为好友,reason: " + reason);
			// 设置相应status
			msg.setStatus(InviteMesageStatus.BEINVITEED);
			notifyNewIviteMessage(msg);
			Intent intent = new Intent();
			intent.setAction("com.hx.refresh");
			sendBroadcast(intent);
		}

		@Override
		public void onContactAgreed(String username) {
			List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
			for (InviteMessage inviteMessage : msgs) {
				if (inviteMessage.getFrom().equals(username)) {
					return;
				}
			}
			// 自己封装的javabean
			InviteMessage msg = new InviteMessage();
			msg.setFrom(username);
			msg.setTime(System.currentTimeMillis());
			Log.d(TAG, username + "同意了你的好友请求");
			msg.setStatus(InviteMesageStatus.BEAGREED);
			notifyNewIviteMessage(msg);
			Intent intent = new Intent();
			intent.setAction("com.hx.refresh");
			sendBroadcast(intent);
		}

		@Override
		public void onContactRefused(String username) {

			// 参考同意，被邀请实现此功能,demo未实现
			Log.d(username, username + "拒绝了你的好友请求");
		}

	}

	/**
	 * 显示帐号在别处登录dialog
	 */
	private void showConflictDialog() {
		isConflictDialogShow = true;
		MyApplication.getInstance().logout(null);
		SharedPreferences sp = getSharedPreferences("Login", Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putBoolean("islogin", false);
		edit.putInt("userId", -1);
		edit.commit();
		MyApplication.islogin = false;
		MyApplication.getInfo();
		String st = getResources().getString(R.string.Logoff_notification);
		if (!MainActivity.this.isFinishing()) {
			// clear up global variables
			try {
				if (conflictBuilder == null)
					conflictBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
				conflictBuilder.setTitle(st);
				conflictBuilder.setMessage(R.string.connect_conflict);
				conflictBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						conflictBuilder = null;
						Intent intent = new Intent(MainActivity.this, Activity_Login.class);
						SharedPreferences sp = getSharedPreferences("Login", Context.MODE_PRIVATE);
						Editor edit = sp.edit();
						edit.putBoolean("islogin", false);
						edit.commit();
						intent.putExtra("isother", 1);
						startActivity(intent);
						MyApplication.getInfo();
					}
				});
				conflictBuilder.setCancelable(false);
				conflictBuilder.create().show();
				isConflict = true;
			} catch (Exception e) {
				EMLog.e(TAG, "---------color conflictBuilder error" + e.getMessage());
			}

		}

	}

	/**
	 * 帐号被移除的dialog
	 */
	private void showAccountRemovedDialog() {
		isAccountRemovedDialogShow = true;
		MyApplication.getInstance().logout(null);
		String st5 = getResources().getString(R.string.Remove_the_notification);
		if (!MainActivity.this.isFinishing()) {
			// clear up global variables
			try {
				if (accountRemovedBuilder == null)
					accountRemovedBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
				accountRemovedBuilder.setTitle(st5);
				accountRemovedBuilder.setMessage(R.string.em_user_remove);
				accountRemovedBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						accountRemovedBuilder = null;
						finish();
						startActivity(new Intent(MainActivity.this, Activity_Login.class));
						SharedPreferences sp = getSharedPreferences("Login", Context.MODE_PRIVATE);
						Editor edit = sp.edit();
						edit.putBoolean("islogin", false);
						edit.commit();
					}
				});
				accountRemovedBuilder.setCancelable(false);
				accountRemovedBuilder.create().show();
				isCurrentAccountRemoved = true;
			} catch (Exception e) {
				EMLog.e(TAG, "---------color userRemovedBuilder error" + e.getMessage());
			}

		}

	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	/**
	 * 刷新未读消息数
	 */
	public void updateUnreadLabel() {
		int count = getUnreadMsgCountTotal();

		Intent intent = new Intent();
		intent.setAction("com.hx.refresh");
		System.out.println("zuo未读总数" + count);
		sendBroadcast(intent);

		Intent intent_unred = new Intent();
		intent_unred.setAction("com.hx.refresh.unread");
		intent_unred.putExtra("count", count);
		sendBroadcast(intent_unred);

	}

	/**
	 * 获取未读消息数
	 * 
	 * @return
	 */
	public int getUnreadMsgCountTotal() {
		int unreadMsgCountTotal = 0;
		int chatroomUnreadMsgCount = 0;
		unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
		for (EMConversation conversation : EMChatManager.getInstance().getAllConversations().values()) {
			if (conversation.getType() == EMConversationType.ChatRoom)
				chatroomUnreadMsgCount = chatroomUnreadMsgCount + conversation.getUnreadMsgCount();
		}
		return unreadMsgCountTotal - chatroomUnreadMsgCount;
	}

	/**
	 * 获取未读申请与通知消息
	 * 
	 * @return
	 */
	public int getUnreadAddressCountTotal() {

		int unreadAddressCountTotal = 0;
		if (MyApplication.getInstance().getContactList().get(Constant.NEW_FRIENDS_USERNAME) != null)
			unreadAddressCountTotal = MyApplication.getInstance().getContactList().get(Constant.NEW_FRIENDS_USERNAME)
					.getUnreadMsgCount();
		return unreadAddressCountTotal;
	}

	/**
	 * 刷新申请与通知消息数
	 */
	public void updateUnreadAddressLable() {
		runOnUiThread(new Runnable() {
			public void run() {
				int count = getUnreadAddressCountTotal();
				if (count > 0) {
					Intent intent = new Intent();
					intent.setAction("com.hx.refresh");
					sendBroadcast(intent);
					// unreadAddressLable.setText(String.valueOf(count));
					// unreadAddressLable.setVisibility(View.VISIBLE);
				} else {
					// unreadAddressLable.setVisibility(View.INVISIBLE);
				}
			}
		});

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);

		if (!isConflict && !isCurrentAccountRemoved && HXSDKHelper.getInstance().isLogined()) {
			updateUnreadLabel();
			updateUnreadAddressLable();
			EMChatManager.getInstance().activityResumed();
		}

		// unregister this event listener when this activity enters the
		// background
		DemoHXSDKHelper sdkHelper = (DemoHXSDKHelper) DemoHXSDKHelper.getInstance();
		sdkHelper.pushActivity(this);

		// register the event listener when enter the foreground
		EMChatManager.getInstance().registerEventListener(
				this,
				new EMNotifierEvent.Event[] { EMNotifierEvent.Event.EventNewMessage, EMNotifierEvent.Event.EventOfflineMessage,
						EMNotifierEvent.Event.EventConversationListChanged });
	}

	@Override
	public void Control() {
		rb_me.setOnClickListener(this);
		rb_customization.setOnClickListener(this);
		rb_community.setOnClickListener(this);
		rb_store.setOnClickListener(this);
		rb_design_tab.setOnClickListener(this);
	}

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_main);
		SharedPreferences sp = getSharedPreferences("Login", Context.MODE_PRIVATE);
		islogin = sp.getBoolean("islogin", false);
		userid = sp.getInt("userid", 0);
		type = sp.getInt("type", -1);
		initFragment();
	}

	private boolean isstore = true;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rb_store:
			if (!islogin) {
				Intent intent = new Intent(MainActivity.this, Activity_Login.class);
				startActivity(intent);
				rb_customization.setChecked(true);
				return;
			}
			if (type == 2) {
				fragmentShowOrHide(fstore, fcustomization, fcommenity, fmec, false);
			} else {
				fragmentShowOrHide(fstore, fcustomization, fcommenity, fme, false);
			}
			isstore = false;
			// if (Fragment_Hot.getInstance() != null)
			// Fragment_Hot.getInstance().scrollTo00();
			break;
		case R.id.rb_community1:
			if (islogin) {
				if (type == 2) {
					fragmentShowOrHide(fcommenity, fstore, fcustomization, fmec, false);
				} else {
					fragmentShowOrHide(fcommenity, fstore, fcustomization, fme, false);
				}

			} else {
				Intent intent = new Intent(MainActivity.this, Activity_Login.class);
				startActivity(intent);
				if (isstore) {
					rb_customization.setChecked(true);
				} else {
					rb_store.setChecked(true);
				}

			}

			break;
		case R.id.rb_customization:
			isstore = true;
			if (type == 2) {
				fragmentShowOrHide(fcustomization, fstore, fcommenity, fmec, false);
			} else {
				fragmentShowOrHide(fcustomization, fstore, fcommenity, fme, false);
			}
			if (islogin) {

			} else {
				// Intent intent = new Intent(MainActivity.this,
				// Activity_Login.class);
				// startActivity(intent);
				// if (isstore) {
				// rb_store.setChecked(true);
				// } else {
				// rb_design_tab.setChecked(true);
				// }
			}
			break;
		case R.id.rb_me:
			if (islogin) {
				if (type == 2) {
					fragmentShowOrHide(fmec, fstore, fcustomization, fcommenity, false);
				} else {
					fragmentShowOrHide(fme, fstore, fcustomization, fcommenity, false);
				}

			} else {
				Intent intent = new Intent(MainActivity.this, Activity_Login.class);
				startActivity(intent);
				if (isstore) {
					rb_customization.setChecked(true);
				} else {
					rb_store.setChecked(true);
				}
			}
			break;
		case R.id.rb_design_tab:

			// if (type == 2) {
			// fragmentShowOrHide(fde, fmec, fstore, fcustomization, fcommenity,
			// false);
			// } else {
			// fragmentShowOrHide(fde, fme, fstore, fcustomization, fcommenity,
			// false);
			// }
			// isstore = false;
			// if (islogin) {
			//
			//
			// } else {
			// Intent intent = new Intent(MainActivity.this,
			// Activity_Login.class);
			// startActivity(intent);
			// }
			break;
		case R.id.im_newmflags:

			if (MyApplication.islogin) {
				// showSettingFaceDialog();
				Intent intent = new Intent(this, Activity_Send_Flags.class);
				startActivity(intent);
			} else {
				Intent intent = new Intent(this, Activity_Login.class);
				startActivity(intent);
			}
			break;
		default:
			break;
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

		super.onDestroy();

	}

	private void initFragment() {
		transaction = getSupportFragmentManager().beginTransaction();
		fstore = new Fragment_Message();
		fcustomization = new Fragment_MGC();
		fcommenity = new Fragment_Community();
		fde = new Fragment_Designer();
		if (type == 2) {
			fmec = new Fragment_Me_Custom();
		} else {
			fme = new Fragment_Me();
		}

		transaction.add(R.id.fragment_container, fstore);
		transaction.add(R.id.fragment_container, fcustomization);
		transaction.add(R.id.fragment_container, fcommenity);
		if (type == 2) {
			transaction.add(R.id.fragment_container, fmec);
		} else {
			transaction.add(R.id.fragment_container, fme);
		}
		if (type == 2) {
			fragmentShowOrHide(fcustomization, fstore, fcommenity, fmec, true);
		} else {
			fragmentShowOrHide(fcustomization, fstore, fcommenity, fme, true);
		}

	}

	private void fragmentShowOrHide(Fragment showFragment, Fragment hideFragment1, Fragment hideFragment2,
			Fragment hideFragment3, boolean isInit) {
		if (!isInit) {
			// transaction = getFragmentManager().beginTransaction();
			transaction = getSupportFragmentManager().beginTransaction();
		}
		transaction.show(showFragment);
		transaction.hide(hideFragment1);
		transaction.hide(hideFragment2);
		transaction.hide(hideFragment3);
		transaction.commit();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 按两次才推出程序
		if (keyCode == KeyEvent.KEYCODE_HOME) {
			Toast.makeText(this, "home", Toast.LENGTH_SHORT).show();
		}
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			if (firstClickTime > 0) {
				long secondClickTime = System.currentTimeMillis();
				long dTiem = secondClickTime - firstClickTime;
				if (dTiem < 2000) {
					toast.cancel();
					if (conflictBuilder != null) {
						conflictBuilder.create().dismiss();
						conflictBuilder = null;
					}

					if (connectionListener != null) {
						EMChatManager.getInstance().removeConnectionListener(connectionListener);
					}

					if (groupChangeListener != null) {
						EMGroupManager.getInstance().removeGroupChangeListener(groupChangeListener);
					}

					try {
						unregisterReceiver(internalDebugReceiver);
					} catch (Exception e) {
					}
					// toast.cancel();
					// try {
					// Thread.sleep(600);
					// } catch (InterruptedException e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// }
					MyApplication.isback = true;
					MyApplication.exit();
					finish();
					ImageLoader.getInstance().clearMemoryCache();
					ImageLoader.getInstance().clearDiscCache();
					System.gc();
					// try {
					// Thread.sleep(1000);
					// } catch (InterruptedException e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// }
					// Intent intent = new Intent(Intent.ACTION_MAIN);
					// intent.addCategory(Intent.CATEGORY_HOME);
					// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					// this.startActivity(intent);

				}
			}
			firstClickTime = System.currentTimeMillis();
			toast.show();
			// Toast.makeText(this, "再按一次推出程序", Toast.LENGTH_SHORT).show();
			return true;
		}
		return super.onKeyDown(keyCode, event);

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

	@Override
	protected void onStop() {
		EMChatManager.getInstance().unregisterEventListener(this);
		DemoHXSDKHelper sdkHelper = (DemoHXSDKHelper) DemoHXSDKHelper.getInstance();
		sdkHelper.popActivity(this);

		super.onStop();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean("isConflict", isConflict);
		outState.putBoolean(Constant.ACCOUNT_REMOVED, isCurrentAccountRemoved);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (getIntent().getBooleanExtra("conflict", false) && !isConflictDialogShow) {
			showConflictDialog();
		} else if (getIntent().getBooleanExtra(Constant.ACCOUNT_REMOVED, false) && !isAccountRemovedDialogShow) {
			showAccountRemovedDialog();
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.context_tab_contact, menu);
	}

	@Override
	public void onEvent(EMNotifierEvent event) {
		// TODO Auto-generated method stub
		switch (event.getEvent()) {
		case EventNewMessage: // 普通消息
		{
			EMMessage message = (EMMessage) event.getData();

			// 提示新消息
			HXSDKHelper.getInstance().getNotifier().onNewMsg(message);
			refreshUI();
			break;
		}

		case EventOfflineMessage: {

			refreshUI();
			break;
		}

		case EventConversationListChanged: {
			refreshUI();
			break;
		}

		default:
			break;
		}
	}

	private void refreshUI() {
		runOnUiThread(new Runnable() {
			public void run() {
				System.out.println("zuo=====有新消息");
				Intent intent_ = new Intent();
				intent_.setAction("com.hx.refresh");
				sendBroadcast(intent_);
				// 刷新bottom bar消息未读数
				updateUnreadLabel();
				if (currentTabIndex == 0) {
					// 当前页面如果为聊天历史页面，刷新此页面
					if (chatHistoryFragment != null) {
						Intent intent = new Intent();
						intent.setAction("com.hx.refresh");
						sendBroadcast(intent);
					}
					Intent intent = new Intent();
					intent.setAction("com.hx.refresh");
					sendBroadcast(intent);
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onWindowFocusChanged(boolean)
	 */
	@SuppressWarnings("static-access")
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		// Intent intent = new Intent();
		// intent.putExtra("hasFocus", hasFocus);
		// intent.setAction("com.femto.hasfocus");
		// sendBroadcast(intent);
		// new MyInterface().interfa.onWinds(hasFocus);

	}

	private String[] items = new String[] { "图库", "拍照" };

	private void showSettingFaceDialog() {

		new AlertDialog.Builder(MainActivity.this).setTitle("图片来源").setCancelable(true)
				.setItems(items, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:// Local Image
							MyApplication.pictype = 1;
							MyApplication.flags.clear();
							MyApplication.flags.add(new Flags("", null));
							Intent intent = new Intent();
							intent.putExtra("nub", 1);
							intent.setClass(MainActivity.this, ImgFileListActivity.class);
							startActivity(intent);
							break;
						case 1:// Take Picture
							photo();
							break;
						}
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();

	}

	// 拍照
	public void photo() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		IMAGE_FILE = getPhotoFileName();
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE)));
		startActivityForResult(openCameraIntent, TAKE_PICTURE);
	}

	private static final int TAKE_PICTURE = 0x000001;
	private String IMAGE_FILE;
	private String filepath;

	/**
	 * 用当前时间给取得的图片命名
	 * 
	 */
	public String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

		return dateFormat.format(date) + ".jpg";
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case TAKE_PICTURE:

			if (resultCode == RESULT_OK) {
				File tempFile = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE);
				IMAGE_FILE = getPath(MainActivity.this, Uri.fromFile(tempFile));
				filepath = IMAGE_FILE;
				Intent intent_ = new Intent(MainActivity.this, ActivityAddTags.class);
				intent_.putExtra("image_path", filepath);
				startActivity(intent_);
			}

			break;
		}
	}

	// 以下是关键，原本uri返回的是file:///...来着的，android4.4返回的是content:///...

	@TargetApi(Build.VERSION_CODES.KITKAT)
	@SuppressLint("NewApi")
	public static String getPath(final Context context, final Uri uri) {

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}

			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {
				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
						Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] { split[1] };

				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {
			// Return the remote address
			if (isGooglePhotosUri(uri))
				return uri.getLastPathSegment();

			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 * 
	 * @param context
	 *            The context.
	 * @param uri
	 *            The Uri to query.
	 * @param selection
	 *            (Optional) Filter used in the query.
	 * @param selectionArgs
	 *            (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };

		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}
}
