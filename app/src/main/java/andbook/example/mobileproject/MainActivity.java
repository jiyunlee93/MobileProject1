package andbook.example.mobileproject;

import android.app.FragmentManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
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

import java.io.IOException;
import java.util.List;


public class MainActivity extends FragmentActivity implements OnMapReadyCallback{

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
    GoogleMap mMap;

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

}
