<?php
require_once("../utility.php");
require_once("../dao/SessionDao.php");
require_once("../dao/UserDao.php");
require_once("../exception/Exceptions.php");

try{
  $body = bodyRequest();
  if(!isset($body[TOKEN]) ||
     !isset($body[IMG]) )
      throw new UrlExeception();

  $id_user= SessionDao::checkForToken($body[TOKEN])[ID_USER];

  print json_encode(UserDao::getImageProfileForIdUserAndName($id_user, $body[IMG]));

}catch(Exception $e){
  print json_errors($e);
}

?>
