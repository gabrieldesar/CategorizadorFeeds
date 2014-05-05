var rootURL = "http://localhost:8080/CatFeed/rest/feed";
var loginURL = "http://localhost:8080/CatFeed";

function redirecionarPaginaLogin() {
	window.location.href = loginURL;
}

function recuperarListaPostsEsportes(postsSpinner) {
	  var accessToken = FB.getAuthResponse()['accessToken'];
      var accessTokenString = JSON.stringify({"accessToken": accessToken});
      
      jQuery.ajax({
          type: "POST",
          url: rootURL + '/clima',
          data: accessTokenString,
          contentType: "application/json",
          error: function (xhr, status) {
                  console.log("Ocorreu um erro ao obter a lista de posts sobre clima: " + status + '.');
              },
          success: function (data, msg) {
              console.log("Lista de posts sobre clima recuperada com sucesso.");
              renderizarListaPostsEsportes(data, postsSpinner);
          }
      });
}
  
function renderizarListaPostsEsportes(data, postsSpinner) {

	var list = data == null ? [] : (data instanceof Array ? data : [data]);

	postsSpinner.stop();
	jQuery.each(list, function(index, post) {
		
		jQuery('#posts').append('<div class="blog-post">' + 
								   		'<p class="blog-post-meta">'+post.data+'by <a href="https://www.facebook.com/search/more/?q='+post.autor+'" target="_blank">'+post.autor+'</a></p>' +
								   		'<p>'+post.mensagem+'</p>' +
								   		'</div>');
	});
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
	
	var spinnerDiv = document.getElementById('spinnerDiv');
	var postsSpinner = obterNovoSpinner(spinnerDiv);
	
	FB.getLoginStatus(function(response) {
	  if (response.status === 'connected') {
		recuperarListaPostsEsportes(postsSpinner);
	  } else {
	   	redirecionarPaginaLogin();
	  }
	});
});