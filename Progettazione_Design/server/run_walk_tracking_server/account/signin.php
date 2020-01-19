<?php
require_once("../utility.php");
require_once("../dao/SessionDao.php");
require_once("../dao/UserDao.php");
require_once("../dao/SettingsDao.php");
require_once("../dao/WorkoutDao.php");

try{
  //print json_encode(Request::getBody(SIGN_IN));

  $userCredentials = Request::getBody(SIGN_IN); 

   $id_user = UserDao::instance()->checkSignIn($userCredentials[USERNAME], $userCredentials[PASSWORD]);

   $session = SessionDao::instance()->checkForIdUser($id_user);

   $session = SessionDao::instance()->update($id_user);
   print json_encode(array(SESSION => $session));

}
catch(SessionTokenExeception $se){
  //FIRST_LOGIN
  print json_encode(array(FIRST_LOGIN => $session==NULL));
}
catch(Exception $e){
  print json_errors($e);
}
