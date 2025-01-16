package ro.pub.cs.systems.eim.practicaltest02v9;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivityV9 extends FragmentActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        Button openGoogleMapsButton = findViewById(R.id.open_google_maps_button);
        openGoogleMapsButton.setOnClickListener(v -> openGoogleMaps());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        LatLng bucharest = new LatLng(44.4268, 26.1025);
        googleMap.addMarker(new MarkerOptions().position(bucharest).title("Marker în București"));

        LatLng ghelmegioaia = new LatLng(44.4497, 23.9833);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ghelmegioaia, 10));
    }

    private void openGoogleMaps() {
        String geoUri = "geo:44.4497,23.9833?q=Ghelmegioaia, România";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
        intent.setPackage("com.google.android.apps.maps");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
