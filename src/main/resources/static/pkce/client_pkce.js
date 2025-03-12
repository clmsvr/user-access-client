
let accessToken = "";

function login() {
    
//  let state = btoa(Math.random());
//  localStorage.setItem("clientState", state);
  
  let codeVerifier = generateCodeVerifier();
  let codeChallenge = generateCodeChallenge(codeVerifier);

  window.location.href = `${config.authorizeUrl}?response_type=code&client_id=${config.clientId}&redirect_uri=${config.callbackUrl}&scope=${config.scope}&code_challenge_method=S256&code_challenge=${codeChallenge}`;
}

function generateCodeVerifier() {
  let codeVerifier = generateRandomString(128);
  localStorage.setItem("codeVerifier", codeVerifier);
  return codeVerifier;
}

function getCodeVerifier() {
  return localStorage.getItem("codeVerifier");
}

function generateRandomString(length) {
  let text = "";
  let possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  
  for (let i = 0; i < length; i++) {
    text += possible.charAt(Math.floor(Math.random() * possible.length));
  }
  return text;
}

function generateCodeChallenge(codeVerifier) {
  return base64URL(CryptoJS.SHA256(codeVerifier));
}
function base64URL(string) {
  return string.toString(CryptoJS.enc.Base64).replace(/=/g, '').replace(/\+/g, '-').replace(/\//g, '_');
}


function gerarAccessToken(code) 
{
  //alert("Gerando access token com code " + code);
  $("#code").text(code);

  let clientAuth = btoa(config.clientId + ":" + config.clientSecret);
  
  let codeVerifier = getCodeVerifier();

  let params = new URLSearchParams();
  params.append("grant_type", "authorization_code");
  params.append("code", code);
  params.append("redirect_uri", config.callbackUrl);
  //params.append("client_id", config.clientId);
  params.append("code_verifier", codeVerifier);
  

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

      //alert("Access token gerado: " + accessToken);
      $("#token").text(accessToken);
      
      consultar();
    },

    error: function(error) {
      $("#error").text("Erro ao gerar access key:" + error);
    }
  });
}


function consultar() {
  //alert("Consultando recurso com access token " + accessToken);

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

  //window.location.search : querystring part of a URL
  let params = new URLSearchParams(window.location.search);

  let error = params.get("error");
  let error_description = params.get("error_description");

  let code = params.get("code");
//  let state = params.get("state");
//  let currentState = localStorage.getItem("clientState");
  
  if (error){
    alert(error + " - " + error_description);
  }
  else if (code) {
    gerarAccessToken(code);
  }
  
});

$("#btn-consultar").click(login);
