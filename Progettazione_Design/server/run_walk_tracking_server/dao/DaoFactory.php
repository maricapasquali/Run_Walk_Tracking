<?php
require_once("interfaces.php");
//require_once("db_utility.php");

define("PARAMETER_NOT_VALID", "Parametro 'callback' non è un funzione. ");
class DaoFactory {

  private $host = "localhost";  // E' il server a cui ti vuoi connettere.
  private $database = "run_walk_tracking_db_server"; // Nome del database.
  private $user_ub = "sec_user_tracking"; // E' l'utente con cui ti collegherai al DB.
  private $password_db = "owUpTYWuQEagbc9J";  // Password di accesso al DB.

  private $connection;

  private static $instance;
  public static function instance(){
    if(self::$instance==null) self::$instance = new self();
    return self::$instance;
  }

  public function connect() {
    error_reporting(E_ERROR | E_PARSE);
    $this->connection = new mysqli($this->host, $this->user_ub, $this->password_db, $this->database);
    return !self::getErrorConnection();
  }

  public function getConnection(){
    return $this->connection;
  }

  public function startTransaction(){
      $this->connection->autocommit(false);
  }

  public function commitTransaction(){
    $this->connection->commit();
  }

  public function rollbackTransaction(){
    $this->connection->rollback();
    self::closeConnection();
  }

  public function getErrorConnection(){
    return $this->connection->error;
  }

  public function isDuplicate(){
    return  self::getErrorCodeConnection()==1062;
  }

  public function getErrorCodeConnection(){
    return $this->connection->errno;
  }

  public function closeConnection(){
    $this->connection->close();
  }

  function transaction($callback){
    try {
      if(self::connect())
      {
        self::startTransaction();

        if (is_callable($callback))
            $callback();
        else
          throw new Exception(PARAMETER_NOT_VALID);

        self::commitTransaction();
      }
    }catch (Exception $e) {
      self::rollbackTransaction();
      throw $e;
    }
    self::closeConnection();
    return true;
  }

  function selection($callback){
    if(self::connect()){

      if (is_callable($callback))
          $callback();
      else
        throw new Exception(PARAMETER_NOT_VALID);
    }
    self::closeConnection();
    return true;
  }

}
?>
