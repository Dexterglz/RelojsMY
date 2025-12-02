package com.example.myhealthlife.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myhealthlife.R;

import java.util.List;

public class DeviceAdapter extends ArrayAdapter<BluetoothDevice> {
    private Context context;
    private List<BluetoothDevice> devices;

    public DeviceAdapter(Context context, List<BluetoothDevice> devices) {
        super(context, R.layout.item_device, devices);
        this.context = context;
        this.devices = devices;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            // Inflar el layout por primera vez
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_device, parent, false);

            // Configurar el ViewHolder
            holder = new ViewHolder();
            holder.deviceName = convertView.findViewById(R.id.device_name);
            holder.deviceMac = convertView.findViewById(R.id.device_mac);
            holder.deviceRssi = convertView.findViewById(R.id.device_signal);
            holder.deviceImage = convertView.findViewById(R.id.device_image);

            convertView.setTag(holder);
        } else {
            // Reciclar la vista existente
            holder = (ViewHolder) convertView.getTag();
        }

        // Obtener el dispositivo actual
        BluetoothDevice device = devices.get(position);

        // Configurar los textos
        holder.deviceName.setText(device.getName());
        holder.deviceMac.setText(device.getMac());
        holder.deviceRssi.setText(device.getRssi()+" dBm");
        setDeviceImage(holder.deviceImage, device.getName());

        return convertView;
    }

    // Clase ViewHolder para mejorar el rendimiento
    private static class ViewHolder {
        TextView deviceName, deviceMac, deviceRssi;
        ImageView deviceImage;
    }

    // Método para actualizar la lista
    public void updateDevices(List<BluetoothDevice> newDevices) {
        devices.clear();
        devices.addAll(newDevices);
        notifyDataSetChanged();
    }

    public static void setDeviceImage(ImageView imageView, String deviceName) {

        if (deviceName != null) {
            if (deviceName.startsWith("ET")) {
                imageView.setImageResource(R.mipmap.device_watch);
            } else if (deviceName.startsWith("R")) {
                imageView.setImageResource(R.mipmap.device_ring);
            } else {
                // Imagen por defecto si no coincide
                imageView.setImageResource(R.mipmap.device_watch);
            }
        }
    }

}
