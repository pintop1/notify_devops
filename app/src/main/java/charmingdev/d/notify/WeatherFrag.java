package charmingdev.d.notify;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import github.vatsal.easyweather.Helper.TempUnitConverter;
import github.vatsal.easyweather.Helper.WeatherCallback;
import github.vatsal.easyweather.WeatherMap;
import github.vatsal.easyweather.retrofit.models.Weather;
import github.vatsal.easyweather.retrofit.models.WeatherResponseModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class WeatherFrag extends Fragment {

    View fragView;

    private EditText CITY;
    private Button SEARCH;
    private TextView TEMP,DESC,WIND,PRESSURE,HUMIDITY,SUNSET;
    private ImageView ICON;
    private ProgressBar LOADER;

    public WeatherFrag() {
        // Required empty public constructor
    }

    WeatherMap weatherMap;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragView = inflater.inflate(R.layout.fragment_weather, container, false);
        weatherMap = new WeatherMap(getActivity(), "256b23f13d7494c300ccc648210fc563");

        CITY = (EditText) fragView.findViewById(R.id.city);
        SEARCH = (Button) fragView.findViewById(R.id.search);
        TEMP = (TextView) fragView.findViewById(R.id.todayTemperature);
        WIND = (TextView) fragView.findViewById(R.id.todayWind);
        DESC = (TextView) fragView.findViewById(R.id.todayDescription);
        PRESSURE = (TextView) fragView.findViewById(R.id.todayPressure);
        HUMIDITY = (TextView) fragView.findViewById(R.id.todayHumidity);
        SUNSET = (TextView) fragView.findViewById(R.id.todayLocation);
        ICON = (ImageView) fragView.findViewById(R.id.todayIcon);
        LOADER = (ProgressBar) fragView.findViewById(R.id.loader);
        getByCity("lagos");

        SEARCH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = CITY.getText().toString().trim();
                if(TextUtils.isEmpty(city)){
                    CITY.setError("Please enter city");
                    CITY.requestFocus();
                }else {
                    getByCity(city);
                }
            }
        });

        return fragView;
    }

    public void getByCity(final String city){
        LOADER.setVisibility(View.VISIBLE);

        TEMP.setVisibility(View.GONE);
        WIND.setVisibility(View.GONE);
        DESC.setVisibility(View.GONE);
        PRESSURE.setVisibility(View.GONE);
        HUMIDITY.setVisibility(View.GONE);
        SUNSET.setVisibility(View.GONE);
        ICON.setVisibility(View.GONE);
        weatherMap.getCityWeather(city, new WeatherCallback() {
            @Override
            public void success(WeatherResponseModel response) {
                Weather weather[] = response.getWeather();
                String weatherMain = weather[0].getMain();
                String location = response.getName();
                String humidity= response.getMain().getHumidity();
                String pressure = response.getMain().getPressure();
                String windSpeed = response.getWind().getSpeed();
                String iconLink = weather[0].getIconLink();
                Double temperature = TempUnitConverter.convertToCelsius(response.getMain().getTemp());
                String result = String.format("%.2f", temperature);

                TEMP.setText(result + " °C");
                WIND.setText("Wind: " + windSpeed + " m/s");
                PRESSURE.setText("Pressure: " + pressure + " hpa");
                HUMIDITY.setText("Humidity: " + humidity + " %");
                SUNSET.setText(location.toUpperCase());
                Picasso.get().load(iconLink).into(ICON);

                LOADER.setVisibility(View.GONE);

                TEMP.setVisibility(View.VISIBLE);
                WIND.setVisibility(View.VISIBLE);
                DESC.setVisibility(View.VISIBLE);
                PRESSURE.setVisibility(View.VISIBLE);
                HUMIDITY.setVisibility(View.VISIBLE);
                SUNSET.setVisibility(View.VISIBLE);
                ICON.setVisibility(View.VISIBLE);

            }

            @Override
            public void failure(String message) {

            }
        });
    }



}
