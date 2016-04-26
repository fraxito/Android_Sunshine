package com.example.xp.sunshine;



import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;

import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import java.text.SimpleDateFormat;
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
/*        String [] datos = {
                "Lun   25/04  - Soleado  - 24º/12º",
                "Mar   26/04  - Soleado  - 24º/12º",
                "Mie   27/04  - Soleado  - 24º/12º",
                "Jue   28/04  - Soleado  - 24º/12º",
                "Vie   29/04  - Soleado  - 24º/12º",
                "Sab   30/04  - Soleado  - 24º/12º",
                "Dom   01/05  - Soleado  - 24º/12º"
        };*/



        String [] datos = cargaJson();


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


    }





    private String[] getClimaDesdeJson (String prediccionJson) throws JSONException{

        //por cada campo a extraer del Json declaro un String

        final String temperatura = "temp";
        final String maxima = "temp_max";
        final String minima = "temp_min";
        final String lista  = "list";

        JSONObject miJson = new JSONObject(prediccionJson);
        JSONArray arrayDatosClima = miJson.getJSONArray(lista);


        Time hoy = new Time();
        hoy.setToNow();

        //necesito saber el dia de hoy en formato "humano"

        int diaInicioJuliano = Time.getJulianDay(System.currentTimeMillis(), hoy.gmtoff);

        hoy = new Time();

        // 5 son los dias que puedo leer con la API gratuita
        String[] auxiliar = new String [6];

        String dia = "";
        String descripcion = "";
        String maxmin = "";

        for (int i=0; i < 6; i++){
            //vamos a usar el formato para la fila: DIA - DESCRIPCION - MAX/MIN
            JSONObject prediccionDiaria = arrayDatosClima.getJSONObject(i);
            long fechaHora = hoy.setJulianDay(diaInicioJuliano + i);

            SimpleDateFormat fechaFormateada = new SimpleDateFormat("EEE MMM dd");
            dia = fechaFormateada.format(fechaHora);


            auxiliar[i] = dia;
            Log.v("miapp", dia);
        }

        return auxiliar;


    }




    protected String[] cargaJson(){

        //hago una petición de datos al API de OpenWeatherMap
        //me va a responder con un JSON con los datos
        HttpURLConnection direccionURL = null;
        BufferedReader lector = null;
        String prediccionJSON = "";
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
                return null;
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

        try{
            return getClimaDesdeJson(prediccionJSON);
        }catch (JSONException e){

        }

        return null;

    }
}
