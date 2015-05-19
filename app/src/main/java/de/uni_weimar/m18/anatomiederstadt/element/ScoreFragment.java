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


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.baasbox.android.BaasBox;
import com.baasbox.android.BaasException;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.json.JsonException;
import com.baasbox.android.json.JsonObject;
import com.baasbox.android.net.HttpRequest;

import java.text.DecimalFormat;

import de.uni_weimar.m18.anatomiederstadt.AnatomieDerStadtApplication;
import de.uni_weimar.m18.anatomiederstadt.R;
import de.uni_weimar.m18.anatomiederstadt.util.LevelStateManager;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TextFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScoreFragment extends Fragment {

    private static final String LOG_TAG = ScoreFragment.class.getSimpleName();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    TextView mTextView;


    public static ScoreFragment newInstance() {
        ScoreFragment fragment = new ScoreFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ScoreFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    private BaasHandler<JsonObject> OnCompleted = new BaasHandler<JsonObject>() {
        @Override
        public void handle(BaasResult<JsonObject> baasResult) {
            Log.d(LOG_TAG, "result: " + baasResult.toString());
            if (baasResult.isSuccess()) {
                try {
                    JsonObject result = baasResult.get();
                    JsonObject data = result.get("data");
                    Double score = data.get("score");
                    String scoreString = new DecimalFormat("#####").format(score);
                    mTextView.setText(scoreString + " Punkte");

                    Animation anim = new AlphaAnimation(0.5f, 1.0f);
                    anim.setDuration(100); //You can manage the blinking time with this parameter
                    anim.setStartOffset(20);
                    anim.setRepeatMode(Animation.REVERSE);
                    anim.setRepeatCount(Animation.INFINITE);
                    mTextView.startAnimation(anim);

                } catch (BaasException e) {

                }
            } else {
                mTextView.setText("Could not get score");
            }
        }

    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_score, container, false);

        //TextView tv = (TextView)rootView.findViewById(R.id.text_view);
        mTextView = (TextView)rootView.findViewById(R.id.text_view);
        BaasBox cli = BaasBox.getDefault();
        LevelStateManager stateManager =
                ((AnatomieDerStadtApplication) getActivity().getApplicationContext()).getStateManager(getActivity());

        cli.rest(HttpRequest.GET,
                "plugin/getscore.bb/" + stateManager.getBasePath(),
                new JsonObject(),
                true,
                OnCompleted
        );


        return rootView;
    }


}
