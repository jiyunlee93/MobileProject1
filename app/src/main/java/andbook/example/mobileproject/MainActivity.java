package andbook.example.mobileproject;

import android.app.FragmentManager;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
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
    private TextView textView;
    private String htmlContentInStringFormat="";
    String addr1="";
    String addr2="";
    String addr3="";
    String addr4="";
    String curAddress="";
    int cnt=0;
    String allText="";
    String[] onlyName=null;
    String[] onlyAddress=null;
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
        //해당 주소 얻기
        CameraPosition carPos=mMap.getCameraPosition();
        System.out.println("latitude:::::::::::::::::::::::::::::"+ carPos.target.latitude);
        System.out.println("longitude:"+ carPos.target.longitude);
        textviewHtmlDocument = (TextView)findViewById(R.id.text_crawling);
        textviewHtmlDocument.setMovementMethod(new ScrollingMovementMethod()); //스크롤 가능한 텍스트뷰로 만들기
        textView=(TextView) findViewById(R.id.text_crawling);
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
                //주소 파싱
                tv.setText(list.get(0).toString());
                System.out.println("----------------------------");
                //앞부분
                String address = list.get(0).toString().split("]")[0];
                //뒷부분
                address = address.split("\"")[1].trim();
                curAddress=address;
                tv.setText(curAddress);
                String[] address2 = address.split(" ");
                for(int i=0; i<address2.length;i++){
                    if(address2[1].trim()=="경기도"){
                        //경기도일 때
                        addr1="경기도";
                        switch(i){
                            case 2:
                                //시
                                addr2=address2[i].trim();
                                break;
                            case 3:
                                //시
                                addr3=address2[i].trim();
                                break;
                            case 4:
                                //시
                                addr4=address2[i].trim();
                                break;
                        }
                    }
                    else{
                        //광역시일때
                        switch(i){
                            case 1:
                                //시
                                addr1=address2[i].trim();
                                break;
                            case 2:
                                //시
                                addr2=address2[i].trim();
                                break;
                            case 3:
                                //시
                                addr3=address2[i].trim();
                                addr4="";
                                break;
                        }
                    }
                }
                System.out.println(list.get(0).toString());
                htmlPageUrl = "https://search.naver.com/search.naver?sm=top_hty&fbm=0&ie=utf8&query="+addr1+" "+addr2+" "+addr3+" "+addr4+" 어린이집"; //파싱할 홈페이지의 URL주소
            }
        }
        //address 가지고 크롤링하기
        crawling(v);
        //주소값 가지고 위치 찍기
        //getXYfromLocation();
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
            System.out.println("dollllllllllllllllllllllllllllllllllllllllllllln here");
            String tmpName="";
            String tmpAddress="";
            int curT=0;
            try {
                Document doc = Jsoup.connect(htmlPageUrl).get();
                Elements titles= doc.select("ul.lst_map dl.info_area");
                onlyName = new String[titles.size()];
                onlyAddress = new String[titles.size()];
                System.out.println("-------------------------------------------------------------");
                for(Element e: titles){
                    String a = e.text();
                    String name = a.split("보내기")[0];
                    if(name.contains("...")){
                        System.out.println("name= "+ name);
                        int index=0;
                        try{
                            index = name.lastIndexOf("어");
                        }catch(StringIndexOutOfBoundsException f){
                            f.printStackTrace();
                        }
                        System.out.println("index= "+ index);
                        if(index>2){
                            //긴 어린이집
                            name=name.substring(0,index-1);
                            name=name+"어린이집";
                        }else{
                            try{
                                index=name.lastIndexOf("유");
                            }catch(StringIndexOutOfBoundsException f2){
                                f2.printStackTrace();
                            }
                            if(index>2){
                                //긴 유치원
                                System.out.println("herere index::"+index);
                                name=name.substring(0,index);
                                name=name+"유치원";
                            }
                        }
                        System.out.println("AFtername= "+ name);
                    }
                    System.out.println("title: " + e.text());
                    System.out.println("이름 :"+name);
                    tmpName=name;
                    if(a.contains("열기")){
                        System.out.println("주소 :"+a.split("열기")[1]);
                        tmpAddress=a.split("열기")[1];
                    }
                    else{
                        System.out.println("주소 : "+a);
                        tmpAddress=a;
                    }
                    //얻은 주소 넣기
                    onlyName[curT]=tmpName;
                    onlyAddress[curT]=tmpAddress;
                    curT++;
                    allText=allText+tmpName+" : "+tmpAddress + "\n";
                    System.out.println("alltext= "+allText);
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
            textView.setText(allText);
        }
    }
    public void crawling (View v){
        System.out.println( (cnt+1) +"번째 파싱");
        JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
        jsoupAsyncTask.execute();
        System.out.println("hereeeeeeeeeeeaalltext"+allText);
    }
    public void getXYfromLocation(View v){
        //주소를 위도 경도로 변환
        //주소는 allText에 있다.
        // 주소입력후 지도2버튼 클릭시 해당 위도경도값의 지도화면으로 이동
        List<Address> list = null;
        Geocoder geocoder = new Geocoder(this);

        for(int i=0; i<onlyName.length;i++){
            try {
                list = geocoder.getFromLocationName
                        (onlyName[i], // 지역 이름
                                10); // 읽을 개수
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("test","입출력 오류 - 서버에서 주소변환시 에러발생");
            }
            if (list != null) {
                if (list.size() == 0) {
                    System.out.println("해당되는 주소 정보는 없습니다");
                    //tv.setText("해당되는 주소 정보는 없습니다");
                } else {
                    // 해당되는 주소로 인텐트 날리기
                    Address addr = list.get(0);
                    double lat = addr.getLatitude();
                    double lon = addr.getLongitude();
                    System.out.println("lati:"+lat + " //////// lon = "+lon);
                    String sss = String.format("geo:%f,%f", lat, lon);
                    System.out.println("name--"+onlyName[i]);
                    MarkerOptions mOptions = new MarkerOptions();
                    mOptions.title(onlyName[i]);
                    mOptions.snippet(onlyName[i]+"입니다");
                    mOptions.position(new LatLng(lat,lon));
                    mMap.addMarker(mOptions);

                    //Intent intent = new Intent(
                    //        Intent.ACTION_VIEW,
                    //        Uri.parse(sss));
                    //startActivity(intent);
                }
            }
        }
        //cur 얘네들 동적해제
        onlyName = null;
        onlyAddress = null;
    }

}
