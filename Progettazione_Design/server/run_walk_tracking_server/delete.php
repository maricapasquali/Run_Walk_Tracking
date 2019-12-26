<?php
require_once("utility.php");
require_once("dao/SessionDao.php");
require_once("dao/UserDao.php");
require_once("dao/WorkoutDao.php");
require_once("dao/WeightDao.php");
try{

  $body = bodyRequest();

  if(!isset($body[TOKEN]) || !isset($body[LAST_UPDATE]) || !isset($body[FILTER]) || !isset($body[DATA]))
  throw new Exception(URL_NOT_VALID);
  /*{
    "token" : "hfenogimewgowr23gèwe",
    "filter": "user",
    "data":{
      "id_weight": 1
    }
  }*/

  $id_user = SessionDao::getForToken($body[TOKEN])[ID_USER];
  if($id_user==NULL) throw new Exception(SESSION_TOKEN_NOT_VALID);

  if(count($body[DATA])<=0 && $body[FILTER]!= USER){
    print json_encode(array(DELETE =>false));
    return;
  }

  switch($body[FILTER]){
    case USER :
        $delete = UserDao::delete($id_user); //ok
        break;
    case WORKOUT:
        $delete = WorkoutDao::delete($body[DATA][ID_WORKOUT]); //ok
        break;
    case WEIGHT:
        $delete = WeightDao::delete($body[DATA][ID_WEIGHT]); //ok
        break;
    default:
        throw new Exception(FILTER_NOT_VALID);
        break;
  }

  print json_encode(array(DELETE => ($delete ? SessionDao::updateLastUpdate($body[LAST_UPDATE], $id_user) : false)));

}catch(Exception $e){
  print json_errors($e->getMessage());
}

?>
