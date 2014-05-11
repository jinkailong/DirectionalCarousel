/*
 * Copyright 2014 acbelter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.acbelter.directionalcarousel;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import com.acbelter.directionalcarousel.page.PageFragment;
import com.acbelter.directionalcarousel.page.PageItem;
import com.acbelter.directionalcarousel.page.PageLayout;

import java.util.ArrayList;

public class CarouselPagerAdapter extends FragmentPagerAdapter implements
        ViewPager.OnPageChangeListener {
    private CarouselConfig mConfig;
    private int mPagesCount;
    private int mFirstPosition;

    private FragmentManager mFragmentManager;
    private ArrayList<PageItem> mItems;

    public CarouselPagerAdapter(FragmentManager fragmentManager, ArrayList<PageItem> items) {
        super(fragmentManager);
        mConfig = CarouselConfig.getInstance();
        mFragmentManager = fragmentManager;
        if (items == null) {
            mItems = new ArrayList<PageItem>(0);
        } else {
            mItems = items;
        }
        mPagesCount = mItems.size();
        if (mConfig.infinite) {
            mFirstPosition = mPagesCount * CarouselConfig.LOOPS / 2;
        }
    }

    @Override
    public Fragment getItem(int position) {
        if (mConfig.infinite) {
            position = position % mPagesCount;
        }
        return PageFragment.newInstance(mItems.get(position));
    }

    @Override
    public int getCount() {
        if (mConfig.infinite) {
            return mPagesCount * CarouselConfig.LOOPS;
        }
        return mPagesCount;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
        if (!mConfig.scrollScaling) {
            return;
        }

        PageLayout current = getRootView(position);
        PageLayout next = getRootView(position + 1);

        if (current != null) {
            current.setScaleBoth(CarouselConfig.BIG_SCALE
                    - CarouselConfig.DIFF_SCALE * positionOffset);
        }

        if (next != null) {
            next.setScaleBoth(CarouselConfig.SMALL_SCALE
                    + CarouselConfig.DIFF_SCALE * positionOffset);
        }
    }

    @Override
    public void onPageSelected(int position) {
        // Fix fast scroll scaling bug
        int scalingPages = CarouselConfig.getInstance().pageLimit;
        if (scalingPages == 0) {
            return;
        } else {
            scalingPages--;
        }

        if (scalingPages > 2) {
            int oneSidePages = (scalingPages - 2) / 2;
            for (int i = 0; i < oneSidePages; i++) {
                PageLayout prevSidePage = getRootView(position - 1 - (i + 1));
                if (prevSidePage != null) {
                    if (mConfig.scrollScaling) {
                        prevSidePage.setScaleBoth(CarouselConfig.SMALL_SCALE);
                    } else {
                        prevSidePage.setScaleBoth(CarouselConfig.BIG_SCALE);
                    }
                }
                PageLayout nextSidePage = getRootView(position + 1 + (i + 1));
                if (nextSidePage != null) {
                    if (mConfig.scrollScaling) {
                        nextSidePage.setScaleBoth(CarouselConfig.SMALL_SCALE);
                    } else {
                        nextSidePage.setScaleBoth(CarouselConfig.BIG_SCALE);
                    }
                }
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    public int getFirstPosition() {
        return mFirstPosition;
    }

    private PageLayout getRootView(int position) {
        String tag = CarouselConfig.getInstance().getPageFragmentTag(position);
        Fragment f = mFragmentManager.findFragmentByTag(tag);
        if (f != null && f.getView() != null) {
            return (PageLayout) f.getView().findViewById(R.id.root);
        }
        return null;
    }
}
