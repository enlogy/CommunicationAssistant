package com.example.enlogty.communicationassistant.activity;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.enlogty.communicationassistant.R;
import com.example.enlogty.communicationassistant.adapter.ContactAdapter;
import com.example.enlogty.communicationassistant.adapter.ContactItemDecoration;
import com.example.enlogty.communicationassistant.adapter.StickyDecoration;
import com.example.enlogty.communicationassistant.base.BaseActivity;
import com.example.enlogty.communicationassistant.bean.Contact;
import com.example.enlogty.communicationassistant.customview.CircleTextView;
import com.example.enlogty.communicationassistant.customview.MySlideView;
import com.example.enlogty.communicationassistant.presenter.ContactsPresenter;
import com.example.enlogty.communicationassistant.view.IContacts;
import com.github.promeg.pinyinhelper.Pinyin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by enlogty on 2017/9/5.
 */

public class ContactsActivity extends BaseActivity<IContacts,ContactsPresenter> implements IContacts ,MySlideView.onTouchListener {
    private RecyclerView rv;
    public static List<Contact> contactList;
    private Set<String> firstPinYin = new LinkedHashSet<>();
    public static List<String> pinyinList = new ArrayList<>();
    private CircleTextView circleTxt;
    private MySlideView mySlideView;
    private LinearLayoutManager manager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window=getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //获取样式中的属性值
            TypedValue typedValue = new TypedValue();
            this.getTheme().resolveAttribute(android.R.attr.colorPrimary, typedValue, true);
            int[] attribute = new int[] { android.R.attr.colorPrimary };
            TypedArray array = this.obtainStyledAttributes(typedValue.resourceId, attribute);
            int color = array.getColor(0, Color.TRANSPARENT);
            array.recycle();
            window.setStatusBarColor(color);
        }
        setContentView(R.layout.activity_contacts);
        mPresenter.fetch();
    }

    @Override
    public ContactsPresenter createPresenter() {
        return new ContactsPresenter();
    }

    @Override
    public void showContactData(List<Contact> contacts) {
        contactList = contacts;
        firstPinYin.clear();
        pinyinList.clear();

        for (Contact contact : contactList){
            contact.setPingyin(transformPinYin(contact.getName()));
            firstPinYin.add(contact.getFirstpingyin());
        }
        for (String str : firstPinYin){
            pinyinList .add(str);
        }
        Collections.sort(pinyinList);
        Collections.sort(contactList,new PingYinComparator());
        rv = (RecyclerView) findViewById(R.id.rv_contact);
        manager = new LinearLayoutManager(this);
        rv.setLayoutManager(new LinearLayoutManager(this));
        ContactAdapter adapter = new ContactAdapter(contactList,this);
        rv.setAdapter(adapter);
        rv.addItemDecoration(new StickyDecoration(this,contactList));
        adapter.setOnItemClickListener(new ContactAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(ContactsActivity.this,ContactInfoActivity.class);
                intent.putExtra("name",contactList.get(position).getName());
                intent.putExtra("number",contactList.get(position).getNumber());
                startActivity(intent);
            }
        });
        mySlideView = (MySlideView) findViewById(R.id.my_slide_view);
        circleTxt = (CircleTextView) findViewById(R.id.my_circle_view);
        mySlideView.setListener(this);
    }
    public String transformPinYin(String character) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < character.length(); i++) {
            buffer.append(Pinyin.toPinyin(character.charAt(i)));
        }
        return buffer.toString();
    }

    @Override
    public void showTextView(String textView, boolean dismiss) {
        if (dismiss) {
            circleTxt.setVisibility(View.GONE);
        } else {
            circleTxt.setVisibility(View.VISIBLE);
            circleTxt.setText(textView);
        }

        int selectPosition = 0;
        for (int i = 0; i < contactList.size(); i++) {
            if (contactList.get(i).getFirstpingyin().equals(textView)) {
                selectPosition = i;
                break;
            }
        }
        scrollPosition(selectPosition);
    }

    private void scrollPosition(int index) {
        int firstPosition = manager.findFirstVisibleItemPosition();
        int lastPosition = manager.findLastVisibleItemPosition();
        if (index <= firstPosition) {
            rv.scrollToPosition(index);
        } else if (index <= lastPosition) {
            int top = rv.getChildAt(index - firstPosition).getTop();
            rv.scrollBy(0, top);
        } else {
            rv.scrollToPosition(index);
        }
    }
    class PingYinComparator implements Comparator<Contact>{

        @Override
        public int compare(Contact o1, Contact o2) {
            return o1.getPingyin().compareTo(o2.getPingyin());
        }
    }
}
