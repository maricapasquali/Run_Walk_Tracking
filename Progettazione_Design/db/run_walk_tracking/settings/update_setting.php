<?php
require_once("../utility.php");
require_once("../dao/SettingsDao.php");

try{
  $changed_setting = bodyRequest();
  if(!isset($changed_setting[ID_USER]) ||!isset($changed_setting[FILTER]) || !isset($changed_setting[VALUE]))
  throw new Exception(URL_NOT_VALID);

  switch ($changed_setting[FILTER]) {
    case SPORT :
      print json_encode(SettingsDao::updateSportFor($changed_setting[VALUE], $changed_setting[ID_USER]));
      break;
    case TARGET :
      print json_encode(SettingsDao::updateTargetFor($changed_setting[VALUE], $changed_setting[ID_USER]));
      break;
    case DISTANCE :
      print json_encode(SettingsDao::updateUnitDistanceFor($changed_setting[VALUE],  $changed_setting[ID_USER]));
      break;
    case WEIGHT :
      print json_encode(SettingsDao::updateUnitWeightFor($changed_setting[VALUE],  $changed_setting[ID_USER]));
      break;
    case HEIGHT :
      print json_encode(SettingsDao::updateUnitHeightFor($changed_setting[VALUE],  $changed_setting[ID_USER]));
      break;
    default:
      throw new Exception(FILTER_NOT_VALID);
      break;
  }

}catch(Exception $e){
  print json_errors($e->getMessage());
}

?>
