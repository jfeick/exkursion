/*
 * Copyright 2015. J.F.Eick
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package de.uni_weimar.m18.anatomiederstadt.element;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import de.uni_weimar.m18.anatomiederstadt.R;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QuizMultipleChoiceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuizMultipleChoiceFragment extends Fragment implements View.OnClickListener {

    private static final String LOG_TAG = QuizMultipleChoiceFragment.class.getSimpleName();
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "button1text";
    private static final String ARG_PARAM2 = "button2text";
    private static final String ARG_PARAM3 = "button3text";
    private static final String ARG_PARAM4 = "button4text";
    private static final String ARG_PARAM5 = "correctAnswer";
    private static final String ARG_PARAM6 = "correctTargetId";

    // TODO: Rename and change types of parameters
    private String mButton1Text;
    private String mButton2Text;
    private String mButton3Text;
    private String mButton4Text;
    private int mCorrectAnswer;
    private String mCorrectTargetId;

    private OnFragmentInteractionListener mListener;
    public interface OnFragmentInteractionListener {
        public void correctAnswerAction(String pageId);
    }

    public static QuizMultipleChoiceFragment newInstance(String button1text, String button2text,
                                                         String button3text, String button4text,
                                                         int correctAnswer, String correctTargetId) {
        QuizMultipleChoiceFragment fragment = new QuizMultipleChoiceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, button1text);
        args.putString(ARG_PARAM2, button2text);
        args.putString(ARG_PARAM3, button3text);
        args.putString(ARG_PARAM4, button4text);
        args.putInt(ARG_PARAM5, correctAnswer);
        args.putString(ARG_PARAM6, correctTargetId);
        fragment.setArguments(args);
        return fragment;
    }

    public QuizMultipleChoiceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onClick(View v) {
        Button correctButton;
        Log.v(LOG_TAG, "Button in quiz clicked!");
        switch (mCorrectAnswer) {
            case 1:
                correctButton = (Button)getActivity().findViewById(R.id.button1);
                break;
            case 2:
                correctButton = (Button)getActivity().findViewById(R.id.button2);
                break;
            case 3:
                correctButton = (Button)getActivity().findViewById(R.id.button3);
                break;
            case 4:
                correctButton = (Button)getActivity().findViewById(R.id.button4);
                break;
            default:
                correctButton = (Button)getActivity().findViewById(R.id.button1);
        }
        if(v.getId() == correctButton.getId()) {
            Toast.makeText(getActivity(), "RRRRRRRICHTIG!", Toast.LENGTH_SHORT).show();
            if(mListener != null) {
                mListener.correctAnswerAction(mCorrectTargetId);
            }
        } else {
            Toast.makeText(getActivity(), "BZZZZZZZZZTTTT... falsch!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mButton1Text = getArguments().getString(ARG_PARAM1);
            mButton2Text = getArguments().getString(ARG_PARAM2);
            mButton3Text = getArguments().getString(ARG_PARAM3);
            mButton4Text = getArguments().getString(ARG_PARAM4);
            mCorrectAnswer = getArguments().getInt(ARG_PARAM5);
            mCorrectTargetId = getArguments().getString(ARG_PARAM6);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_quiz_multiple_choice, container, false);

        Button button1 = (Button)rootView.findViewById(R.id.button1);
        button1.setText(mButton1Text);
        Button button2 = (Button)rootView.findViewById(R.id.button2);
        button2.setText(mButton2Text);
        Button button3 = (Button)rootView.findViewById(R.id.button3);
        button3.setText(mButton3Text);
        Button button4 = (Button)rootView.findViewById(R.id.button4);
        button4.setText(mButton4Text);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


}
