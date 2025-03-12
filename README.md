

### Aplicação "Client" para o projeto do Repositório [user-access-template-oidc-apiserver](https://github.com/clmsvr/user-access-template-oidc-apiserver).

4 tipos de Clientes implementados:

* **JavaScript** com fluxo "Authorization Code Grant" **sem** PKCE
* **JavaScript** com fluxo "Authorization Code Grant" **com** PKCE
* **Java** com fluxo "Authorization Code Grant" sem PKCE (e sem usar os recursos do Spring Oauth2 Client)
* **Spring Oauth2 Client** com fluxo "Authorization Code Grant" .

Estes Clientes devem ser configurados para usar o mesmo  servidor de authenticação da aplicação [server](https://github.com/clmsvr/user-access-template-oidc-apiserver).

Neste caso foi usado o [Aws Cognito](https://aws.amazon.com/pt/cognito/).

##### Necessário configurar o **ClientId**, o **ClientSecret** e a **urls** do Cognito Pool nos arquivos:
* /src/main/resources/application.properties
* /src/main/resources/static/secret.js

A página inicial (**http://localhost:8080**) contém um indice para os exemplos.
