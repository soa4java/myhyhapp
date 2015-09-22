package com.hyh.www.session;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.gezitech.basic.GezitechActivity;
import com.gezitech.widget.MyListView;
import com.hyh.www.R;

//添加通讯录好友
public class ContactsAddBuddyPhoneActivity extends GezitechActivity implements OnClickListener {
	private ContactsAddBuddyPhoneActivity _this = this;
	private Button bt_my_post;
	private Button bt_home_msg;
	private TextView tv_title;
	int page = 1;
	int pageSize = 20;
	private List<String[]> data;
	private List<View> contactsItems = new ArrayList<View>();
	private MyListView listView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts_add_buddy_phone);
		
		_init();
	}
	private void _init() {
		bt_my_post = (Button) _this.findViewById(R.id.bt_my_post);
		bt_my_post.setVisibility(View.GONE);

		bt_home_msg = (Button) _this.findViewById(R.id.bt_home_msg);
		bt_home_msg.setBackgroundResource(R.drawable.button_common_back);
		bt_home_msg.setOnClickListener(this);

	    tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText( "分享通讯录好友" );
		data = QueryContact();
		for (int i=0; i<data.size(); i++)
		{
			View view = LayoutInflater.from( this ).inflate(R.layout.list_phone_item, null);
			TextView tv_phone_name = (TextView) view.findViewById( R.id.tv_phone_name );
			tv_phone_name.setText(data.get(i)[0]);
			
			LinearLayout linearLayout = (LinearLayout) ((LinearLayout)view).getChildAt(0);
			final int index = i;
			linearLayout.setOnClickListener( new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String phone = data.get( index )[1];
					
					Uri uri = Uri.parse("smsto:" + phone);           
					Intent it = new Intent(Intent.ACTION_SENDTO, uri);            
					it.putExtra("sms_body", String.format( getString( R.string.share_content),user.inviteCode ) );            
					startActivity(it); 
				}
			});
			
			contactsItems.add(view);
		}

		listView = (MyListView) findViewById( R.id.list_view );
		listView.footerShowState( 2 );
		listView.setAdapter(new ListViewBaseAdapter());
	}

	private class ListViewBaseAdapter extends BaseAdapter
	{
		List<View> datas;
		public ListViewBaseAdapter()
		{
			datas = new ArrayList<View>();
		}
	
		@Override
		public int getCount()
		{
			return data.size();
		}
	
		@Override
		public Object getItem(int position)
		{
			return datas.get(position);
		}
	
		@Override
		public long getItemId(int position)
		{
			return 0;
		}
	
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			return contactsItems.get(position);
		}
	}

	//查询电话联系人
	private List<String[]> QueryContact() 
	{
		List<String[]> contactsList = new ArrayList<String[]>();
		
		Uri uri = Uri.parse("content://com.android.contacts/contacts");
		ContentResolver resolver = getContentResolver();
		Cursor cursor = resolver.query(uri, new String[] { "_id" }, null, null, null);
		while (cursor.moveToNext())
		{
			String[] contacts = new String[2];
			
			int contractID = cursor.getInt(0);
			
			uri = Uri.parse("content://com.android.contacts/contacts/" + contractID + "/data");
			Cursor cursor1 = resolver.query(uri, new String[] { "mimetype", "data1", "data2" }, null, null, null);
			
			while (cursor1.moveToNext())
			{
				String data1 = cursor1.getString(cursor1.getColumnIndex("data1"));
				String mimeType = cursor1.getString(cursor1.getColumnIndex("mimetype"));
				if ("vnd.android.cursor.item/name".equals(mimeType))
				{ // 是姓名
					contacts[0] =  data1;
				}  else if ("vnd.android.cursor.item/phone_v2".equals(mimeType))
				{ // 手机
					if (contacts[0] == null)
					{
						contacts[0] = data1;
					}
					contacts[1] = data1;
				}
			}
			contactsList.add(contacts);
			cursor1.close();
		}
		cursor.close();
		return contactsList;
	}
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.bt_home_msg:
			finish();
			break;

		default:
			break;
		}
	}
}
