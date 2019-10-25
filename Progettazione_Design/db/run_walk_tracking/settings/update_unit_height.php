<?php
require_once("../utility.php");
require_once("../dao/SettingsDao.php");

try{
  $changed_unit_height = bodyRequest();
  if(!isset($changed_unit_height[ID_USER]) || !isset($changed_unit_height[UNIT]))
  throw new Exception(URL_NOT_VALID);

  print json_encode(array(UPDATE => SettingsDao::updateUnitHeightFor($changed_unit_height[UNIT],
  $changed_unit_height[ID_USER])));

}catch(Exception $e){
  print json_errors($e->getMessage());
}

?>
