// User Class
var mongoose = require('mongoose'),  
    Schema = mongoose.Schema;  
  
var infoSchema = new Schema({  
    version: String,
    zona: String
});  
  
//Export the schema  
module.exports = mongoose.model('InfoDatos', infoSchema); 