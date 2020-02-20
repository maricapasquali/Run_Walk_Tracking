<?php
require_once("../utility.php");
require_once("../dao/UserDao.php");

try{
   $new_user = Request::getBody(SIGN_UP); 

    //$new_user = Request::getBody(SIGN_UP);print $new_user->toJson();
    print json_encode(array(SIGN_UP => UserDao::instance()->create($new_user)));

}catch(Exception $e){
  print json_errors($e);
}
?>
