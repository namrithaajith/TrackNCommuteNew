package com.mobioetech.trackncommute.trackncommute.User;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobioetech.trackncommute.trackncommute.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ajithkp on 19/06/17.
 */

public class VehicletypesAdapterOwnerRegistration extends RecyclerView.Adapter<VehicletypesAdapterOwnerRegistration.ViewHolder> {

    private ArrayList<VehicleCategories> vehicle_categories;
    private Context context;
    private static final String LOG = "vehicletypesAdapter";

    public VehicletypesAdapterOwnerRegistration(Context context, ArrayList<VehicleCategories> vehicle_categories) {
        this.context = context;
        this.vehicle_categories = vehicle_categories;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vehiclecategories_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.i(LOG,"inside  onBindViewHolder");
        holder.tv_category.setText(vehicle_categories.get(position).getVehicle_category());
        String img_url = vehicle_categories.get(position).getVehicle_category_url();
        int resID = context.getResources().getIdentifier(img_url, "drawable", context.getPackageName());
        Picasso.get().load(resID)
                .into(holder.img_category);
        //leak found after adding this
        holder.mView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = null;
                switch(position){
                    case 0:

                        intent = new Intent(context,AutoRegistration.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Log.i(LOG,"inside auto onclick");
                        intent.putExtra("vehicleType",vehicle_categories.get(position).getVehicle_category());
                        context.startActivity(intent);
                            break;
                    case 1:new Intent(context,TaxiRegistration.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Log.i(LOG,"inside auto onclick");
                        context.startActivity(intent);
                        break;

                    case 2:new Intent(context,SheTaxiRegistration.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Log.i(LOG,"inside auto onclick");
                        context.startActivity(intent);
                        break;
                    case 3:new Intent(context,RentACarRegistration.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Log.i(LOG,"inside auto onclick");
                        context.startActivity(intent);
                        break;
                    case 4:new Intent(context,BusRegistration.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Log.i(LOG,"inside auto onclick");
                        context.startActivity(intent);
                        break;
                    default:
                        Log.i(LOG,"inside default onclick");
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return vehicle_categories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_category;
        ImageView img_category;
        public final View mView;
        public ViewHolder(View view) {
            super(view);
            mView = view;
            tv_category = (TextView)view.findViewById(R.id.tv_category);
            img_category = (ImageView)view.findViewById(R.id.img_category);
        }

    }
}
