<?php
require_once("../utility.php");
require_once("../dao/SessionDao.php");
require_once("../dao/UserDao.php");
require_once("../dao/SettingsDao.php");
require_once("../dao/WeightDao.php");
require_once("../dao/WorkoutDao.php");
require_once("../exception/Exceptions.php");
try{
  $userCredentials = bodyRequest();
  if(!isset($userCredentials[USERNAME]) ||
     !isset($userCredentials[PASSWORD]) ||
     !isset($userCredentials[TOKEN]) ||
     !isset($userCredentials[DEVICE]))
      throw new UrlExeception();

   $id_user = UserDao::checkSignUp($userCredentials);

   $session = SessionDao::create($id_user, null);//, $userCredentials[DEVICE]);

   print json_encode(array(SESSION => $session,
                           DATA =>UserDao::allData($session[TOKEN], $userCredentials[DEVICE])));

 }catch(Exception $e){
  print json_errors($e);
}
?>
