package com.gezitech.service.xmpp;

public class Constant {
	/**
	 * ���е�action�ļ���ı���Ҫ��"ACTION_"��ͷ
	 * 
	 */

	/**
	 * �������ɾ���ACTION��KEY
	 */
	public static final String ROSTER_DELETED = "roster.deleted";
	public static final String ROSTER_DELETED_KEY = "roster.deleted.key";

	/**
	 * ������и��µ�ACTION��KEY
	 */
	public static final String ROSTER_UPDATED = "roster.updated";
	public static final String ROSTER_UPDATED_KEY = "roster.updated.key";

	/**
	 * ����������ӵ�ACTION��KEY
	 */
	public static final String ROSTER_ADDED = "roster.added";
	public static final String ROSTER_ADDED_KEY = "roster.added.key";

	/**
	 * ������г�Ա״̬�иı��ACTION��KEY
	 */
	public static final String ROSTER_PRESENCE_CHANGED = "roster.presence.changed";
	public static final String ROSTER_PRESENCE_CHANGED_KEY = "roster.presence.changed.key";

	/**
	 * �յ�������������
	 */
	public static final String ROSTER_SUBSCRIPTION = "roster.subscribe";
	public static final String ROSTER_SUB_FROM = "roster.subscribe.from";
	public static final String NOTICE_ID = "notice.id";
	//新消息广播
	public static final String NEW_MESSAGE_ACTION = "roster.newmessage";
	//更新界面的广播
	public static final String UPDATE_MESSAGE_ACTION = "roster.updatemessage";
	//聊天界面发消息通知界面的广播
	public static final String SEND_UPDATE_MESSAGE_ACTION = "roster.sendupdatemessage";
	//私聊联系人列表的广播
	public static final String UPDATE_CONTACTS_ACTION = "roster.update_chatlist_action";
	//喜欢和评论的广播
	public static final String LIKE_COMMENT_ACTION = "roster.like_comment_action";
	//更新聊天列表的广播
	public static final String UPDATE_CHAT_ACTION = "roster.update_chat_action";
	//好友请求条数
	public static final String FRIEND_REQUEST_COUNT = "roster.friend_request_count";
	//删除好友的请求
	public static final String DELETE_FRIEND_REQUEST = "roster.delete_friend_request";
	//系统消息广播
	public static final String SYSTEM_REQUEST = "roster.system_request";
	//系统消息广播
	public static final String NEAR_NEW_MSG_HINT = "roster.near.new.msg.hint";
	//微信支付的回掉
	public static final String WE_CHAT_PAY_CALLBACK = "we.chat.pay.callback";
	/**
	 * �ҵ���Ϣ
	 */
	public static final String MY_NEWS = "my.news";
	public static final String MY_NEWS_DATE = "my.news.date";

	/**
	 * ������������
	 */
	public static final String LOGIN_SET = "eim_login_set";// ��¼����
	public static final String USERNAME = "username";// �˻�
	public static final String PASSWORD = "password";// ����
	public static final String XMPP_HOST = "xmpp_host";// ��ַ
	public static final String XMPP_PORT = "xmpp_port";// �˿�
	public static final String XMPP_SEIVICE_NAME = "xmpp_service_name";// ������
	public static final String IS_AUTOLOGIN = "isAutoLogin";// �Ƿ��Զ���¼
	public static final String IS_NOVISIBLE = "isNovisible";// �Ƿ�����
	public static final String IS_REMEMBER = "isRemember";// �Ƿ��ס�˻�����
	public static final String IS_FIRSTSTART = "isFirstStart";// �Ƿ��״�����
	/**
	 * ��¼��ʾ
	 */
	public static final int LOGIN_SECCESS = 0;// �ɹ�
	public static final int HAS_NEW_VERSION = 1;// �����°汾
	public static final int IS_NEW_VERSION = 2;// ��ǰ�汾Ϊ����
	public static final int LOGIN_ERROR_ACCOUNT_PASS = 3;// �˺Ż����������
	public static final int SERVER_UNAVAILABLE = 4;// �޷����ӵ�������
	public static final int LOGIN_ERROR = 5;// ����ʧ��

	public static final String XMPP_CONNECTION_CLOSED = "xmpp_connection_closed";// �����ж�

	public static final String LOGIN = "login"; // ��¼
	public static final String RELOGIN = "relogin"; // ���µ�¼

	/**
	 * �����б� ����
	 */
	public static final String ALL_FRIEND = "���к���";// ���к���
	public static final String NO_GROUP_FRIEND = "δ�������";// ���к���
	/**
	 * ϵͳ��Ϣ
	 */
	public static final String ACTION_SYS_MSG = "action_sys_msg";// ��Ϣ���͹ؼ���
	public static final String MSG_TYPE = "broadcast";// ��Ϣ���͹ؼ���
	public static final String SYS_MSG = "sysMsg";// ϵͳ��Ϣ�ؼ���
	public static final String SYS_MSG_DIS = "ϵͳ��Ϣ";// ϵͳ��Ϣ
	public static final String ADD_FRIEND_QEQUEST = "��������";// ϵͳ��Ϣ�ؼ���
	/**
	 * ����ĳ���������ص�״ֵ̬
	 */
	public static final int SUCCESS = 0;// ����
	public static final int FAIL = 1;// ������
	public static final int UNKNOWERROR = 2;// ����Ī��Ĵ���.
	public static final int NETWORKERROR = 3;// �������
	/***
	 * ��ҵͨѶ¼����û������û���ȥ������Ա�е���������Ƿ������֯
	 */
	public static final int containsZz = 0;
	/***
	 * �������������ϵ���б�xml��ҳ����
	 */
	public static final String currentpage = "1";// ��ǰ�ڼ�ҳ
	public static final String pagesize = "1000";// ��ǰҳ������

	/***
	 * ��������xml��������
	 */
	public static final String add = "00";// ����
	public static final String rename = "01";// ����
	public static final String remove = "02";// ����

	/**
	 * ������
	 */
	/**
	 * ������״̬acttion
	 * 
	 */
	public static final String ACTION_RECONNECT_STATE = "action_reconnect_state";
	/**
	 * ����������״̬�Ĺػ��ӣ��ķŵ�intent�Ĺؼ���
	 */
	public static final String RECONNECT_STATE = "reconnect_state";
	/**
	 * ���������ӣ�
	 */
	public static final boolean RECONNECT_STATE_SUCCESS = true;
	public static final boolean RECONNECT_STATE_FAIL = false;
	/**
	 * �Ƿ����ߵ�SharedPreferences���
	 */
	public static final String PREFENCE_USER_STATE = "prefence_user_state";
	public static final String IS_ONLINE = "is_online";
	/**
	 * ��ȷ������
	 */
	public static final String MS_FORMART = "yyyy-MM-dd HH:mm:ss SSS";
	
	public static final String HYH_URL = "http://demo1.star-lai.cn/index.php/";

	//聊天的参数配置
	public static final String CHAT_CONTENT = "chatcontent"; //聊天返回内容的key
	
	
}
