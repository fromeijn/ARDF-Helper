package nl.fromeijn.ardfhelper;

import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;
import com.orm.SugarRecord;

import java.util.Locale;

public class FoxLog extends SugarRecord {

    int fox;
    double lat;
    double lon;
    int angle;

    public FoxLog(){

    }

    public FoxLog(int fox, double lat, double lon, int angle){
        this.fox = fox;
        this.lat = lat;
        this.lon = lon;
        this.angle = angle;
    }

    public PolygonOptions FoxLogPolygon(){
        double uncertainty = 2.5; //in degrees, times 2 equals aperture

        double δ = 10000.0 / 6371000; // angular distance in radians
        double φ1 = Math.toRadians(lat);
        double λ1 = Math.toRadians(lon);

        double θ = Math.toRadians(angle + uncertainty);
        double φ2 = Math.asin(Math.sin(φ1) * Math.cos(δ) +
                Math.cos(φ1) * Math.sin(δ) * Math.cos(θ));
        double λ2 = λ1 + Math.atan2(Math.sin(θ)*Math.sin(δ)*Math.cos(φ1),
                Math.cos(δ)-Math.sin(φ1)*Math.sin(φ2));
        λ2 = (λ2+3*Math.PI) % (2*Math.PI) - Math.PI; // normalise to -180..+180°

        double latmax = Math.toDegrees(φ2);
        double lonmax = Math.toDegrees(λ2);

        θ = Math.toRadians(angle - uncertainty);
        φ2 = Math.asin(Math.sin(φ1) * Math.cos(δ) +
                Math.cos(φ1) * Math.sin(δ) * Math.cos(θ));
        λ2 = λ1 + Math.atan2(Math.sin(θ)*Math.sin(δ)*Math.cos(φ1),
                Math.cos(δ)-Math.sin(φ1)*Math.sin(φ2));
        λ2 = (λ2+3*Math.PI) % (2*Math.PI) - Math.PI; // normalise to -180..+180°

        double latmin = Math.toDegrees(φ2);
        double lonmin = Math.toDegrees(λ2);

        System.out.println(String.valueOf(lat));
        System.out.println(String.valueOf(lon));
        System.out.println(String.valueOf(latmax));
        System.out.println(String.valueOf(lonmax));
        System.out.println(String.valueOf(latmin));
        System.out.println(String.valueOf(lonmin));

        PolygonOptions zone = new PolygonOptions()
            .add(new LatLng(lat, lon), new LatLng(latmax, lonmax), new LatLng(latmin, lonmin), new LatLng(lat, lon));
            zone.strokeColor(Color.TRANSPARENT);
            zone.strokeWidth(0);

        switch (fox){
            case 1: zone.fillColor(Color.argb(50,255,0,0));
                break;
            case 2: zone.fillColor(Color.argb(50,0,255,0));
                break;
            case 3: zone.fillColor(Color.argb(50,255,0,255));
                break;
            case 4: zone.fillColor(Color.argb(50,0,0,255));
                break;
            case 5: zone.fillColor(Color.argb(50,0,0,0));
                break;
        }

        return zone;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "%d,%f,%f,%d", this.fox, this.lat, this.lon, this.angle);
    }
}
