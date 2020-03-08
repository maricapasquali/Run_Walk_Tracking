<?php
require_once("utility.php");
require_once("dao/UserDao.php");
try{

    $request =  Request::getBody(REQUEST_CHANGE_PASSWORD);

    $key = rand();
    $expiry_date = date_end_validity_link();

    print json_encode(array(EMAIL=>$request[EMAIL], KEY=>$key, EXPIRY_DATE =>$expiry_date) +
    array(REQUEST_PASSWORD_FORGOT_SEND =>UserDao::instance()->requestForgotPassword($request[EMAIL], $key, $expiry_date)));

}catch(Exception $e){
  print json_errors($e);
}

 ?>
