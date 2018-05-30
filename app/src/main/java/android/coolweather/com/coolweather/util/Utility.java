package android.coolweather.com.coolweather.util;

import android.coolweather.com.coolweather.db.City;
import android.coolweather.com.coolweather.db.County;
import android.coolweather.com.coolweather.db.Province;
import android.coolweather.com.coolweather.gson.Weather;
import android.text.TextUtils;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Hasee on 2017/11/19.
 */

public class Utility {
    /**
     *解析和处理服务器返回的省级数据；
     */
    public static boolean handleProvinceResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allProvinces = new JSONArray(response);
                for (int i = 0; i < allProvinces.length(); i++) {
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    /*
                    province类已经引射到litpal数据库中，最后province.save()就是在数据库中进行保存
                     */
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();

                }
                return true;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    /*
    解析和处理服务器返回的市级数据
     */
    public static boolean handleCityResponse(String response,int provinceId){
        if( !TextUtils.isEmpty(response)){
            try {
                JSONArray allcities=new JSONArray(response);
                for(int i=0;i<allcities.length();i++){
                    JSONObject cityObject=allcities.getJSONObject(i);
                    City city=new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
     /*
    解析和处理服务器返回的市级数据
     */
     public static boolean handleCountyResponse(String response,int cityId){
         if( !TextUtils.isEmpty(response)){
             try {
                 JSONArray allcounties=new JSONArray(response);
                 for(int i=0;i<allcounties.length();i++){
                     JSONObject countyObject=allcounties.getJSONObject(i);
                     County county=new County();
                     county.setCountyName(countyObject.getString("name"));
                     county.setCityId(cityId);
                     county.setWeatherId(countyObject.getString("weather_id"));
                     county.save();
                 }
                 return true;

             } catch (JSONException e) {
                 e.printStackTrace();
             }
         }
         return false;
     }
     /*
     将返回的JSON数据解析成Weather实体类
      */
     public static Weather handleWeatherResponse(String response){
         try{
             JSONObject jsonObject=new JSONObject(response);
             JSONArray jsonArray =jsonObject.getJSONArray("HeWeather");
             String weatherContent=jsonArray.getJSONObject(0).toString();
             return new Gson().fromJson(weatherContent,Weather.class);
         }catch (Exception e){
             e.printStackTrace();
         }
         return null;
     }

}

























