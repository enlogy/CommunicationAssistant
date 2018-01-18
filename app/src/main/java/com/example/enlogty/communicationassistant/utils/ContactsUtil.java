package com.example.enlogty.communicationassistant.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.enlogty.communicationassistant.bean.Contact;
import com.example.enlogty.communicationassistant.bean.SyncTask;
import com.example.enlogty.communicationassistant.rx.RxBus;

public class ContactsUtil {
	private Lock mLock = new ReentrantLock();
	private int writeSuccessCount = 0;
	private static List<Contact> contactList = new ArrayList<Contact>();
    private static int count;
    public List<Contact> getPersonList(){
    	if(contactList == null){
			contactList = new ArrayList<Contact>();
    		return contactList;
    	}else{
    		return contactList;
    	}
    }
    public static int getContactsCount(ContentResolver contentResolver){
    	Cursor cursor = contentResolver.query(Uri.parse("content://com.android.contacts/raw_contacts"), null, null, null, null);
    	count = cursor.getCount();
    	if(cursor != null)
    		cursor.close();
    	return count;
    }
    public List<Contact> getContactsData(ContentResolver contentResolver){
		contactList.clear();
		Contact contact = null;
    	String contact_id , name , number , email;
    	Cursor cursor;
        cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
		if(cursor!=null&&cursor.moveToFirst()){
		 do{
			 contact = new Contact();
			 name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			 number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			 contact.setName(name);
			 contact.setNumber(number);
			 boolean equals = false;
			 if (contactList!=null){
				 for (Contact c : contactList){
					 if (c.getNumber().equals(number)){
						 equals = true;
						 break;
					 }
				 }
			 }
			 if (!equals){
				 contactList.add(contact);
			 }
		}while(cursor.moveToNext());
		}

    	if(cursor != null)
    	{
    		cursor.close();
    	}

		return contactList;
    }

	public void writeContactToLocalDB(Context context,String name,String phoneNumber,List<Contact> localData,@Nullable WriteCallback callback){
		for (Contact contact : localData){
			if (phoneNumber.equals(contact.getNumber())){
				if (callback!=null){
					writeSuccessCount++;
					callback.success(writeSuccessCount);
				}
				return;
			}
		}
		// 添加联系人手机号
		ContentValues values = new ContentValues();
		Uri rawContactUri = context.getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);
		long rawContactId = ContentUris.parseId(rawContactUri);
		values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
		values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
		//values.put(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, "AAAAAAAA");
		values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber);
		values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM);
		values.put(ContactsContract.CommonDataKinds.Phone.LABEL, "free directory assistance");
		Uri dataUri = context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
		// 向联系人URI添加联系人名字  
		values.clear();
		values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
		values.put(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
		values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name);
		context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
		//Toast.makeText(context,"联系人数据添加成功",Toast.LENGTH_SHORT).show();
		mLock.lock();
			SyncTask.getInstance().ContactDownloadedCount++;
		mLock.unlock();
		RxBus.getInstance().post(SyncTask.getInstance());
		if (callback!=null){
			writeSuccessCount++;
			callback.success(writeSuccessCount);
		}
	}
	public interface WriteCallback{
		void success(int count);
	}
}
