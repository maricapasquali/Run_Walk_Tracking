<?php
require_once("../utility.php");
require_once("../dao/SettingsDao.php");

try{
  $changed_sport = bodyRequest();
  if(!isset($changed_sport[ID_USER]) || !isset($changed_sport[SPORT]))
  throw new Exception(URL_NOT_VALID);

  print json_encode(array(UPDATE => SettingsDao::updateSportFor($changed_sport[SPORT],
  $changed_sport[ID_USER])));

}catch(Exception $e){
  print json_errors($e->getMessage());
}

?>
