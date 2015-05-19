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
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

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

import de.uni_weimar.m18.anatomiederstadt.AnatomieDerStadtApplication;
import de.uni_weimar.m18.anatomiederstadt.R;
import de.uni_weimar.m18.anatomiederstadt.util.LevelStateManager;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InputFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InputFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InputCheckFragment extends Fragment {
    private static final String LOG_TAG = InputCheckFragment.class.getSimpleName();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";
    private static final String ARG_PARAM5 = "param5";
    private static final String ARG_PARAM6 = "param6";
    private static final String ARG_PARAM7 = "param7";

    // TODO: Rename and change types of parameters
    private String mButtonCaption;
    private String mVar;
    private String mTargetId;
    private String mCorrect;
    private int mPoints;
    private String mHint;
    private String mQuestionId;

    private OnFragmentInteractionListener mListener;

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onInputCheckClick(String pageId);
    }


    public static InputCheckFragment newInstance(String buttonCaption, String var, String targetId,
                                                 String correct, int points, String hint, String questionId) {
        InputCheckFragment fragment = new InputCheckFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, buttonCaption);
        args.putString(ARG_PARAM2, var);
        args.putString(ARG_PARAM3, targetId);
        args.putString(ARG_PARAM4, correct);
        args.putInt(ARG_PARAM5, points);
        args.putString(ARG_PARAM6, hint);
        args.putString(ARG_PARAM7, questionId);
        fragment.setArguments(args);
        return fragment;
    }

    public InputCheckFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mButtonCaption = getArguments().getString(ARG_PARAM1);
            mVar = getArguments().getString(ARG_PARAM2);
            mTargetId = getArguments().getString(ARG_PARAM3);
            mCorrect = getArguments().getString(ARG_PARAM4);
            mPoints = getArguments().getInt(ARG_PARAM5);
            mHint = getArguments().getString(ARG_PARAM6);
            mQuestionId = getArguments().getString(ARG_PARAM7);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_input, container, false);

        final EditText editText = (EditText) view.findViewById(R.id.editText);

        Button button = (Button) view.findViewById(R.id.inputButton);
        button.setText(mButtonCaption);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                LevelStateManager stateManager =
                        ((AnatomieDerStadtApplication) getActivity().getApplicationContext()).getStateManager(getActivity());

                String value = "0.0";
                if (!TextUtils.isEmpty(editText.getText())) {
                    if (editText.getText().toString().equals(mCorrect)) {
                        // richtige Antwort
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
                                                if (mListener != null) {
                                                    mListener.onInputCheckClick(mTargetId);
                                                }
                                            }
                                        })
                        );

                    } else {
                        // show Hint
                        new MaterialDialog.Builder(getActivity())
                                .title(getString(R.string.wrong_answer_dialog_title))
                                .content(mHint)
                                .positiveText(R.string.wrong_answer_dismiss)
                                .show();
                        if (mListener != null) {
                            mListener.onInputCheckClick(mTargetId);
                        }
                    }
                }

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
