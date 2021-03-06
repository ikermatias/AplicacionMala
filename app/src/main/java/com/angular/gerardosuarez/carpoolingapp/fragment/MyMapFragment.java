package com.angular.gerardosuarez.carpoolingapp.fragment;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.angular.gerardosuarez.carpoolingapp.R;
import com.angular.gerardosuarez.carpoolingapp.activity.MainActivity;
import com.angular.gerardosuarez.carpoolingapp.fragment.base.BaseMapPreferenceFragment;
import com.angular.gerardosuarez.carpoolingapp.fragment.base.OnPageSelectedListener;
import com.angular.gerardosuarez.carpoolingapp.mvp.model.PassengerBooking;
import com.angular.gerardosuarez.carpoolingapp.mvp.presenter.MyMapFragmentPresenter;
import com.angular.gerardosuarez.carpoolingapp.mvp.view.MyMapView;
import com.angular.gerardosuarez.carpoolingapp.service.DriverMapService;
import com.angular.gerardosuarez.carpoolingapp.service.MyBookingDriverService;
import com.angular.gerardosuarez.carpoolingapp.service.PassengerMapService;
import com.angular.gerardosuarez.carpoolingapp.service.UserService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.observers.DisposableObserver;
import timber.log.Timber;

public class MyMapFragment extends BaseMapPreferenceFragment
        implements
        OnMapReadyCallback,
        LocationListener,
        PlaceSelectionListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraIdleListener,
        GoogleMap.OnMarkerClickListener,
        CompoundButton.OnCheckedChangeListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        OnPageSelectedListener {

    public static final int PERMISSION_REQUEST_FINE_LOCATION = 1;
    private MyMapFragmentPresenter presenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_map, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter = new MyMapFragmentPresenter(
                new MyMapView(this),
                new DriverMapService(),
                new PassengerMapService(),
                rolePreference,
                mapPreference,
                new UserService(),
                new MyBookingDriverService(),(MainActivity) getActivity());
        if (presenter.googleServicesAvailable()) {
            presenter.initMap();
        }
        presenter.initView();
        presenter.setAutocompleteFragment();
    }

    @Override
    public void onPageSelected() {
        if (presenter != null) {
            presenter.changeViewElements();
            //presenter.cleanMapIfNecessary();
        }
    }

    @OnClick(R.id.btn_hour)
    void onTimeClick() {
        presenter.showTimePickerFragment(new OnTimeSelectedObserver());
    }

    //On switch changed
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (presenter != null) {
            presenter.onSwitchChanged(isChecked);
        }
    }

    private class OnTimeSelectedObserver extends DisposableObserver<String> {

        @Override
        public void onNext(String time) {
            presenter.onTimeSelected(time);
        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onComplete() {
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.unsubscribeFirebaseListener();
            presenter.unsubscribeObservers();
        }
    }

    @OnClick(R.id.btn_date)
    void onDateClick() {
        presenter.showDatePickerFragment(new OnDateSelectedObserver());
    }

    private class OnDateSelectedObserver extends DisposableObserver<String> {


        @Override
        public void onNext(String s) {
            presenter.onDateSelected(s);
        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onComplete() {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (presenter != null) {
            presenter.setListeners();
            presenter.getBookingsAndAddMarkers();
        }
    }

    public void onRoleClicked(boolean newRole) {
        presenter.onRoleChanged(newRole);
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.removeListeners();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_FINE_LOCATION) {
            presenter.addLocationButton(permissions, grantResults);
        }
    }

    //OnMapReady Callback
    @Override
    public void onMapReady(GoogleMap googleMap) {
        presenter.setMap(googleMap);
        presenter.init();
        presenter.setAutocompleteFragmentText();
    }

    //LocationListener callbacks
    @Override
    public void onLocationChanged(Location location) {
        presenter.onLocationChanged(location);
    }

    //Autocomplete Fragment callbacks
    @Override
    public void onPlaceSelected(Place place) {
        presenter.searchPlace(place);
    }

    @Override
    public void onError(Status status) {
        Timber.e(status.toString());
    }

    //OnCameraMoveStartedListener callbacks
    @Override
    public void onCameraMoveStarted(int reason) {
        presenter.onCameraMoveStarted(reason);
    }

    //OnCameraMoveStartedListener callbacks
    @Override
    public void onCameraMove() {

    }

    @Override
    public void onCameraIdle() {
        presenter.onCameraIdle();
    }

    //GoogleMap.OnMarkerClickListener callback
    @Override
    public boolean onMarkerClick(Marker marker) {
        presenter.showClickMarkerDialog(new OnClickMarkerDialogResponseObserver(), marker);
        return true;
    }

    private class OnClickMarkerDialogResponseObserver extends DisposableObserver<PassengerBooking> {

        @Override
        public void onNext(PassengerBooking passengerBooking) {

            presenter.onClickMarkerDialogResponse(passengerBooking);
        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onComplete() {
        }
    }

    //GoogleApiClient.ConnectionCallbacks,
    //GoogleApiClient.OnConnectionFailedListener
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //presenter.setLocationRequest(); TODO REMOVE COMMENT
    }

    @Override
    public void onConnectionSuspended(int i) {
        Timber.i("suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Timber.i("failed");
    }

    @OnClick(R.id.btn_cancel_route)
    void onCancelBooking() {
        presenter.onCancelRoute();
    }


}
