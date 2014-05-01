var rootURL = "http://localhost:8080/CatFeed/rest/feed";
var loginURL = "http://localhost:8080/CatFeed";

function redirecionarPaginaLogin() {
	window.location.href = loginURL;
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

function recuperarListaKeywords(wordCloudSpinner) {
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
              renderizarCloudFeed(data, wordCloudSpinner);
          }
      });
}

function recuperarArrayCategoriasNumeroPosts(feedChartSpinner) {
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
            renderizarFeedChart(data, feedChartSpinner);
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

function renderizarCloudFeed(data, wordCloudSpinner) {

	var listaPalavrasChave = data == null ? [] : (data instanceof Array ? data : [data]);

	wordCloudSpinner.stop();
	jQuery("#wordCloud").jQCloud(listaPalavrasChave);
}

function renderizarFeedChart(data, feedChartSpinner) {	
	
	var listaPalavrasChave = data == null ? [] : [data];

	feedChartSpinner.stop();
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

function obterNovoSpinner(elemento) {
	
	var opts = { lines: 13, length: 20, width: 10, radius: 30, corners: 1, rotate: 0, direction: 1, 
				 color: '#000', speed: 1, trail: 60, shadow: false, hwaccel: false, className: 'spinner', 
				 zIndex: 2e9, top: '50%', left: '50%' };

	return new Spinner(opts).spin(elemento);
}

jQuery(window).load(function() {

	FB.init({
	    appId      : '207499559461358',
	    status     : true,
	    cookie     : true,
	    xfbml      : true
	});
	
	var wordCloudDiv = document.getElementById('wordCloud');
	var feedChartDiv = document.getElementById('feedChart');

	var wordCloudSpinner = obterNovoSpinner(wordCloudDiv);
	var feedChartSpinner = obterNovoSpinner(feedChartDiv);
	
	FB.getLoginStatus(function(response) {
	  if (response.status === 'connected') {
	  	recuperarListaKeywords(wordCloudSpinner);
	   	recuperarArrayCategoriasNumeroPosts(feedChartSpinner);
	  } else {
	   	redirecionarPaginaLogin();
	  }
	});
});