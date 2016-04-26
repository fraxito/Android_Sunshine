package com.example.xp.sunshine;



import android.support.v4.app.Fragment;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 */
public class Prediccion extends Fragment {


    public Prediccion() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_prediccion, container, false);
    }

    @Override
    public void onStart(){
        super.onStart();
        String [] datos = {
                "Lun   25/04  - Soleado  - 24º/12º",
                "Mar   26/04  - Soleado  - 24º/12º",
                "Mie   27/04  - Soleado  - 24º/12º",
                "Jue   28/04  - Soleado  - 24º/12º",
                "Vie   29/04  - Soleado  - 24º/12º",
                "Sab   30/04  - Soleado  - 24º/12º",
                "Dom   01/05  - Soleado  - 24º/12º"
        };

        ListView listaVista ;
        ArrayList<String> listadoPredicciones = new ArrayList <String>(Arrays.asList(datos));


        listaVista = (ListView) getActivity().findViewById(R.id.listadoPredicciones);
        // Este es el array adapter, necesita el activity como primer parámetro
        // , el tipo de listView como segundo parámetro y el array de datos
        // como tercer parámetro.
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this.getActivity(),
                android.R.layout.simple_list_item_1,
                listadoPredicciones );

        listaVista.setAdapter(arrayAdapter);


        //hago una petición de datos al API de OpenWeatherMap
        //me va a responder con un JSON con los datos
        HttpURLConnection direccionURL = null;
        BufferedReader lector = null;
        String prediccionJSON = null;
        try{
            String cadenaConexion = "http://api.openweathermap.org/data/2.5/forecast?q=Madrid,es&mode=json&appid=246af0c89d66b8f64a2772be17de73b8";
            URL url = new URL(cadenaConexion);

            direccionURL = (HttpURLConnection) url.openConnection();
            direccionURL.setRequestMethod("GET");
            direccionURL.connect();

            //leo el json que recibo de openweathermap y lo guardo en un StringBuffer
            InputStream entradaDatos = direccionURL.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (entradaDatos == null){
                //no hay datos que leer
                return;
            }

            lector = new BufferedReader(new InputStreamReader(entradaDatos));

            String linea;
            while ((linea = lector.readLine()) != null){
                buffer.append(linea + "\n");
            }
            prediccionJSON = buffer.toString();


        }
        catch (IOException e){

        }
        finally {
            if (direccionURL != null){
                direccionURL.disconnect();
            }
        }



    }

}
