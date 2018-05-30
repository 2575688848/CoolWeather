package android.coolweather.com.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Hasee on 2017/11/27.
 */

public class Forecast {
    public String date;
    @SerializedName("tmp")
    public Temperature temperAature;
    @SerializedName("cond")
    public More more;
    public class Temperature{
        public String max;
        public String min;

    }
    public class More{
        @SerializedName("txt_d")
        public String info;
    }
}
