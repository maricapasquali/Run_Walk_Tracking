<?php
require_once("../utility.php");
require_once("../dao/StatisticsDao.php");

try{
  $weight = bodyRequest();

  if(!isset($weight[ID_WEIGHT]))
  throw new Exception(URL_NOT_VALID);

  print json_encode(array(DELETE => StatisticsDao::deleteWeight($weight[ID_WEIGHT])));

}catch(Exception $e){
  print json_errors($e->getMessage());
}

 ?>
