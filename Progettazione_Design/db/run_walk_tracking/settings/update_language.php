<?php
require_once("../utility.php");
require_once("../dao/SettingsDao.php");

try{
  $changed_language = bodyRequest();
  if(!isset($changed_language[ID_USER]) || !isset($changed_language[LANGUAGE]))
  throw new Exception(URL_NOT_VALID);

  print json_encode(array(UPDATE => SettingsDao::updateLanguageFor($changed_language[LANGUAGE],
  $changed_language[ID_USER])));

}catch(Exception $e){
  print json_errors($e->getMessage());
}

?>
