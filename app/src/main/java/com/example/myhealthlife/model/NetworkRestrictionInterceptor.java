package com.example.myhealthlife.model;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Response;

public class NetworkRestrictionInterceptor implements Interceptor {
    private NetworkRestrictionManager restrictionManager;

    public NetworkRestrictionInterceptor(NetworkRestrictionManager restrictionManager) {
        this.restrictionManager = restrictionManager;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (restrictionManager.isDataRestricted() && !restrictionManager.isWifiConnected()) {
            throw new IOException("Conexión de datos móviles restringida temporalmente");
        }

        return chain.proceed(chain.request());
    }
}
