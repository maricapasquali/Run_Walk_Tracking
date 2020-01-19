<?php
require_once("../utility.php");
require_once("../dao/SessionDao.php");
require_once("../dao/UserDao.php");
require_once("../dao/SettingsDao.php");
require_once("../dao/WeightDao.php");
require_once("../dao/WorkoutDao.php");
try{
   $userCredentials = Request::getBody(FIRST_LOGIN);

   $id_user = UserDao::instance()->checkSignUp($userCredentials);

   $session = SessionDao::instance()->create($id_user, $userCredentials[DEVICE]);

   print json_encode(array(SESSION => getEncodedSession($session),
                           DATA =>UserDao::instance()->allData($session[TOKEN], $userCredentials[DEVICE])));

 }catch(Exception $e){
  print json_errors($e);
}
?>
