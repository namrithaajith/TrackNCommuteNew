package com.mobioetech.trackncommute.trackncommute.Driver;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.mobioetech.trackncommute.trackncommute.R;
import com.mobioetech.trackncommute.trackncommute.User.MainActivityUser;

public class OwnerWaitingApproval extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_waiting_approval);
    }

    public void continueasuser(View view) {
        Intent intent = new Intent(OwnerWaitingApproval.this,MainActivityUser.class);
        startActivity(intent);
    }
}
