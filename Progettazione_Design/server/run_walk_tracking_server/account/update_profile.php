<?php
require_once("../utility.php");
require_once("../dao/UserDao.php");

try{
  $changed_profile = bodyRequest();
  if(!isset($changed_profile[ID_USER]))
  throw new Exception(URL_NOT_VALID);

  print json_encode(array(UPDATE =>count($changed_profile)<2 ? false : UserDao::update($changed_profile)));

}catch(Exception $e){
  print json_errors($e->getMessage());
}


 ?>
