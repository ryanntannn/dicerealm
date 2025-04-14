package com.example.dicerealmandroid.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dicerealm.core.locations.Location;
import com.example.dicerealmandroid.R;

import java.util.ArrayList;
import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {

    private Location currentLocation;
    private List<Location> adjacentLocations = new ArrayList<>();


    public LocationAdapter() {}

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate your item layout and create the ViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_dropdown_layout, parent, false);
        return new LocationViewHolder(view); // Replace with actual implementation
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        Location adjacentLocation = this.adjacentLocations.get(position);

        holder.locationName.setText(adjacentLocation.getDisplayName());
        holder.locationDesc.setText(adjacentLocation.getDescription());
    }

    @Override
    public int getItemCount() {
        return this.adjacentLocations.size();
    }

    public void updateCurrentLocation(Location loc) {
        currentLocation = loc;
        notifyDataSetChanged();
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void updateAdjacentLocations(List<Location> locations) {
        adjacentLocations.clear();
        adjacentLocations.addAll(locations);
        notifyDataSetChanged();
    }




    static class LocationViewHolder extends RecyclerView.ViewHolder {
        TextView locationName, locationDesc;

        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);
            locationName = itemView.findViewById(R.id.locationName);
            locationDesc = itemView.findViewById(R.id.locationDesc);
        }
    }
}
