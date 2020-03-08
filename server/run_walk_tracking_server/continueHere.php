<?php

require_once("utility.php");
require_once("dao/SessionDao.php");

try{

  $body = Request::get(_CONTINUE_);
  $session = getDecodedSession($body[SESSION]);
  $id_user = SessionDao::instance()->checkForIdUser($session[ID_USER])[ID_USER];
  if($id_user != $session[ID_USER]) throw new UserExeception();
  $new_session = SessionDao::instance()->updateAll($session);
  $new_session[LAST_UPDATE] = $session[LAST_UPDATE];

  print json_encode(array(SESSION => getEncodedSession($new_session)));

} catch(Exception $e){
    print json_errors($e);
}

?>
