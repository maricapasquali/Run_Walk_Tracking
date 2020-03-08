<?php
require_once("DaoFactory.php");

class SessionDao implements ISessionDao{

  private $daoFactory;
  public function __construct(){
      $this->daoFactory = DaoFactory::instance();
  }

  private static $instance;
  public static function instance(){
    if(self::$instance==null) self::$instance = new self();
    return self::$instance;
  }

  public function create($id_user){
    if($this->daoFactory->transaction(function() use ($id_user){
      $stmt = $this->daoFactory->getConnection()->prepare("INSERT INTO session(id_user, token, last_update) VALUES (?, ?, ?)");
      if(!$stmt) throw new Exception("create session : Preparazione fallita. Errore: ". $this->daoFactory->getErrorConnection());
      $stmt->bind_param("isi", $id_user, getToken(100), current_unixdatetime());
      if(!$stmt->execute()) throw new Exception("create session : Selezione fallita. Errore: ". $this->daoFactory->getErrorConnection());
      $stmt->close();
    }))
    {
      return self::checkForIdUser($id_user);
    }

  }

  public function checkForIdUser($id_user){
    if($this->daoFactory->selection(function() use ($id_user, &$session){
          $stmt = $this->daoFactory->getConnection()->prepare("SELECT * FROM session WHERE id_user=?");
          if(!$stmt) throw new Exception("check session : Preparazione fallita. Errore: ". $this->daoFactory->getErrorConnection());
          $stmt->bind_param("i", $id_user);
          if(!$stmt->execute()) throw new Exception("check session : Selezione fallita. Errore: ". $this->daoFactory->getErrorConnection());
          $result = $stmt->get_result();
          $session = $result->fetch_assoc();
          $stmt->close();
          if($session==NULL) throw new SessionTokenExeception();
    }))
    {
      return $session;
    }

  }

  public function checkForToken($session_token){

    if($this->daoFactory->selection(function() use ($session_token, &$session){
            $stmt = $this->daoFactory->getConnection()->prepare("SELECT * FROM session WHERE token=?");
            if(!$stmt) throw new Exception("check session : Preparazione fallita. Errore: ". $this->daoFactory->getErrorConnection());
            $stmt->bind_param("s", $session_token);
            if(!$stmt->execute()) throw new Exception("check session : Selezione fallita. Errore: ". $this->daoFactory->getErrorConnection());
            $result = $stmt->get_result();
            $session = $result->fetch_assoc();
            $stmt->close();
            if($session==NULL) throw new SessionTokenExeception();
    }))
    {
      return $session;
    }
  }

  public function updateAll($old_session){

    if($this->daoFactory->transaction(function() use (&$old_session){
          $stmt = $this->daoFactory->getConnection()->prepare("UPDATE session SET token=?, device = ? WHERE id_user = ?");
          if(!$stmt) throw new Exception("update all session : Preparazione fallita. Errore: ". $this->daoFactory->getErrorConnection());
          $stmt->bind_param("ssi", getToken(100), $old_session[DEVICE],  $old_session[ID_USER]);
          if(!$stmt->execute()) throw new Exception("update all session : Selezione fallita. Errore: ". $this->daoFactory->getErrorConnection());
          $stmt->close();
    }))
    {
      return self::checkForIdUser($old_session[ID_USER]);
    }

  }

  public function update($id_user){

    if($this->daoFactory->transaction(function() use ($id_user){
          $stmt = $this->daoFactory->getConnection()->prepare("UPDATE session SET token=? WHERE id_user = ?");
          if(!$stmt) throw new Exception("update session : Preparazione fallita. Errore: ". $this->daoFactory->getErrorConnection());
          $stmt->bind_param("si", getToken(100),  $id_user);
          if(!$stmt->execute()) throw new Exception("update session : Selezione fallita. Errore: ". $this->daoFactory->getErrorConnection());
          $stmt->close();
    }))
    {
      return self::checkForIdUser($id_user);
    }

  }

  public function setLastUpdate($last_update, $id_user){
    return $this->daoFactory->transaction(function() use ($last_update, $id_user){
          $stmt = $this->daoFactory->getConnection()->prepare("UPDATE session SET last_update=? WHERE id_user = ?");
          if(!$stmt) throw new Exception("update session (last update): Preparazione fallita. Errore: ". $this->daoFactory->getErrorConnection());
          $stmt->bind_param("ii", $last_update, $id_user);
          if(!$stmt->execute()) throw new Exception("update session (last update): Selezione fallita. Errore: ". $this->daoFactory->getErrorConnection());
          $stmt->close();
    });

  }

  public function setNewDevice($device, $id_user){

    return $this->daoFactory->transaction(function() use ($device, $id_user){
            $stmt = $this->daoFactory->getConnection()->prepare("UPDATE session SET device=MD5(?) WHERE id_user = ?");
            if(!$stmt) throw new Exception("update session (device): Preparazione fallita. Errore: ". $this->daoFactory->getErrorConnection());
            $stmt->bind_param("si", $device, $id_user);
            if(!$stmt->execute()) throw new Exception("update session (device): Selezione fallita. Errore: ". $this->daoFactory->getErrorConnection());
            $stmt->close();
    });
  }

}

?>
