<?php
$servername = "localhost";
$username = "chi";
$password = "----";
$dbname = "RESTappDB";


header('Content-Type: application/json');
// # Create Payment using PayPal as payment method
// This sample code demonstrates how you can process a
// PayPal Account based Payment.
// API used: /v1/payments/payment

require __DIR__ . '/../bootstrap.php';
use PayPal\Api\Amount;
use PayPal\Api\Details;
use PayPal\Api\Item;
use PayPal\Api\ItemList;
use PayPal\Api\Payer;
use PayPal\Api\Payment;
use PayPal\Api\RedirectUrls;
use PayPal\Api\Transaction;
session_start();

 $authorizationCode = $_GET["authorization_code"];

try {
    // Exchange authorization_code for long living refresh token. You should store
    // it in a database for later use
     $refreshToken = $apiContext->getCredential()->getRefreshToken($apiContext->getConfig(), $authorizationCode);


    $conn = new PDO("mysql:host=$servername;dbname=$dbname", $username, $password);
    // set the PDO error mode to exception
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
//     $sql = "CREATE DATABASE myDBPDO"; // step1: create DB
    // use exec() because no results are returned
    //step2: create Table.
//     $sql = "CREATE TABLE myRefreshToken(id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
//     refreshToken VARCHAR(60) NOT NULL,
//     reg_date TIMESTAMP
//     )";
     $sql = "INSERT INTO myRefreshToken(refreshToken)
     VALUES('$refreshToken')";

    $conn->exec($sql);

    $jsonArray = array('refreshToken'=> $refreshToken);

     echo(json_encode($jsonArray));


} catch (PayPal\Exception\PPConnectionException $ex) {
    echo "Exception: " . $ex->getMessage() . PHP_EOL;
    var_dump($ex->getData());
    exit(1);
} catch(PDOException $e)
    {
    echo $sql . "<br>" . $e->getMessage();
    }

$conn = null;

?>