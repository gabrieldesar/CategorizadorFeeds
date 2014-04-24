var rootURL = "http://localhost:8080/CatFeed/rest/feed";
var homeURL = "http://localhost:8080/CatFeed/pri/home.jsf";

window.fbAsyncInit = function() {
  FB.init({
    appId      : '207499559461358',
    status     : true,
    cookie     : true,
    xfbml      : true
  });

  FB.Event.subscribe('auth.authResponseChange', function(response) {
    if (response.status === 'connected') {
      persistirFeed();
    } else if (response.status === 'not_authorized') {

    	FB.login(function(response) {
    	    if (response.authResponse) {
    	        persistirFeed();
    	    }
    	}, {scope:'read_stream'});
    	
    } else {
    	
    	FB.login(function(response) {
    	    if (response.authResponse) {
    	        persistirFeed();
    	    }
    	}, {scope:'read_stream'});
 
    }
  });
};
  
function exibirFeed() {
	  var accessToken = FB.getAuthResponse()['accessToken'];
      var accessTokenString = JSON.stringify({"accessToken": accessToken});
      
      jQuery.ajax({
          type: "POST",
          url: rootURL,
          data: accessTokenString,
          contentType: "application/json",
          error: function (xhr, status) {
                  console.log("Ocorreu um erro ao exibir o feed: " + status + '.');
              },
          success: function (data, msg) {
              console.log("Feed exibido com sucesso.");
              renderizarPostsFeed(data);
          }
      });
}

function persistirFeed() {
	console.log('Conectado, persistindo o feed no banco...');

	var accessToken = FB.getAuthResponse()['accessToken'];
	var accessTokenString = JSON.stringify({"accessToken": accessToken});
    
    jQuery.ajax({
        type: "POST",
        url: rootURL + '/salvarFeed',
        data: accessTokenString,
        contentType: "application/json",
        error: function (xhr, status) {
                console.log("Ocorreu um erro ao persistir o feed: " + status + '.');
            },
        success: function (msg) {
            console.log("Feed salvo com sucesso.");
            redirecionarPaginaPrincipal();
        }
    });
}

function redirecionarPaginaPrincipal() {
	window.location = homeURL;
}

function recuperarMapaFrequenciasPalavraChave() {
	  var accessToken = FB.getAuthResponse()['accessToken'];
      var accessTokenString = JSON.stringify({"accessToken": accessToken});

      jQuery.ajax({
          type: "POST",
          url: rootURL + '/mapaFrequencias',
          data: accessTokenString,
          contentType: "application/json",
          dataType: "json",
          error: function (xhr, status) {
                  console.log("Ocorreu um erro ao recuperar o mapa de frequências das palavras chave: " + status + '.');
              },
          success: function (data, msg) {
              console.log("Mapa de frequências recuperado com sucesso.");
              renderizarCloudFeed(data);
          }
      });
}
  
function renderizarPostsFeed(data) {

	var list = data == null ? [] : (data instanceof Array ? data : [data]);

	jQuery('#listaPosts li').remove();
	$.each(list, function(index, mensagemPost) {
		$('#listaPosts').append('<li>'+mensagemPost+'</li>');
	});
}

function renderizarCloudFeed(data) {

	var listaPalavrasChave = data == null ? [] : (data instanceof Array ? data : [data]);

	jQuery("#wordCloud").jQCloud(listaPalavrasChave);
}
  
jQuery(document).ready(function() {

	jQuery('#btnExibirFeed').click(function() {
		console.log('Exibindo o feed...');
		exibirFeed();
		return false;
	});

	jQuery('#btnCloudFeed').click(function() {
		console.log('Gerando cloud do feed...');
		recuperarMapaFrequenciasPalavraChave();
		return false;
	});
});