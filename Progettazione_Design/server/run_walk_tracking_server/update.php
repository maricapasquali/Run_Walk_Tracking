<?php
require_once("utility.php");
require_once("dao/SessionDao.php");
require_once("dao/UserDao.php");
require_once("dao/SettingsDao.php");
require_once("dao/WorkoutDao.php");
require_once("dao/WeightDao.php");
try{

  $body = Request::getBody(UPDATE);

  if(!isset($body[TOKEN]) || !isset($body[LAST_UPDATE]) || !isset($body[FILTER]) || !isset($body[DATA]))
  throw new UrlExeception(URL_NOT_VALID);
  /*{
    "token" : "hfenogimewgowr23gèwe",
    "last_update": 21312312,
    "filter": "user",
    "data":{
      "gender": "FEMALE",
      "email" : "carlo@gmail.com"
    }
  }*/

  $id_user = SessionDao::instance()->checkForToken($body[TOKEN])[ID_USER];

  if(count($body[DATA])<=0){
    print json_encode(array(UPDATE =>false));
    return;
  }
// TODO: FARE CONTROLLI
  switch($body[FILTER]){
    case USER :
        $update = UserDao::instance()->update($body[DATA], $id_user); // ok
        break;
    case SPORT :
        $update = SettingsDao::instance()->updateSportFor($body[DATA][VALUE], $id_user); //ok
        break;
    case TARGET :
        $update = SettingsDao::instance()->updateTargetFor($body[DATA][VALUE], $id_user); //ok
        break;
    case UNIT_DISTANCE :
        $update = SettingsDao::instance()->updateUnitDistanceFor($body[DATA][VALUE], $id_user); //ok
        break;
    case UNIT_WEIGHT :
        $update = SettingsDao::instance()->updateUnitWeightFor($body[DATA][VALUE], $id_user); //ok
        break;
    case UNIT_HEIGHT :
        $update = SettingsDao::instance()->updateUnitHeightFor($body[DATA][VALUE], $id_user); //ok
        break;
    case WORKOUT:
        $update = WorkoutDao::instance()->update($body[DATA], $id_user);  //ok
        break;
    case WEIGHT:
        $update = WeightDao::instance()->update($body[DATA], $id_user);  //ok
        break;
    default:
        throw new FilterExeception();
   }

   print json_encode(array(UPDATE => ($update ? SessionDao::instance()->setLastUpdate($body[LAST_UPDATE], $id_user) : false)));

}catch(Exception $e){
  print json_errors($e);
}
?>
