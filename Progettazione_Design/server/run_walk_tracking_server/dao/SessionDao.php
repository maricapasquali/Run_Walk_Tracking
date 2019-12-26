<?php
require_once("DaoFactory.php");

class SessionDao {

  static function create($id_user){

    try {
      if(connect())
      {
        startTransaction();

        $stmt = getConnection()->prepare("INSERT INTO session(id_user, token, last_update) VALUES (?, ?, ?)");
        if(!$stmt) throw new Exception("create session : Preparazione fallita. Errore: ". getErrorConnection());
        $stmt->bind_param("isi", $id_user, getToken(100), current_unixdatetime());
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
    return self::getForIdUser($id_user);
  }

  static function checkForIdUser($id_user){
    $session = array();
    if(connect()){
        $stmt = getConnection()->prepare("SELECT count(*) as row FROM session WHERE id_user=?");
        if(!$stmt) throw new Exception("check session : Preparazione fallita. Errore: ". getErrorConnection());
        $stmt->bind_param("i", $id_user);
        if(!$stmt->execute()) throw new Exception("check session : Selezione fallita. Errore: ". getErrorConnection());
        $stmt->bind_result($row);
        $stmt->fetch();
        $stmt->close();
        if($row==NULL) return NULL;
    }
    closeConnection();
    return self::getForIdUser($id_user);
  }

  static function checkForToken($session_token){
    $session = array();
    if(connect()){
        $stmt = getConnection()->prepare("SELECT count(*) as row FROM session WHERE token=?");
        if(!$stmt) throw new Exception("check session : Preparazione fallita. Errore: ". getErrorConnection());
        $stmt->bind_param("s", $session_token);
        if(!$stmt->execute()) throw new Exception("check session : Selezione fallita. Errore: ". getErrorConnection());
        $stmt->bind_result($row);
        $stmt->fetch();
        $stmt->close();
        if($row==NULL) return NULL;
    }
    closeConnection();
    return self::getForToken($session_token);
  }

  static function getForToken($session_token){
    $session = array();
    if(connect()){
        $stmt = getConnection()->prepare("SELECT * FROM session WHERE token=?");
        if(!$stmt) throw new Exception("session : Preparazione fallita. Errore: ". getErrorConnection());
        $stmt->bind_param("s", $session_token);
        if(!$stmt->execute()) throw new Exception("session : Selezione fallita. Errore: ". getErrorConnection());
        $result = $stmt->get_result();
        $session = $result->fetch_assoc();
        $stmt->close();

        if($session==NULL) return NULL;
    }
    closeConnection();
    return $session;
  }

  static function getForIdUser($id_user){
    $session = array();
    if(connect()){
        $stmt = getConnection()->prepare("SELECT * FROM session WHERE id_user=?");
        if(!$stmt) throw new Exception("session : Preparazione fallita. Errore: ". getErrorConnection());
        $stmt->bind_param("i", $id_user);
        if(!$stmt->execute()) throw new Exception("session : Selezione fallita. Errore: ". getErrorConnection());
        $result = $stmt->get_result();
        $session = $result->fetch_assoc();
        $stmt->close();

        if($session==NULL) return NULL;
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
    return self::getForIdUser($id_user);
  }

  static function updateLastUpdate($last_update, $id_user){
    try {
      if(connect())
      {
        startTransaction();

        $stmt = getConnection()->prepare("UPDATE session SET last_update=? WHERE id_user = ?");
        if(!$stmt) throw new Exception("update session : Preparazione fallita. Errore: ". getErrorConnection());
        $stmt->bind_param("ii", $last_update, $id_user);
        if(!$stmt->execute()) throw new Exception("update session : Selezione fallita. Errore: ". getErrorConnection());
        if(!$stmt->affected_rows) return;
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
