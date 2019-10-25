<?php
require_once("../utility.php");
require_once("../dao/WorkoutDao.php");

try{
  if(!isset($_GET[ID_USER])) throw new Exception(URL_NOT_VALID);

  print json_encode(array(WORKOUTS =>WorkoutDao::getAllForUser($_GET[ID_USER])));

}catch(Exception $e){
  print json_errors($e->getMessage());
}
?>
