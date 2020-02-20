<?php
require_once("../utility.php");
require_once("../dao/SessionDao.php");
require_once("../dao/UserDao.php");
require_once("../dao/SettingsDao.php");
require_once("../dao/WorkoutDao.php");

try{

   $userCredentials = Request::getBody(SIGN_IN);
   $id_user = UserDao::instance()->checkSignIn($userCredentials[USERNAME], $userCredentials[PASSWORD]);
   $session = SessionDao::instance()->checkForIdUser($id_user);
   $session[DEVICE] = $userCredentials[DEVICE];
   $session = SessionDao::instance()->updateAll($session);
   print json_encode(array(SESSION => getEncodedSession($session)));

}
catch(SessionTokenExeception $se){
  //FIRST_LOGIN
  print json_encode(array(FIRST_LOGIN => $session==NULL));
}
catch(Exception $e){
  print json_errors($e);
}
