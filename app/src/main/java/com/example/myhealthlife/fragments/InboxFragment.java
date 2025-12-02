package com.example.myhealthlife.fragments;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myhealthlife.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class InboxFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<NotificationItem> notifications;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);

        recyclerView = view.findViewById(R.id.recyclerNotifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Crear lista temporal
        notifications = new ArrayList<>();
        loadDummyNotifications(); // luego lo reemplazas con datos reales

        adapter = new NotificationAdapter(notifications);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                NotificationItem deleted = notifications.get(position);

                notifications.remove(position);
                adapter.notifyItemRemoved(position);

                Snackbar.make(recyclerView, "Notificación eliminada", Snackbar.LENGTH_LONG)
                        .setAction("Deshacer", v -> {
                            notifications.add(position, deleted);
                            adapter.notifyItemInserted(position);
                        }).show();
            }
        };

        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);




        return view;
    }


    // Cargar datos temporales
    private void loadDummyNotifications() {
        notifications.add(new NotificationItem("Bienvenido!", "Gracias por usar la app", System.currentTimeMillis()));
        notifications.add(new NotificationItem("Oferta disponible", "Tienes un cupón de descuento", System.currentTimeMillis()));
        notifications.add(new NotificationItem("Alerta", "Hemos detectado un nuevo inicio de sesión", System.currentTimeMillis()));
        notifications.add(new NotificationItem("Alerta", "Hemos detectado un nuevo inicio de sesión", System.currentTimeMillis()));
        notifications.add(new NotificationItem("Alerta", "Hemos detectado un nuevo inicio de sesión", System.currentTimeMillis()));
        notifications.add(new NotificationItem("Alerta", "Hemos detectado un nuevo inicio de sesión", System.currentTimeMillis()));
    }
}
