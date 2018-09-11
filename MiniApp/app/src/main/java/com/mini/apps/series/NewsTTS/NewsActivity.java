package com.mini.apps.series.NewsTTS;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.mini.apps.series.Adapter.ListAdapter;
import com.mini.apps.series.R;
import com.mini.apps.series.UtilLog;

import java.util.ArrayList;

public class NewsActivity extends AppCompatActivity{

    private View mConvertView;
    private AlertDialog.Builder mBuilder;
    private AlertDialog mNewsDialog;

    private Button mOKButton;
    private EditText mEditText;
    private RadioGroup mRadioGroup;
    private Spinner mSpinner;

    private String mSearchText;
    private int mSearchCount = 0;
    private String mSearchSort;

    private ListAdapter mListAdapter;
    private ListView mListView;
    private ArrayList<NewsItem> mList;
    private NewsItem mNewsItem = new NewsItem();

    private Handler mHandler;

    public class NewsItem{
        private String mNewsTitle;
        private String mNewsSub;
        private String mNewsLink;

        public NewsItem(){

        }

        public NewsItem(String  _title, String _sub) {
            this.mNewsTitle = _title;
            this.mNewsSub = _sub;
        }

        public String getNewsSub() {
            if(mNewsSub == null)
                mNewsSub = "";
            return mNewsSub;
        }

        public String getNewsTitle() {
            return mNewsTitle;
        }

        public void setNewsSub(String mNewsSub) {
            this.mNewsSub = mNewsSub;
        }

        public void setNewsTitle(String mNewsTitle) {
            this.mNewsTitle = mNewsTitle;
        }

        public String getNewsLink() {
            return mNewsLink;
        }

        public void setNewsLink(String mNewsLink) {
            this.mNewsLink = mNewsLink;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        UtilLog.v("onCreate()");

        initView();
        initHandler();
        initNewsSettingPopup();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNewsDialog.show();
                initPopupLayout();
            }
        });

    }

    private void initView(){
        LayoutInflater inflater = getLayoutInflater();
        mConvertView = inflater.inflate(R.layout.news_popup, null);

        mList = new ArrayList<NewsItem>();
        mListView = (ListView)findViewById(R.id.listView);
        mListAdapter = new ListAdapter(this, R.layout.list_item_main, mList);
        if(mListAdapter != null) {
            mListView.setAdapter(mListAdapter);
            mListAdapter.notifyDataSetChanged();
        }

        mListView.setOnItemClickListener(mItemClickListener);
    }

    private void initNewsSettingPopup(){
        UtilLog.v("openNewsSettingPopup()");

        mBuilder = new AlertDialog.Builder(this);
        mBuilder.setView(mConvertView);

        mBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        mNewsDialog = mBuilder.create();
    }

    private void initHandler() {
        if (mHandler == null) {
            mHandler = new Handler() {
                public void handleMessage(Message msg) {

                    switch (msg.what) {
                        case NewsSetting.COMPLETED_GET_NEWS: {
                            setNewsListView();
                        }
                        break;
                    }
                }
            };
        }
    }

    private void initPopupLayout(){
        UtilLog.v("initPopupLayout()");

        mOKButton =  (Button)mNewsDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        mOKButton.setOnClickListener(okButtonClickListener);
        mOKButton.setEnabled(false);

        mEditText = (EditText)mConvertView.findViewById(R.id.search_text);

        if(mEditText!=null) {
            mEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    mSearchText = s.toString();
                    if(mSearchCount!=0){
                        mOKButton.setEnabled(true);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

        mRadioGroup = (RadioGroup)mConvertView.findViewById(R.id.radio_group);
        if(mRadioGroup!=null)mRadioGroup.setOnCheckedChangeListener(radioGroupButtonChangeListener);

        mSpinner = (Spinner)mConvertView.findViewById(R.id.spinner_layout);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        mSearchSort = "sim";
                        break;
                    case 1:
                        mSearchSort = "date";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mEditText.setText("");
        mRadioGroup.clearCheck();
        mSpinner.setSelection(0);
        mOKButton.setEnabled(false);

        mNewsItem = new NewsItem();
        int count = mSearchCount-1;
        if(mList.size() == mSearchCount) {
            while (count >= 0) {
                mList.remove(count);
                count--;
            }
        }
        mListAdapter.notifyDataSetChanged();
    }

    Button.OnClickListener okButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String sort;
            if(mSearchSort.contentEquals("sim")) sort = "유사도 순";
            else sort = "날짜 순";

            setToast("검색할 내용은 "+mSearchText+
                    "\n검색 개수는 "+mSearchCount +"개"+
                    "\n검색 정렬은 "+ sort +" 입니다.");
            UtilLog.d("");
            NewsSetting.setSettingInfo(mHandler, mSearchText, mSearchCount, mSearchSort);

            if(mNewsDialog.isShowing()) {
                // init popup layout
                mEditText.setText("");
                mRadioGroup.clearCheck();
                mSpinner.setSelection(0);
                mNewsDialog.dismiss();
            }

        }
    };

    RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId){
                case R.id.radio_5:
                    mSearchCount = 5;
                    if(mSearchText!=null)mOKButton.setEnabled(true);
                    break;
                case R.id.radio_10:
                    mSearchCount = 10;
                    if(mSearchText!=null)mOKButton.setEnabled(true);
                    break;
                case R.id.radio_15:
                    mSearchCount = 15;
                    if(mSearchText!=null)mOKButton.setEnabled(true);
                    break;
                case R.id.radio_20:
                    mSearchCount = 20;
                    if(mSearchText!=null)mOKButton.setEnabled(true);
                    break;
            }
        }
    };

    private void setNewsListView(){
        UtilLog.e("setNewsListView()");
        String[] title = NewsSetting.getResultTitle();
        String[] sub = NewsSetting.getResultDescription();
        String[] link = NewsSetting.getResultLink();

        UtilLog.d("setNewsListView() : "+title.length);
        for(int i = 0; i < mSearchCount; i++){
            UtilLog.d("setNewsListView() mSearchCount: "+mSearchCount);
            UtilLog.d("setNewsListView() : "+title[i] +" , "+ sub[i]);
            mNewsItem = new NewsItem(title[i], sub[i]);
            mNewsItem.setNewsLink(link[i]);
            mList.add(mNewsItem);
            mListAdapter.notifyDataSetChanged();
            mListView.setAdapter(mListAdapter);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mNewsDialog != null){
            mNewsDialog.dismiss();
            mNewsDialog = null;
        }
        if(mListAdapter != null){
            mListAdapter = null;
        }
        if(mHandler != null){
            mHandler.removeMessages(NewsSetting.COMPLETED_GET_NEWS);
        }
    }

    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String link = mListAdapter.getLink(position).toString();
            UtilLog.e("link: "+link);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(link));
            startActivity(intent);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // automatically handle clicks on the Home/Up button, so long
        //        //        // Handle action bar item clicks here. The action bar will
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setToast(String msg){
        Toast.makeText(this,msg, Toast.LENGTH_SHORT).show();
    }

}
