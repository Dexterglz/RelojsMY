package com.example.myhealthlife.model;

import android.content.Context;

import java.io.IOException;

public class NetworkOperationManager {
    private Context context;
    private NetworkRestrictionManager restrictionManager;
    private NetworkUtils networkUtils;

    public NetworkOperationManager(Context context) {
        this.context = context;
        this.restrictionManager = new NetworkRestrictionManager(context);
        this.networkUtils = new NetworkUtils(context);
    }

    public interface NetworkOperation<T> {
        T execute() throws Exception;
    }

    public interface NetworkOperationCallback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }

    public <T> void executeWithRestrictions(
            boolean mobileDataAllowed,
            NetworkOperation<T> operation,
            NetworkOperationCallback<T> callback) {

        new Thread(() -> {
            try {
                // Verificar si hay conexión
                if (!networkUtils.isNetworkAvailable()) {
                    callback.onError("Sin conexión a internet");
                    return;
                }

                // Verificar restricciones de datos móviles
                if (!mobileDataAllowed && !restrictionManager.isWifiConnected()) {
                    callback.onError("Se requiere conexión WiFi");
                    return;
                }

                // Verificar restricciones temporales
                if (restrictionManager.isDataRestricted() && !restrictionManager.canUseMobileData()) {
                    callback.onError("Uso de datos móviles restringido temporalmente");
                    return;
                }

                T result = operation.execute();
                callback.onSuccess(result);

            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        }).start();
    }

    public NetworkRestrictionManager getRestrictionManager() {
        return restrictionManager;
    }

    public NetworkUtils getNetworkUtils() {
        return networkUtils;
    }
}
