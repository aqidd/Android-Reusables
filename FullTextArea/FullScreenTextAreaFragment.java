package com.flipbox.fulltext.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flipbox.fulltext.R;
import com.flipbox.fulltext.conts.S;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FullScreenTextAreaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class FullScreenTextAreaFragment extends BaseFragment
{

    private static final int CHARACTER_LIMIT = 255;
    private OnFragmentInteractionListener mListener;
    private LinearLayout mContainer;
    private TextInputEditText editText;
    private LinearLayout characterCounter;
    private TextView characterLabel;
    private TextView characterCount;

    public FullScreenTextAreaFragment()
    {
        // Required empty public constructor

        // Just to be an empty Bundle. You can use this later with getArguments().set...
        setArguments(new Bundle());
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener)
        {
            mListener = (OnFragmentInteractionListener) context;
        }
        else
        {
            throw new RuntimeException(context.toString()
                                       + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        String hint = getArguments().getString(S.hint);
        String note = getArguments().getString(S.note);

        container.setBackgroundColor(getResources().getColor(R.color.white_two));

        mContainer = new LinearLayout(getActivity());
        mContainer.setOrientation(LinearLayout.VERTICAL);
        mContainer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                 ViewGroup.LayoutParams.MATCH_PARENT, 10f));
        mContainer.setGravity(Gravity.TOP);

        characterLabel = new TextView(getActivity());
        characterLabel.setId(View.generateViewId());
        characterLabel.setText("Sisa Karakter : ");

        characterCount = new TextView(getActivity());
        characterCount.setId(View.generateViewId());
        characterCount.setText("255");

        characterCounter = new LinearLayout(getActivity());
        characterCounter.setId(View.generateViewId());
        characterCounter.setOrientation(LinearLayout.HORIZONTAL);
        characterCounter.setGravity(Gravity.RIGHT);
        characterCounter.setPadding(50, 30, 50, 30);
        characterCounter.setBackgroundColor(getResources().getColor(R.color.white_three));
        characterCounter.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        characterCounter.addView(characterLabel);
        characterCounter.addView(characterCount);

        editText = new TextInputEditText(getActivity());
        editText.setGravity(Gravity.TOP | Gravity.LEFT);
        editText.setId(View.generateViewId());
        editText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 9f));
        editText.setHint(hint);
        editText.setMinLines(10);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(CHARACTER_LIMIT)});

        if (note != null)
        {
            editText.setText(note);
        }
        editText.setPadding(50, 30, 50, 30);
        editText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                characterCount.setText(String.valueOf(CHARACTER_LIMIT - s.length()));
            }

            @Override
            public void afterTextChanged(Editable s)
            {
            }
        });
        editText.setBackgroundColor(Color.WHITE);
        mContainer.addView(editText);
        mContainer.addView(characterCounter);

        setHasOptionsMenu(true);

        //auto focus to editText
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            editText.setShowSoftInputOnFocus(true);
        }
        editText.requestFocus();

        return mContainer;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_submit, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_submit:
                mListener.onNoteSubmitted(editText.getHint().toString(), editText.getText().toString().trim());
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name ?
        void onNoteSubmitted(String hint, String note);
    }
}
