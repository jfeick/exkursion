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

package de.uni_weimar.m18.anatomiederstadt;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.eftimoff.androipathview.PathView;

public class SplashActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final PathView pathView = (PathView) findViewById(R.id.pathView);
        pathView.useNaturalColors();

        int pathDelay = 1000;
        int pathDuration = 2500;

        final Animation a = new AlphaAnimation(0.00f, 1.00f);
        a.setStartOffset(3500);
        a.setDuration(1000);

        //pathView.setSvgResource(R.raw.vitruvian_man_weimar_kk3);
        pathView.getPathAnimator().
                delay(pathDelay).
                duration(pathDuration).
                interpolator(new AccelerateDecelerateInterpolator()).
                start();

        final ImageView logoImage = (ImageView) findViewById(R.id.logoImage);

        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                logoImage.setVisibility(View.VISIBLE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        launchMainActivity();
                    }
                }, 1500);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });



        logoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchMainActivity();
            }
        });

        logoImage.startAnimation(a);

    }

    private void launchMainActivity() {
        Intent intent = new Intent(this, LevelSelectActivity.class);
        startActivity(intent);
    }
}
