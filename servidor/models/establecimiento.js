// User Class
var mongoose = require('mongoose'),  
    Schema = mongoose.Schema;  
  
var establecimientoSchema = new Schema({  
    nombre: String,
    tipo: String, 
    direccion: String, 
    numero: String, 
    cp: Number, 
    latitud: String, 
    longitud: String, 
    municipio: String, 
    plazas: Number
});  
  
//Export the schema  
module.exports = mongoose.model('Establecimiento', establecimientoSchema); 