package andbook.example.mobileproject;

import android.app.FragmentManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;


public class MainActivity extends FragmentActivity implements OnMapReadyCallback{

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
    GoogleMap mMap;
    private String htmlPageUrl = "https://search.naver.com/search.naver?sm=top_hty&fbm=0&ie=utf8&query=서울특별시+중구+의주로2가+어린이집"; //파싱할 홈페이지의 URL주소
    private TextView textviewHtmlDocument;
    private String htmlContentInStringFormat="";
    int cnt=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment)fragmentManager.findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        //mapFragment.getMapAsync(this);


    }
    public void mOnClick(View v){

        CameraPosition carPos=mMap.getCameraPosition();
        System.out.println("latitude:::::::::::::::::::::::::::::"+ carPos.target.latitude);
        System.out.println("longitude:"+ carPos.target.longitude);
        textviewHtmlDocument = (TextView)findViewById(R.id.text_crawling);
        textviewHtmlDocument.setMovementMethod(new ScrollingMovementMethod()); //스크롤 가능한 텍스트뷰로 만들기

        final Geocoder geocoder = new Geocoder(this);
        // 위도,경도 입력 후 변환 버튼 클릭
        List<Address> list = null;
        try {
            double d1 = carPos.target.latitude;
            double d2 = carPos.target.longitude;
            list = geocoder.getFromLocation(
                    d1, // 위도
                    d2, // 경도
                    10); // 얻어올 값의 개수
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("test", "입출력 오류 - 서버에서 주소변환시 에러발생");
        }
        TextView tv = (TextView) findViewById(R.id.textAddress);
        if (list != null) {
            if (list.size()==0) {
                tv.setText("해당되는 주소 정보는 없습니다");
            } else {
                tv.setText(list.get(0).toString());
                System.out.println("----------------------------");
                System.out.println(list.get(0).toString());
            }
        }


    }
    @Override
    public void onMapReady(final GoogleMap map) {
        mMap=map;
        LatLng SEOUL = new LatLng(37.56, 126.97);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(SEOUL);
        markerOptions.title("서울");
        markerOptions.snippet("한국의 수도");
        map.addMarker(markerOptions);

        map.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
        map.animateCamera(CameraUpdateFactory.zoomTo(10));
        CameraPosition carPos=map.getCameraPosition();
        System.out.println("latitude:::::::::::::::::::::::::::::"+ carPos.target.latitude);
        System.out.println("longitude:"+ carPos.target.longitude);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                MarkerOptions mOptions = new MarkerOptions();

                // 마커 타이틀
                mOptions.title("마커 좌표");
                Double latitude = point.latitude; // 위도
                Double longitude = point.longitude; // 경도
                // 마커의 스니펫(간단한 텍스트) 설정
                mOptions.snippet(latitude.toString() + ", " + longitude.toString());
                // LatLng: 위도 경도 쌍을 나타냄
                mOptions.position(new LatLng(latitude, longitude));
                System.out.println("latitude:::::::::::::::::::::::::::::"+ latitude);
                System.out.println("longitude:"+ longitude);
                // 마커(핀) 추가
                map.addMarker(mOptions);
            }
        });
        // try{
        //     List<Address> mResultList=mGeoCoder.getFromLocation(
        //     )
        // }catch(IOException e){
        //     e.printStackTrace();
        //     System.out.println("주소 변환 실패");
        // }

    }

    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... params) {
            try {

                Document doc = Jsoup.connect(htmlPageUrl).get();


                ////테스트1
                //Elements titles= doc.select("div.news-con h1.tit-news");
//
                //System.out.println("-------------------------------------------------------------");
                //for(Element e: titles){
                //    System.out.println("title: " + e.text());
                //    htmlContentInStringFormat += e.text().trim() + "\n";
                //}
//
                ////테스트2
                //titles= doc.select("div.news-con h2.tit-news");
//
                //System.out.println("-------------------------------------------------------------");
                //for(Element e: titles){
                //    System.out.println("title: " + e.text());
                //    htmlContentInStringFormat += e.text().trim() + "\n";
                //}
//
                //테스트3
                Elements titles= doc.select("ul.lst_map dl.info_area");

                System.out.println("-------------------------------------------------------------");
                for(Element e: titles){
                    String a = e.text();
                    String name = a.split("보내기")[0];
                    if(name.contains("...")){
                        System.out.println("name= "+ name);
                        int index = name.lastIndexOf("어");
                        System.out.println("index= "+ index);
                        name=name.substring(0,index-1);
                        System.out.println("AFtername= "+ name);
                        name=name+"어린이집";
                    }
                    System.out.println("title: " + e.text());
                    System.out.println("이름 :"+name);
                    System.out.println("주소 :"+a.split("열기")[1]);
                    System.out.println("/////////////////////////////////////////");
                    htmlContentInStringFormat += e.text().trim() + "\n";
                }
                System.out.println("-------------------------------------------------------------");

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            textviewHtmlDocument.setText(htmlContentInStringFormat);
        }
    }
    public void crawling (View v){
        System.out.println( (cnt+1) +"번째 파싱");
        JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
        jsoupAsyncTask.execute();
    }

}
