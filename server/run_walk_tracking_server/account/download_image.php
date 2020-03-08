<?php
require_once("../utility.php");
require_once("../dao/SessionDao.php");
require_once("../dao/UserDao.php");

try{
  $body = Request::getBody(DOWNLOAD_IMAGE);

  $id_user= SessionDao::instance()->checkForToken($body[TOKEN])[ID_USER];

  print json_encode(UserDao::instance()->getImageProfileForIdUserAndName($id_user, $body[IMG]));

}catch(Exception $e){
  print json_errors($e);
}

?>
