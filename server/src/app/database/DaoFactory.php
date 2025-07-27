<?php
namespace app\database;

use Exception;
use mysqli;

define("PARAMETER_NOT_VALID", "Parametro 'callback' non è un funzione. ");

class DaoFactory
{

  private $host;
  private $database;
  private $user_db;
  private $password_db;

  private $connection;

  private static $instance;

  public static function instance(): DaoFactory
  {
    if (self::$instance == null)
      self::$instance = new self();
    return self::$instance;
  }

  public function __construct()
  {
    $this->host = MYSQL_HOST;
    $this->database = MYSQL_DATABASE;
    $this->user_db = MYSQL_USER;
    $this->password_db = MYSQL_PASSWORD;
  }

  public function connect(): bool
  {
    error_reporting(E_ERROR | E_PARSE);
    $this->connection = new mysqli(
      $this->host,
      $this->user_db,
      $this->password_db,
      $this->database
    );
    return !self::getErrorConnection();
  }

  public function getConnection()
  {
    return $this->connection;
  }

  public function getErrorConnection()
  {
    return $this->connection->error;
  }

  public function isDuplicate(): bool
  {
    return $this->connection->errno == 1062;
  }

  public function closeConnection()
  {
    $this->connection->close();
  }

  public function transaction($callback): bool
  {
    try {
      if (self::connect()) {
        $this->connection->autocommit(false);

        if (!is_callable($callback))
          throw new Exception(PARAMETER_NOT_VALID);

        $callback();

        $this->connection->commit();
      }
    } catch (Exception $e) {
      $this->connection->rollback();
      throw $e;
    } finally {
      self::closeConnection();
    }
    return true;
  }

  public function selection($callback): bool
  {
    if (self::connect()) {

      if (!is_callable($callback))
        throw new Exception(PARAMETER_NOT_VALID);

      $callback();
    }
    self::closeConnection();
    return true;
  }

}


