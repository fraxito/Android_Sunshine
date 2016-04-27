package com.example.xp.sunshine;



import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;


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
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class Prediccion extends Fragment {

    private ArrayAdapter<String> prediccionAdapter;

    public Prediccion() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ObtenJson j = new ObtenJson();
        j.execute();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_prediccion, container, false);
    }

    @Override
    public void onStart(){
        super.onStart();
        String [] datos = {
                "espere mientras se actualizan los datos"

        };

        ListView listaVista ;
        ArrayList<String> listadoPredicciones = new ArrayList <String>(Arrays.asList(datos));

        listaVista = (ListView) getActivity().findViewById(R.id.listadoPredicciones);
        // Este es el array adapter, necesita el activity como primer parámetro
        // , el tipo de listView como segundo parámetro y el array de datos
        // como tercer parámetro.
        prediccionAdapter = new ArrayAdapter<String>(
                this.getActivity(),
                android.R.layout.simple_list_item_1,
                listadoPredicciones );

        listaVista.setAdapter(prediccionAdapter);

    }







    class ObtenJson extends AsyncTask<String, Void, String[]> {

        private Exception exception;
        @Override
        protected String[] doInBackground(String... params) {

            //hago una petición de datos al API de OpenWeatherMap
            //me va a responder con un JSON con los datos
            HttpURLConnection direccionURL = null;
            BufferedReader lector = null;
            String prediccionJSON = "";
            try {
                String cadenaConexion = "http://api.openweathermap.org/data/2.5/forecast?q=Madrid,es&mode=json&units=metric&lang=es&appid=246af0c89d66b8f64a2772be17de73b8";
                URL url = new URL(cadenaConexion);

                direccionURL = (HttpURLConnection) url.openConnection();
                direccionURL.setRequestMethod("GET");
                direccionURL.connect();

                //leo el json que recibo de openweathermap y lo guardo en un StringBuffer
                InputStream entradaDatos = direccionURL.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (entradaDatos == null) {
                    //no hay datos que leer
                    return null;
                }

                lector = new BufferedReader(new InputStreamReader(entradaDatos));

                String linea;
                while ((linea = lector.readLine()) != null) {
                    buffer.append(linea + "\n");

                }
                prediccionJSON = buffer.toString();


            } catch (IOException e) {
                Log.v("miapp", "NOOOOOO");
            } finally {
                if (direccionURL != null) {
                    direccionURL.disconnect();
                }
            }

            try {
                Log.v("miapp", "SIIII");
                return getClimaDesdeJson(prediccionJSON);
            } catch (JSONException e) {
                Log.v("miapp", e.toString());
            }

            return null;

        }



        private String[] getClimaDesdeJson (String prediccionJson) throws JSONException{

            JSONObject miJson = new JSONObject(prediccionJson);
            JSONArray arrayDatosClima = miJson.getJSONArray("list");

            // 5 son los dias que puedo leer con la API gratuita
            String[] auxiliar = new String [5];

            String dia = "";
            String descripcion = "";
            String maxmin = "";
            long fechaHora;
            SimpleDateFormat fechaFormateada = new SimpleDateFormat("dd.MM.yyyy", new Locale("es", "ES"));
            for (int i=0; i < 5; i++){
                //vamos a usar el formato para la fila: DIA - DESCRIPCION - MAX/MIN
                JSONObject prediccionDiaria = arrayDatosClima.getJSONObject(i*6 +1);

                fechaHora = prediccionDiaria.getLong("dt")*1000;
                dia = fechaFormateada.format(fechaHora);

                // description is in a child array called "weather", which is 1 element long.
                JSONObject weatherObject = prediccionDiaria.getJSONArray("weather").getJSONObject(0);
                descripcion = weatherObject.getString("description");

                // Temperatures are in a child object called "temp".  Try not to name variables
                // "temp" when working with temperature.  It confuses everybody.
                JSONObject temperatureObject = prediccionDiaria.getJSONObject("main");
                double high = temperatureObject.getDouble("temp_max");
                double low = temperatureObject.getDouble("temp_min");
                double presion = temperatureObject.getDouble("pressure");

                maxmin = Math.round(high) + " / " + Math.round(low);

                auxiliar[i] = dia + "-" + descripcion + "-" + maxmin + "/" + presion;
                Log.v("miapp",  auxiliar[i]);
            }

            return auxiliar;


        }

        @Override
        	        protected void onPostExecute(String[] result) {
            	            if (result != null && prediccionAdapter!= null) {
                                prediccionAdapter.clear();
                	                for(String dayForecastStr : result) {
                                        prediccionAdapter.add(dayForecastStr);
                    	                }
                	                // New data is back from the server.  Hooray!
                	            }
            	        }
    }

}

