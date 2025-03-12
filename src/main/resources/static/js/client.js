
let accessToken = "";

function login() {
  // https://auth0.com/docs/protocols/oauth2/oauth-state
  let state = btoa(Math.random());
  localStorage.setItem("clientState", state);

  window.location.href = `${config.authorizeUrl}?response_type=code&client_id=${config.clientId}&state=${state}&redirect_uri=${config.callbackUrl}&scope=${config.scope}`;
}

function gerarAccessToken(code) {

  $("#code").text(code);
    
  let clientAuth = btoa(config.clientId + ":" + config.clientSecret);
  
  let params = new URLSearchParams();
  params.append("grant_type", "authorization_code");
  params.append("code", code);
  params.append("redirect_uri", config.callbackUrl);

  $.ajax({
    url: config.tokenUrl,
    type: "post",
    data: params.toString(),
    contentType: "application/x-www-form-urlencoded",

    beforeSend: function(request) {
      request.setRequestHeader("Authorization", "Basic " + clientAuth);
    },

    success: function(response) {
      accessToken = response.access_token;

      $("#token").text(accessToken);
      
      consultar();
    },

    error: function(error) {
      $("#error").text("Erro ao gerar access key:" + error);
    }
  });
}

function consultar() {

  $.ajax({
    url: config.usersApiUrl,
    type: "get",

    beforeSend: function(request) {
      request.setRequestHeader("Authorization", "Bearer " + accessToken);
    },

    success: function(response) {
      var json = JSON.stringify(response);
      $("#resultado").text(JSON.stringify(JSON.parse(json), null, 4));
    },

    error: function(error) {
      $("#error").text("Erro ao consultar recurso:" + error);
    }
  });
}


$(document).ready(function() {
  let params = new URLSearchParams(window.location.search);

  let error = params.get("error");
  let error_description = params.get("error_description");

  let code = params.get("code");
  let state = params.get("state");
  let currentState = localStorage.getItem("clientState");

  if (error){
    
    $("#error").text(error + " - " + error_description);
  
  }else if (code) {
    // window.history.replaceState(null, null, "/");

    if (currentState == state) {
      gerarAccessToken(code);
    } else {
      $("#error").text("State inválido");
    }
  }
});

$("#btn-consultar").click(login);
