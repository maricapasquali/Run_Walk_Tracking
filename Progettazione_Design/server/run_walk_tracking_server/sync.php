<?php
require_once("utility.php");
require_once("dao/SessionDao.php");
require_once("dao/UserDao.php");
require_once("dao/SettingsDao.php");
require_once("dao/StatisticsDao.php");
require_once("dao/WorkoutDao.php");
try{
    $code = bodyRequest();
    if(!isset($code[TOKEN])|| !isset($code[LAST_UPDATE])) throw new Exception(URL_NOT_VALID);

    $session = SessionDao::checkForToken($code[TOKEN]);
    if($session == NULL)  throw new Exception(SESSION_TOKEN_NOT_VALID);

    if($code[LAST_UPDATE] == $session[LAST_UPDATE])
    {
      print json_encode(array(STATE => array( CODE => 0 , DESCRIPTION => CONSISTENT)));
    }
    else if($code[LAST_UPDATE] < $session[LAST_UPDATE])
    {
      print json_encode(array(STATE => array( CODE => 1 ,
                                              DESCRIPTION => array(SESSION => $session,
                                                                   DATA =>UserDao::allData($session[TOKEN])))));

    }
    else {
        print json_encode(array(STATE => array( CODE=> 2 , DESCRIPTION => NO_CONSISTENT)));
    }

 }catch(Exception $e){
  print json_errors($e->getMessage());
 }


?>
