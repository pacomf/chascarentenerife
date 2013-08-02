//Load app dependencies  
var express = require('express'),  
  mongoose = require('mongoose'), 
  path = require('path'),  
  http = require('http'),
  util = require('util'),
  csv = require('csv'),
  GooglePlaces = require('google-places'),
  request = require('request');

var app = express();  

//Configure: bodyParser to parse JSON data  
//           methodOverride to implement custom HTTP methods  
//           router to crete custom routes  
app.configure(function(){  
  app.set('views', __dirname + '/views');
  app.set('view engine', 'jade');
  app.use(express.favicon());
  app.use(express.logger('dev'));
  app.use(express.bodyParser());
  app.use(express.methodOverride());
  app.use(app.router);
  app.use(express.static(path.join(__dirname, 'public')));
});  
  
app.configure('development', function(){  
  app.use(express.errorHandler());  
});  
  
//Sample routes are in a separate module, just for keep the code clean  
routes = require('./routes/router')(app);  
  
//Connect to the MongoDB test database  
mongoose.connect('mongodb://localhost/odc');  
  
//Start the server  
http.createServer(app).listen(4815); 

//new cronJob('00 00 02 * * *', getCSV(), null, true, null);

// Init
var Establecimiento = require('./models/establecimiento');
var InfoDatos = require('./models/info');

InfoDatos.find({}, function(err, resultado){
  if (resultado.length === 0){
     var infod = new InfoDatos({version:"1", zona:"Tenerife"});
     infod.save();
  }
});

// Para convertir String de posicion a Double y poder utilizarlo en comparaciones de Area
/*Establecimiento.find({}, function(error, establecimientos) {
      console.log("Empece");
      establecimientos.forEach(function(est) {
          est.latitudn = parseFloat(est.latitud);
          est.longitudn = parseFloat(est.longitud);
          est.save();
      });
      console.log("Termine");
});*/

// Trabajando con el CSV
var getCSV = function(){
    console.log("A por el CSV!");
    request.get({uri: 'http://ckan.opendatacanarias.es/storage/f/2013-07-04T163440/CabTfe-11062013-RestTFe.csv', encoding: 'binary'}, function (error, response, body) {
      if (!error && response.statusCode == 200) {
          csv()
            .from(body, {delimiter:';'})
            .to.array(function(data, count){
                  var cabecera = true;
                  var nuevos=0;
                  data.forEach(function(dato) {
                      if (cabecera)
                        cabecera=false;
                      else {
                        var direccion = (dato[2] !== "NULL") ? dato[2] : null;
                        var nombre = (dato[0] !== "NULL") ? dato[0] : null;
                        var tipo = (dato[1] !== "NULL") ? dato[1] : null;
                        var municipio = (dato[8] !== "NULL") ? dato[8] : null;
                        Establecimiento.findOne({$and: [{direccion: direccion}, {nombre: nombre}, {tipo: tipo}, {municipio: municipio}]}, function(err, encontrado) {  
                            if ((encontrado === null) || (encontrado === undefined)){
                                var establecimiento = new Establecimiento({  
                                   nombre: (dato[0] !== "NULL") ? dato[0] : null, 
                                   tipo: (dato[1] !== "NULL") ? dato[1] : null,  
                                   direccion: (dato[2] !== "NULL") ? dato[2] : null, 
                                   numero: (dato[3] !== "NULL") ? dato[3] : null, 
                                   cp: (dato[4] !== "NULL") ? dato[4] : null, 
                                   latitud: ((dato[5] !== "NULL") && (dato[5].indexOf(",") !== -1)) ? dato[5].replace(",", ".") : "0",  
                                   longitud: ((dato[6] !== "NULL") && (dato[5].indexOf(",") !== -1)) ? dato[6].replace(",", ".") : "0", 
                                   latitudn: ((dato[5] !== "NULL") && (dato[5].indexOf(",") !== -1)) ? parseFloat(dato[5]) : 0,
                                   longitudn: ((dato[6] !== "NULL") && (dato[6].indexOf(",") !== -1)) ? parseFloat(dato[6]) : 0,
                                   municipio: (dato[8] !== "NULL") ? dato[8] : null,  
                                   plazas: (dato[9] !== "NULL") ? dato[9] : null, });  

                                establecimiento.save();
                                nuevos++;
                            }
                        }); 
                        
                      }
                  });
                  console.log("Fichero CSV Parseado!");
                  //buscarGeo_basico();
                }
          );
      }
    })   
}

//getCSV();

geolocalizarEstablecimiento = function (direccion, id){
    var places = new GooglePlaces('AIzaSyClBprsTLkbUoiFhHUtSLHpWt0GHc5vGF0');

    places.autocomplete({input: direccion}, function(err, response) {
        if (response.predictions.length > 0){
          console.log("autocomplete: ", response.predictions[0].description);
          places.details({reference: response.predictions[0].reference}, function(err, response) {
            Establecimiento.findOne({_id: id}, function (err, obj){
                 if ((obj !== null) && (obj !== undefined) && (response.result !== undefined)){
                    console.log("FIND!: "+response.result.geometry.location.lat+"-"+response.result.geometry.location.lng);
                    obj.latitud=response.result.geometry.location.lat;
                    obj.longitud=response.result.geometry.location.lng;
                    obj.save();
                 }
            });
          });
        } else {
          //console.log("No encontrado");
        }
    });
}

buscarGeo_basico = function () {
  Establecimiento.find({latitud: "0"}, function(err, encontrados) {
    encontrados.forEach(function (encontrado){
        if ((encontrado.latitud === "0") || (encontrado.latitud === null) || (encontrado.latitud === undefined)){
            var direccion = encontrado.direccion;
            geolocalizarEstablecimiento(direccion+" "+encontrado.municipio+" Tenerife Spain", encontrado._id);
        }
    });
    buscarGeo_intermedio();
  });
}

buscarGeo_intermedio = function () {
  Establecimiento.find({latitud: "0"}, function(err, encontrados) {
    encontrados.forEach(function (encontrado){
        if ((encontrado.latitud === "0") || (encontrado.latitud === null) || (encontrado.latitud === undefined)){
            var direccion = encontrado.direccion;
            if (direccion.indexOf("(") !== -1){
              direccion = direccion.replace(/\(.*\)/gi, "");
            }
            geolocalizarEstablecimiento(direccion+" "+encontrado.municipio+" Tenerife Spain", encontrado._id);
        }
    });
    buscarGeo_avanzado();
  });
}

buscarGeo_avanzado = function () {
  Establecimiento.find({latitud: "0"}, function(err, encontrados) {
    encontrados.forEach(function (encontrado){
      if ((encontrado.latitud === "0") || (encontrado.latitud === null) || (encontrado.latitud === undefined)){
          var direccion = encontrado.direccion;
          if (direccion.indexOf("(") !== -1){
            direccion = direccion.replace(/\(.*\)/gi, "");
          }
          if (direccion.indexOf(",") !== -1){
            direccion = direccion.replace(/,\s*(\d*).*/g, " $1");
          }
          geolocalizarEstablecimiento(direccion+" "+encontrado.municipio+" Tenerife Spain", encontrado._id);
      }
    });
    console.log("Fin");
  });
}

buscarGeo_basico();


