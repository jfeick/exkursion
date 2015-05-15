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
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.baasbox.android.BaasBox;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.json.JsonException;
import com.baasbox.android.json.JsonObject;
import com.baasbox.android.net.HttpRequest;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.EventListener;

import org.json.JSONException;
import org.json.JSONObject;

import de.uni_weimar.m18.anatomiederstadt.AnatomieDerStadtApplication;
import de.uni_weimar.m18.anatomiederstadt.R;
import de.uni_weimar.m18.anatomiederstadt.util.LevelStateManager;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Quiz4Buttons#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Quiz4Buttons extends Fragment implements View.OnClickListener {

    // target, correctInt, button1, button2, button3, button4, hint, pointsInt, penaltyInt

    private static final String LOG_TAG = Quiz4Buttons.class.getSimpleName();
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "target";
    private static final String ARG_PARAM2 = "correct";
    private static final String ARG_PARAM3 = "button1";
    private static final String ARG_PARAM4 = "button2";
    private static final String ARG_PARAM5 = "button3";
    private static final String ARG_PARAM6 = "button4";
    private static final String ARG_PARAM7 = "hint";
    private static final String ARG_PARAM8 = "points";
    private static final String ARG_PARAM9 = "penalty";
    private static final String ARG_PARAM10 = "questionId";

    private String mTarget;
    private int mCorrect;
    private String mButton1;
    private String mButton2;
    private String mButton3;
    private String mButton4;
    private String mHint;
    private int mPoints;
    private int mPenalty;
    private String mQuestionId;

    private int mTries = 0;
    private final int mMaxTries = 3;

    private OnFragmentInteractionListener mListener;
    public interface OnFragmentInteractionListener {
        public void correctAnswerAction(String pageId);
    }

    public static Quiz4Buttons newInstance(
            String target, int correctInt, String button1, String button2, String button3, String button4,
            String hint, int pointsInt, int penaltyInt, String questionId) {
        Quiz4Buttons fragment = new Quiz4Buttons();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, target);
        args.putInt(ARG_PARAM2, correctInt);
        args.putString(ARG_PARAM3, button1);
        args.putString(ARG_PARAM4, button2);
        args.putString(ARG_PARAM5, button3);
        args.putString(ARG_PARAM6, button4);
        args.putString(ARG_PARAM7, hint);
        args.putInt(ARG_PARAM8, pointsInt);
        args.putInt   (ARG_PARAM9, penaltyInt);
        args.putString(ARG_PARAM10, questionId);
        fragment.setArguments(args);
        return fragment;
    }

    public Quiz4Buttons() {
        // Required empty public constructor
    }

    public void submitPointsToBackend(int points) {
        BaasBox cli = BaasBox.getDefault();
        LevelStateManager stateManager =
                ((AnatomieDerStadtApplication) getActivity().getApplicationContext()).getStateManager(getActivity());
        JsonObject parameters = new JsonObject();
        try {
            parameters.put("level", stateManager.getBasePath());
            parameters.put("question", "q" + mQuestionId);
            parameters.put("score", points);
        } catch(JsonException e) {

        }
        cli.rest(HttpRequest.POST,
                "plugin/updatescore.bb",
                parameters,
                true,
                new BaasHandler<JsonObject>() {
                    @Override
                    public void handle(BaasResult<JsonObject> baasResult) {
                        Log.d(LOG_TAG, "result: " + baasResult.toString());
                        if (baasResult.isSuccess()) {
                            Log.d(LOG_TAG, "Score for question " + mQuestionId + " submitted successfully.");
                        } else {
                            Log.d(LOG_TAG, "Error. Could not submit score.");
                        }
                    }

                }
        );

    }

    @Override
    public void onClick(View v) {
        Button correctButton;
        switch (mCorrect) {
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



        mTries += 1;

        if(v.getId() == correctButton.getId()) { // correct answer
            String congratulations = String.format(getString(R.string.congratulations_format_string), mPoints);
            SnackbarManager.show(
                    Snackbar.with(getActivity())
                            .position(Snackbar.SnackbarPosition.TOP)
                            .margin(32, 32)
                            .backgroundDrawable(R.drawable.points_snackbar_shape)
                            .text(congratulations)
                            .eventListener(new EventListener() {
                                @Override
                                public void onShow(Snackbar snackbar) {

                                }

                                @Override
                                public void onShowByReplace(Snackbar snackbar) {

                                }

                                @Override
                                public void onShown(Snackbar snackbar) {

                                }

                                @Override
                                public void onDismiss(Snackbar snackbar) {

                                }

                                @Override
                                public void onDismissByReplace(Snackbar snackbar) {

                                }

                                @Override
                                public void onDismissed(Snackbar snackbar) {
                                    if(mListener != null) {
                                        mListener.correctAnswerAction(mTarget);
                                    }
                                }
                            })
            );
            //Toast.makeText(getActivity(), "RRRRRRRICHTIG!", Toast.LENGTH_SHORT).show();
            submitPointsToBackend(mPoints);

        }
        else if(mTries >= mMaxTries) { // user entered wrong answer too often
            String sorry = getString(R.string.sorry_correct_answer) + "\n\"" + correctButton.getText() + "\"";
            new MaterialDialog.Builder(getActivity())
                    .title(getString(R.string.sorry_correct_answer_dialog_title))
                    .content(sorry)
                    .positiveText(getString(R.string.sorry_dismiss))
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            if(mListener != null) { // go to next page anyway
                                mListener.correctAnswerAction(mTarget);
                            }
                        }
                    })
                    .show();
        }
        else { // wrong answer
            mPoints -= mPenalty;
            Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(300);
            new MaterialDialog.Builder(getActivity())
                    .title(getString(R.string.wrong_answer_dialog_title))
                    .content(mHint)
                    .positiveText(R.string.wrong_answer_dismiss)
                    .show();
            //Toast.makeText(getActivity(), "BZZZZZZZZZTTTT... falsch!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTarget  = getArguments().getString(ARG_PARAM1);
            mCorrect = getArguments().getInt(ARG_PARAM2);
            mButton1 = getArguments().getString(ARG_PARAM3);
            mButton2 = getArguments().getString(ARG_PARAM4);
            mButton3 = getArguments().getString(ARG_PARAM5);
            mButton4 = getArguments().getString(ARG_PARAM6);
            mHint    = getArguments().getString(ARG_PARAM7);
            mPoints  = getArguments().getInt(ARG_PARAM8);
            mPenalty = getArguments().getInt(ARG_PARAM9);
            mQuestionId = getArguments().getString(ARG_PARAM10);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_quiz_multiple_choice, container, false);

        Button button1 = (Button)rootView.findViewById(R.id.button1);
        button1.setText(mButton1);
        Button button2 = (Button)rootView.findViewById(R.id.button2);
        button2.setText(mButton2);
        Button button3 = (Button)rootView.findViewById(R.id.button3);
        button3.setText(mButton3);
        Button button4 = (Button)rootView.findViewById(R.id.button4);
        button4.setText(mButton4);

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
