var rootURL = "http://localhost:8080/CatFeed/rest/feed";

window.fbAsyncInit = function() {
  FB.init({
    appId      : '207499559461358',
    status     : true,
    cookie     : true,
    xfbml      : true
  });

  FB.Event.subscribe('auth.authResponseChange', function(response) {
    if (response.status === 'connected') {
      reportarStatusAPI();
    } else if (response.status === 'not_authorized') {

    	FB.login(function(response) {
    	    if (response.authResponse) {
    	        console.log('Logado com sucesso.');
    	    }
    	}, {scope:'read_stream'});
    	
    } else {
    	
    	FB.login(function(response) {
    	    if (response.authResponse) {
    	        console.log('Logado com sucesso.');
    	    }
    	}, {scope:'read_stream'});
 
    }
  });
  };

  (function(d){
   var js, id = 'facebook-jssdk', ref = d.getElementsByTagName('script')[0];
   if (d.getElementById(id)) {return;}
   js = d.createElement('script'); js.id = id; js.async = true;
   js.src = "//connect.facebook.net/en_US/all.js";
   ref.parentNode.insertBefore(js, ref);
  }(document));

  function reportarStatusAPI() {
    FB.api('/me', function(response) {
      console.log('API do Facebook conectada com sucesso.');
    });
  }
  
  function salvarFeed() {
	  var accessToken = FB.getAuthResponse()['accessToken'];
      var accessTokenString = JSON.stringify({"accessToken": accessToken});
      
      jQuery.ajax({
          type: "POST",
          url: rootURL,
          data: accessTokenString,
          contentType: "application/json",
          error: function (xhr, status) {
                  console.log("Ocorreu um erro ao salvar o feed: " + status + '.');
              },
          success: function (msg) {
              console.log("Feed salvo com sucesso.");
          }
      });
  }