package com.example.chchi.paypal_restapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;


public class MainActivity extends Activity {
    private static String F_TAG = "FuturePayment";
    private static String PayPal_Server_URL = "http://changchingchi.com/Demo/PayPalPlayground/restapi/paypal/rest-api-sdk-php/sample/payments/CreateFuturePayment2.php";
    private static String Paypal_DB_URL = "http://changchingchi.com/Demo/PayPalPlayground/restapi/paypal/rest-api-sdk-php/sample/payments/DB.php";
    private static final int PAYPAL_REQUEST = 100;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 101;
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId("AVo6vRBmro2mOMfFbrsWVZhaFoDaGB60ga2pRaFC6iTZ02I2jzmH9IalQVlJ")
            .merchantName("PayPalRestAPIExample")
            .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
            .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));
    Button mBuynow, mFutureBuynow, mFutureConsent;
    AsyncHttpClient mHttpClient;
    String mResponse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBuynow = (Button)findViewById(R.id.pp_buynow);
        mFutureBuynow = (Button) findViewById(R.id.pp_futurebuynow);
        mFutureConsent = (Button) findViewById(R.id.PP_futureConsent);
        mHttpClient = new AsyncHttpClient();



    }

    @Override
    protected void onResume() {
        super.onResume();
        mBuynow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PayPalPayment payment = new PayPalPayment(new BigDecimal("50"),"USD","Moto360",PayPalPayment.PAYMENT_INTENT_SALE);
                Intent intent = new Intent(MainActivity.this, PaymentActivity.class);
                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
                intent.putExtra(PaymentActivity.EXTRA_PAYMENT,payment);
                startActivityForResult(intent,PAYPAL_REQUEST);
            }
        });

        mFutureConsent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PayPalFuturePaymentActivity.class);

                // send the same configuration for restart resiliency
                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

                startActivityForResult(intent, REQUEST_CODE_FUTURE_PAYMENT);
            }
        });
        mFutureBuynow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendClientIDToServer();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PAYPAL_REQUEST && resultCode == Activity.RESULT_OK){

            PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (confirm != null) {
                try {
                    Log.i("paymentExample", confirm.toJSONObject().toString(4));

                    // TODO: send 'confirm' to your server for verification.
                    // see https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
                    // for more details.

                } catch (JSONException e) {
                    Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                }
            }
        }else if (requestCode == REQUEST_CODE_FUTURE_PAYMENT && resultCode == Activity.RESULT_OK) {
            PayPalAuthorization auth = data
                    .getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
            if(auth!=null){
                // talk to your server!
                sendAuthorizationToServer(auth);

            }

        }
        else if (resultCode == Activity.RESULT_CANCELED) {
            Log.i("paymentExample", "The user canceled.");
        }
        else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
        }
    }

        private void sendAuthorizationToServer(PayPalAuthorization authorization) {


            /**
             * TODO: Send the authorization response to your server, where it can
             * exchange the authorization code for OAuth access and refresh tokens.
             *
             * Your server must then store these tokens, so that your server code
             * can execute payments for this user in the future.
             *
             * A more complete example that includes the required app-server to
             * PayPal-server integration is available from
             * https://github.com/paypal/rest-api-sdk-python/tree/master/samples/mobile_backend
             * send auth_code --> get refresh token and store in on Server.

             */



            RequestParams params = new RequestParams();
            params.put("authorization_code", authorization.getAuthorizationCode());
//            params.put("correlationID",config.getApplicationCorrelationId(this));

            Log.d("auth_code",authorization.getAuthorizationCode());
//            Log.d("corID",config.getApplicationCorrelationId(this));

            mHttpClient.get(Paypal_DB_URL, params, new TextHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        Log.d("responseString", responseString);
                        mResponse = new JSONObject(responseString).getString("refreshToken");
                        Toast.makeText(MainActivity.this, "refresh_token: "+mResponse, Toast.LENGTH_LONG).show();
                        Log.d("refresh_token: ",mResponse);

                    } catch (JSONException e) {
                        Toast.makeText(MainActivity.this, "Unable to decode json", Toast.LENGTH_LONG).show();
                        Log.d("json error", " ", e);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Toast.makeText(MainActivity.this, "Unable to get a json. Status code:Error:" +
                            responseString, Toast.LENGTH_LONG).show();
                }

            });



         }

    private void sendClientIDToServer() {

        final ProgressDialog mProgressDialog = ProgressDialog.show(this,"progress","sending request to server and getting response back...", true);

        /**
         * 1. you have a DB created and your refresh token generated already.
         * 2. when you submit the request to your server this time, you need to retrieve the refresh
         * token you have from step1 and get a new accessToken. Also you need to attach your
         * client ID as a param here to your server.
         */
        RequestParams params = new RequestParams();
//        params.put("authorization_code", authorization.getAuthorizationCode());
            params.put("correlationID",config.getApplicationCorrelationId(this));

//        Log.d("auth_code",authorization.getAuthorizationCode());
            Log.d("corID",config.getApplicationCorrelationId(this));

        mHttpClient.get(PayPal_Server_URL, params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Log.d("responseString", responseString);
                    mResponse = new JSONObject(responseString).getString("refreshToken");
                    Toast.makeText(MainActivity.this, "refresh_token: "+mResponse, Toast.LENGTH_LONG).show();
                    Log.d("refresh_token: ",mResponse);
                    mProgressDialog.dismiss();

                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, "Unable to decode json", Toast.LENGTH_LONG).show();
                    Log.d("json error", " ", e);
                    mProgressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(MainActivity.this, "Unable to get a json. Status code:Error:" +
                        responseString, Toast.LENGTH_LONG).show();
                mProgressDialog.dismiss();
            }

        });



    }



    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
