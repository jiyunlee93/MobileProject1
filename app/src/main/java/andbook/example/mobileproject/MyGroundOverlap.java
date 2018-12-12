package andbook.example.mobileproject;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import com.google.android.gms.maps.MapView;

public class MyGroundOverlap {

	/*@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {

	    super.draw(canvas, mapView, shadow);
	    Paint paint1 = new Paint();
	    Paint paint2 = new Paint();

	    paint1.setARGB(255, 255, 0, 0); // a, r, g, b
	    paint2.setARGB(255, 255, 0,0);

	    // 아래에서 사용된 아규먼트는 위도, 경도 정보(40.756054)에서 가운데 '.' 을 뺀 것
	    GeoPoint geoPoint = new GeoPoint(37517180,127041268);
	    Point pixPoint = new Point();
	    mapView.getProjection().toPixels(geoPoint, pixPoint); // 지리좌표를 화면상의 픽셀좌표로 변환

	    canvas.drawCircle(pixPoint.x, pixPoint.y, 10, paint1);
	    canvas.drawText("강남구청역", pixPoint.x-30, pixPoint.y + 30, paint2);
	}*/

}
