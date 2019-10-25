<?php
require_once("../utility.php");
require_once("../dao/WorkoutDao.php");

try{
  $workout = $_GET;//bodyRequest();

  if(!isset($workout[ID_WORKOUT]))
  throw new Exception(URL_NOT_VALID);

  print json_encode(array(DELETE => WorkoutDao::delete($workout[ID_WORKOUT])));

}catch(Exception $e){
  print json_errors($e->getMessage());
}

 ?>
