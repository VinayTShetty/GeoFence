package com.succorfish.geofence.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.succorfish.geofence.BaseFragment.BaseFragment;
import com.succorfish.geofence.MainActivity;
import com.succorfish.geofence.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FragmentRemoteTrackingMap extends BaseFragment {
    View fragmentRemoteTrackingView;
    private Unbinder unbinder;
    MainActivity mainActivity;
    MapView mMapView;
    String connected_bleAddress="";
    GoogleMap fgragmentRemoteTrackinggoogleMap;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentRemoteTrackingView = inflater.inflate(R.layout.fragment_remotetracking_map, container, false);
        unbinder = ButterKnife.bind(this, fragmentRemoteTrackingView);
        bottomLayoutVisibility(false);
        setUpMap(fragmentRemoteTrackingView, savedInstanceState);
        return fragmentRemoteTrackingView;
    }
    private void bottomLayoutVisibility(boolean hide_true_unhide_false){
        mainActivity.hideBottomLayout(hide_true_unhide_false);
    }

    private void setUpMap(View fragmentMapView, Bundle savedInstanceState) {
        mMapView = (MapView) fragmentMapView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                /**
                 * ask for Updates on Live Location Tracking.
                 */
            }
        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public String toString() {
        return FragmentRemoteTrackingMap.class.getSimpleName();
    }
}
