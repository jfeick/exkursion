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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.baasbox.android.BaasBox;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.json.JsonException;
import com.baasbox.android.json.JsonObject;
import com.baasbox.android.net.HttpRequest;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.EventListener;

import java.util.ArrayList;

import de.uni_weimar.m18.anatomiederstadt.AnatomieDerStadtApplication;
import de.uni_weimar.m18.anatomiederstadt.R;
import de.uni_weimar.m18.anatomiederstadt.util.LevelStateManager;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link QuizMulti.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link QuizMulti#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuizMulti extends Fragment {

    private static final String LOG_TAG = QuizMulti.class.getSimpleName();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";
    private static final String ARG_PARAM5 = "param5";

    // TODO: Rename and change types of parameters
    private ArrayList<String> mOptions;
    private ArrayList<String> mCorrect;
    private String mTargetId;
    private int mPoints;
    private String mQuestionId;


    private OnFragmentInteractionListener mListener;

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onMultiClick(String pageId);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment QuizMulti.
     */
    // TODO: Rename and change types and number of parameters
    public static QuizMulti newInstance(ArrayList<String> options, ArrayList<String> correctList,
                                        String targetId, int points, String questionId) {
        QuizMulti fragment = new QuizMulti();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_PARAM1, options);
        args.putStringArrayList(ARG_PARAM2, correctList);
        args.putString(ARG_PARAM3, targetId);
        args.putInt(ARG_PARAM4, points);
        args.putString(ARG_PARAM5, questionId);
        fragment.setArguments(args);
        return fragment;
    }

    public QuizMulti() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mOptions = getArguments().getStringArrayList(ARG_PARAM1);
            mCorrect = getArguments().getStringArrayList(ARG_PARAM2);
            mTargetId = getArguments().getString(ARG_PARAM3);
            mPoints = getArguments().getInt(ARG_PARAM4);
            mQuestionId = getArguments().getString(ARG_PARAM5);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_quiz_multi, container, false);
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.quizMultiLayout);

        final ArrayList<CheckBox> checkBoxes = new ArrayList<>();
        for(int i = 0; i < mOptions.size(); ++i) {
            CheckBox checkBox = new CheckBox(getActivity());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(16, 0, 0 ,0);
            checkBox.setLayoutParams(layoutParams);
            checkBox.setText(mOptions.get(i));
            linearLayout.addView(checkBox);
            checkBoxes.add(checkBox);
        }

        Button button = new Button(getActivity());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        button.setLayoutParams(layoutParams);

        button.setText("Eingeben");

        linearLayout.addView(button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // evaluate score
                int correct = 0;
                int notcorrect = 0;

                for(int i = 0; i < checkBoxes.size(); ++i) {
                    if(checkBoxes.get(i).isChecked()) {
                        if(mCorrect.get(i).equals("true")) {
                            correct += 1;
                        }
                        else {
                            notcorrect += 1;
                        }
                    } else {
                        if(mCorrect.get(i).equals("false")) {
                            //correct += 1;
                        } else {
                            notcorrect += 1;
                        }
                    }
                }

                int totalcorrect = correct - notcorrect;
                if(totalcorrect < 0) totalcorrect = 0;
                int score = totalcorrect * mPoints;
                // TODO: submit score
                submitPointsToBackend(score);
                String congratulations = "";
                if(totalcorrect == 2) {
                    congratulations = String.format(getString(R.string.congratulations_exact_format_string), score);
                } else if(totalcorrect == 1) {
                    congratulations = String.format(getString(R.string.congratulations_not_quite_format_string), score);
                } else {
                    congratulations = String.format(getString(R.string.congratulations_wrong_format_string), score);
                }
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
                                        if (mListener != null) {
                                            mListener.onMultiClick(mTargetId);
                                        }
                                    }
                                })
                );

            }
        });

        return view;
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

}
