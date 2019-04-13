package com.mobioetech.trackncommute.trackncommute.User;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.mobioetech.trackncommute.trackncommute.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OwnerRegistrationActivity extends AppCompatActivity {
    private static final String LOG = "ownerregistration";

    @BindView(R.id.vehicletypes_rcv)
    RecyclerView vehicletypes_rcv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_registration);
        ButterKnife.bind(this);
        Log.i(LOG,"Owner registration activity...");
//        btnregister.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(OwnerRegistrationActivity.this, OwnerDetailRegistrationActivity.class);
//                startActivity(intent);
//            }
//        });

        vehicletypes_rcv.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),2);
        vehicletypes_rcv.setLayoutManager(layoutManager);

        ArrayList vehicleTypes = prepareData();
        VehicletypesAdapterOwnerRegistration adapter = new VehicletypesAdapterOwnerRegistration(getApplicationContext(),vehicleTypes);
        vehicletypes_rcv.setAdapter(adapter);
    }
    private ArrayList prepareData() {
        ArrayList vehicle_category = new ArrayList<>();
        String[] categoriesArray = getResources().getStringArray(R.array.vehicletypes);
        String[] urlsArray = getResources().getStringArray(R.array.vehicles_images_url);
        for(int i=0;i<categoriesArray.length;i++)
        {
            VehicleCategories tayCategory = new VehicleCategories();
            tayCategory.setVehicle_category(categoriesArray[i]);
            tayCategory.setVehicle_category_url(urlsArray[i]);
            Log.i(LOG,"Category---->"+categoriesArray[i]);

            vehicle_category.add(tayCategory);
        }
        return vehicle_category;
    }
}
