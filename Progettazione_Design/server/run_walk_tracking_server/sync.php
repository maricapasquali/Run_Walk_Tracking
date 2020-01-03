<?php
require_once("utility.php");
require_once("dao/SessionDao.php");
require_once("dao/UserDao.php");
require_once("dao/SettingsDao.php");
require_once("dao/WeightDao.php");
require_once("dao/WorkoutDao.php");
require_once("exception/Exceptions.php");

function sendData($session, $device){
  print json_encode(array(STATE => array( CODE => 1 ,
                                          DESCRIPTION => NO_CONSISTENT_RECEIVE_DATA,
                                          DATA => array(SESSION => $session)+UserDao::allData($session[TOKEN], $device))));
}


try{
    $code = bodyRequest();
    if(!isset($code[TOKEN])||
       !isset($code[LAST_UPDATE])||
       !isset($code[DB_EXIST]) ||
       !isset($code[DEVICE]))
        throw new UrlExeception();
    $session = SessionDao::checkForToken($code[TOKEN]);
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
