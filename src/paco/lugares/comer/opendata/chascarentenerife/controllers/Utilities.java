package paco.lugares.comer.opendata.chascarentenerife.controllers;

import java.util.ArrayList;

import android.content.Context;

import paco.lugares.comer.opendata.chascarentenerife.R;

public class Utilities {
	
	//private static final int DEGREE_DISTANCE_AT_EQUATOR = 111329;
    /**
     * the radius of the earth in meters.
     */
    //private static final double EARTH_RADIUS = 6378137; //meters
    /**
     * the length of one minute of latitude in meters, i.e. one nautical mile in meters.
     */
    private static final double MINUTES_TO_METERS = 1852d;
    /**
     * the amount of minutes in one degree.
     */
    private static final double DEGREE_TO_MINUTES = 60d;


/**
     * This method extrapolates the endpoint of a movement with a given length from a given starting point using a given
     * course.
     *
     * @param startPointLat the latitude of the starting point in degrees, must not be {@link Double#NaN}.
     * @param startPointLon the longitude of the starting point in degrees, must not be {@link Double#NaN}.
     * @param course        the course to be used for extrapolation in degrees, must not be {@link Double#NaN}.
     * @param distance      the distance to be extrapolated in meters, must not be {@link Double#NaN}.
     *
     * @return the extrapolated point.
     */
    public static ArrayList<Long> extrapolate(final double startPointLat, final double startPointLon, final double course,
                                    final double distance) {
        //
        //lat =asin(sin(lat1)*cos(d)+cos(lat1)*sin(d)*cos(tc))
        //dlon=atan2(sin(tc)*sin(d)*cos(lat1),cos(d)-sin(lat1)*sin(lat))
        //lon=mod( lon1+dlon +pi,2*pi )-pi
        //
        // where:
        // lat1,lon1  -start pointi n radians
        // d          - distance in radians Deg2Rad(nm/60)
        // tc         - course in radians

        final double crs = Math.toRadians(course);
        final double d12 = Math.toRadians(distance / MINUTES_TO_METERS / DEGREE_TO_MINUTES);

        final double lat1 = Math.toRadians(startPointLat);
        final double lon1 = Math.toRadians(startPointLon);

        final double lat = Math.asin(Math.sin(lat1) * Math.cos(d12)
            + Math.cos(lat1) * Math.sin(d12) * Math.cos(crs));
        final double dlon = Math.atan2(Math.sin(crs) * Math.sin(d12) * Math.cos(lat1),
            Math.cos(d12) - Math.sin(lat1) * Math.sin(lat));
        final double lon = (lon1 + dlon + Math.PI) % (2 * Math.PI) - Math.PI;

        ArrayList<Long> ret =  new ArrayList<Long>();
        ret.add(Long.valueOf((String.format("%.7f", Math.toDegrees(lat)).replace(",", ""))));
        ret.add(Long.valueOf((String.format("%.7f", Math.toDegrees(lon)).replace(",", ""))));
        return ret;
    }

	public static ArrayList<Long> getXMeterAreaToPoint(long latitud, long longitud, Double meters){
		
		ArrayList<Long> ret = new ArrayList<Long>();
		
		Double lat=Double.valueOf(latitud)/10000000;
		Double lng=Double.valueOf(longitud)/10000000;
		
		// Derecha
		ArrayList<Long> calc = extrapolate(lat, lng, 0, meters);
		ret.add(calc.get(0)); //lat
		//ret.add(calc.get(1)); //lon
		
		// Superior
		calc = extrapolate(lat, lng, 90, meters);
		//ret.add(calc.get(0)); //lat
		ret.add(calc.get(1)); //lon
		
		// Izquierda
		calc = extrapolate(lat, lng, 180, meters);
		ret.add(calc.get(0)); //lat
		//ret.add(calc.get(1)); //lon
		
		// Debajo
		calc = extrapolate(lat, lng, 270, meters);
		//ret.add(calc.get(0)); //lat
		ret.add(calc.get(1)); //lon
		
		return ret;
	}
	
	public static int getIconoTipo(Context ctx, String tipo){
		if (tipo.equals(ctx.getResources().getString(R.string.arepera).toUpperCase())){
			return R.drawable.bocadillos;
		} else if (tipo.equals(ctx.getResources().getString(R.string.asador).toUpperCase())){
			return R.drawable.barbacoa;
		} else if (tipo.equals(ctx.getResources().getString(R.string.autobar).toUpperCase())){
			return R.drawable.autobar;
		} else if (tipo.equals(ctx.getResources().getString(R.string.bar).toUpperCase())){
			return R.drawable.bar;
		} else if (tipo.equals(ctx.getResources().getString(R.string.bar_piscina).toUpperCase())){
			return R.drawable.barjardin;
		} else if (tipo.equals(ctx.getResources().getString(R.string.barcafeteria).toUpperCase())){
			return R.drawable.cafe;
		} else if (tipo.equals(ctx.getResources().getString(R.string.boutique_del_pan).toUpperCase())){
			return R.drawable.panaderia;
		} else if (tipo.equals(ctx.getResources().getString(R.string.buffet).toUpperCase())){
			return R.drawable.buffet;
		} else if (tipo.equals(ctx.getResources().getString(R.string.cafe).toUpperCase())){
			return R.drawable.cafe;
		} else if (tipo.equals(ctx.getResources().getString(R.string.cafeteria).toUpperCase())){
			return R.drawable.cafe;
		} else if (tipo.equals(ctx.getResources().getString(R.string.cafeteriarestaurante).toUpperCase())){
			return R.drawable.restaurante;
		} else if (tipo.equals(ctx.getResources().getString(R.string.cervezeria).toUpperCase())){
			return R.drawable.bar;
		} else if (tipo.equals(ctx.getResources().getString(R.string.churrasqueria).toUpperCase())){
			return R.drawable.carne;
		} else if (tipo.equals(ctx.getResources().getString(R.string.churreria).toUpperCase())){
			return R.drawable.desayuno;
		} else if (tipo.equals(ctx.getResources().getString(R.string.ciber_cafe).toUpperCase())){
			return R.drawable.wifi;
		} else if (tipo.equals(ctx.getResources().getString(R.string.club).toUpperCase())){
			return R.drawable.villa;
		} else if (tipo.equals(ctx.getResources().getString(R.string.croissanteria).toUpperCase())){
			return R.drawable.desayuno;
		} else if (tipo.equals(ctx.getResources().getString(R.string.discoteca).toUpperCase())){
			return R.drawable.disco;
		} else if (tipo.equals(ctx.getResources().getString(R.string.dulceria).toUpperCase())){
			return R.drawable.heladeria;
		} else if (tipo.equals(ctx.getResources().getString(R.string.guachinche).toUpperCase())){
			return R.drawable.tipico;
		} else if (tipo.equals(ctx.getResources().getString(R.string.hamburgueseria).toUpperCase())){
			return R.drawable.comidarapida;
		} else if (tipo.equals(ctx.getResources().getString(R.string.heladeria).toUpperCase())){
			return R.drawable.heladeria;
		} else if (tipo.equals(ctx.getResources().getString(R.string.hotel_rural).toUpperCase())){
			return R.drawable.motel;
		} else if (tipo.equals(ctx.getResources().getString(R.string.karaoke).toUpperCase())){
			return R.drawable.musica;
		} else if (tipo.equals(ctx.getResources().getString(R.string.kiosco).toUpperCase())){
			return R.drawable.parallevar;
		} else if (tipo.equals(ctx.getResources().getString(R.string.marisqueria).toUpperCase())){
			return R.drawable.pescado;
		} else if (tipo.equals(ctx.getResources().getString(R.string.meson).toUpperCase())){
			return R.drawable.restaurante;
		} else if (tipo.equals(ctx.getResources().getString(R.string.parrilla).toUpperCase())){
			return R.drawable.barbacoa;
		} else if (tipo.equals(ctx.getResources().getString(R.string.pasteleria).toUpperCase())){
			return R.drawable.heladeria;
		} else if (tipo.equals(ctx.getResources().getString(R.string.piano_bar).toUpperCase())){
			return R.drawable.disco;
		} else if (tipo.equals(ctx.getResources().getString(R.string.pizzeria).toUpperCase())){
			return R.drawable.pizzaria;
		} else if (tipo.equals(ctx.getResources().getString(R.string.pub).toUpperCase())){
			return R.drawable.disco;
		} else if (tipo.equals(ctx.getResources().getString(R.string.restaurante).toUpperCase())){
			return R.drawable.restaurante;
		} else if (tipo.equals(ctx.getResources().getString(R.string.restuarantecafeteria).toUpperCase())){
			return R.drawable.restaurante;
		} else if (tipo.equals(ctx.getResources().getString(R.string.sala_de_fiestas).toUpperCase())){
			return R.drawable.disco;
		} else if (tipo.equals(ctx.getResources().getString(R.string.salon_recreativo).toUpperCase())){
			return R.drawable.wifi;
		} else if (tipo.equals(ctx.getResources().getString(R.string.self_service).toUpperCase())){
			return R.drawable.selfservice;
		} else if (tipo.equals(ctx.getResources().getString(R.string.snack).toUpperCase())){
			return R.drawable.selfservice;
		} else if (tipo.equals(ctx.getResources().getString(R.string.taberna).toUpperCase())){
			return R.drawable.bar;
		} else if (tipo.equals(ctx.getResources().getString(R.string.tasca).toUpperCase())){
			return R.drawable.restaurante;
		} else if (tipo.equals(ctx.getResources().getString(R.string.terraza).toUpperCase())){
			return R.drawable.terraza;
		} else if (tipo.equals(ctx.getResources().getString(R.string.terraza_de_verano).toUpperCase())){
			return R.drawable.terraza;
		} else if (tipo.equals(ctx.getResources().getString(R.string.ventorrillo).toUpperCase())){
			return R.drawable.vino;
		} else if (tipo.equals(ctx.getResources().getString(R.string.zumeria).toUpperCase())){
			return R.drawable.zumeria;
		} else {
			return R.drawable.restaurante;
		}
	}
	
	public static String getCamelCase(String init){
		if (init==null)
	        return "";

	    final StringBuilder ret = new StringBuilder(init.length());

	    for (final String word : init.split(" ")) {
	        if (!word.equals("")) {
	        	ret.append(Character.toUpperCase(word.charAt(0)));
	            ret.append(word.substring(1).toLowerCase());
	        }
	        if (!(ret.length()==init.length()))
	            ret.append(" ");
	    }

	    return ret.toString();
	}
	
	public static int getIconoMunicipio (Context ctx, String municipio){
		if (municipio.equals(ctx.getResources().getString(R.string.adeje).toUpperCase())){
			return R.drawable.adeje;
		} else if (municipio.equals(ctx.getResources().getString(R.string.arafo).toUpperCase())){
			return R.drawable.arafo;
		} else if (municipio.equals(ctx.getResources().getString(R.string.arona).toUpperCase())){
			return R.drawable.arona;
		} else if (municipio.equals(ctx.getResources().getString(R.string.arico).toUpperCase())){
			return R.drawable.arico;
		} else if (municipio.equals(ctx.getResources().getString(R.string.buenavista_del_norte).toUpperCase())){
			return R.drawable.buenavista;
		} else if (municipio.equals(ctx.getResources().getString(R.string.candelaria).toUpperCase())){
			return R.drawable.candelaria;
		} else if (municipio.equals(ctx.getResources().getString(R.string.el_rosario).toUpperCase())){
			return R.drawable.elrosario;
		} else if (municipio.equals(ctx.getResources().getString(R.string.el_sauzal).toUpperCase())){
			return R.drawable.elsauzal;
		} else if (municipio.equals(ctx.getResources().getString(R.string.fasnia).toUpperCase())){
			return R.drawable.fasnia;
		} else if (municipio.equals(ctx.getResources().getString(R.string.garachico).toUpperCase())){
			return R.drawable.garachico;
		} else if (municipio.equals(ctx.getResources().getString(R.string.granadilla).toUpperCase())){
			return R.drawable.granadilla;
		} else if (municipio.equals(ctx.getResources().getString(R.string.guia_de_isora).toUpperCase())){
			return R.drawable.guia;
		} else if (municipio.equals(ctx.getResources().getString(R.string.guimar).toUpperCase())){
			return R.drawable.guimar;
		} else if (municipio.equals(ctx.getResources().getString(R.string.icod_de_los_vinos).toUpperCase())){
			return R.drawable.icod;
		} else if (municipio.equals(ctx.getResources().getString(R.string.la_guancha).toUpperCase())){
			return R.drawable.laguancha;
		} else if (municipio.equals(ctx.getResources().getString(R.string.la_laguna).toUpperCase())){
			return R.drawable.lalaguna;
		} else if (municipio.equals(ctx.getResources().getString(R.string.la_matanza_de_acentejo).toUpperCase())){
			return R.drawable.lamatanza;
		} else if (municipio.equals(ctx.getResources().getString(R.string.la_orotava).toUpperCase())){
			return R.drawable.laorotava;
		} else if (municipio.equals(ctx.getResources().getString(R.string.la_victoria_de_acentejo).toUpperCase())){
			return R.drawable.lavictoria;
		} else if (municipio.equals(ctx.getResources().getString(R.string.los_realejos).toUpperCase())){
			return R.drawable.losrealejos;
		} else if (municipio.equals(ctx.getResources().getString(R.string.los_silos).toUpperCase())){
			return R.drawable.lossilos;
		} else if (municipio.equals(ctx.getResources().getString(R.string.puerto_de_la_cruz).toUpperCase())){
			return R.drawable.puertodelacruz;
		} else if (municipio.equals(ctx.getResources().getString(R.string.san_juan_de_la_rambla).toUpperCase())){
			return R.drawable.sanjuandelarambla;
		} else if (municipio.equals(ctx.getResources().getString(R.string.san_miguel).toUpperCase())){
			return R.drawable.sanmiguel;
		} else if (municipio.equals(ctx.getResources().getString(R.string.santa_cruz_de_tenerife).toUpperCase())){
			return R.drawable.santacruz;
		} else if (municipio.equals(ctx.getResources().getString(R.string.santa_ursula).toUpperCase())){
			return R.drawable.santaursula;
		} else if (municipio.equals(ctx.getResources().getString(R.string.santiago_del_teide).toUpperCase())){
			return R.drawable.santiagodelteide;
		} else if (municipio.equals(ctx.getResources().getString(R.string.tacoronte).toUpperCase())){
			return R.drawable.tacoronte;
		} else if (municipio.equals(ctx.getResources().getString(R.string.tanque).toUpperCase())){
			return R.drawable.eltanque;
		} else if (municipio.equals(ctx.getResources().getString(R.string.tegueste).toUpperCase())){
			return R.drawable.tegueste;
		} else if (municipio.equals(ctx.getResources().getString(R.string.vilaflor).toUpperCase())){
			return R.drawable.vilaflor;
		} else {
			return R.drawable.laorotava;
		}
	}
	
	public static String getPrecioStr(Context ctx, String precio){
		if (precio == null)
			return ctx.getResources().getString(R.string.sinprecio);
		else if (precio.equals("1")){
			return ctx.getResources().getString(R.string.barato);
		} else if (precio.equals("2")){
			return ctx.getResources().getString(R.string.normal);
		} else if (precio.equals("3")){
			return ctx.getResources().getString(R.string.carillo);
		} else if (precio.equals("4")){
			return ctx.getResources().getString(R.string.caro);
		} else {
			return ctx.getResources().getString(R.string.sinprecio);
		}
	}

}
