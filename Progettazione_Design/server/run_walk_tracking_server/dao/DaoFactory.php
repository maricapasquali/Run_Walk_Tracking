<?php
define("HOST", "localhost"); // E' il server a cui ti vuoi connettere.
define("DATABASE", "id11865186_runwalktracking"); // Nome del database.

define("USER_UB", "id11865186_sec_user_tracking"); // E' l'utente con cui ti collegherai al DB.
define("PASSWORD_DB", "owUpTYWuQEagbc9J"); // Password di accesso al DB.


$conn;

function connect() {
  error_reporting(E_ERROR | E_PARSE);
  global $conn;
  $conn = new mysqli(HOST, USER_UB, PASSWORD_DB, DATABASE);
  return !$conn->connect_error;
}

function getConnection(){
  global $conn;
  return $conn;
}

function startTransaction(){
  global $conn;
  $conn->autocommit(false);
}

function commitTransaction(){
  global $conn;
  $conn->commit();
}

function rollbackTransaction(){
  global $conn;
  $conn->rollback();
  $conn->close();
}

function getErrorConnection(){
  global $conn;
  return $conn->error;
}

function closeConnection(){
  global $conn;
  $conn->close();
}

?>
