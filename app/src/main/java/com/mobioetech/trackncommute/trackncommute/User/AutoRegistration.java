package com.mobioetech.trackncommute.trackncommute.User;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.mobioetech.trackncommute.trackncommute.CircleTransform;
import com.mobioetech.trackncommute.trackncommute.R;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AutoRegistration extends AppCompatActivity {
    private static final String LOG = "ownerregistration";
    String vehicleType;

    @BindView(R.id.iv_vehicle_image)
    ImageView mIvVehicleImageView;

    @BindView(R.id.et_name)
    EditText mEtNameView;

    @BindView(R.id.et_phone)
    EditText mEtPhoneView;

    @BindView(R.id.et_vehicle)
    EditText mEtVehicleView;

    @BindView(R.id.et_vehicle_no)
    EditText mEtVehicleNoView;

    @BindView(R.id.bt_submit)
    TextView mBtSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_registration);
        ButterKnife.bind(this);
        vehicleType = getIntent().getStringExtra("vehicleType");
        Log.i(LOG,"vehicleType------->"+vehicleType);
        mEtPhoneView.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        mEtVehicleView.setText(vehicleType);
        switch(vehicleType){
            case "Auto Rikshaw":
                Picasso.get()
                    .load(String.valueOf(getResources().getDrawable(R.drawable.auto)))
                    .transform(new CircleTransform())
                    .into(mIvVehicleImageView);
                break;
            case "Taxi":
                break;
            case "She Taxi":
                break;
            case "Rent A Car":
                break;
            case "Bus":
                break;
        }


    }
}
