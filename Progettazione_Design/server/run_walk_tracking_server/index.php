<?php
try{
require_once("utility.php");
require_once("dao/SessionDao.php");
/* UTILITY --- */
define("SEPARATOR", ".");

function encode($val){
  return base64_encode(base64_encode($val));
}

function decode($val){
  return base64_decode(base64_decode($val));
}

function cast($strNum) {
  return is_numeric($strNum) ? intval($strNum): $strNum;
}
/* FINE --- */

//print "BENVENUTO<br>";
function getTokenSession($id_user, $last_update, $device){
   // $device ==  md5(md5(device)) : il primo md5 applicato dal client e il secondo dal server
  return encode(implode(SEPARATOR, array(encode($id_user), encode($last_update), encode($device))));
}

function getSession($token_session){
  return array_combine(array(ID_USER, LAST_UPDATE, DEVICE),
                       array_map(function($value){
                                   return cast(decode($value));
                       }, explode(SEPARATOR, decode($token_session))));
}

$token_session =  getTokenSession(22, 1578167181, "ef62c1f173d2f81dc16779189c2dfdb6");
$session = getSession($token_session);
print json_encode(array(TOKEN => $token_session, SESSION => $session ));

/*
$session[TOKEN]=Session::createToken();
$sessionObj = new Session((object)$session);
print $sessionObj->toJson();
print json_encode(SessionDao::instance()->instance());
*/

}catch(Exception $e){
  print json_errors($e);
}
?>
