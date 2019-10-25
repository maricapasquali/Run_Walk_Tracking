<?php
require_once("../utility.php");
require_once("../dao/SettingsDao.php");

try{
  $changed_target = bodyRequest();
  if(!isset($changed_target[ID_USER]) || !isset($changed_target[TARGET]))
  throw new Exception(URL_NOT_VALID);

  print json_encode(array(UPDATE => SettingsDao::updateTargetFor($changed_target[TARGET],
  $changed_target[ID_USER])));

}catch(Exception $e){
  print json_errors($e->getMessage());
}

?>
