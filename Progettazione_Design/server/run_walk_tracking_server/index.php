<?php
try{

  print "BENVENUTO";

}catch(Exception $e){
  print json_errors($e->getMessage());
}
?>
