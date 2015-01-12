# PayPal_RESTAPP
PayPal Android SDK Demo APP

# client-side

Please import PayPal androidSDK 2.8.4 (as today) and this project into your android studio usuing build.gradle. change the credential to your own one from developer.paypal.com. 
Need to find your own way to establish web service, you can decide what verb you want to use to connect to your own server. Later on you submit the request to your own server and your server will need to call PHP method using PayPal PHP library installed on your server side. 

# server-side

First of all, if you are using PHP on your server-side, you will need to install the PayPal PHP library from developer.paypal.com. You need to catch request from your android client-side and put them as params into PHP library. Please follow the instructions on PayPal to do the Oauth2.0 and refresh Token/access token. 
https://github.com/paypal/PayPal-Android-SDK/blob/master/docs/future_payments_server.md


