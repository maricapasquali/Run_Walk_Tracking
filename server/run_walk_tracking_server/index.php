<?php
try{
require_once("utility.php");
require_once("dao/SessionDao.php");

print "BENVENUTO<br>";
/*
define("SESSION_ENCODED", "session_encoded");
define("SESSION_DECODED", "session_decoded");
$se = array(ID_USER => 22, TOKEN => "kq2sOan0e1umJO8azHDJb3LMwKXNLhxaxCYzhcqP7jRXHdYVm6J9fZ3x77HaP2fjV6mD4yXTSKk6kYS1lcrEwUqZLOv0VcYTxwY3",
LAST_UPDATE => 1578167181, DEVICE=>"ef62c1f173d2f81dc16779189c2dfdb6");
$token_session =  getEncodedSession($se);
$session = getDecodedSession($token_session);
print json_encode(array(SESSION_ENCODED => $token_session, SESSION_DECODED => $session ));


$session[TOKEN]=Session::createToken();
$sessionObj = new Session((object)$session);
print $sessionObj->toJson();
print json_encode(SessionDao::instance()->instance());
*/

}catch(Exception $e){
  print json_errors($e);
}
?>
