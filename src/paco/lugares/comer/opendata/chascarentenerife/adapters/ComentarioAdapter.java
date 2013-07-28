package paco.lugares.comer.opendata.chascarentenerife.adapters;


import static com.roscopeco.ormdroid.Query.eql;

import java.util.ArrayList;
import java.util.List;

import com.roscopeco.ormdroid.Entity;

import paco.lugares.comer.opendata.chascarentenerife.R;
import paco.lugares.comer.opendata.chascarentenerife.controllers.Utilities;
import paco.lugares.comer.opendata.chascarentenerife.models.Comentario;
import paco.lugares.comer.opendata.chascarentenerife.models.Establecimiento;
import paco.lugares.comer.opendata.chascarentenerife.models.ValoracionEstablecimiento;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ComentarioAdapter extends BaseAdapter {
	  protected Activity activity;
	  protected List<Comentario> items;
	         
	  public ComentarioAdapter(Activity activity, List<Comentario> items) {
	    this.activity = activity;
	    this.items = items;
	  }
	 
	  @Override
	  public int getCount() {
	    return (items == null) ? 0 : items.size();
	  }
	 
	  @Override
	  public Object getItem(int position) {
	    return items.get(position);
	  }
	 
	  @Override
	  public long getItemId(int position) {
	    return position;
	  }

	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    View vi=convertView;
	         
	    if(convertView == null) {
	      LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	      vi = inflater.inflate(R.layout.item_comentario, null);
	    }
	             
	    Comentario item = items.get(position);

	    ImageView v1 = (ImageView) vi.findViewById(R.id.v1);
	    ImageView v2 = (ImageView) vi.findViewById(R.id.v2);
	    ImageView v3 = (ImageView) vi.findViewById(R.id.v3);
	    ImageView v4 = (ImageView) vi.findViewById(R.id.v4);
	    ImageView v5 = (ImageView) vi.findViewById(R.id.v5);
	    
	    setValoracion(v1, v2, v3, v4, v5, item.valoracion);
	         
	    TextView precio = (TextView) vi.findViewById(R.id.precio);
	    precio.setText(Utilities.getPrecioStr(vi.getContext(), item.precio));
 
	    TextView fecha = (TextView) vi.findViewById(R.id.fecha);
	    fecha.setText(item.fecha);
	    
	    TextView usuario = (TextView) vi.findViewById(R.id.usuario);
	    usuario.setText(item.usuario);
	    
	    TextView recomendacion = (TextView) vi.findViewById(R.id.recomendacion);
	    recomendacion.setText(item.recomendacion);
	    
	    TextView opinion = (TextView) vi.findViewById(R.id.opinion);
	    opinion.setText(item.opinion);

	    return vi;
	  }
	  
	  // TODO: Completar esta funcion
	  private void setValoracion(ImageView v1, ImageView v2, ImageView v3, ImageView v4, ImageView v5, String valoracion){
		  
	  }

}