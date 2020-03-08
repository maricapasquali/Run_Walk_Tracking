<?php
require_once("utility.php");
require_once("dao/SessionDao.php");
require_once("dao/UserDao.php");
require_once("dao/SettingsDao.php");
require_once("dao/WorkoutDao.php");
require_once("dao/WeightDao.php");

try{

  $body = Request::getBody(INSERT);
  /*
  {
  	"token": "3AnDPoEE0ncGOKqlq5saP9ptjHhEVJZ1qSqEk4RXaQTIDqbyMq6B0Dnp6WMTIKhbjGbg5Ug2SuYtIync515cZRutGCrxSDAPlqaW",
    "last_update": 1212121,
  "filter": "workout",
	"data":{
		 "id_workout": 1,
		 "date":"{% now 'millis', '12 25 2019' %}",
		 "duration": 1500,
		 "sport": "RUN"
	}
  {
	"token": "3AnDPoEE0ncGOKqlq5saP9ptjHhEVJZ1qSqEk4RXaQTIDqbyMq6B0Dnp6WMTIKhbjGbg5Ug2SuYtIync515cZRutGCrxSDAPlqaW",
    "last_update": 1212121,
	"filter": "weight",
	"data":{
		 "id_weight": 23,
		 "date":"2019-12-29",
		 "value": 50.0
	}
}
*/

  $id_user = SessionDao::instance()->checkForToken($body[TOKEN])[ID_USER];

  if(count($body[DATA])<=0){
    print json_encode(array(INSERT =>false));
    return;
  }
  $body[DATA][ID_USER] = $id_user;
  switch($body[FILTER]){
    case WORKOUT:
        if(!isset($body[DATA][ID_USER])||!isset($body[DATA][DATE])||!isset($body[DATA][SPORT]) ||!isset($body[DATA][DURATION]))
        throw new DataExeception(WORKOUT);
        $insert = WorkoutDao::instance()->create($body[DATA]);  //ok
      //  print json_encode(new Workout((object)$body[DATA]));
        break;
    case WEIGHT:
        if(!isset($body[DATA][ID_USER])||  !isset($body[DATA][DATE])|| !isset($body[DATA][VALUE]))
        throw new DataExeception(WEIGHT);
        $insert = WeightDao::instance()->create($body[DATA]);  //ok
        //print json_encode(new Weight((object)$body[DATA]));
        break;
    default:
        throw new FilterExeception();
  }

  print json_encode(array(INSERT => ($insert ? SessionDao::instance()->setLastUpdate($body[LAST_UPDATE], $id_user) : false)));

}catch(Exception $e){
  print json_errors($e);
}

?>
