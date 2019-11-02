<?php
require_once("utility.php");
require_once("dao/UserDao.php");
try{

    $request =  bodyRequest();
    if(!isset($request[EMAIL])) throw new Exception(URL_NOT_VALID);

    $key = rand();
    $expiry_date = date_end_validity_link();

    print json_encode(array("email"=>$request[EMAIL], "key"=>$key, "expiry_date" =>$expiry_date) +
    array("request_sended" =>UserDao::requestForgotPassword($request[EMAIL], $key, $expiry_date)));

}catch(Exception $e){
  print json_errors($e->getMessage());
}

 ?>
