package com.example.myhealthlife.ui.home;
import static com.example.myhealthlife.R.id.heartFragment;
import static com.example.myhealthlife.domain.util.YCBKUtils.healthMonitoringFun;
import static com.yucheng.ycbtsdk.YCBTClient.connectState;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myhealthlife.domain.BatteryWorker;
import com.example.myhealthlife.ecg.EcgActicvity;
import com.example.myhealthlife.ui.HomeNav;
import com.example.myhealthlife.R;
import com.example.myhealthlife.domain.common.AnimatedCircularProgress;

import com.example.myhealthlife.activities.fragments.BluetoothDialogFragment;
import com.example.myhealthlife.ui.common.HealthViewModel;
import com.example.myhealthlife.ui.common.SyncViewModel;
import com.example.myhealthlife.views.TickerTextView;
import com.yucheng.ycbtsdk.Constants;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class HomeFragment extends Fragment {
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView steps_sport, kcal_sport, goalSteps_sport, distance_sport, last_update;
    private AnimatedCircularProgress circularProgress;
    private HomeInfoCardView ecg_card, sleep_card, heart_rate_card, blood_card, sugar_card,
            tempeture_card, tryg_card, acid_card;
    private HealthViewModel viewModel;
    private boolean isLoading = false;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        //Declarar vistas
        View view = inflater.inflate(
                R.layout.fragment_home,
                container,
                false
        );

        // Sport Views
        circularProgress = view.findViewById(R.id.circularProgress);
        steps_sport = view.findViewById(R.id.steps_sport);
        goalSteps_sport = view.findViewById(R.id.goalSteps_sport);
        kcal_sport = view.findViewById(R.id.kcal_sport);
        distance_sport = view.findViewById(R.id.distance_sport);
        // Health Views --
        last_update = view.findViewById(R.id.last_update);
        blood_card = view.findViewById(R.id.blood_card);
        heart_rate_card = view.findViewById(R.id.heart_rate_card);
        tempeture_card = view.findViewById(R.id.tempeture_card);
        sleep_card = view.findViewById(R.id.sleep_card);
        sugar_card = view.findViewById(R.id.sugar_card);
        tryg_card = view.findViewById(R.id.tryg_card);
        acid_card = view.findViewById(R.id.acid_card);
        ecg_card = view.findViewById(R.id.ecg_card);
        ecg_card.setOnClickOpenActivity(EcgActicvity.class);

        //Crear viewModel
        viewModel = new ViewModelProvider(this).get(HealthViewModel.class);


        sleep_card.setOnClickListener(v ->
                viewModel.onSleepClicked()
        );
        heart_rate_card.setOnClickListener(v ->
                viewModel.onHeartClicked()
        );
        blood_card.setOnClickListener(v ->
                viewModel.onBloodClicked()
        );
        sugar_card.setOnClickListener(v ->
                viewModel.onSugarlicked()
        );
        tempeture_card.setOnClickListener(v ->
                viewModel.onTempClicked()
        );
        tryg_card.setOnClickListener(v ->
                viewModel.onFatClicked()
        );
        acid_card.setOnClickListener(v ->
                viewModel.onAcidClicked()
        );


        viewModel.navigation().observe(
                getViewLifecycleOwner(),
                event -> {
                    HomeNav nav = event.getContentIfNotHandled();
                    if (nav == null) return;
                    NavController navController =
                            NavHostFragment.findNavController(this);

                    switch (nav) {
                        case SLEEP:
                            navController.navigate(R.id.sleepFragment);
                            break;
                        case HEART:
                            navController.navigate(heartFragment);
                            break;
                        case BLOOD:
                            navController.navigate(R.id.bloodFragment);
                            break;
                        case SUGAR:
                            navController.navigate(R.id.sugarFragment);
                            break;
                        case TEMP:
                            navController.navigate(R.id.tempetureFragment);
                            break;
                        case FAT:
                            navController.navigate(R.id.trigFragment);
                            break;
                        case URIC:
                            navController.navigate(R.id.uricAcidFragment);
                            break;
                    }
                }
        );

        //Contenido
        ecg_card.render(viewModel.ecgCardState());
        sleep_card.render(viewModel.sleepCardState());
        heart_rate_card.render(viewModel.heartCardState());
        sugar_card.render(viewModel.sugarCardState());
        tempeture_card.render(viewModel.tempCardState());
        blood_card.render(viewModel.bloodCardState());
        acid_card.render(viewModel.acidCardState());
        tryg_card.render(viewModel.trigCardState());

        //Valores
        viewModel.sleepValue().observe(getViewLifecycleOwner(), value -> {
            sleep_card.setValue(value);
        });
        viewModel.heartValue().observe(getViewLifecycleOwner(), value -> {
            heart_rate_card.setValue(value);
        });
        viewModel.sugarValue().observe(getViewLifecycleOwner(), value -> {
            sugar_card.setValue(value);
        });
        viewModel.tempValue().observe(getViewLifecycleOwner(), value -> {
            tempeture_card.setValue(value);
        });
        viewModel.bloodValue().observe(getViewLifecycleOwner(), value -> {
            blood_card.setValue(value);
        });
        viewModel.acidValue().observe(getViewLifecycleOwner(), value -> {
            acid_card.setValue(value);
        });
        viewModel.trigValue().observe(getViewLifecycleOwner(), value -> {
            tryg_card.setValue(value);
        });
        viewModel.stepsValue().observe(getViewLifecycleOwner(), value -> {
            steps_sport.setText(value);
        });
        viewModel.stepsPct().observe(getViewLifecycleOwner(), value -> {
            circularProgress.setProgressWithAnimation(value);
        });
        viewModel.caloriesValue().observe(getViewLifecycleOwner(), value -> {
            kcal_sport.setText(value);
        });
        viewModel.distanceValue().observe(getViewLifecycleOwner(), value -> {
            distance_sport.setText(value);
        });

        //Actualizar Tiempos
        viewModel.timeValue().observe(getViewLifecycleOwner(), value -> {
            sleep_card.setLastUpdate(getContext(), value);
            heart_rate_card.setLastUpdate(getContext(), value);
            blood_card.setLastUpdate(getContext(), value);
            tempeture_card.setLastUpdate(getContext(), value);
            sugar_card.setLastUpdate(getContext(), value);
            acid_card.setLastUpdate(getContext(), value);
            tryg_card.setLastUpdate(getContext(), value);
        });
        viewModel.lastUpdate().observe(getViewLifecycleOwner(), v -> {
            last_update.setText(v);
        });

        // SwipeRefreshLayout
        swipeRefreshLayout = view.findViewById(R.id.homeFragment);
        int density = (int) getResources().getDisplayMetrics().density;
        swipeRefreshLayout.setSlingshotDistance(10 * density);
        swipeRefreshLayout.setProgressViewOffset(
                false,
                0,
                40 * density
        );


        SyncViewModel viewModelSync =
                new ViewModelProvider(this).get(SyncViewModel.class);

        /*viewModelSync.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            swipeRefreshLayout.setRefreshing(isLoading);
        });*/

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Llamar al método de actualización
                if (isLoading){
                    Toast.makeText(requireContext(), getString(R.string.home_por_favor_espera), Toast.LENGTH_SHORT).show();
                }
                else {
                    if(updateFunctions()){
                        isLoading = true;
                        Toast.makeText(getContext(),getString(R.string.home_actualizando), Toast.LENGTH_SHORT).show();
                        new Handler().postDelayed(() -> {
                            isLoading = false;
                            swipeRefreshLayout.setRefreshing(false);
                            //last_update.setText(viewModel.lastUpdate());
                            viewModelSync.syncAll();
                        }, 15000); //  10 segundos
                    }
                    else{
                        Toast.makeText(requireContext(), getString(R.string.home_por_favor_conecte), Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            }
        });

        if(updateFunctions()){
            isLoading = true;
            Toast.makeText(getContext(),getString(R.string.home_actualizando), Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> {
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
                //last_update.setText(viewModel.lastUpdate());
                viewModelSync.syncAll();
            }, 15000); //  10 segundos
        }
        else{
            Toast.makeText(requireContext(), getString(R.string.home_por_favor_conecte), Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
        }


        //circularProgress.setProgressWithAnimation(lastSteps);
        //Bluetooth
        View blue_icon = view.findViewById(R.id.ble_icon);
        BluetoothDialogFragment dialog = new BluetoothDialogFragment();
        blue_icon.setOnClickListener(v -> {
            dialog.show(getChildFragmentManager(), "BluetoothDialogFragment");
        });

        TickerTextView ticker = view.findViewById(R.id.tickerText);
        ticker.setSpeed(100);      // px por segundo
        ticker.setPauseMillis(50);
        ticker.startScroll();

        PeriodicWorkRequest request =
                new PeriodicWorkRequest.Builder(
                        BatteryWorker.class,
                        15, TimeUnit.MINUTES
                ).build();

        WorkManager.getInstance(getContext()).enqueue(request);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        healthMonitoringFun(1,false);
        updateFunctions();
    }
    @Override
    public void onStart() {
        super.onStart();
        healthMonitoringFun(1,false);
    }
    @Override
    public void onStop() {
        super.onStop();
        healthMonitoringFun(0,false);
    }

    //------------------------------------------------------------------------------------------------
    private boolean updateFunctions() {
        if(connectState() != Constants.BLEState.ReadWriteOK)
            return false;
        viewModel.syncAllFromBle();
        return true;
    }
}