<?php
require_once("../utility.php");
require_once("../dao/UserDao.php");

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
      throw new Exception(URL_NOT_VALID);

     print json_encode(array(SIGN_UP => UserDao::create($new_user)));

}catch(Exception $e){
  print json_errors($e->getMessage());
}
/*
INPUT
{
    "name" : "Maria",
    "last_name":"Bianchi",
    "gender": "Female",
    "birth_date" : "1990-02-11",
    "email": "mariabianchi@gmail.com",
    "city": "Milano",
    "phone": "3333333333",
    "height": 1.55,
    "target": 2,
    "weight": 70.5,
    "username": "mariabianchi$1",
    "password": "pass"
}

OUTPUT
{
  "id_user" : .....
	"token" : ".........."
}
*/


?>
