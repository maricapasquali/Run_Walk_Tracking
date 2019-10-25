<?php
require_once("../utility.php");
require_once("../dao/StatisticsDao.php");

try{
  if(!isset($_GET[ID_USER]) || !isset($_GET[FILTER])) throw new Exception(URL_NOT_VALID);

  switch($_GET[FILTER]){
    case MIDDLE_SPEED:
      print json_encode(array(MIDDLE_SPEEDS => StatisticsDao::getAllMiddleSpeedFor($_GET[ID_USER])));
    break;
    case CALORIES:
      print json_encode(array(CALORIES =>StatisticsDao::getAllCaloriesFor($_GET[ID_USER])));
    break;
    case DISTANCE:
      print json_encode(array(DISTANCES => StatisticsDao::getAllDistanceFor($_GET[ID_USER])));
    break;
    case WEIGHT:
      print json_encode(array(WEIGHTS =>StatisticsDao::getAllWeightFor($_GET[ID_USER])));
    break;
    default: throw new Exception("Filtro non corretto");
    break;
  }
}catch(Exception $e){
  print json_errors($e->getMessage());
}
?>
