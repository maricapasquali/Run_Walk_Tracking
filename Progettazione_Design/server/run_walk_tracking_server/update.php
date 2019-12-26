<?php
require_once("utility.php");
require_once("dao/SessionDao.php");
require_once("dao/UserDao.php");
require_once("dao/SettingsDao.php");
require_once("dao/WorkoutDao.php");
require_once("dao/WeightDao.php");
try{

  $body = bodyRequest();

  if(!isset($body[TOKEN]) || !isset($body[LAST_UPDATE]) || !isset($body[FILTER]) || !isset($body[DATA]))
  throw new Exception(URL_NOT_VALID);
  /*{
    "token" : "hfenogimewgowr23gèwe",
    "last_update": 21312312,
    "filter": "user",
    "data":{
      "gender": "FEMALE",
      "email" : "carlo@gmail.com"
    }
  }*/

  $id_user = SessionDao::getForToken($body[TOKEN])[ID_USER];
  if($id_user==NULL) throw new Exception(SESSION_TOKEN_NOT_VALID);

  if(count($body[DATA])<=0){
    print json_encode(array(UPDATE =>false));
    return;
  }

  switch($body[FILTER]){
    case USER :
        $update = UserDao::update($body[DATA], $id_user); // ok
        break;
    case SPORT :
        $update = SettingsDao::updateSportFor($body[DATA], $id_user); //ok
        break;
    case TARGET :
        $update = SettingsDao::updateTargetFor($body[DATA], $id_user); //ok
        break;
    case UNIT_DISTANCE :
        $update = SettingsDao::updateUnitDistanceFor($body[DATA], $id_user); //ok
        break;
    case UNIT_WEIGHT :
        $update = SettingsDao::updateUnitWeightFor($body[DATA], $id_user); //ok
        break;
    case UNIT_HEIGHT :
        $update = SettingsDao::updateUnitHeightFor($body[DATA], $id_user); //ok
        break;
    case WORKOUT:
        $update = WorkoutDao::update($body[DATA], $id_user);  //ok
        break;
    case WEIGHT:
        $update = WeightDao::update($body[DATA], $id_user);  //ok
        break;
    default:
        throw new Exception(FILTER_NOT_VALID);
        break;
   }

   print json_encode(array(UPDATE => ($update ? SessionDao::updateLastUpdate($body[LAST_UPDATE], $id_user) : false)));

}catch(Exception $e){
  print json_errors($e->getMessage());
}

?>
