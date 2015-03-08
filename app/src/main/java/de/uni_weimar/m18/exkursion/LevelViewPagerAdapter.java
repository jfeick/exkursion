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
import java.util.List;

class LevelViewPagerAdapter extends FragmentStatePagerAdapter {

    private Fragment fragment = null;
    private String mBasePath = null;
    private List<Fragment> mPages;



    public LevelViewPagerAdapter(FragmentManager fm, String basePath) {
        super(fm);
        this.mBasePath = basePath;
        this.mPages = new ArrayList<Fragment>();
        this.mPages.add(LevelPageFragment.newInstance("start", mBasePath));
    }

    public void addPage(String pageId) {
        mPages.add(LevelPageFragment.newInstance(pageId, mBasePath));
        super.notifyDataSetChanged();
    }

    public int getItemById(String pageId) {
        int position = mPages.indexOf(pageId);
        if (position < 0) {
            addPage(pageId);
            return mPages.size() - 1; // return last item (added Page)
        } else {
            return position;
        }
    }

    @Override
    public Fragment getItem(int position) {
        return mPages.get(position);
    }

    @Override
    public int getCount() {
        return mPages.size();
    }
}