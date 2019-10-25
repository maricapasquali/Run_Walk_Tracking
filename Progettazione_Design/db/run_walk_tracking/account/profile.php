<?php
require_once("../utility.php");
require_once("../dao/UserDao.php");

try{
  if(!isset($_GET[ID_USER]))
  throw new Exception(URL_NOT_VALID);
  print json_encode(array(USER =>UserDao::getUserForId($_GET[ID_USER])));

}catch(Exception $e){
  print json_errors($e->getMessage());
}
?>
