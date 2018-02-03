package com.angular.gerardosuarez.carpoolingapp.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.angular.gerardosuarez.carpoolingapp.R;
import com.angular.gerardosuarez.carpoolingapp.mvp.model.DriverInfoRequest;
import com.angular.gerardosuarez.carpoolingapp.mvp.model.PassengerBooking;
import com.angular.gerardosuarez.carpoolingapp.mvp.model.PassengerInfoRequest;
import com.angular.gerardosuarez.carpoolingapp.mvp.model.User;
import com.angular.gerardosuarez.carpoolingapp.service.MyBookingPassengerService;
import com.angular.gerardosuarez.carpoolingapp.service.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.reactivex.observers.DisposableObserver;
import io.reactivex.subjects.PublishSubject;
import timber.log.Timber;


public class DialogMarker extends Dialog implements
        View.OnClickListener {

    public Context context;
    private PassengerBooking passengerBooking;
    private PublishSubject<PassengerBooking> publishSubject = PublishSubject.create();
    private FirebaseAuth firebaseAuth;
    private UserService userService;
    private String community;
    private String fromOrTo;
    private TextView textDrivenBy;
    private View layutDrivenBy;


    public DialogMarker(Context context, PassengerBooking passengerBooking, String community, String fromOrTo) {
        super(context);
        this.context = context;
        this.passengerBooking = passengerBooking;
        firebaseAuth = FirebaseAuth.getInstance();
        userService=new UserService();
        this.community=community;
        this.fromOrTo=fromOrTo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.passenger_marker_dialog);
        ImageView imageFacebook = (ImageView) findViewById(R.id.image_facebook);
        Button buttonAccept = (Button) findViewById(R.id.btn_accept_quota);
        Button buttonCancel = (Button) findViewById(R.id.btn_cancel_quota);
        TextView textName = (TextView) findViewById(R.id.txt_name);
        TextView textDateTime = (TextView) findViewById(R.id.txt_date_time);
        textDrivenBy = (TextView) findViewById(R.id.txt_driven);
        layutDrivenBy = (View) findViewById(R.id.layout_driven);

        if (passengerBooking != null) {

            if (passengerBooking.getName() != null) {
                textName.setText(passengerBooking.getName());
            }
            TextView textDescription = (TextView) findViewById(R.id.txt_description);
            if (passengerBooking.address != null) {
                textDescription.setText(passengerBooking.address);
            }
            if (!TextUtils.isEmpty(passengerBooking.getPhotoUri())) {
                Picasso.with(imageFacebook.getContext()).load(passengerBooking.getPhotoUri()).into(imageFacebook);
            }
            if(passengerBooking.getDatePass() != null && !passengerBooking.getDatePass().equals("")
                    && passengerBooking.getHourPass() != null && !passengerBooking.getHourPass().equals("")){
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyy");
                SimpleDateFormat simpleDateFormatHour = new SimpleDateFormat("HHmm");
                SimpleDateFormat simpleDateFormatStr = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat simpleDateFormatHourStr = new SimpleDateFormat("HH:mm");
                try {
                    Date date = simpleDateFormat.parse(passengerBooking.getDatePass());
                    Date hour = simpleDateFormatHour.parse(passengerBooking.getHourPass());

                    String dateStr = simpleDateFormatStr.format(date);
                    String hourStr = simpleDateFormatHourStr.format(hour);
                    textDateTime.setText("Cupo para: " + dateStr + " " + hourStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if(PassengerInfoRequest.STATUS_ACCEPTED.equalsIgnoreCase(passengerBooking.status)){
                getDriversRequestInfo(passengerBooking.getKey(),
                        passengerBooking.getDatePass(),
                        passengerBooking.getHourPass(),passengerBooking.getIntervalo(),community,fromOrTo);
                buttonAccept.setVisibility(View.GONE);
                buttonCancel.setVisibility(View.GONE);
            }else{
                buttonAccept.setVisibility(View.VISIBLE);
                buttonCancel.setVisibility(View.VISIBLE);
                textDrivenBy.setVisibility(View.GONE);
                layutDrivenBy.setVisibility(View.GONE);
            }
        }
        buttonAccept.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_accept_quota:
                publishSubject.onNext(passengerBooking);
                dismiss();
                break;
            case R.id.btn_cancel_quota:
                publishSubject.onNext(null);
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }

    public void subscribeToDialogEvent(DisposableObserver<PassengerBooking> observer) {
        publishSubject.subscribe(observer);
    }

    public void unsubscribeToDialogEvent() {
        publishSubject.onComplete();
    }

    /**
     *
     */
    //region MyDriverRequest
    public void getDriversRequestInfo(String uid,String dateD,String hourD, String intervalo,String community,String fromOrTo) {
        MyBookingPassengerService bookingPassengerService =new MyBookingPassengerService();
        ValueEventListener bookingPassengerListener = bookingPassengerService.
                getPassengerBookings(community, fromOrTo, dateD, hourD,intervalo, uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        processDriverInfoRequest(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Timber.e(databaseError.toString(), databaseError);
                    }
                });
    }

    private void processDriverInfoRequest(DataSnapshot dataSnapshot) {
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            try {
                DriverInfoRequest driverInfoRequest = snapshot.getValue(DriverInfoRequest.class);
                if (driverInfoRequest != null) {
                    if (!DriverInfoRequest.STATUS_CANCELED.equalsIgnoreCase(driverInfoRequest.status)) {
                        setDriverAditionalInfoQuery(snapshot.getKey(), driverInfoRequest);
                    }
                }
            } catch (DatabaseException e) {
                Timber.e(e.getMessage(), e);
            }
        }
    }

    private void setDriverAditionalInfoQuery(final String uid, final DriverInfoRequest driverInfoRequest) {
        userService.getUserByUid(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                processDriverAdditionalInfo(dataSnapshot, driverInfoRequest, uid);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void processDriverAdditionalInfo(DataSnapshot dataSnapshot, final DriverInfoRequest driverInfoRequest, final String uid) {
        try {
            User user = dataSnapshot.getValue(User.class);
            if (user != null) {
                if (PassengerInfoRequest.STATUS_ACCEPTED.equalsIgnoreCase(driverInfoRequest.status)) {
                    driverInfoRequest.setDriverPhone(user.phone);
                    driverInfoRequest.setDriverEmail(user.email);
                }
                driverInfoRequest.setDriverName(user.name);
                driverInfoRequest.setDriverPhotoUri(user.photo_uri);
                driverInfoRequest.setKey(uid);
                driverInfoRequest.setTokenPasajero(user.getTokenNotificacion());

                textDrivenBy.setText("Será transportado por: " + driverInfoRequest.getDriverName() + "\n¡Todos juntos estamos creando comunidad!");
                textDrivenBy.setVisibility(View.VISIBLE);
                layutDrivenBy.setVisibility(View.VISIBLE);
            }
        } catch (DatabaseException e) {
            Timber.e(e.getMessage(), e);
        }
    }
}