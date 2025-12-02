package com.example.myhealthlife.fragments;

import static com.example.myhealthlife.model.AppUtils.prepararDatosGrafico;
import static com.example.myhealthlife.model.PrefsHelper.obtenerHistorial;
import static com.example.myhealthlife.model.TablaGlucemiaHelper.setGlucosaTabla;
import static com.example.myhealthlife.model.TimestampManager.contieneTimestamp;
import static com.example.myhealthlife.model.TimestampManager.obtenerTimestamps;

import static org.apache.commons.lang3.ClassUtils.getPackageName;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myhealthlife.R;
import com.example.myhealthlife.activities.DiagnosisResultsActivity;
import com.example.myhealthlife.model.DataPoint;
import com.example.myhealthlife.model.HistoryData;
import com.example.myhealthlife.model.TablaGlucemiaHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HealthFragment extends Fragment {
    public HealthFragment() {}
    ListView listView;
    List<String> nombresPDFs;
    Long timestamp;
    List<File> pdfs;
    public static HealthFragment newInstance(String param1, String param2) {
        HealthFragment fragment = new HealthFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_health, container, false);
        View SCAN = view.findViewById(R.id.open_devices_button);

        /*if(!obtenerHistorial(view.getContext()).isEmpty()){
            HistoryData h = obtenerHistorial(getContext()).get(
                    obtenerHistorial(getContext()).size()-1
            );
            timestamp = h.timestamp;

            listView = view.findViewById(R.id.listViewPDFs);

            pdfs = obtenerPDFs(getContext());
            nombresPDFs = new ArrayList<>();
            for (File pdf : pdfs) {
                nombresPDFs.add(pdf.getName());
                Log.d("PDFs", "Archivo: " + pdf.getAbsolutePath());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    getContext(),
                    android.R.layout.simple_list_item_1,
                    nombresPDFs
            );
            listView.setAdapter(adapter);
            setListViewHeightBasedOnChildren(listView);
        }

        //Generar Diagnóstico
        SCAN.setOnClickListener(new View.OnClickListener() {
            //Obtener lista filtrada del periodo de hoy
            ArrayList<HistoryData> historialCompleto = obtenerHistorial(getContext());
            ArrayList<DataPoint> dataPoints = prepararDatosGrafico(historialCompleto, "heartValue", "hoy");
            Integer historySize = dataPoints.size();

            @Override
            public void onClick(View v) {
                if(!obtenerHistorial(getContext()).isEmpty()){
                    if(contieneTimestamp(getContext(),timestamp) || historySize < 3){
                        //Toast.makeText(getContext(),"Registros: "+historySize,Toast.LENGTH_SHORT).show();
                        Toast.makeText(getContext(),R.string.health_diagnostico_no_diponible,Toast.LENGTH_SHORT).show();
                    }else{
                        openSCAN();
                    }
                }
            }
        });

        if (!obtenerHistorial(view.getContext()).isEmpty()) {
            listView.setOnItemClickListener((parent, v, position, id) -> {
                try {
                    // Asegúrate de que la lista 'pdfs' esté inicializada y sincronizada
                    if (pdfs == null || pdfs.isEmpty() || position < 0 || position >= pdfs.size()) {
                        Toast.makeText(getContext(), "No se encontró el archivo PDF.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    File pdfSeleccionado = pdfs.get(position);
                    if (pdfSeleccionado == null || !pdfSeleccionado.exists()) {
                        Toast.makeText(getContext(), "El archivo no existe o fue eliminado.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Construir URI con FileProvider
                    Uri uri = FileProvider.getUriForFile(
                            requireContext(),
                            requireContext().getPackageName() + ".provider",
                            pdfSeleccionado
                    );

                    // Crear intent para abrir el PDF
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "application/pdf");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                    // Verificar si hay app que pueda abrir PDF
                    if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
                        startActivity(intent);
                    } else {
                        Toast.makeText(getContext(), R.string.health_no_tienes_app_pdf, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error al abrir el PDF.", Toast.LENGTH_SHORT).show();
                }
            });
        }*/


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
    private void openSCAN() {
        Intent intent = new Intent(requireActivity(), DiagnosisResultsActivity.class);
        startActivity(intent);
    }
    public List<File> obtenerPDFs(Context context) {
        File directorio = requireContext().getExternalFilesDir(null);  // mismo directorio que usaste para guardar
        List<File> listaPDFs = new ArrayList<>();

        if (directorio != null && directorio.exists()) {
            File[] archivos = directorio.listFiles();

            if (archivos != null) {
                for (File archivo : archivos) {
                    if (archivo.getName().toLowerCase().endsWith(".pdf")) {
                        listaPDFs.add(archivo);
                    }
                }
            }
        }
        return listaPDFs;
    }
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) return;

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(
                    View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            totalHeight += listItem.getMeasuredHeight();
        }

        /*ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();*/
    }




}