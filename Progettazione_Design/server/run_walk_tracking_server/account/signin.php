<?php
require_once("../utility.php");
require_once("../dao/SessionDao.php");
require_once("../dao/UserDao.php");
try{
  $userCredentials = bodyRequest();

  if(!isset($userCredentials[USERNAME]) ||
     !isset($userCredentials[PASSWORD]) )
      throw new Exception(URL_NOT_VALID);

   $user = UserDao::checkSignIn($userCredentials[USERNAME], $userCredentials[PASSWORD]);
   if($user[ID_USER]==NULL) throw new Exception(USER_NOT_FOUND);

   $session = SessionDao::checkForIdUser($user[ID_USER]);
   if($session==NULL)
   {
     //FIRST_LOGIN
     print json_encode(array(FIRST_LOGIN => $session==NULL));
   }else {
     $session = SessionDao::update($user[ID_USER]);
     print json_encode(array(SESSION => $session));
   }

}catch(Exception $e){
  print json_errors($e->getMessage());
}
