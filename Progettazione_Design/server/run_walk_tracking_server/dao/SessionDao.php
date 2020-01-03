<?php
require_once("DaoFactory.php");

class SessionDao {

  static function create($id_user, $device){

    try {
      if(connect())
      {
        startTransaction();
        $stmt = getConnection()->prepare("INSERT INTO session(id_user, device, token, last_update) VALUES (?, MD5(?), ?, ?)");
        if(!$stmt) throw new Exception("create session : Preparazione fallita. Errore: ". getErrorConnection());
        $stmt->bind_param("issi", $id_user, $device, getToken(100), current_unixdatetime());
        if(!$stmt->execute()) throw new Exception("create session : Selezione fallita. Errore: ". getErrorConnection());
        if(!$stmt->affected_rows) return;
        $stmt->close();

        commitTransaction();
      }
    }catch (Exception $e) {
      rollbackTransaction();
      throw new Exception($e->getMessage());
    }
    closeConnection();
    return self::checkForIdUser($id_user);
  }

  static function checkForIdUser($id_user){
    if(connect()){
        $stmt = getConnection()->prepare("SELECT * FROM session WHERE id_user=?");
        if(!$stmt) throw new Exception("check session : Preparazione fallita. Errore: ". getErrorConnection());
        $stmt->bind_param("i", $id_user);
        if(!$stmt->execute()) throw new Exception("check session : Selezione fallita. Errore: ". getErrorConnection());
        $result = $stmt->get_result();
        $session = $result->fetch_assoc();
        $stmt->close();
        if($session==NULL) throw new SessionTokenExeception();
    }
    closeConnection();
    return $session;
  }

  static function checkForToken($session_token){
    if(connect()){
        $stmt = getConnection()->prepare("SELECT * FROM session WHERE token=?");
        if(!$stmt) throw new Exception("check session : Preparazione fallita. Errore: ". getErrorConnection());
        $stmt->bind_param("s", $session_token);
        if(!$stmt->execute()) throw new Exception("check session : Selezione fallita. Errore: ". getErrorConnection());
        $result = $stmt->get_result();
        $session = $result->fetch_assoc();
        $stmt->close();
        if($session==NULL) throw new SessionTokenExeception();
    }
    closeConnection();
    return $session;
  }

  static function update($id_user){

    try {
      if(connect())
      {
        startTransaction();

        $stmt = getConnection()->prepare("UPDATE session SET token=? WHERE id_user = ?");
        if(!$stmt) throw new Exception("update session : Preparazione fallita. Errore: ". getErrorConnection());
        $stmt->bind_param("si", getToken(100),  $id_user);
        if(!$stmt->execute()) throw new Exception("update session : Selezione fallita. Errore: ". getErrorConnection());

        $stmt->close();

        commitTransaction();
      }
    }catch (Exception $e) {
      rollbackTransaction();
      throw new Exception($e->getMessage());
    }
    closeConnection();
    return self::checkForIdUser($id_user);
  }

  static function setLastUpdate($last_update, $id_user){
    try {
      if(connect())
      {
        startTransaction();

        $stmt = getConnection()->prepare("UPDATE session SET last_update=? WHERE id_user = ?");
        if(!$stmt) throw new Exception("update session (last update): Preparazione fallita. Errore: ". getErrorConnection());
        $stmt->bind_param("ii", $last_update, $id_user);
        if(!$stmt->execute()) throw new Exception("update session (last update): Selezione fallita. Errore: ". getErrorConnection());
        $stmt->close();

        commitTransaction();
      }
    }catch (Exception $e) {
      rollbackTransaction();
      throw new Exception($e->getMessage());
    }
    closeConnection();
    return true;
  }

  static function setNewDevice($device, $id_user){
    try {
      if(connect())
      {
        startTransaction();

        $stmt = getConnection()->prepare("UPDATE session SET device=MD5(?) WHERE id_user = ?");
        if(!$stmt) throw new Exception("update session (device): Preparazione fallita. Errore: ". getErrorConnection());
        $stmt->bind_param("si", $device, $id_user);
        if(!$stmt->execute()) throw new Exception("update session (device): Selezione fallita. Errore: ". getErrorConnection());
        $stmt->close();

        commitTransaction();
      }
    }catch (Exception $e) {
      rollbackTransaction();
      throw new Exception($e->getMessage());
    }
    closeConnection();
    return true;
  }

}

?>
