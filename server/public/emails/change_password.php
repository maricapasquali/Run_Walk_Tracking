<?php

require __DIR__ . '/../../vendor/autoload.php';

use app\exceptions\LinkExpiredException;
use app\exceptions\UrlException;
use app\request\Request;
use app\database\DaoFactory;
use function utility\current_datetime;
use function utility\json_response_errors;

try {
  Request::checkMethod('GET');

  if (!isset($_GET[C_KEY]))
    throw new UrlException();

  $dao = DaoFactory::instance();
  $dao->selection(function () use ($dao, &$end_validity) {
    $stmt = $dao->getConnection()->prepare("SELECT end_validity from request_forgot_password where c_key=?");
    if (!$stmt)
      throw new Exception("Preparazione fallita. Error: " . $dao->getErrorConnection());
    $stmt->bind_param("s", $_GET[C_KEY]);
    if (!$stmt->execute())
      throw new Exception("Cancellazione fallita. Error: " . $dao->getErrorConnection());
    $stmt->bind_result($end_validity);
    $stmt->fetch();
    $stmt->close();
  });
  $isExpired = current_datetime() >= $end_validity;
  if ($isExpired)
    throw new LinkExpiredException();
} catch (Exception $e) {
  if ($e instanceof UrlException)
    http_response_code(400);
  elseif ($e instanceof LinkExpiredException)
    http_response_code(403);
  else
    http_response_code(500);
  print json_response_errors($e);
  die();
}
?>
<!DOCTYPE html>
<html lang="it" dir="ltr">

    <head>
      <meta charset="utf-8">
      <meta name="viewport" content="width=device-width, initial-scale=1.0">

      <title>Change Password</title>

      <!-- jQuery JS -->
      <script src="https://code.jquery.com/jquery-3.3.1.min.js"
        integrity="sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8=" crossorigin="anonymous"></script>

      <!-- Bootstrap CSS -->
      <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css"
        integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">

      <!-- Custom CSS -->
      <link media="screen" rel="stylesheet" href="/assets/css/emails/change_password.css">
    </head>

    <body>
      <!-- change PASSWORD -->
      <div class="container">
        <div class="bg-faded px-4 py-3 my-3 mx-5">
          <div class="text-center">
            <h2 class="card-title d-inline-flex"> <?= APP_NAME ?> </h2>
          </div>
          <div class="text-center">
            <h3 class="card-title d-inline-flex"> Reset password </h3>
          </div>
          <div class="alert-container"> </div>
          <form id="change_password" class="form-horizontal">
            <div class="fields-for-password">
              <div class="form-group">
                <label class="control-label" for="username">Username *</label>
                <div class="class-new-password">
                  <input id="username" class="form-control" type="text" name="username" placeholder="insert" required>
                  <span class="feedback"></span>
                </div>
              </div>

              <div class="form-group">
                <label class="control-label" for="new-password">New Password *</label>
                <div class="class-new-password">
                  <input id="new-password" class="form-control" type="password" name="password" placeholder="insert"
                    required>
                  <span class="feedback"></span>
                </div>
              </div>

              <div class="form-group">
                <label class="control-label" for="conf-new-password">Confirm Password *</label>
                <div class="class-conf-password">
                  <input id="conf-new-password" class="form-control" type="password" name="conf-password"
                    placeholder="insert" required>
                  <span class="feedback"></span>
                </div>
              </div>
              <div class="form-group d-flex justify-content-between">
                <button class="btn btn-secondary" name='reset_form' type="reset">Reset</button>
                <button class="btn btn-primary" name='change_password' type="button">Change</button>
              </div>
            </div>
          </form>
        </div>
      </div>

      <template id="success_operation">
        <div class="alert alert-success" role="alert"></div>
      </template>
      <template id="error_operation">
        <div class="alert alert-danger" role="alert"></div>
      </template>

      <script src="/assets/js/md5.min.js"></script>
      <script src="/assets/js/utility.js"></script>
      <script src="/assets/js/emails/change_password.js"></script>
    </body>

</html>