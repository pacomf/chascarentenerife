module.exports = function(app){  

    var Establecimiento = require('../models/establecimiento');
    var Comentario = require('../models/comentario');
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
    	Establecimiento.find({$and: [{latitudn: {$gte: req.params.latmin, $lte: req.params.latmax}}, 
                           			 {longitudn:{$gte: req.params.lngmin, $lte: req.params.lngmax}}
                           			]
                           	 }, function(error, establecimientos){
                           	 		res.send(establecimientos);
                           	 });
    }

    listEstablecimientosAreaValoracion = function(req, res){
        Establecimiento.find({$and: [{latitudn: {$gte: req.params.latmin, $lt: req.params.latmax}}, 
                                     {longitudn:{$gte: req.params.lngmin, $lt: req.params.lngmax}}
                                    ]
                             }, function(error, establecimientos){
                                    var respuesta = [];
                                    establecimientos.forEach(function(est) {
                                        var object = {idserver: est._id, media: est.media, precio: est.precio};
                                        respuesta.push(object);
                                    });
                                    res.send(JSON.stringify(respuesta)); 
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

    listEstablecimientosGeoMunicipio = function(req, res) {  
        Establecimiento.find({$and: [{latitud: {$ne: "0"}}, {municipio: replaceAll(req.params.municipio, "_", " ")}]}, function(error, establecimientos) {
            var respuesta = [];
            establecimientos.forEach(function(est) {
                var object = {idserver: est._id, media: est.media, precio: est.precio};
                respuesta.push(object);
            });
            res.send(JSON.stringify(respuesta));  
        });
    };

    function replaceAll( text, busca, reemplaza ){
      while (text.toString().indexOf(busca) != -1)
          text = text.toString().replace(busca,reemplaza);
      return text;
    }

    listComentariosEstablecimiento = function(req, res) {  
        Comentario.find({idestablecimiento: req.params.establecimiento}).sort({fecha:1}).limit(10).exec(function(error, comentarios) {
            res.send(comentarios);  
        });
    };

    nuevoComentario = function (req, res){
        var fecha = req.body.fecha;
        var valoracion = req.body.valoracion;
        var usuario = req.body.usuario;
        var precio = req.body.precio;
        var recomendacion = req.body.recomendacion;
        var opinion = req.body.opinion;

        Establecimiento.findOne({_id: req.params.idestablecimiento}, function(error, establecimiento) {
            if ((establecimiento !== null) && (establecimiento !== undefined)){
                var media = establecimiento.media;
                var precioMedio = establecimiento.precio;
                Comentario.find({idestablecimiento: req.params.idestablecimiento}, function(err, comentarios) { 
                     var count = comentarios.length; 
                     var mediaSum = media * count;
                     establecimiento.media = (mediaSum+valoracion)/(count+1);
                     establecimiento.precio = calcularModaPrecioComentarios(comentarios, precio);
                     establecimiento.save();
                     var comentarioNuevo = new Comentario({ usuario: usuario,
                                                            fecha: new Date(fecha), 
                                                            valoracion: valoracion, 
                                                            precio: precio,
                                                            recomendacion: recomendacion, 
                                                            opinion: opinion,
                                                            idestablecimiento: req.params.idestablecimiento});
                     comentarioNuevo.save();
                     res.send("ok");
                });  
            } else {
                res.send("err");
            }
        });
    }

    function calcularModaPrecioComentarios (comentarios, nuevoPrecio){
        var barato = 0;
        var normal = 0;
        var carillo = 0;
        var caro = 0;
        if  (nuevoPrecio === 1)
            barato++;
        else if (nuevoPrecio === 2)
            normal++;
        else if (nuevoPrecio === 3)
            carillo++;
        else if (nuevoPrecio === 4)
            caro++;
        comentarios.forEach(function(com) {
            if  (com.precio === 1)
                barato++;
            else if (com.precio === 2)
                normal++;
            else if (com.precio === 3)
                carillo++;
            else if (com.precio === 4)
                caro++;
        });
        return mayorValor(barato, normal, carillo, caro);
    }

    function mayorValor(barato, normal, carillo, caro){
        var max = Math.max(barato, normal, carillo, caro);
        if (max === barato)
            return 1;
        else if (max === normal)
            return 2;
        else if (max === carillo)
            return 3;
        else if (max === caro)
            return 4;
    }


    app.post('/comentario/:idestablecimiento', nuevoComentario);

    app.get('/actualizarDatosGeo/:zona/:version', actualizarDatosGeo);
    app.get('/infoDatos/:zona', version);  

    app.get('/establecimientosLoc/:municipio', listEstablecimientosMunicipios); 
    app.get('/establecimientosTipo/:tipo', listEstablecimientosTipo); 
    app.get('/establecimientosGeo', listEstablecimientosGeo);
    app.get('/establecimientosGeo/:municipio', listEstablecimientosGeoMunicipio);
    app.get('/establecimientosNoGeo', listEstablecimientosNoGeo);
    app.get('/establecimientosAreaValoracion/:latmin/:latmax/:lngmin/:lngmax', listEstablecimientosAreaValoracion); 
    app.get('/establecimientosArea/:latmin/:latmax/:lngmin/:lngmax', listEstablecimientosArea); 

    app.get('/comentarios/:establecimiento', listComentariosEstablecimiento); 
} 