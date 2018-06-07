package org.dyndns.gill_roxrud.frodeg.animalexchange.walk;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.dyndns.gill_roxrud.frodeg.animalexchange.AnimalExchangeApplication;
import org.dyndns.gill_roxrud.frodeg.animalexchange.AnimalExchangeDBHelper;
import org.dyndns.gill_roxrud.frodeg.animalexchange.GameState;
import org.dyndns.gill_roxrud.frodeg.animalexchange.R;
import org.dyndns.gill_roxrud.frodeg.animalexchange.logic.SyncQueueManager;
import org.dyndns.gill_roxrud.frodeg.animalexchange.walk.overlays.AnimalGiftOverlay;
import org.dyndns.gill_roxrud.frodeg.animalexchange.walk.overlays.MyLocationOverlay;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.CopyrightOverlay;
import org.osmdroid.views.overlay.ScaleBarOverlay;


public class MapFragment extends Fragment implements LocationListener, MapListener {

    private MapView mapView;


    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, null);
        mapView = view.findViewById(R.id.mapview);
        return view;
    }

    private void setHardwareAccelerationOff() {
        mapView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Configuration.getInstance().setDebugTileProviders(false);
        Configuration.getInstance().setDebugMode(false);

        mapView.addMapListener(this);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapView.setTilesScaledToDpi(true);

        mapView.getOverlays().add(new CopyrightOverlay(AnimalExchangeApplication.getContext()));
        mapView.getOverlays().add(new AnimalGiftOverlay());
        mapView.getOverlays().add(new MyLocationOverlay());
        mapView.getOverlays().add(new ScaleBarOverlay(mapView));

        setHasOptionsMenu(true);

        GameState.getInstance().loadPosition(mapView);
        EnableLocationUpdates();
    }

    @Override
    public void onPause() {
        GameState.getInstance().savePositionT(mapView);
        super.onPause();
        mapView.onPause();
        mapView.setUseDataConnection(false);
        DisableLocationUpdates();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        GameState gameState = GameState.getInstance();
        gameState.loadPosition(mapView);
        mapView.setUseDataConnection(gameState.getUseDataConnection());
        EnableLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        GameState gameState = GameState.getInstance();
        gameState.onPositionChangedT(this, location.getLongitude(), location.getLatitude());

        if (gameState.getSnapToCentre()) {
            GeoPoint position = new GeoPoint(location.getLatitude(), location.getLongitude());
            mapView.getController().setCenter(position);
        }
        mapView.postInvalidate();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }

    private void DisableLocationUpdates() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(this);
        }
    }

    private void EnableLocationUpdates() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (!locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(getContext(), "Failed to find a Location Provider (GPS)", Toast.LENGTH_LONG).show();
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                                   AnimalExchangeApplication.LOCATION_UPDATE_INTERVAL,
                                                   AnimalExchangeApplication.LOCATION_UPDATE_DISTANCE,
                                                   this);
        }
    }

    @Override
    public boolean onScroll(ScrollEvent event) {
        return false;
    }

    @Override
    public boolean onZoom(ZoomEvent event) {
        GameState gameState = GameState.getInstance();
        AnimalExchangeDBHelper db = gameState.getDB();

        boolean successful = true;
        SQLiteDatabase dbInTransaction = db.StartTransaction();
        try {
            db.SetDoubleProperty(dbInTransaction, AnimalExchangeDBHelper.PROPERTY_ZOOM_LEVEL, event.getZoomLevel());
        } catch (SQLException e) {
            successful = false;
            Toast.makeText(AnimalExchangeApplication.getContext(), "ERR: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        db.EndTransaction(dbInTransaction, successful);
        return true;
    }

    public void onScoreUpdated(final double speed) {
        TextView view;
        try {
            view = getView().findViewById(R.id.speed);
            view.setText(Double.toString(((int)(speed*10))/10.0));
        }
        catch(NullPointerException e) {}

        try {
            GameState gameState = GameState.getInstance();
            SyncQueueManager syncQueueManager = gameState.getSyncQueueManager();

            view = getView().findViewById(R.id.food);
            view.setText(Integer.toString((int)syncQueueManager.getFood()));
        } catch(NullPointerException e) {}
    }

}
