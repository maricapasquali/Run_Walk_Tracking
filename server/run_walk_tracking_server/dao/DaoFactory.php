<?php
require_once("interfaces.php");
define("PARAMETER_NOT_VALID", "Parametro 'callback' non è un funzione. ");

class DaoFactory {

  private $host = "localhost";
  private $database = "id11865186_runwalktracking";
  private $user_db = "id11865186_sec_user_tracking";
  private $password_db = "owUpTYWuQEagbc9J";

  private $connection;

  private static $instance;
  public static function instance(){
    if(self::$instance==null) self::$instance = new self();
    return self::$instance;
  }

  public function connect() {
    error_reporting(E_ERROR | E_PARSE);
    $this->connection = new mysqli($this->host, $this->user_db,
                                   $this->password_db, $this->database);
    return !self::getErrorConnection();
  }

  public function getConnection(){
    return $this->connection;
  }

  public function getErrorConnection(){
    return $this->connection->error;
  }

  public function isDuplicate(){
    return  $this->connection->errno==1062;
  }

  public function closeConnection(){
    $this->connection->close();
  }

  public function transaction($callback){
    try {
      if(self::connect())
      {
        $this->connection->autocommit(false);

        if (!is_callable($callback))
          throw new Exception(PARAMETER_NOT_VALID);

        $callback();

        $this->connection->commit();
      }
    }catch (Exception $e) {
      $this->connection->rollback();
      self::closeConnection();
      throw $e;
    }
    self::closeConnection();
    return true;
  }

  public function selection($callback){
    if(self::connect()){

      if (!is_callable($callback))
        throw new Exception(PARAMETER_NOT_VALID);

      $callback();
    }
    self::closeConnection();
    return true;
  }

}
?>
