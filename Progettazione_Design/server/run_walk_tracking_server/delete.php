<?php
require_once("utility.php");
require_once("dao/SessionDao.php");
require_once("dao/UserDao.php");
require_once("dao/WorkoutDao.php");
require_once("dao/WeightDao.php");
try{

  $body = Request::getBody(DELETE);

  /*{
    "token" : "hfenogimewgowr23gèwe",
    "filter": "user",
    "data":{
      "id_weight": 1
    }
  }*/
  $id_user = SessionDao::instance()->checkForToken($body[TOKEN])[ID_USER];
  $delete = false;
  switch($body[FILTER]){
    case USER :
        if(count($body[DATA])>0)
          throw new DataExeception(USER);
        $delete = UserDao::instance()->delete($id_user); //ok
        break;
    case WORKOUT:
        if(!isset($body[DATA][ID_WORKOUT]))
        throw new DataExeception(WORKOUT);

        $delete = WorkoutDao::instance()->delete($body[DATA][ID_WORKOUT]); //ok
        break;
    case WEIGHT:

        if(!isset($body[DATA][ID_WEIGHT]))
        throw new DataExeception(WEIGHT);

        $delete = WeightDao::instance()->delete($body[DATA][ID_WEIGHT]); //ok
        break;
    default:
        throw new FilterExeception();
  }
  if(count($body[DATA])<=0 && $body[FILTER]!= USER){
    print json_encode(array(DELETE =>false));
    return;
  }
  print json_encode(array(DELETE => ($delete ? SessionDao::instance()->setLastUpdate($body[LAST_UPDATE], $id_user) : false)));

}catch(Exception $e){
  print json_errors($e);
}

?>
