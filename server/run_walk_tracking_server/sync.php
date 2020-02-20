<?php
require_once("utility.php");
require_once("dao/SessionDao.php");
require_once("dao/UserDao.php");
require_once("dao/SettingsDao.php");
require_once("dao/WeightDao.php");
require_once("dao/WorkoutDao.php");

function sendData($session, $device){
  print json_encode(array(STATE => array( CODE => 1 ,
                                          DESCRIPTION => NO_CONSISTENT_RECEIVE_DATA,
                                          DATA => array(SESSION => getEncodedSession($session)) +
                                                  UserDao::instance()->allData($session[TOKEN], $device))));
}

try{
    $code = Request::getBody(SYNC);

    $session = SessionDao::instance()->checkForToken($code[TOKEN]);
    if($code[DB_EXIST]){
      if($code[LAST_UPDATE] == $session[LAST_UPDATE])
      {
        print json_encode(array(STATE => array( CODE => 0 , DESCRIPTION => CONSISTENT)));
      }
      else if($code[LAST_UPDATE] < $session[LAST_UPDATE]) // receive data from server
      {
        sendData($session, $code[DEVICE]);
      }
      else { // send data to server
          print json_encode(array(STATE => array( CODE=> 2 , DESCRIPTION => NO_CONSISTENT_SEND_DATA)));
      }
    }else{
        sendData($session, $code[DEVICE]);
    }

 }
 catch(Exception $e){
  print json_errors($e);
 }

?>
