package com.example.weatherapiapp;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WeatherDataService {

    Context context;
    String cityID;

    public WeatherDataService(Context context) {
        this.context = context;
    }


    public interface VolleyResponseListener {
        void onError(String message);

        void onResponse(String cityId);
    }

    public static final String QUERY_FOR_CITY_ID = "https://www.metaweather.com/api/location/search/?query=";
    public static final String QUERY_FOR_CITY_WEATHER_BY_ID= "https://www.metaweather.com/api/location/";

    public void getCityId(String cityName,VolleyResponseListener volleyResponseListener){

        String url = QUERY_FOR_CITY_ID + cityName;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                 cityID = " ";
                JSONArray jsonArray;
                try {
                    JSONObject cityInfo = response.getJSONObject(0);
                    cityID = cityInfo.getString("woeid");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

//                Toast.makeText(context,"City ID = " + cityID ,Toast.LENGTH_LONG).show();
                 volleyResponseListener.onResponse(cityID);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(context,"Error Occured",Toast.LENGTH_LONG).show();

                volleyResponseListener.onError("Something Wrong");
            }
        });
        MySingleton.getInstance(context).addToRequestQueue(request);
//        return cityID;
    }


    public interface ForeCastIDResponse {
        void onError(String message);

        void onResponse(List<WeatherReportModel> weatherReportModels);
    }



    public  void getForecastById(String cityId,ForeCastIDResponse foreCastIDResponse){

        List<WeatherReportModel> weatherReportModels = new ArrayList<>();
        String url = QUERY_FOR_CITY_WEATHER_BY_ID + cityId;
        // Get The JsonObject
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

//                Toast.makeText(context,response.toString(),Toast.LENGTH_SHORT).show();

                try {
                    JSONArray consolidated_Weather_list = response.getJSONArray("consolidated_weather");





                    for(int i=0;i<consolidated_Weather_list.length();i++) {
                        WeatherReportModel one_day_weather = new WeatherReportModel();
                        JSONObject first_day_of_Api = (JSONObject) consolidated_Weather_list.get(i);
                        one_day_weather.setId(first_day_of_Api.getInt("id"));
                        one_day_weather.setWeather_state_name(first_day_of_Api.getString("weather_state_name"));
                        one_day_weather.setWeather_state_abbr(first_day_of_Api.getString("weather_state_abbr"));
                        one_day_weather.setWind_direction_compass(first_day_of_Api.getString("wind_direction_compass"));
                        one_day_weather.setCreated(first_day_of_Api.getString("created"));
                        one_day_weather.setApplicable_date(first_day_of_Api.getString("applicable_date"));
                        one_day_weather.setMin_temp(first_day_of_Api.getLong("min_temp"));
                        one_day_weather.setMax_temp(first_day_of_Api.getLong("max_temp"));
                        one_day_weather.setThe_temp(first_day_of_Api.getLong("the_temp"));
                        one_day_weather.setWind_speed(first_day_of_Api.getLong("wind_speed"));
                        one_day_weather.setWind_direction(first_day_of_Api.getLong("wind_direction"));
                        one_day_weather.setAir_pressure(first_day_of_Api.getLong("air_pressure"));
                        one_day_weather.setHumidity(first_day_of_Api.getInt("humidity"));
                        one_day_weather.setVisibility(first_day_of_Api.getLong("visibility"));
                        one_day_weather.setPredictability(first_day_of_Api.getInt("predictability"));
                        weatherReportModels.add(one_day_weather);
                    }

                    foreCastIDResponse.onResponse(weatherReportModels);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });


                // Get The Property Called "Consolidated_Weather" which is an Array

                //Get Each Iteam in array and assign it to new weather report

        MySingleton.getInstance(context).addToRequestQueue(request);
    }

    public interface GetCityForecastByNameCallBack{
        void onError(String message);
        void onResponse(List<WeatherReportModel>weatherReportModels);

    }
    public void getForecastByName(String cityName,GetCityForecastByNameCallBack getCityForecastByNameCallBack){

        //Fetch The City Id Given The City Name

        getCityId(cityName, new VolleyResponseListener() {
            @Override
            public void onError(String message) {

            }

            @Override
            public void onResponse(String cityId) {

                getForecastById(cityId, new ForeCastIDResponse() {
                    @Override
                    public void onError(String message) {

                    }

                    @Override
                    public void onResponse(List<WeatherReportModel> weatherReportModels) {

                        // We Have The Weather Report
                        getCityForecastByNameCallBack.onResponse(weatherReportModels);
                    }
                });
            }
        });

        //Fetch The City Forecast Given The City Id
    }

}
