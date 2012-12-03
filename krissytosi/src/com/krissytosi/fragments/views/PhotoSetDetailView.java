/*
   Copyright 2012 Sean O' Shea

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.krissytosi.fragments.views;

import android.app.Activity;
import android.support.v4.view.ViewPager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.krissytosi.R;
import com.krissytosi.api.domain.Photo;
import com.krissytosi.api.domain.PhotoSet;
import com.krissytosi.fragments.adapters.ImagePagerAdapter;
import com.krissytosi.utils.KrissyTosiUtils;
import com.krissytosi.utils.KrissyTosiUtils.ImageSize;

import java.util.List;

/**
 * View for a particular photo set. Allows a user to swipe through the photos in
 * a photo set.
 */
public class PhotoSetDetailView extends BaseDetailView {

    private static final String LOG_TAG = "PhotoSetDetailView";

    /**
     * PhotoSet which backs this view.
     */
    private PhotoSet photoSet;

    private TextView photoSetTitle;

    /**
     * The pager which is the primary component in this view.
     */
    private ViewPager photoSetViewPager;

    /**
     * Builds the page and kicks off the request to find all the images for this
     * photo set.
     */
    public void buildPage() {
        photoSetTitle = (TextView) getBaseView().findViewById(R.id.photoset_title);
        photoSetViewPager = (ViewPager) getBaseView().findViewById(R.id.pager);
        photoSetTitle.setText(photoSet.getTitle());
        List<Photo> photos = photoSet.getPhotos();
        String[] images = new String[photos.size()];
        int counter = 0;
        int maximumHeight = 0;
        for (Photo photo : photos) {
            images[counter] = KrissyTosiUtils.determineImageUrl(photo, ImageSize.LARGE);
            if (photo.getHeightMedium() > maximumHeight) {
                maximumHeight = photo.getHeightMedium();
            }
            counter++;
        }
        photoSetViewPager.setAdapter(new ImagePagerAdapter(images, (Activity) getContext()));
        photoSetViewPager.setCurrentItem(0);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) photoSetViewPager
                .getLayoutParams();
        params.height = maximumHeight;
    }

    // Getters/Setters

    public PhotoSet getPhotoSet() {
        return photoSet;
    }

    public void setPhotoSet(PhotoSet photoSet) {
        this.photoSet = photoSet;
    }
}
