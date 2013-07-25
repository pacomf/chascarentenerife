module.exports = function(app){  

    var Establecimiento = require('../models/establecimiento');
    var InfoDatos = require('../models/info');
  
    //find establecimientos por municipios
    listEstablecimientosMunicipios = function(req, res) {  
        Establecimiento.find({municipio: req.params.municipio}, function(error, establecimientos) {
            res.send(establecimientos);  
        });
    };

    //find establecimientos por municipios
    listEstablecimientosTipo = function(req, res) {  
        Establecimiento.find({tipo: req.params.tipo}, function(error, establecimientos) {
            res.send(establecimientos);  
        });
    };

    listEstablecimientosGeo = function(req, res) {  
        Establecimiento.find({latitud: {$ne: "0"}}, function(error, establecimientos) {
            res.send(establecimientos);  
        });
    };

    listEstablecimientosNoGeo = function(req, res) {  
        Establecimiento.find({latitud: "0"}, function(error, establecimientos) {
            res.send(establecimientos);  
        });
    };

    listEstablecimientosArea = function(req, res){
    	Establecimiento.find({$and: [{latitud: {$gt: req.params.latmin, $lt: req.params.latmax}}, 
                           			 {longitud:{$gt: req.params.lngmin, $lt: req.params.lngmax}}
                           			]
                           	 }, function(error, establecimientos){
                           	 		res.send(establecimientos);
                           	 });
    }

    actualizarDatosGeo = function(req, res) {  
        InfoDatos.findOne({zona: req.params.zona}, function(error, info) {
            if (info.version !== req.params.version){
            	Establecimiento.find({latitud: {$ne: "0"}}, function(error, establecimientos) {
            		res.send(establecimientos);  
        		});
            } else{
            	res.send([]);
            }
        });
    };

    version = function(req, res) {  
        InfoDatos.findOne({zona: req.params.zona}, function(error, info) {
           	res.send(info.version);
        });
    };

    app.get('/actualizarDatosGeo/:zona/:version', actualizarDatosGeo);
    app.get('/infoDatos/:zona', version);  

    app.get('/establecimientosLoc/:municipio', listEstablecimientosMunicipios); 
    app.get('/establecimientosTipo/:tipo', listEstablecimientosTipo); 
    app.get('/establecimientosGeo', listEstablecimientosGeo);
    app.get('/establecimientosNoGeo', listEstablecimientosNoGeo); 
    app.get('/establecimientosArea/:latmin/:latmax/:lngmin/:lngmax', listEstablecimientosArea); 

} 