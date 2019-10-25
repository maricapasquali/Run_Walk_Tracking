<?php

require_once("../utility.php");
require_once("../dao/WeightDao.php");

try{
  $new_weight = bodyRequest();
  if(!isset($new_weight[ID_USER])||
     !isset($new_weight[DATE])||
     !isset($new_weight[VALUE])
  )
  throw new Exception(URL_NOT_VALID);

  print json_encode(array(WEIGHT =>WeightDao::create($new_weight)));


}catch(Exception $e){
  print json_errors($e->getMessage());
}

/*
INPUT
{
	"id_user": 38,
	"date": "2019-10-23",
	"value" : 71.5
}
*/
?>
