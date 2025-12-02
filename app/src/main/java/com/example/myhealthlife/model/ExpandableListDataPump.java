package com.example.myhealthlife.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListDataPump {

    public static HashMap<String , List<String>> getData() {

        HashMap<String, List<String>> expandableListDetail = new HashMap<>();

        List<String> s_cardiov = new ArrayList<>();
        s_cardiov.add("Electrocardiograma (ECG)");
        s_cardiov.add("Ritmo Cardíaco");
        s_cardiov.add("Frecuencia Respiratoria");

        List<String> s_circulatorio = new ArrayList<>();
        s_circulatorio.add("Presión Arterial");
        s_circulatorio.add("Ritmo Cardíaco");
        s_circulatorio.add("Oxigenación");

        List<String> s_fragmentado = new ArrayList<>();
        s_fragmentado.add("Pasos Diarios");
        s_fragmentado.add("Ritmo Cardíaco");
        s_fragmentado.add("Frecuencia Respiratoria");

        List<String> metabolico = new ArrayList<>();
        metabolico.add("Pasos Diarios");
        metabolico.add("Ritmo Cardíaco");
        metabolico.add("Presión Arterial");

        List<String> infeccion = new ArrayList<>();
        infeccion.add("Temperatura");
        infeccion.add("Frecuencia Respiratoria");
        infeccion.add("Oxigenación");

        expandableListDetail.put("Sistema Cardiovascular",s_cardiov);
        expandableListDetail.put("Sistema Circulatorio",s_circulatorio);
        expandableListDetail.put("Sueño Fragmentado",s_fragmentado);
        expandableListDetail.put("Actividad y recuperación Metbólico",metabolico);
        expandableListDetail.put("Monitoreo de Infección:",infeccion);

        return expandableListDetail;
    }
}
