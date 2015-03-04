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

package de.uni_weimar.m18.exkursion;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

class LevelViewPagerAdapter extends FragmentStatePagerAdapter {

    Fragment fragment = null;
    int mCount = 0;
    String mBasePath = null;

    public LevelViewPagerAdapter(FragmentManager fm, int count, String basePath) {
        super(fm);
        this.mCount = count;
        this.mBasePath = basePath;
    }

    @Override
    public Fragment getItem(int i) {
        ArrayList<LevelPageFragment> fragments = new ArrayList<>();
        for(int k = 0; k < mCount; ++k) {
            fragments.add(LevelPageFragment.newInstance("test" + Integer.toString(k), k, mBasePath));
        }
        return fragments.get(i);

    }

    @Override
    public int getCount() {
        return mCount;
    }
}