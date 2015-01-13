<?php
require __DIR__ . '/../bootstrap.php';
header('Content-Type: application/json');
// # Create Payment using PayPal as payment method
// This sample code demonstrates how you can process a
// PayPal Account based Payment.
// API used: /v1/payments/payment
$servername = "-----";
$username = "----";
$password = "ConnecttoDB";
$dbname = "RESTappDB";

use PayPal\Api\Amount;
use PayPal\Api\Details;
use PayPal\Api\Item;
use PayPal\Api\ItemList;
use PayPal\Api\Payer;
use PayPal\Api\Payment;
use PayPal\Api\RedirectUrls;
use PayPal\Api\Transaction;
session_start();

try{
    $conn = new PDO("mysql:host=$servername;dbname=$dbname", $username, $password);
    // set the PDO error mode to exception
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    $stmt = $conn->prepare("SELECT refreshToken FROM myRefreshToken WHERE id=1 LIMIT 1");
    $stmt->execute();
    $row = $stmt->fetch();
    $refreshToken=$row['refreshToken'];

}catch(PDOException $e) {
    echo "Error: " . $e->getMessage();
}
$conn = null;




// ### Payer
// A resource representing a Payer that funds a payment
// For paypal account payments, set payment method
// to 'paypal'.
$payer = new Payer();
$payer->setPaymentMethod("paypal");

// ### Amount
// Lets you specify a payment amount.
// You can also specify additional details
// such as shipping, tax.
$amount = new Amount();
$amount->setCurrency("USD")
    ->setTotal("0.18");

// ### Transaction
// A transaction defines the contract of a
// payment - what is the payment for and who
// is fulfilling it.
$transaction = new Transaction();
$transaction->setAmount($amount)
    ->setDescription("Payment description");

// ### Redirect urls
// Set the urls that the buyer must be redirected to after
// payment approval/ cancellation.
$baseUrl = getBaseUrl();
$redirectUrls = new RedirectUrls();
$redirectUrls->setReturnUrl("$baseUrl/ExecutePayment.php?success=true")
    ->setCancelUrl("$baseUrl/ExecutePayment.php?success=false");

// ### Payment
// A Payment Resource; create one using
// the above types and intent set to 'sale'
$payment = new Payment();
$payment->setIntent("sale")
    ->setPayer($payer)
    ->setRedirectUrls($redirectUrls)
    ->setTransactions(array($transaction));

// correlation id from mobile sdk
$correlationId = $_GET["correlationID"];
// $correlationID = "41b26d8644964c2db4f98483e38a3bf6";
try {
    // Exchange authorization_code for long living refresh token. You should store
    // it in a database for later use
    $jsonArray = array('refreshToken'=> $refreshToken,'correlationID'=>$correlationId);
    echo(json_encode($jsonArray));
    // Update the access token in apiContext
    $apiContext->getCredential()->updateAccessToken($apiContext->getConfig(), $refreshToken);

    // ### Create Future Payment
    // Create a payment by calling the 'create' method
    // passing it a valid apiContext.
    // (See bootstrap.php for more on `ApiContext`)
    // The return object contains the state and the
    // url to which the buyer must be redirected to
    // for payment approval
    // Please note that currently future payments works only with Paypal as a funding instrument.
    $payment->create($apiContext, $correlationId);

} catch (PayPal\Exception\PPConnectionException $ex) {
    echo "Exception: " . $ex->getMessage() . PHP_EOL;
    var_dump($ex->getData());
    exit(1);
}

?>


