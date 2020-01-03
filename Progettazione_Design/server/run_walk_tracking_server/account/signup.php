<?php
require_once("../utility.php");
require_once("../dao/UserDao.php");
require_once("../exception/Exceptions.php");
try{
  $new_user = bodyRequest();
  if(!isset($new_user[NAME]) ||
     !isset($new_user[LAST_NAME]) ||
     !isset($new_user[GENDER]) ||
     !isset($new_user[BIRTH_DATE]) ||
     !isset($new_user[EMAIL]) ||
     !isset($new_user[CITY]) ||
     !isset($new_user[PHONE]) ||
     !isset($new_user[HEIGHT]) ||
     !isset($new_user[TARGET]) ||
     !isset($new_user[WEIGHT]) ||
     !isset($new_user[USERNAME]) ||
     !isset($new_user[PASSWORD]))
      throw new UrlExeception();

     print json_encode(array(SIGN_UP => UserDao::create($new_user)));
    //print json_encode(array(SIGN_UP => false));

}catch(Exception $e){
  print json_errors($e);
}
?>
