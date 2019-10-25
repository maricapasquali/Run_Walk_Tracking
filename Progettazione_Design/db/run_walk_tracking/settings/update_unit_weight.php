<?php
require_once("../utility.php");
require_once("../dao/SettingsDao.php");

try{
  $changed_unit_weight = bodyRequest();
  if(!isset($changed_unit_weight[ID_USER]) || !isset($changed_unit_weight[UNIT]))
  throw new Exception(URL_NOT_VALID);

  print json_encode(array(UPDATE => SettingsDao::updateUnitWeightFor($changed_unit_weight[UNIT],
  $changed_unit_weight[ID_USER])));

}catch(Exception $e){
  print json_errors($e->getMessage());
}

?>
