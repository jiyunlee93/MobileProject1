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
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;


public class MainActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMarkerClickListener{

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
    String allText="";
    String[] onlyName=null;
    String[] onlyAddress=null;
    String[] onlyNumber=null;
    Marker curMar=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DBHelper dbHelper = new DBHelper(getApplicationContext(), "LIKEDB.db", null, 1);
        dbHelper.delete();
        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment)fragmentManager.findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        //mapFragment.getMapAsync(this);
    }
    public void mOnClick(View v){
        //해당 주소 얻기
        CameraPosition carPos=mMap.getCameraPosition();
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
                //앞부분
                String address = list.get(0).toString().split("]")[0];
                //뒷부분
                address = address.split("\"")[1].trim();
                curAddress=address;
                tv.setText(curAddress);
                String[] address2 = address.split(" ");
                for(int i=0; i<address2.length;i++){
                    if(address2[1].trim().contains("경기도") || address2[1].trim().contains("경기")){
                        //경기도일 때
                        addr1="경기도";
                        switch(i){
                            case 2:
                                //시
                                addr2=address2[i].trim();
                                break;
                            case 3:
                                //구
                                addr3=address2[i].trim();
                                break;
                            case 4:
                                //시
                                addr4=address2[i].trim();
                                break;
                        }
                    }else{
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
                htmlPageUrl = "https://search.naver.com/search.naver?sm=top_hty&fbm=0&ie=utf8&query="+addr1+" "+addr2+" "+addr3+" "+addr4+" 어린이집"; //파싱할 홈페이지의 URL주소
                //System.out.println(list.get(0).toString());
                System.out.println("query:" + htmlPageUrl);

            }
        }
        //address 가지고 크롤링하기
        crawling(v);
        //주소값 가지고 위치 찍기
    }
    public void addToDB(View v){
        if(curMar!=null){
            //onCLicked 되어있을 때
            DBHelper dbHelper = new DBHelper(getApplicationContext(), "LIKEDB.db", null, 1);
            final Geocoder geocoder = new Geocoder(this);
            // 위도,경도 입력 후 변환 버튼 클릭
            List<Address> list = null;
            try {
                double d1 = curMar.getPosition().latitude;
                double d2 = curMar.getPosition().longitude;
                list = geocoder.getFromLocation(
                        d1, // 위도
                        d2, // 경도
                        10); // 얻어올 값의 개수
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("test", "입출력 오류 - 서버에서 주소변환시 에러발생");
            }
            //get Address again
            String _address=list.get(0).toString();
            _address=_address.split("0")[1];
            _address=_address.split("]")[0];
            _address=_address.replace("\"","").trim();
            dbHelper.insert(curMar.getTitle().trim(),curMar.getSnippet().trim(),_address);
            TextView tvv= (TextView)findViewById(R.id.text_crawling);
            tvv.setText(dbHelper.getResult());
        }
        return ;
    }
    public void backToMain(View v){
        setContentView(R.layout.activity_main);
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

        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String number= marker.getSnippet().replace("-","");
                Intent call = new Intent(Intent.ACTION_DIAL);
                call.setData(Uri.parse("tel:"+number));
                startActivity(call);
                //startActivity(new Intent("android.intent.action.DIAL",Uri.parse("Tel:"+number)));
            }
        });
       //{
       //    @Override
       //    public void onMapClick(LatLng point) {
       //        MarkerOptions mOptions = new MarkerOptions();

       //        // 마커 타이틀
       //        mOptions.title("마커 좌표");
       //        Double latitude = point.latitude; // 위도
       //        Double longitude = point.longitude; // 경도
       //        // 마커의 스니펫(간단한 텍스트) 설정
       //        mOptions.snippet(latitude.toString() + ", " + longitude.toString());
       //        // LatLng: 위도 경도 쌍을 나타냄
       //        mOptions.position(new LatLng(latitude, longitude));
       //        System.out.println("latitude:::::::::::::::::::::::::::::"+ latitude);
       //        System.out.println("longitude:"+ longitude);
       //        // 마커(핀) 추가
       //        map.addMarker(mOptions);
       //    }
       //});
    }
    @Override
    public boolean onMarkerClick(Marker marker){
        marker.showInfoWindow();
        curMar=marker;
        return true;
    }
    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... params) {
            String tmpName="";
            String tmpAddress="";
            allText="";
            int curT=0;
            try {
                Document doc = Jsoup.connect(htmlPageUrl).get();
                Elements titles= doc.select("ul.lst_map dl.info_area");
                onlyName = new String[titles.size()];
                onlyAddress = new String[titles.size()];
                onlyNumber = new String[titles.size()];
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
                                name=name.substring(0,index);
                                name=name+"유치원";
                            }
                        }
                    }
                    tmpName=name;
                    if(a.contains("열기")){
                        tmpAddress=a.split("열기")[1];
                        if(tmpAddress.contains("지번")){
                            //지번있는 경우 앞쪽으로
                            int dex2=tmpAddress.indexOf("지번");
                            String atmpAddress=tmpAddress.substring(0,dex2);
                            tmpAddress= atmpAddress;
                        }
                    }
                    else{
                        tmpAddress=a;
                    }
                    //얻은 주소 넣기
                    onlyName[curT]=tmpName.trim();
                    onlyAddress[curT]=tmpAddress.trim();
                    //번호 얻기
                    String number1=a.split("보내기")[1].trim();
                    String ad=(onlyAddress[curT]).split(" ")[0].trim();
                    System.out.println("이름:"+tmpName.trim());
                    System.out.println("주소:"+tmpAddress.trim());
                    System.out.println("번호:"+onlyNumber[curT]);
                    int index=number1.indexOf(ad);
                    if(index<1){
                        //전화번호없는경우
                        onlyNumber[curT]="";
                    }
                    else{
                        String number= number1.substring(0,index);
                        onlyNumber[curT]=number;
                    }
                    curT++;
                    allText=allText+tmpName+" : "+tmpAddress + "\n";
                    htmlContentInStringFormat += e.text().trim() + "\n";
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //textviewHtmlDocument.setText(htmlContentInStringFormat);
            //textView.setText(allText);
        }
    }
    public void crawling (View v){
        JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
        jsoupAsyncTask.execute();
    }
    public void getXYfromLocation(View v){
        //주소를 위도 경도로 변환
        //주소는 allText에 있다.
        //주소입력후 지도2버튼 클릭시 해당 위도경도값의 지도화면으로 이동
        List<Address> list = null;
        Geocoder geocoder = new Geocoder(this);

        for(int i=0; i<onlyName.length;i++){
            try {
                list = geocoder.getFromLocationName
                        (onlyAddress[i], // 지역 이름
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
                    mOptions.snippet(onlyNumber[i]);
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
        onlyNumber=null;
    }

}
