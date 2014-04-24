var rootURL = "http://localhost:8080/CatFeed/rest/feed";
var homeURL = "http://localhost:8080/CatFeed/pri/home.jsf";

window.fbAsyncInit = function() {
  FB.init({
    appId      : '207499559461358',
    status     : true,
    cookie     : true,
    xfbml      : true
  });

  FB.getLoginStatus(function(response) {
	  if (response.status === 'connected') {
		  redirecionarPaginaPrincipal();
    } 
  });
  
  FB.Event.subscribe('auth.login', function () {
	  persistirFeed();
  });
};
  
function persistirFeed() {
	console.log('Conectado, persistindo o feed no banco...');
	
	jQuery("#mensagem").text("Por favor, aguarde...");

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