package android.coolweather.com.coolweather;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.coolweather.com.coolweather.db.City;
import android.coolweather.com.coolweather.db.County;
import android.coolweather.com.coolweather.db.Province;
import android.coolweather.com.coolweather.util.HttpUtil;
import android.coolweather.com.coolweather.util.Utility;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;
import org.litepal.exceptions.DataSupportException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Hasee on 2017/11/19.
 */

public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE=0;
    public static final int LEVEL_COUNTY=2;
    public static final int LEVEL_CITY=1;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button Backbutton;
    private ListView listView;
    private ArrayAdapter<String>adapter;
    private List<String>dataList=new ArrayList<>();
    /*
    省列表
     */
    private List<Province>provinceList;
    /*
    市列表
     */
    private List<City>cityList;
    /*
    县列表
     */
    private List<County>countyList;
    /*
    选中的省份
     */
    private Province selectedprovince;
    /*
    选中的市
     */
    private City selectedcity;
    private County selectedcounty;
    /*
    当前选中的级别
     */
    private int currentLevel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.choose_area,container,false);
        titleText=(TextView)view.findViewById(R.id.title_text);
        Backbutton=(Button)view.findViewById(R.id.back_button);
        listView=(ListView)view.findViewById(R.id.list_view);
        adapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedsavedInstanceState){
        super.onActivityCreated(savedsavedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel==LEVEL_PROVINCE){
                    selectedprovince=provinceList.get(position);
                    queryCities();
                }else if(currentLevel==LEVEL_CITY){
                    selectedcity=cityList.get(position);
                    queryCounties();
                }
            }
        });
        Backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentLevel==LEVEL_COUNTY){
                    queryCities();

                }else if(currentLevel==LEVEL_CITY){
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }
/*
查询省内的市
 */
    private void queryCities() {
        titleText .setText(selectedprovince.getProvinceName());
        Backbutton.setVisibility(View.VISIBLE);
        cityList=DataSupport.where("provinceid = ?",String.valueOf(selectedprovince.getId())).find(City.class);
        if(cityList.size()>0){
            dataList.clear();
            for(City city:cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_CITY;
        }else {
            int provinceCode=selectedprovince.getProvinceCode();
            String address="http://guolin.tech/api/china/"+provinceCode;
            queryFromServer(address,"city");
        }
    }
/*
查询市内的县
 */
    private void queryCounties() {
        titleText .setText(selectedcity.getCityName());
        Backbutton.setVisibility(View.VISIBLE);
        countyList=DataSupport.where("cityid = ?",String.valueOf(selectedcity.getId())).find(County.class);
        if(countyList.size()>0){
            dataList.clear();
            for(County county:countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_COUNTY;
        }else {
            int provinceCode=selectedprovince.getProvinceCode();
            int citycode=selectedcity.getCityCode();
            String address="http://guolin.tech/api/china/"+provinceCode+"/"+citycode;
            queryFromServer(address,"county");
        }
    }
    /*
    查询全国的所有的省
     */
    private void queryProvinces() {
        titleText.setText("中国");
        Backbutton.setVisibility(View.GONE);
        provinceList= DataSupport.findAll(Province.class);
        if(provinceList.size()>0){
            dataList.clear();
            for(Province province:provinceList){
                dataList.add(province.getProvinceName());


            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_PROVINCE;
        }else {
          String address="http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }
    }
    private void queryFromServer(String address,final String type){
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
             String responseText=response.body().string();
                boolean result=false;
                if("province".equals(type)){
                    result= Utility.handleProvinceResponse(responseText);
                }else if("city".equals(type)){
                    result=Utility.handleCityResponse(responseText,selectedprovince.getId());
                }else if("county".equals(type)){
                    result=Utility.handleCountyResponse(responseText,selectedcity.getId());
                }
                if(result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)){
                                queryProvinces();
                            }else if("city".equals(type)){
                                queryCities();
                            }else if("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
    }
    private void showProgressDialog(){
        if(progressDialog==null){
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载。。。");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    private void closeProgressDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }
}


















