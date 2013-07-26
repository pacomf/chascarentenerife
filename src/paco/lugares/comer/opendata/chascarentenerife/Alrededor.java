package paco.lugares.comer.opendata.chascarentenerife;

import java.util.List;
import java.util.Locale;

import paco.lugares.comer.opendata.chascarentenerife.controllers.EstablecimientosController;
import paco.lugares.comer.opendata.chascarentenerife.location.MyLocation;
import paco.lugares.comer.opendata.chascarentenerife.location.MyLocation.LocationResult;
import paco.lugares.comer.opendata.chascarentenerife.models.Establecimiento;
import paco.lugares.comer.opendata.chascarentenerife.places.DetailsPlaceOne;
import paco.lugares.comer.opendata.chascarentenerife.places.FillPlace;
import paco.lugares.comer.opendata.chascarentenerife.server.IStandardTaskListener;
import paco.lugares.comer.opendata.chascarentenerife.server.Utilities;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.roscopeco.ormdroid.ORMDroidApplication;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Alrededor extends SherlockFragmentActivity {

	private GoogleMap mapa = null;
	private ProgressDialog pd;
	private Double lat, lng;
	private String place="";
	private Marker markerActual=null;
	private AutoCompleteTextView lugar = null;
	private DetailsPlaceOne geoLugar;
	private FillPlace buscarLugar;
	private ImageView myLocation, clearPlace;
	Menu _menu = null;
	private Marker marcador;
	Context ctx;
	private Thread thread;  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_alrededor);
		
		ActionBar ab = getSupportActionBar();
        ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_HOME_AS_UP);
		
		lugar = (AutoCompleteTextView) findViewById(R.id.editTextPlace);
		myLocation = (ImageView) findViewById(R.id.placeHere);
		clearPlace = (ImageView) findViewById(R.id.placeIcon);
		mapa = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		
		ctx = this;
		
		initActionBar();
		initMapa();
		initElements();
		
		moverMapaCenterTenerife();
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
        inflater.inflate(R.menu.menu_solo_info, menu);
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
        } else if (item.getItemId() == R.id.infoDetails) {
        	Intent myIntent = new Intent(this, Info.class);
            startActivity(myIntent);
        	return true;
        }
        return false;
    }
	
	private void moverMapaCenterTenerife(){
		LatLng go = new LatLng(28.212589603148185, -16.55430374335936);
		
		mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(go, 9));
	}
	
	private void initElements(){
		
		
		// Preparamos el adaptador usado para mostrar las propuestas de autocompletado, en este caso una lista
		final ArrayAdapter<String> adapterFrom = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
		adapterFrom.setNotifyOnChange(true);
		lugar.setAdapter(adapterFrom);
	
		lugar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				resetearMenu();
			}
		});

		lugar.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				geoLugar = new DetailsPlaceOne();
				geoLugar.setContext(arg1.getContext());
				thread=  new Thread(){
			        @Override
			        public void run(){
			            try {
			                synchronized(this){
			                    wait(700);
			                }
			            }
			            catch(InterruptedException ex){                    
			            }        
			            pd.dismiss();
				        if ((buscarLugar.referencesPlace.isEmpty()) || (buscarLugar.referencesPlace.get(lugar.getText().toString()) == null)){
				        	showToastError();
				        } else {
					        geoLugar.setListener(new PlaceToPointMap_TaskListener(lugar.getText().toString()));
							geoLugar.execute(buscarLugar.referencesPlace.get(lugar.getText().toString()));
							place=lugar.getText().toString();
				        }
			        }
			    };
			    pd = ProgressDialog.show(ctx, getResources().getText(R.string.procesando), getResources().getText(R.string.esperar));
		        pd.setIndeterminate(false);
		        pd.setCancelable(true);
			    thread.start();
			}
        });
		
		// Monitorizamos el evento de cambio en el campo a autocompletar para buscar las propuestas de autocompletado
		lugar.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// Calculamos el autocompletado
				if (count%3 == 1){
					adapterFrom.clear();
					// Ejecutamos en segundo plano la busqueda de propuestas de autocompletado
					buscarLugar = new FillPlace(adapterFrom, lugar, getBaseContext());
					buscarLugar.execute(lugar.getText().toString());
				}
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			public void afterTextChanged(Editable s) {}
		});
		
		clearPlace.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				lugar.setText("");
				mapa.clear();
				markerActual=null;
				place=null;
				lng=null;
				lat=null;
				resetearMenu();
			}
		});
	}
	
	public void showToastError(){
	    runOnUiThread(new Runnable() {
	        public void run()
	        {
	        	Toast.makeText(ctx, R.string.problemautocompletado, Toast.LENGTH_LONG).show();
	        }
	    });
	}
	
	private void initActionBar(){
		ActionBar ab = getSupportActionBar();
        ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_SHOW_HOME);
	}
	
	private void initMapa(){

		myLocation.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				resetearMenu();
				
				LocationResult locationResult = new LocationResult(){
				    @Override
				    public void gotLocation(Location location){
				    	try {
				    		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
				    		Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
							List<Address> addresses = geoCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
							String add = "";
							if (addresses.size() > 0){
							for (int i=0; i<addresses.get(0).getMaxAddressLineIndex();i++)
								add += addresses.get(0).getAddressLine(i) + " ";
							}
							LatLng go = new LatLng(location.getLatitude(), location.getLongitude());
			        		CameraPosition camPos = new CameraPosition.Builder()
				                .target(go)   //Centramos el mapa 
				                .zoom(16)         //Establecemos el zoom 
				                .tilt(45)         //Bajamos el punto de vista de la cÃ¡mara
				                .build();
			         
			        		CameraUpdate camUpd = CameraUpdateFactory.newCameraPosition(camPos);
			        		mapa.animateCamera(camUpd);
			        		mapa.clear();
		
			        		place = add;
							lng = location.getLongitude();
							lat = location.getLatitude();
			        		
			        		// Insertamos el Marcador
			        		markerActual = mapa.addMarker(new MarkerOptions()
			    	        	.position(new LatLng(lat, lng))
			    	        	.title(getResources().getString(R.string.milocalizacion))
			    	        	.draggable(true)
			    	        	.icon(BitmapDescriptorFactory.fromResource(R.drawable.miposicion)));
			        		
			        		pd.dismiss();
			        		pintarMarkers(Long.parseLong(String.format("%.7f",  lat).replace(",", "")), Long.parseLong(String.format("%.7f",  lng).replace(",", "")));
							
						} catch (Exception e) {
							pd.dismiss();
						}
				    	
				    }
				};
				pd = ProgressDialog.show(v.getContext(), getResources().getText(R.string.procesando), getResources().getText(R.string.location));
		        pd.setIndeterminate(false);
		        pd.setCancelable(true);
				MyLocation myLocation = new MyLocation();
				myLocation.getLocation(v.getContext(), locationResult);
			}
		});
		
		if (mapa != null){
			mapa.setOnMapClickListener(new OnMapClickListener() {
				
				@Override
				public void onMapClick(LatLng point) {
					
					resetearMenu();

				}
			});
			
			mapa.setOnMapLongClickListener(new OnMapLongClickListener() {
			    public void onMapLongClick(LatLng point) {
			    	resetearMenu();
			    	InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
	                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
			        Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
			        try {
						List<Address> addresses = geoCoder.getFromLocation(point.latitude, point.longitude, 1);
						String add = "";
						if (addresses.size() > 0){
						for (int i=0; i<addresses.get(0).getMaxAddressLineIndex();i++)
							add += addresses.get(0).getAddressLine(i) + " ";
						}
						lugar.setText(add);
		        		place = add;
						lng = point.longitude;
						lat = point.latitude;
						
						mapa.clear();
		        		
		        		// Insertamos el Marcador
		        		markerActual = mapa.addMarker(new MarkerOptions()
		    	        	.position(new LatLng(lat, lng))
		    	        	.title(getResources().getString(R.string.milocalizacion))
		    	        	.draggable(true)
		    	        	.icon(BitmapDescriptorFactory.fromResource(R.drawable.miposicion)));
		        		pintarMarkers(Long.parseLong(String.format("%.7f",  lat).replace(",", "")), Long.parseLong(String.format("%.7f",  lng).replace(",", "")));
					} catch (Exception e) {
					}
			        
			    }
			});
		}
		
		mapa.setOnMarkerDragListener(new OnMarkerDragListener() {
			
			@Override
			public void onMarkerDragStart(Marker marker) {
				marker.hideInfoWindow();
				marker.setTitle("");
				resetearMenu();
			}
			
			@Override
			public void onMarkerDragEnd(Marker marker) {
				LatLng newPosition = marker.getPosition();
				Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
				try {
					List<Address> addresses = geoCoder.getFromLocation(newPosition.latitude, newPosition.longitude, 1);
					String add = "";
					if (addresses.size() > 0){
					for (int i=0; i<addresses.get(0).getMaxAddressLineIndex();i++)
						add += addresses.get(0).getAddressLine(i) + " ";
					}
					mapa.clear();
					markerActual = mapa.addMarker(new MarkerOptions()
	    	        	.position(new LatLng(newPosition.latitude, newPosition.longitude))
	    	        	.title(getResources().getString(R.string.milocalizacion))
	    	        	.draggable(true)
	    	        	.icon(BitmapDescriptorFactory.fromResource(R.drawable.miposicion)));

					lugar.setText(add);
					place=add;
					lng = newPosition.longitude;
					lat = newPosition.latitude;
					pintarMarkers(Long.parseLong(String.format("%.7f",  lat).replace(",", "")), Long.parseLong(String.format("%.7f",  lng).replace(",", "")));
				} catch (Exception e) {
				}
			}
			
			@Override
			public void onMarkerDrag(Marker marker) {}
		});
		
		mapa.setOnMarkerClickListener(new OnMarkerClickListener() {
			
			@Override
			public boolean onMarkerClick(Marker marker) {
				
				if (marker.getTitle().equals(getResources().getString(R.string.milocalizacion))){
					return false;
				}

				marcador = marker;
				_menu.clear();
				MenuInflater inflater = getSupportMenuInflater();
		        inflater.inflate(R.menu.menu_establecimientos_mapa, _menu);
			        
				return false;
			}
		});
	}
	
	private void pintarMarkers(long lat, long lng){
		pd = ProgressDialog.show(ctx, getResources().getText(R.string.procesando), getResources().getText(R.string.esperar));
        pd.setIndeterminate(false);
        pd.setCancelable(true);
		for (Establecimiento es: EstablecimientosController.getCercanos(lat, lng, 500D)){
			// Insertamos el Marcador
			Double latitud = Double.valueOf(es.latitud)/10000000;
			Double longitud = Double.valueOf(es.longitud)/10000000;
    		mapa.addMarker(new MarkerOptions()
	        	.position(new LatLng(latitud, longitud))
	        	.title(paco.lugares.comer.opendata.chascarentenerife.controllers.Utilities.getCamelCase(es.tipo+" "+es.nombre))
	        	.icon(BitmapDescriptorFactory.fromResource(paco.lugares.comer.opendata.chascarentenerife.controllers.Utilities.getIconoTipo(this,  es.tipo))));
		}
		pd.dismiss();
	}
	
	private class PlaceToPointMap_TaskListener implements IStandardTaskListener {

        private String markerStr;
        
        public PlaceToPointMap_TaskListener(String markerStr) {
        	this.markerStr =  markerStr;
        }
        
        @Override
        public void taskComplete(Object result) {
        	if ((Boolean)result){
        		// Esconder teclado para que se vea la animación del mapa
        		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
        		// Recogemos la posicion que hemos pedido al Web Service de Google Place
        		lat = geoLugar.coordinates.get("lat");
        		lng = geoLugar.coordinates.get("lng");
        		if ((lat == null) || (lng == null))
        			return;
        		LatLng go = new LatLng(lat, lng);
        		
        		// Creamos la animación de movimiento hacia el lugar que hemos introducido
        		CameraPosition camPos = new CameraPosition.Builder()
	                .target(go)
	                .zoom(16)         //Establecemos el zoom en 17
	                .tilt(45)         //Bajamos el punto de vista de la cámara 70 grados
	                .build();
         
        		// Lanzamos el movimiento
        		CameraUpdate camUpd = CameraUpdateFactory.newCameraPosition(camPos);
        		mapa.animateCamera(camUpd);
        		
        		mapa.clear();
        		
        		// Insertamos el Marcador
        		markerActual = mapa.addMarker(new MarkerOptions()
    	        	.position(new LatLng(lat, lng))
    	        	.title(getResources().getString(R.string.milocalizacion))
    	        	.draggable(true)
    	        	.icon(BitmapDescriptorFactory.fromResource(R.drawable.miposicion)));
        		
        		pintarMarkers(Long.parseLong(String.format("%.7f",  lat).replace(",", "")), Long.parseLong(String.format("%.7f",  lng).replace(",", "")));
        	}
        }
	}
	
	private void resetearMenu(){
		marcador = null;

		_menu.clear();
		MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.menu_solo_info, _menu);
	}
	

}
