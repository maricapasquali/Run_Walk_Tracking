<?php
require_once("../utility.php");
require_once("../dao/UserDao.php");

try{
  $user = $_GET; //bodyRequest();
  if(!isset($user[ID_USER]))
  throw new Exception(URL_NOT_VALID);
  print json_encode(array(DELETE => UserDao::delete($user[ID_USER])));

}catch(Exception $e){
  print json_errors($e->getMessage());
}
?>
