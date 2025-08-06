<?php

namespace utility;

/* UTILITY SESSION--- */

define("SEPARATOR", ".");

function encode($val): string
{
  return base64_encode(base64_encode($val));
}

function decode($val)
{
  return base64_decode(base64_decode($val));
}

function cast($strNum)
{
  return is_numeric($strNum) ? intval($strNum) : $strNum;
}

function getEncodedSession($session): string
{
  return encode(implode(
    SEPARATOR,
    [
      encode($session[ID_USER]),
      encode($session[TOKEN]),
      encode($session[LAST_UPDATE]),
      encode($session[DEVICE])
    ]
  ));
}

function getDecodedSession($token_session)
{
  return array_combine(
    [ID_USER, TOKEN, LAST_UPDATE, DEVICE],
    array_map(function ($value) {
      return cast(decode($value));
    }, explode(SEPARATOR, decode($token_session)))
  );
}

/* FINE --- */

function hashed_password($password)
{
  return password_hash($password, PASSWORD_BCRYPT);
}

function current_datetime()
{
  return date("Y-m-d H:i:s");
}

function current_unixdatetime()
{
  return strtotime(current_datetime());
}

function formatDate($date)
{
  return date("Y-m-d", strtotime($date));
}

function date_end_validity_link()
{
  // $datetime = '+30 minutes';
  $datetime = '+24 hours';
  return date('Y-m-d H:i:s', strtotime($datetime, strtotime(current_datetime())));
}

function json_response($data)
{
  header('Content-Type: application/json; charset=utf-8');
  return json_encode($data, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
}

function json_response_errors($ex)
{
  error_log($ex);
  return json_response([ERROR => [CODE => $ex->getCode(), DESCRIPTION => $ex->getMessage()]]);
}

function getToken($length): string
{
  $token = "";
  $codeAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  $codeAlphabet .= "abcdefghijklmnopqrstuvwxyz";
  $codeAlphabet .= "0123456789";
  $max = strlen($codeAlphabet); // edited

  for ($i = 0; $i < $length; $i++) {
    $token .= $codeAlphabet[random_int(0, $max - 1)];
  }
  return $token;
}
