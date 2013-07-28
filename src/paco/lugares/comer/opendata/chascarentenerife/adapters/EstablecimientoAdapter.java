package paco.lugares.comer.opendata.chascarentenerife.adapters;


import static com.roscopeco.ormdroid.Query.eql;

import java.util.ArrayList;
import java.util.List;

import com.roscopeco.ormdroid.Entity;

import paco.lugares.comer.opendata.chascarentenerife.R;
import paco.lugares.comer.opendata.chascarentenerife.controllers.Utilities;
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

public class EstablecimientoAdapter extends BaseAdapter {
	  protected Activity activity;
	  protected List<Establecimiento> items;
	         
	  public EstablecimientoAdapter(Activity activity, List<Establecimiento> items) {
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
	      vi = inflater.inflate(R.layout.item_establecimiento, null);
	    }
	             
	    Establecimiento item = items.get(position);
	    
	    ValoracionEstablecimiento vE = Entity.query(ValoracionEstablecimiento.class).where(eql("idserver", item.idserver)).execute();
	    
	    if (vE != null){
	    	TextView nombre = (TextView) vi.findViewById(R.id.media);
	    	Double media = (Double.parseDouble(vE.media) != Double.NaN) ? Double.parseDouble(vE.media) : 0;
		    if (media != 0){
		    	int color;
		    	if (media < 3)
			    	color = Color.RED;
			    else if ((media >= 3) && (media < 4))
			    	color = Color.BLUE;
			    else
			    	color = Color.GREEN;
			    nombre.setTextColor(color);
			    nombre.setText(vE.media);
		    }
		    
		    View linea = (View) vi.findViewById(R.id.precioColor);
		    String precio = (vE.precio != null) ? vE.precio : "0";
		    if (precio.equals("1")){
		    	linea.setBackgroundColor(Color.GREEN);
		    } else if (precio.equals("2")){
		    	linea.setBackgroundColor(Color.BLUE);
		    } else if (precio.equals("3")){
		    	linea.setBackgroundColor(Color.YELLOW);
		    } else if (precio.equals("4")){
		    	linea.setBackgroundColor(Color.RED);
		    }
	    }
	    
	    ImageView icon = (ImageView) vi.findViewById(R.id.icon);
	    icon.setImageResource(Utilities.getIconoTipo(vi.getContext(), item.tipo));
	         
	    TextView nombre = (TextView) vi.findViewById(R.id.nombre);
	    nombre.setText(Utilities.getCamelCase(item.nombre));
	         
	    TextView direccion = (TextView) vi.findViewById(R.id.direccion);
	    direccion.setText(Utilities.getCamelCase(item.direccion));

	    return vi;
	  }

}
