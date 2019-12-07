<?php
require_once("../utility.php");
require_once("../dao/WeightDao.php");

try{
  $changed_weight = bodyRequest();
  if(!isset($changed_weight[ID_WEIGHT]))
  throw new Exception(URL_NOT_VALID);

  print json_encode(array(UPDATE => count($changed_weight)<2 ? false : WeightDao::update($changed_weight)));

}catch(Exception $e){
  print json_errors($e->getMessage());
}

?>
