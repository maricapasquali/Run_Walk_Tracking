<?php
require_once("utility.php");
require_once("dao/DaoFactory.php");

try{
    
    $user =  bodyRequest();

    if(!isset($user[IMEI])) throw new Exception(URL_NOT_VALID);

    try{
        if(connect()) {

            startTransaction();
             
            $sql = "UPDATE login SET id_phone=NULL WHERE id_user in (SELECT id_user FROM login WHERE id_phone=?)";
            $stmt = getConnection()->prepare($sql);
            if(!$stmt) throw new Exception("reset id_phone : Preparazione fallita. Errore: ". getErrorConnection());
            $stmt->bind_param("s" , $user[IMEI]);
            if(!$stmt->execute()) throw new Exception("reset id_phone : Update fallito. Errore: ". getErrorConnection());
            $stmt->close();
    
            commitTransaction();
            closeConnection();
            print json_encode(array(RESET=>true));
          
        }
    }catch(Exception $e){
        rollbackTransaction();
        closeConnection();
        print json_errors($e->getMessage());
    }
    
}catch (Exception $e) {
    print json_errors($e->getMessage());
}

?>
