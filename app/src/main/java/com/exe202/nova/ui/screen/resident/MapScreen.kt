//package com.exe202.nova.ui.screen.resident
//
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import com.mapbox.geojson.Point
//import com.mapbox.maps.CameraOptions
//import com.mapbox.maps.MapInitOptions
//import com.mapbox.maps.MapView
//import com.mapbox.maps.plugin.attribution.attribution
//import com.mapbox.maps.plugin.logo.logo
//import com.mapbox.maps.plugin.scalebar.scalebar
//
//class MainActivity : ComponentActivity() {
//    private lateinit var mapView: MapView
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        // Create a map programmatically and set the initial camera
//        mapView = MapView(this, MapInitOptions(
//            this,
//            cameraOptions = CameraOptions.Builder()
//                .center(Point.fromLngLat(-98.0, 39.5))
//                .pitch(0.0)
//                .zoom(2.0)
//                .bearing(0.0)
//                .build(),
//        ))
//
//        // adjust the position of the scalebar, logo, and attribution overlays
//        mapView.scalebar.marginTop = 200f
//        mapView.logo.marginBottom = 140f
//        mapView.attribution.marginBottom = 140f
//
//
//        // Add the map view to the activity (you can also add it to other views as a child)
//        setContentView(mapView)
//    }
//}
