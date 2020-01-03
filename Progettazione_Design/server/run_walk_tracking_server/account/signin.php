<?php
require_once("../utility.php");
require_once("../dao/SessionDao.php");
require_once("../dao/UserDao.php");
require_once("../dao/SettingsDao.php");
require_once("../dao/WorkoutDao.php");
require_once("../exception/Exceptions.php");
try{
  $userCredentials = bodyRequest();

  if(!isset($userCredentials[USERNAME]) ||
     !isset($userCredentials[PASSWORD]) )
      throw new UrlExeception();

   $id_user = UserDao::checkSignIn($userCredentials[USERNAME], $userCredentials[PASSWORD]);

   $session = SessionDao::checkForIdUser($id_user);

   $session = SessionDao::update($id_user);
   print json_encode(array(SESSION => $session));
}
catch(SessionTokenExeception $se){
  //FIRST_LOGIN
  print json_encode(array(FIRST_LOGIN => $session==NULL));
}
catch(Exception $e){
  print json_errors($e);
}
