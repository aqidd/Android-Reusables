package com.flipbox.pazpo.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flipbox.pazpo.R;
import com.flipbox.pazpo.adapters.ContentRecyclerViewAdapter;
import com.flipbox.pazpo.consts.S;
import com.flipbox.pazpo.views.AnimatedGifImageView;
import com.koushikdutta.ion.Response;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ContentFragment extends Fragment
{
    private static final String TAG = ContentFragment.class.getSimpleName();
    private List<Content> lContent;

    @Bind(R.id.ll_error)
    LinearLayout llError;
    @Bind(R.id.tv_error)
    TextView tvError;
    @Bind(R.id.btn_retry)
    TextView btnRetry;
    @Bind(R.id.tv_empty_data)
    TextView tvEmptyData;
    @Bind(R.id.agiv_loader)
    AnimatedGifImageView agivLoader;
    @Bind(R.id.rv_content)
    RecyclerView rvContent;

    public ContentFragment()
    {
        setArguments(new Bundle());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_content, container, false);
        ButterKnife.bind(this, v);

        startLoading();
        initUI();
        initEvent();
        initData(savedInstanceState);

        return v;
    }

    public void initUI()
    {
        rvContent.setAdapter(new ContentRecyclerViewAdapter(lContent, ContentFragment.this));
        rvContent.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    public void initEvent()
    {
        btnRetry.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startLoading();
                initData(null);
            }
        });
    }

    public void initData(@Nullable Bundle savedInstanceState)
    {
        lContent = new ArrayList<>();
    }

    // TODO: Change this method to your API return handler
    public void onCompleted(Exception e, Response<String> response)
    {
        finishLoading();
        if (response == null || e != null)
        {
            onError(S.try_again);
        }
        else if (response.getResult() == null)
        {
            onCancelled();
        }
        else
        {
            onSuccess(response);
        }
    }

    public void startLoading()
    {
        agivLoader.setAnimatedGif(R.raw.load_cat, AnimatedGifImageView.TYPE.AS_IS);
        agivLoader.setVisibility(View.VISIBLE);

        rvContent.setVisibility(View.GONE);
        llError.setVisibility(View.GONE);
        tvEmptyData.setVisibility(View.GONE);
    }

    public void finishLoading()
    {
        agivLoader.setVisibility(View.GONE);
    }

    public void onSuccess(Response<String> response)
    {
        if (response.getResult().isEmpty())
        {
            tvEmptyData.setVisibility(View.VISIBLE);
            tvEmptyData.setText(S.no_data_available);
        }
        else
        {
            //success
            rvContent.setVisibility(View.VISIBLE);
            lContent = // TODO: put your valid API return data here
        }
    }

    public void onError(String message)
    {
        llError.setVisibility(View.VISIBLE);
        tvError.setText(message);
    }

    public void onCancelled()
    {

    }
}
