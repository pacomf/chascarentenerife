package paco.lugares.comer.opendata.chascarentenerife;

import static com.roscopeco.ormdroid.Query.eql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import paco.lugares.comer.opendata.chascarentenerife.controllers.Utilities;
import paco.lugares.comer.opendata.chascarentenerife.location.MyLocation;
import paco.lugares.comer.opendata.chascarentenerife.location.MyLocation.LocationResult;
import paco.lugares.comer.opendata.chascarentenerife.models.Establecimiento;
import paco.lugares.comer.opendata.chascarentenerife.models.Valoracion;
import paco.lugares.comer.opendata.chascarentenerife.models.ValoracionEstablecimiento;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.*;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.roscopeco.ormdroid.Entity;
import com.roscopeco.ormdroid.ORMDroidApplication;

import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

public class MapaEstablecimientos extends SherlockFragmentActivity  {
	
	private GoogleMap mapa = null;
	private ProgressDialog pd;
	private Marker marcador;
	private Context ctx;
	Menu _menu = null;
	private TextView nombreMunicipio;
	Map <Marker, String> marcadorId = new HashMap<Marker, String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mapa_establecimientos);
		
		ActionBar ab = getSupportActionBar();
        ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_HOME_AS_UP);
		
		mapa = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		
		Bundle bundle = getIntent().getExtras();
		String municipio=bundle.getString("municipio");
		
		nombreMunicipio = (TextView) findViewById(R.id.nombreMunicipio);
		nombreMunicipio.setText(Utilities.getCamelCase(municipio));
		
		String nombre=bundle.getString("nombre");
		
		ctx = this;
		
		if (nombre == null)
			initMapaTodos(municipio);
		else
			initMapaUno();
		initClickMarker();
		
	}
	
	@Override
	public void onResume() { 
	    super.onResume(); 
	    ORMDroidApplication.initialize(this);
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		_menu=menu;
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.menu_solo_info, _menu);
        return true;
    }
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	if (item.getItemId() == android.R.id.home) {
            Intent myIntent = new Intent(this, Principal.class);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(myIntent);
            finish();
            return true;
        } else if (item.getItemId() == R.id.gotodirection) {
        	if (marcador != null){
				LocationResult locationResult = new LocationResult(){
				    @Override
				    public void gotLocation(Location location){
				    	try {
				    		Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
		    				Uri.parse("http://maps.google.com/maps?saddr="+location.getLatitude()+","+location.getLongitude()+"&daddr="+marcador.getPosition().latitude+","+marcador.getPosition().longitude));
				    		pd.dismiss();
		    				startActivity(intent);
						} catch (Exception e) {}
				    	pd.dismiss();
				    }
				};
				pd = ProgressDialog.show(ctx, getResources().getText(R.string.procesando), getResources().getText(R.string.location));
		        pd.setIndeterminate(false);
		        pd.setCancelable(true);
				MyLocation myLocation = new MyLocation();
				myLocation.getLocation(ctx, locationResult);
        	}
        	return true;
        } else if (item.getItemId() == R.id.valoraciones) {
        	if (!paco.lugares.comer.opendata.chascarentenerife.server.Utilities.haveInternet(this)){
				Toast.makeText(this, R.string.no_internet, Toast.LENGTH_LONG).show();
				return false;
			}
        	String idserver = marcadorId.get(marcador);
        	if(idserver != null){
        		Establecimiento establecimiento = Entity.query(Establecimiento.class).where(eql("idserver", idserver)).execute();
	        	if (establecimiento != null){
		        	Intent myIntent = new Intent(this, ComentariosEstablecimiento.class);
		        	myIntent.putExtra("idserver", establecimiento.idserver);
		        	myIntent.putExtra("nombre", establecimiento.nombre);
		        	myIntent.putExtra("tipo", establecimiento.tipo);
		        	myIntent.putExtra("direccion", establecimiento.direccion);
		        	Valoracion vE = ValoracionEstablecimiento.valoracion.get(establecimiento.idserver);
					myIntent.putExtra("media", ((vE != null) && (vE.media != null && (!vE.media.equals("0"))) ? vE.media : getResources().getString(R.string.valor_defecto)));
					myIntent.putExtra("precio", (vE != null) ? vE.precio : "0");
		            startActivity(myIntent);
	        	}
        	}
        	return true;
        } else if (item.getItemId() == R.id.infoDetails) {
        	Intent myIntent = new Intent(this, Info.class);
            startActivity(myIntent);
        	return true;
        }
        return false;
    }
	
	private void initClickMarker(){
		mapa.setOnMarkerClickListener(new OnMarkerClickListener() {
			
			@Override
			public boolean onMarkerClick(Marker marker) {

				marcador = marker;

				_menu.clear();
				MenuInflater inflater = getSupportMenuInflater();
		        inflater.inflate(R.menu.menu_establecimientos_mapa, _menu);
			        
				return false;
			}
		});
		
		mapa.setOnMapClickListener(new OnMapClickListener() {
			
			@Override
			public void onMapClick(LatLng point) {
				
				resetearMenu();

			}
		});
	}
	
	private void initMapaUno(){
		Bundle bundle = getIntent().getExtras();
		Long latitud=bundle.getLong("latitud");
		Long longitud=bundle.getLong("longitud");
		String tipo=bundle.getString("tipo");
		String nombre=bundle.getString("nombre");
		String media=bundle.getString("media");
		String precio=bundle.getString("precio");
		String idserver=bundle.getString("idserver");
		
		if ((latitud != null) && (longitud != null)){
			String subtitle = "";
			if (media.equals(getResources().getString(R.string.valor_defecto))){
				subtitle = getResources().getString(R.string.sinvaloraciones);
			} else {
				Double mediaD = Double.parseDouble(media);
				subtitle+=String.format("%.1f", mediaD)+" | "+Utilities.getPrecioStr(this, precio);
			}
			Marker m = mapa.addMarker(new MarkerOptions()
        	.position(new LatLng(Double.valueOf(latitud)/10000000, Double.valueOf(longitud)/10000000))
        	.title(paco.lugares.comer.opendata.chascarentenerife.controllers.Utilities.getCamelCase(tipo+" "+nombre))
        	.snippet(subtitle)
        	.icon(BitmapDescriptorFactory.fromResource(paco.lugares.comer.opendata.chascarentenerife.controllers.Utilities.getIconoTipo(this,  tipo))));
			
			marcadorId.put(m, idserver);
			
			LatLng go = new LatLng(Double.valueOf(latitud)/10000000, Double.valueOf(longitud)/10000000);
    		CameraPosition camPos = new CameraPosition.Builder()
                .target(go)   //Centramos el mapa 
                .zoom(18)         //Establecemos el zoom 
                .tilt(45)         //Bajamos el punto de vista de la cámara
                .build();
     
    		CameraUpdate camUpd = CameraUpdateFactory.newCameraPosition(camPos);
    		mapa.animateCamera(camUpd);
		} else {
			Toast.makeText(this, R.string.nomunicipios, Toast.LENGTH_LONG).show();
			return;
		}
	}
	
	private void initMapaTodos(String municipio){

		if (municipio == null){
			Toast.makeText(this, R.string.nomunicipios, Toast.LENGTH_LONG).show();
			return;
		}
		
		List<Establecimiento> establecimientos = Entity.query(Establecimiento.class).where(eql("municipio", municipio)).executeMulti();
		pintarMarkers(establecimientos);
	}
	
	private void moverMapaCenter(Double latitud, Double longitud, int zoom){
		LatLng go = new LatLng(latitud, longitud);
		
		mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(go, zoom));
	}
	
	private void pintarMarkers(List<Establecimiento> establecimientos){
		for (Establecimiento es: establecimientos){
			// Insertamos el Marcador
			Double latitud = Double.valueOf(es.latitud)/10000000;
			Double longitud = Double.valueOf(es.longitud)/10000000;
			Valoracion vE = ValoracionEstablecimiento.valoracion.get(es.idserver);
			String subtitle = "";
			if (vE != null){
				if ((vE.media.equals(getResources().getString(R.string.valor_defecto))) || (vE.media.equals("0"))){
					subtitle = getResources().getString(R.string.sinvaloraciones);
				} else {
					Double mediaD = Double.parseDouble(vE.media);
					subtitle+=String.format("%.1f", mediaD)+" | "+Utilities.getPrecioStr(this, vE.precio);
				}
			} else{
				subtitle = getResources().getString(R.string.sinvaloraciones);
			}
    		Marker m = mapa.addMarker(new MarkerOptions()
	        	.position(new LatLng(latitud, longitud))
	        	.title(paco.lugares.comer.opendata.chascarentenerife.controllers.Utilities.getCamelCase(es.tipo+" "+es.nombre))
	        	.snippet(subtitle)
	        	.icon(BitmapDescriptorFactory.fromResource(paco.lugares.comer.opendata.chascarentenerife.controllers.Utilities.getIconoTipo(this,  es.tipo))));
    		marcadorId.put(m, es.idserver);
		}
		if (establecimientos.size()>0)
			moverMapaCenter(Double.valueOf(establecimientos.get(0).latitud)/10000000, Double.valueOf(establecimientos.get(0).longitud)/10000000, 12);
	}
	
	private void resetearMenu(){
		marcador = null;

		_menu.clear();
		MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.menu_solo_info, _menu);
	}

}
