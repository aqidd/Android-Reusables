package com.flipbox.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flipbox.R;
import com.flipbox.adapters.MainDataRecyclerViewAdapter;
import com.flipbox.entities.MainData;
import com.flipbox.utils.OAuth10.OAuthHeaderBuilder;
import com.flipbox.views.AnimatedGifImageView;
import com.flipbox.views.SimpleDividerItemDecoration;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.HeadersCallback;
import com.koushikdutta.ion.HeadersResponse;
import com.koushikdutta.ion.Ion;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainFragment extends Fragment {
    private static final String TAG = MainFragment.class.getSimpleName();
    // TODO: adjust below final String based on your API
    private static final String SIGN_METHOD = "HMAC-SHA1";
    private static final String REQ_METHOD_GET = "GET";
    private static final String REQ_METHOD_POST = "POST";
    private static final String BASE_URL = "http://flipbox.co.id:1234/";
    private static final String MAIN_ENGINE = "MaisonWCFAuth.mainengineservice.svc/";
    private static final String SERVICE = "GetMainData";
    private static final String pParam1 = "pParam1";
    private static final String pParamN = "pParamN";
    private static final String PARAM = "?" + pParam1 + "&" + pParamN;
    private static final String URL = BASE_URL + MAIN_ENGINE + SERVICE +PARAM;
    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
    private static final String CONSUMER_SECRET = "w3AS6yhdkj85pth";
    private static final String CONSUMER_KEY = "56bde8a19ef89";

    private static final OAuthHeaderBuilder oAuthHeaderBuilder = new OAuthHeaderBuilder(SIGN_METHOD,
        REQ_METHOD_GET, URL, HMAC_SHA1_ALGORITHM, CONSUMER_KEY, CONSUMER_SECRET);
    //
    @Bind(R.id.loader)
    RelativeLayout loader;
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
    @Bind(R.id.rv_data)
    RecyclerView rvData;
    private ArrayList<MainData> lMainData = new ArrayList<>();
    private Context ctx;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, v);

        initUI();
        initData(savedInstanceState);
        return v;
    }

    private void initUI() {
        ctx = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? getContext() : getActivity().getBaseContext();
        // loader content
        agivLoader.setAnimatedGif(R.raw.load_circular, AnimatedGifImageView.TYPE.AS_IS);
        agivLoader.setVisibility(View.VISIBLE);
        llError.setVisibility(View.GONE);
        tvEmptyData.setVisibility(View.GONE);
        //
        rvData.setVisibility(View.GONE);
        
        rvData.setAdapter(new MainDataRecyclerViewAdapter(lMainData));
        rvData.setLayoutManager(new LinearLayoutManager(ctx));
        rvData.addItemDecoration(new SimpleDividerItemDecoration(ctx));
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("MainData", lMainData);
    }

    private void initData(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            loader.setVisibility(View.GONE);
            rvData.setVisibility(View.VISIBLE);
            //
            lMainData = (ArrayList<MainData>) savedInstanceState.getSerializable("MainData");
            rvData.setAdapter(new MainDataRecyclerViewAdapter(lMainData));
        } else {
            if (lMainData != null && lMainData.size() > 0) {
                //returning from backstack, data is fine, do nothing
                Log.e("backstack", lMainData.size() + "");
            } else {
                //newly created, retrieve data
                getData();
            }
        }
    }

    private void getData() {
        try {
            launchIon(URL, oAuthHeaderBuilder.generateHeader("", ""));
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void launchIon(final String url, String header) {
        Ion.with(ctx)
                .load(url)
                .setHeader("Authorization", header)
                .setLogging("IonLog", Log.DEBUG)
                .onHeaders(new HeadersCallback() {
                    @Override
                    public void onHeaders(HeadersResponse headers) {
                    }
                })
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e != null) {
                            Log.e("error", e.toString());
                            llError.setVisibility(View.VISIBLE);
                            tvError.setText(e.getMessage());
                            btnRetry.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    llError.setVisibility(View.GONE);
                                    // call Main Fragment
                                    FragmentManager fm = getFragmentManager();
                                    FragmentTransaction ft = fm.beginTransaction();
                                    ft.replace(R.id.rl_data, new MainFragment());
                                    ft.commit();
                                }
                            });
                            //
                            tvEmptyData.setVisibility(View.GONE);
                            agivLoader.setVisibility(View.GONE);
                        } else if (result == null || result.isEmpty() || result.equals("[]")) {
                            tvEmptyData.setVisibility(View.VISIBLE);
                            //
                            llError.setVisibility(View.GONE);
                            agivLoader.setVisibility(View.GONE);
                        } else {
                            loader.setVisibility(View.GONE);
                            rvData.setVisibility(View.VISIBLE);
                            // TODO set result to your entity
                            MainData.setData(lMainData, result);
                            rvData.getAdapter().notifyDataSetChanged();
                        }
                    }
                });
    }
}
