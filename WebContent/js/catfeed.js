var rootURL = "http://localhost:8080/CatFeed/rest/feed";
var loginURL = "http://localhost:8080/CatFeed";

window.fbAsyncInit = function() {
  FB.init({
    appId      : '207499559461358',
    status     : true,
    cookie     : true,
    xfbml      : true
  });

  FB.getLoginStatus(function(response) {
    if (response.status === 'connected') {
    	recuperarListaKeywords();
    	recuperarArrayCategoriasNumeroPosts();
    } else {
    	redirecionarPaginaLogin();
    }
  });
};

function redirecionarPaginaLogin() {
	window.location = loginURL;
}

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

function recuperarListaKeywords() {
	  var accessToken = FB.getAuthResponse()['accessToken'];
      var accessTokenString = JSON.stringify({"accessToken": accessToken});

      jQuery.ajax({
          type: "POST",
          url: rootURL + '/listaKeywords',
          data: accessTokenString,
          contentType: "application/json",
          dataType: "json",
          error: function (xhr, status) {
                  console.log("Ocorreu um erro ao recuperar a lista de Keywords: " + status + '.');
              },
          success: function (data, msg) {
              console.log("Lista de Keywords recuperado com sucesso.");
              renderizarCloudFeed(data);
          }
      });
}

function recuperarArrayCategoriasNumeroPosts() {
	var accessToken = FB.getAuthResponse()['accessToken'];
    var accessTokenString = JSON.stringify({"accessToken": accessToken});

    jQuery.ajax({
        type: "POST",
        url: rootURL + '/arrayCategoriasNumeroPosts',
        data: accessTokenString,
        contentType: "application/json",
        dataType: "json",
        error: function (xhr, status) {
                console.log("Ocorreu um erro ao recuperar o array de categorias e número de posts: " + status + '.');
            },
        success: function (data, msg) {
            console.log("Array de categorias e número de posts recuperado com sucesso.");
            renderizarFeedChart(data);
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

function renderizarFeedChart(data) {	
	
	var listaPalavrasChave = data == null ? [] : [data];

	jQuery.jqplot ('feedChart', listaPalavrasChave, 
		    { 
		      seriesDefaults: {
		        renderer: jQuery.jqplot.PieRenderer, 
		        rendererOptions: {
		          showDataLabels: true
		        }
		      }, 
		      legend: { show:true, location: 'e' }
		    }
		  );
}
  