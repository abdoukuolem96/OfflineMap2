package com.example.offlinemap;

import java.io.File;
import java.util.Set;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.IRegisterReceiver;
import org.osmdroid.tileprovider.modules.ArchiveFileFactory;
import org.osmdroid.tileprovider.modules.IArchiveFile;
import org.osmdroid.tileprovider.modules.OfflineTileProvider;
import org.osmdroid.tileprovider.tilesource.FileBasedTileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_main);

        MapView mapView = findViewById(R.id.mapview);
        mapViewOtherData(mapView);
        mapView.getController().setZoom(12);
        mapView.getController().setCenter(new GeoPoint(35.1852695, -0.6506683));
    }

    //Own layer
//mapView.getOverlays (). add ();

//".zip" ". sqlite" ". mbtiles" ". gemf"


    public void mapViewOtherData(MapView mapView) {
        String strFilepath = Environment.getExternalStorageDirectory().getPath() + "/osmdroid/Sba.sqlite";
        Log.d("Offline", strFilepath);
        File exitFile = new File(strFilepath);
        String fileName = "Sba.sqlite";
        if (!exitFile.exists()) {
            Log.d("Offline", "Mefihech2");
            mapView.setTileSource(TileSourceFactory.MAPNIK);
        } else {
            fileName = fileName.substring(fileName.lastIndexOf(".") + 1);
            Log.d("Offline", fileName);
            if (fileName.length() == 0)
                return;
            if (ArchiveFileFactory.isFileExtensionRegistered(fileName)) {
                try {

                    OfflineTileProvider tileProvider = new OfflineTileProvider((IRegisterReceiver) new SimpleRegisterReceiver(this), new File[]{exitFile});
                    mapView.setTileProvider(tileProvider);
                    Log.d("Offline", "Fiha2");
                    String source = "";
                    IArchiveFile[] archives = tileProvider.getArchives();
                    if (archives.length > 0) {
                        Log.d("Offline", "Fiha3");
                        Set<String> tileSources = archives[0].getTileSources();
                        if (!tileSources.isEmpty()) {
                            Log.d("Offline", "Fiha4");
                            source = tileSources.iterator().next();
                            mapView.setTileSource(FileBasedTileSource.getSource(source));
                        } else {
                            Log.d("Offline", "Fiha5");
                            mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
                        }

                    } else mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
                    Log.d("Offline", "Using" + exitFile.getAbsolutePath() + "" + source);
                    Toast.makeText(this, "Using" + exitFile.getAbsolutePath() + "" + source, Toast.LENGTH_LONG).show();
                    mapView.invalidate();
                    return;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                Toast.makeText(this, "did not have any files I can open! Try using MOBAC", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "dir not found!", Toast.LENGTH_LONG).show();
            }
        }
    }

}