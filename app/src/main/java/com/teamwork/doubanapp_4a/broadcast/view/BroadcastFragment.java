package com.teamwork.doubanapp_4a.broadcast.view;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.google.gson.Gson;
import com.teamwork.doubanapp_4a.R;
import com.teamwork.doubanapp_4a.broadcast.adapter.BroadcastListAdapter;
import com.teamwork.doubanapp_4a.broadcast.model.BroadcastContent;
import com.teamwork.doubanapp_4a.broadcast.model.BroadcastsBean;
import com.teamwork.doubanapp_4a.broadcast.utils.FileUtil;
import com.teamwork.doubanapp_4a.broadcast.utils.dbutils.DBHelper;
import com.teamwork.doubanapp_4a.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 广播界面
 */
public class BroadcastFragment extends Fragment implements View.OnClickListener {
    Context mContext;
    Toolbar toolbar;
    ImageView ivSearchUser, ivChat, ivCapturePhotos;
    LinearLayout llSendBroadcast;
    Button btnFindMore;
    RecyclerView recyclerView;
    SwipeRefreshLayout swiperefresh;
    LinearLayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    ScrollView scrollView;

    int lastVisibleItem;

    public BroadcastFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_broadcast, container, false);
        mContext = getContext();
        initViews(view);
        bindListener();
        initDB();

        toolbar.setTitle("");
        //Fragment中设置ToolBar
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        //Fragment中设置ToolBar Menu
        setHasOptionsMenu(true);

        initRecyclerView();
        return view;
    }

    private void initDB() {
        //实例化我们的DBHelper
        DBHelper dbHelper = new DBHelper(mContext);
        //调用了这个方法后，DBHelper中的onCreate才会执行
        dbHelper.getReadableDatabase();
    }


    private void initViews(View view) {
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ivSearchUser = (ImageView) view.findViewById(R.id.iv_search_user);
        ivChat = (ImageView) view.findViewById(R.id.iv_chat);
        ivCapturePhotos = (ImageView) view.findViewById(R.id.iv_capture_photos);
        llSendBroadcast = (LinearLayout) view.findViewById(R.id.ll_send_broadcast);
        btnFindMore = (Button) view.findViewById(R.id.btn_find_more);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        swiperefresh = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        scrollView = (ScrollView) view.findViewById(R.id.scrollview);


    }

    private void bindListener() {
        ivSearchUser.setOnClickListener(this);
        ivChat.setOnClickListener(this);
        ivCapturePhotos.setOnClickListener(this);
        llSendBroadcast.setOnClickListener(this);
        btnFindMore.setOnClickListener(this);
        recyclerView.setOnClickListener(this);
    }

    private void initRecyclerView() {
        BroadcastsBean broadcastsBean= new Gson().fromJson(FileUtil.readAssertResource(getActivity(), "broadcast.txt"), BroadcastsBean.class);
        List<BroadcastsBean.ItemsBean> mDatas = new ArrayList<>();
        mDatas = broadcastsBean.getItems();
        layoutManager = new LinearLayoutManager(mContext);

        //设置为垂直布局，这也是默认的
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        //设置布局管理器
        recyclerView.setLayoutManager(layoutManager);
        adapter = new BroadcastListAdapter(mContext, mDatas);
        recyclerView.setAdapter(adapter);

        //下拉刷新
        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ToastUtil.showShort(mContext, "下拉刷新");
                swiperefresh.setRefreshing(false);
            }
        });

        //RecyclerView滑动监听  上拉加载
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.d("onScrollStateChanged", lastVisibleItem + "");
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == adapter.getItemCount()) {
//                    ToastUtil.showShort(mContext, "上拉加载");
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d("onScrolled", lastVisibleItem + "");
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            }
        });

        scrollView.setFocusable(false);

        swiperefresh.setFocusable(true);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                Log.d("onScrollChanged", layoutManager.findLastVisibleItemPosition() + "");
                swiperefresh.setEnabled(scrollView.getScrollY() == 0);
            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_search_user:
                ToastUtil.showShort(mContext, "查找用户");
                break;
            case R.id.iv_chat:
                ToastUtil.showShort(mContext, "聊天窗口");
                break;
            case R.id.ll_send_broadcast:
                ToastUtil.showShort(mContext, "发送广播");
                break;
            case R.id.iv_capture_photos:
                ToastUtil.showShort(mContext, "添加照片");
                break;
            case R.id.btn_find_more:
                ToastUtil.showShort(mContext, "发现更多");
                break;
        }

    }
}
