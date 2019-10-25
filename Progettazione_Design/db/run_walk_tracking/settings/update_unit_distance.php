<?php
require_once("../utility.php");
require_once("../dao/SettingsDao.php");

try{
  $changed_unit_distance = bodyRequest();
  if(!isset($changed_unit_distance[ID_USER]) || !isset($changed_unit_distance[UNIT]))
  throw new Exception(URL_NOT_VALID);

  print json_encode(array(UPDATE => SettingsDao::updateUnitDistanceFor($changed_unit_distance[UNIT],
  $changed_unit_distance[ID_USER])));

}catch(Exception $e){
  print json_errors($e->getMessage());
}

?>
