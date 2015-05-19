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

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Space;
import android.widget.TextView;

import java.text.DecimalFormat;

import de.uni_weimar.m18.anatomiederstadt.AnatomieDerStadtApplication;
import de.uni_weimar.m18.anatomiederstadt.R;
import de.uni_weimar.m18.anatomiederstadt.util.LevelStateManager;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SliderFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SliderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SliderFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";
    private static final String ARG_PARAM5 = "param5";

    private int mMin;
    private int mMax;
    private String mSuffix;
    private float mGranularity;
    private String mVar;


    // TODO: Rename and change types and number of parameters
    public static SliderFragment newInstance(int min, int max, float granularity, String suffix, String var) {
        SliderFragment fragment = new SliderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, min);
        args.putInt(ARG_PARAM2, max);
        args.putFloat(ARG_PARAM3, granularity);
        args.putString(ARG_PARAM4, suffix);
        args.putString(ARG_PARAM5, var);
        fragment.setArguments(args);
        return fragment;
    }

    public SliderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMin = getArguments().getInt(ARG_PARAM1);
            mMax = getArguments().getInt(ARG_PARAM2);
            mGranularity = getArguments().getFloat(ARG_PARAM3);
            mSuffix = getArguments().getString(ARG_PARAM4);
            mVar = getArguments().getString(ARG_PARAM5);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_slider, container, false);
        SeekBar sb = (SeekBar) view.findViewById(R.id.seekBar);
        // scale mMin and mMax
        mMin = (int)(mMin / mGranularity);
        mMax = (int)(mMax / mGranularity);
        sb.setMax(mMax - mMin);
        sb.setProgress((mMax - mMin) / 2);
        final RelativeLayout balloon = (RelativeLayout) view.findViewById(R.id.indicatorBalloon);
        final Space space = (Space) view.findViewById(R.id.balloonSpace);

        final Context context = getActivity();
        final TextView balloonText = (TextView) view.findViewById(R.id.indicatorTextView);
        final float[] value = {0.0f};
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                p.addRule(RelativeLayout.ABOVE, seekBar.getId());
                Rect thumbRect = seekBar.getThumb().getBounds();
                value[0] = mMin + progress * mGranularity;
                String valueString = new DecimalFormat("#.##").format(value[0]);
                balloonText.setText(String.valueOf(valueString) + " " + mSuffix);
                int balloonWidth = balloonText.getWidth();
                p.setMargins(thumbRect.centerX() - balloonWidth / 2, 0, 0, 0);
                balloon.setLayoutParams(p);
                balloon.setVisibility(View.VISIBLE);

                final Animation animFadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out);
                balloon.startAnimation(animFadeOut);
                animFadeOut.setStartOffset(1000);
                animFadeOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        balloon.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                LevelStateManager stateManager =
                        ((AnatomieDerStadtApplication) getActivity().getApplicationContext()).getStateManager(getActivity());
                stateManager.saveFloat(mVar, value[0]);
            }
        });
        return view;
    }


}
