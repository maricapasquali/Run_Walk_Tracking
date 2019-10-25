<?php
require_once("../utility.php");
require_once("../dao/WorkoutDao.php");

try{
  $changed_workout = bodyRequest();
  if(!isset($changed_workout[ID_WORKOUT]))
  throw new Exception(URL_NOT_VALID);

  print json_encode(array(UPDATE => count($changed_workout)<2 ? false : WorkoutDao::update($changed_workout)));

}catch(Exception $e){
  print json_errors($e->getMessage());
}




 ?>
