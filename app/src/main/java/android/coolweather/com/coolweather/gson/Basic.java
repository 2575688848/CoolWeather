package android.coolweather.com.coolweather.gson;


import com.google.gson.annotations.SerializedName;

/**
 * Created by Hasee on 2017/11/27.
 */

public class Basic {
    @SerializedName("city")
    public String cityName;
    @SerializedName("id")
    public String weatherId;
    public Update update;
public class Update{
        @SerializedName("loc")
    public String updateTime;
    }
}
