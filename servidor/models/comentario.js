// User Class
var mongoose = require('mongoose'),  
    Schema = mongoose.Schema;  
  
var comentarioSchema = new Schema({  
    usuario: String,
    fecha: Date, 
    valoracion: { type: Number, default: 0 }, 
    precio: { type: Number, default: 0 },
    recomendacion: String, 
    opinion: String,
    idestablecimiento: String
});  
  
//Export the schema  
module.exports = mongoose.model('Comentario', comentarioSchema); 