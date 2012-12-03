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

package com.krissytosi.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ViewFlipper;

import com.krissytosi.KrissyTosiApplication;
import com.krissytosi.R;
import com.krissytosi.api.ApiClient;
import com.krissytosi.api.domain.Photo;
import com.krissytosi.api.domain.PhotoSet;
import com.krissytosi.fragments.adapters.PhotoSetsAdapter;
import com.krissytosi.fragments.views.PhotoSetDetailView;
import com.krissytosi.utils.ApiConstants;
import com.krissytosi.utils.KrissyTosiConstants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Displays a grid of photo sets.
 */
public class PhotoSetsFragment extends BaseFragment implements OnItemClickListener {

    private static final String LOG_TAG = "PhotoSetsFragment";

    /**
     * Used to display a summary of photo sets to the user.
     */
    private GridView photoSetsGrid;

    /**
     * Task used to retrieve the photo sets from the API server.
     */
    private GetPhotoSetsTask getPhotoSetsTask;

    /**
     * Data structure of tasks for retrieving photos for a particular photo set.
     */
    private final List<GetPhotosTask> getPhotosTasks = new ArrayList<GetPhotosTask>();

    /**
     * Adapter which backs this view.
     */
    private PhotoSetsAdapter adapter;

    /**
     * View for handling events related to a particular photo set.
     */
    private PhotoSetDetailView photoSetDetailView;

    private List<PhotoSet> photoSets;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.photosets, container, false);
        photoSetsGrid = (GridView) v.findViewById(R.id.photosets_grid);
        photoSetsGrid.setOnItemClickListener(this);
        return v;
    }

    @Override
    public String getFragmentIdentifier() {
        return KrissyTosiConstants.FRAGMENT_PHOTOSETS_ID;
    }

    @Override
    public void onTabSelected() {
        if (getActivity() != null && getPhotoSetsTask == null) {
            toggleLoading(true, getView().findViewById(R.id.photosets_grid));
            getPhotoSetsTask = new GetPhotoSetsTask();
            getPhotoSetsTask.execute(((KrissyTosiApplication) getActivity().getApplication())
                    .getApiClient());
        } else if (adapter != null && adapter.getCount() > 0) {
            toggleLoading(false, getView().findViewById(R.id.photoset_flipper));
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        cleanupPhotoTasks();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        FragmentHelper.toggleFlipper(false,
                (ViewFlipper) getView().findViewById(R.id.photoset_flipper), getActivity(),
                getFragmentIdentifier());
        PhotoSet photoSet = adapter.getItem(position);
        populatePhotoSet(photoSet);
    }

    private void populatePhotoSet(PhotoSet photoSet) {
        if (photoSetDetailView == null) {
            photoSetDetailView = new PhotoSetDetailView();
        }
        photoSetDetailView.setBaseView(getView());
        photoSetDetailView.setPhotoSet(photoSet);
        photoSetDetailView.setContext(getActivity());
        photoSetDetailView.buildPage();
    }

    protected void buildPhotoSets(List<PhotoSet> photoSets) {
        this.photoSets = photoSets;
        cleanupPhotoTasks();
        // populate a new batch of GetPhotosTask objects & execute 'em all
        for (PhotoSet photoSet : photoSets) {
            GetPhotosTask task = new GetPhotosTask();
            task.execute(((KrissyTosiApplication) getActivity().getApplication()).getApiClient(),
                    photoSet.getId());
            getPhotosTasks.add(task);
        }
    }

    protected void buildView() {
        flipperId = R.id.photoset_flipper;
        if (adapter == null) {
            adapter = new PhotoSetsAdapter(getActivity(), R.layout.photoset_detail_view,
                    (ArrayList<PhotoSet>) photoSets);
            photoSetsGrid.setAdapter(adapter);
        }
        adapter.notifyDataSetChanged();
        toggleLoading(false, photoSetsGrid);
    }

    /**
     * Callback executed when the photo sets API response has returned.
     * 
     * @param photoSets the list of photo sets from the server (or a list of
     *            errors detailing why the photo sets were not available).
     */
    protected void onGetPhotoSets(List<PhotoSet> photoSets) {
        // check to see that we actually have *a* photo set back from the API
        // server
        if (photoSets.size() > 0) {
            // check to see that the first photo set isn't an error (is there a
            // better way to communicate these errors?)
            PhotoSet photoSet = photoSets.get(0);
            if (photoSet.getErrorCode() == -1 && photoSet.getErrorDescription() == null) {
                Log.d(LOG_TAG, "Retrieved at least one photo set from the server");
                buildPhotoSets(photoSets);
            } else {
                handlePhotoSetsApiError(photoSet);
            }
        } else {
            PhotoSet photoSet = new PhotoSet();
            photoSet.setErrorCode(ApiConstants.NO_PHOTOSETS);
            photoSet.setErrorDescription(ApiConstants.NO_PHOTOSETS_DESCRIPTION);
            handlePhotoSetsApiError(photoSet);
        }
    }

    /**
     * Callback executed when we get the list of photos back from the API
     * server.
     * 
     * @param photos a list of {@link Photo} objects corresponding to the
     *            photoSet member variable.
     */
    protected void onGetPhotos(List<Photo> photos) {
        String photoSetId = "";
        if (photos.size() != 0) {
            photoSetId = photos.get(0).getPhotoSetId();
        }
        // check to see whether we can go ahead and build the view.
        Iterator<GetPhotosTask> iterator = getPhotosTasks.iterator();
        while (iterator.hasNext()) {
            GetPhotosTask task = iterator.next();
            if (photoSetId.equalsIgnoreCase(task.getPhotoSetId())) {
                iterator.remove();
            }
        }
        for (PhotoSet photoSet : photoSets) {
            if (photoSet.getId() != null && photoSet.getId().equalsIgnoreCase(photoSetId)) {
                photoSet.setPhotos(photos);
            }
        }
        if (getPhotosTasks.size() == 0) {
            buildView();
        }
    }

    /**
     * Callback executed when we fail to retrieve the photo sets from the API.
     * 
     * @param errorPhotoSet
     */
    protected void handlePhotoSetsApiError(PhotoSet errorPhotoSet) {
        Log.d(LOG_TAG, "Photo Set Failure: " + errorPhotoSet.getErrorDescription());
        toggleNoNetwork(true, photoSetsGrid);
    }

    private void cleanupPhotoTasks() {
        if (getPhotoSetsTask != null) {
            getPhotoSetsTask.cancel(true);
        }
        // make sure we're starting with a clean slate.
        if (getPhotosTasks.size() > 0) {
            for (GetPhotosTask task : getPhotosTasks) {
                task.cancel(true);
            }
            getPhotosTasks.clear();
        }
    }

    /**
     * Simple AsynTask to retrieve the list of photo sets from the API server.
     */
    private class GetPhotoSetsTask extends
            AsyncTask<ApiClient, Void, List<PhotoSet>> {

        @Override
        protected List<PhotoSet> doInBackground(ApiClient... params) {
            ApiClient apiClient = params[0];
            return apiClient.getPhotoSetService().getPhotoSets();
        }

        @Override
        protected void onPostExecute(List<PhotoSet> result) {
            onGetPhotoSets(result);
            getPhotoSetsTask = null;
        }
    }

    /**
     * Simple AsynTask to retrieve the photo urls for a photo set.
     */
    private class GetPhotosTask extends
            AsyncTask<Object, Void, List<Photo>> {

        private String photoSetId;

        @Override
        protected List<Photo> doInBackground(Object... params) {
            ApiClient apiClient = (ApiClient) params[0];
            photoSetId = (String) params[1];
            return apiClient.getPhotoSetService().getPhotos(photoSetId);
        }

        @Override
        protected void onPostExecute(List<Photo> photos) {
            onGetPhotos(photos);
        }

        // Getters/Setters

        public String getPhotoSetId() {
            return photoSetId;
        }
    }
}
