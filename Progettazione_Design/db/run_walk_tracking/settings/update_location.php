<?php
require_once("../utility.php");
require_once("../dao/SettingsDao.php");

try{
  $changed_location = bodyRequest();
  if(!isset($changed_location[ID_USER]) || !isset($changed_location[LOCATION]))
  throw new Exception(URL_NOT_VALID);

  print json_encode(array(UPDATE => SettingsDao::updateLocationFor($changed_location[LOCATION],
  $changed_location[ID_USER])));

}catch(Exception $e){
  print json_errors($e->getMessage());
}

?>
