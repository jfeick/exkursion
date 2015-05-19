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
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

import de.uni_weimar.m18.anatomiederstadt.R;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocationFragment extends Fragment
    implements GoogleApiClient.ConnectionCallbacks,
               GoogleApiClient.OnConnectionFailedListener,
               LocationListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String LOG_TAG = LocationFragment.class.getSimpleName();

    // TODO: Rename and change types of parameters
    private String mLongitude;
    private String mLatitude;
    private String mTargetId;

    private GoogleApiClient mGoogleApiClient;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationRequest mLocationRequest;

    TextView mInfoText;

    private OnFragmentInteractionListener mListener;

    public interface OnFragmentInteractionListener {
        public void inProximityAction(String pageId);
    }

    public static LocationFragment newInstance(String latitude, String longitude, String targetId) {
        LocationFragment fragment = new LocationFragment();
        Bundle args = new Bundle();

        args.putString(ARG_PARAM1, latitude);
        args.putString(ARG_PARAM2, longitude);

        args.putString(ARG_PARAM3, targetId);
        fragment.setArguments(args);
        return fragment;
    }

    public LocationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            mLatitude = getArguments().getString(ARG_PARAM1);
            mLongitude = getArguments().getString(ARG_PARAM2);
            mTargetId = getArguments().getString(ARG_PARAM3);
        }

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)
                .setFastestInterval(5 * 1000);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_location, container, false);
        ImageView staticMapView = (ImageView) view.findViewById(R.id.staticMapView);
        Uri staticMapUri = Uri.parse("https://maps.googleapis.com/maps/api/staticmap").buildUpon()
                .appendQueryParameter("center", mLatitude + "," + mLongitude)
                .appendQueryParameter("zoom", "17")
                .appendQueryParameter("size", "400x250")
                .appendQueryParameter("markers", "color:black|" + mLatitude + "," + mLongitude)
                .build();

        Picasso.with(getActivity())
                .load(staticMapUri.toString())
                .into(staticMapView);

        staticMapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:" + mLatitude + "," + mLongitude + "?q=" +
                                             mLatitude + "," + mLongitude + "(Ziel)");
                Log.v(LOG_TAG, "Requested location intent: " + gmmIntentUri.toString());
                Intent intent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });

        mInfoText = (TextView) view.findViewById(R.id.locationInfoText);

        final LocationListener locationListener = this;

        Button updateButton = (Button)view.findViewById(R.id.updateLocationButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mInfoText != null) {
                    mInfoText.setText("...");
                    mInfoText.setVisibility(View.VISIBLE);
                }
                // Request location update
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        mGoogleApiClient, mLocationRequest, locationListener);
            }
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }

    }

    private void handleNewLocation(Location location) {
        Log.d(LOG_TAG, "Handling location: " + location.toString());
        //double currentLatitude = location.getLatitude();
        //double currentLongitude = location.getLongitude();

        Location target = new Location("target");
        target.setLatitude(Double.parseDouble(mLatitude));
        target.setLongitude(Double.parseDouble(mLongitude));

        float distance = target.distanceTo(location);
        Log.d(LOG_TAG, "Distance to target is approximately " + Float.toString(distance) + "meters");
        if (distance > 25.0f) {
            if (mInfoText != null) {
                String distanceFormat = new DecimalFormat("#").format(distance);
                mInfoText.setText("Du bist noch ca " + distanceFormat + "m entfernt.");
                mInfoText.setVisibility(View.VISIBLE);
            }
        } else {
            // we are in proximity, call action to advance
            mListener.inProximityAction(mTargetId);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(LOG_TAG, "Location services connected.");
        /*
        Location location =
                LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if( location == null ) {
            // if we dont have a last know location, request a new one
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
        else {
            handleNewLocation(location);
        }
        */
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(LOG_TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.w(LOG_TAG, "Location service connection failed!");
        if (connectionResult.hasResolution()) {
            try {
                // Start an activity that tries to resolve the error
                connectionResult.startResolutionForResult(getActivity(),
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(LOG_TAG, "Location services connection failed with code " +
                            connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
        // after a successful update we don't want to receive new Updates
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
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

}
