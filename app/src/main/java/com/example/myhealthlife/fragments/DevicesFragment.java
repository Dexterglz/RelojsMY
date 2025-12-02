package com.example.myhealthlife.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myhealthlife.R;
import com.example.myhealthlife.activities.DeviceScanActivity;

public class DevicesFragment extends Fragment {

    public DevicesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el layout
        View view = inflater.inflate(R.layout.fragment_devices, container, false);

        // Referenciar el botón
        View openDevicesButton = view.findViewById(R.id.open_devices_button);

        // Configurar el click listener
        if (openDevicesButton != null) {
            openDevicesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Acción al hacer click
                    openDevicesActivity();
                }
            });
        } else {
            // Esto te ayudará a depurar si el botón no se encuentra
            throw new RuntimeException("Button with ID open_devices_button not found in fragment_devices.xml");
        }

        return view;
    }

    private void openDevicesActivity() {
        Intent intent = new Intent(requireActivity(), DeviceScanActivity.class);
        startActivity(intent);

        // Uncomment if you want to add transition animations
        // requireActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
}