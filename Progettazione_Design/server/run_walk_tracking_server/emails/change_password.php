<?php
  require_once("../utility.php");
  require_once("../dao/DaoFactory.php");
  require_once("../dao/UserDao.php");
  if(!isset($_GET[C_KEY]) || (isset($_GET[C_KEY]) && empty($_GET[C_KEY])))
    die(json_errors(URL_NOT_VALID));

  function isExpired($key) {
    if(connect()){
      $stmt = getConnection()->prepare("SELECT end_validity from request_forgot_password where c_key=?");
      if(!$stmt) throw new Exception("Preparazione fallita. Error: " . $conn->error);
      $stmt->bind_param("s", $key);
      if(!$stmt->execute()) throw new Exception("Cancellazione fallita. Error: " . $conn->error);
      $stmt->bind_result($end_validity);
      $stmt->fetch();
      $stmt->close();
    }
    closeConnection();

    return current_datetime() >= $end_validity;
  }

  try{
      if(isExpired($_GET[C_KEY]))
      die(json_errors(LINK_EXPIRED));
  }catch(Exception $e){
      die(json_errors($e->getMessage()));
  }
?>
<!DOCTYPE html>
<html lang="it" dir="ltr">

  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Change Password</title>
    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css" integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">

    <!-- jQuery first, then Popper.js, then Bootstrap JS -->
    <script src="https://code.jquery.com/jquery-3.3.1.min.js" integrity="sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8=" crossorigin="anonymous"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.6/umd/popper.min.js" integrity="sha384-wHAiFfRlMFy6i5SRaxvfOCifBUQy1xHdJ/yoi7FRNXMRBu5WHdZYu1hA6ZOblgut" crossorigin="anonymous"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.2.1/js/bootstrap.min.js" integrity="sha384-B0UglyR+jN6CkvvICOB2joaf5I4l3gm9GU6Hc1og6Ls7i6U/mkkaduKaBhlAXv9k" crossorigin="anonymous"></script>
    <style media="screen">
      h1{
        font-size: 8vw;
        color:#2B2827; //#b45f06;
      }
      .form-control, button{
         background:#2B2827;// #b45f06;
         color:white;
      }
      label{
        color:#2B2827; //#b45f06;
      }

    </style>
  </head>

  <body>
    <!-- CAMBIO PASSWORD -->
    <div class="container-fluid">
        <div class="bg-faded px-4 py-3 my-3 mx-5">
          <div class="text-center">
            <h1 class="card-title d-inline-flex">Reset password </h1>
          </div>
              <div class="alert-container"> </div>
              <form id="change_password" class="form-horizontal">
                <div class="fields-for-password">
                  <div class="form-group">
                    <label class="control-label" for="username">Username</label>
                    <div class="class-new-password">
                      <input id="username" class="form-control" type="text" name="username" placeholder="insert" required>
                      <span class="feedback"></span>
                    </div>
                  </div>

                  <div class="form-group">
                    <label class="control-label" for="new-password">New Password</label>
                    <div class="class-new-password">
                      <input id="new-password" class="form-control" type="password" name="password" placeholder="insert" required>
                      <span class="feedback"></span>
                    </div>
                  </div>

                  <div class="form-group">
                    <label class="control-label" for="conf-new-password">Confirm Password</label>
                    <div class="class-conf-password">
                      <input id="conf-new-password" class="form-control" type="password" name="conf-password" placeholder="insert" required>
                      <span class="feedback"></span>
                    </div>
                  </div>


                  <div class="form-group text-center">
                      <button class="btn" name='change_password' type="button">Change</button>
                  </div>

                </div>

              </form>
        </div>
    </div>

    <template id="success_operation"><div class="alert alert-success" role="alert"></div></template>
    <template id="error_operation"><div class="alert alert-danger" role="alert"></div></template>

    <script src="../js/md5.min.js"></script>
    <script src="../js/utility.js"></script>
    <script>
      var _ERROR_PASSWORD_ = "Password inserita non è valida";
      function error(error){
        $("div.alert-container").empty();
        $("div.alert-container").append($($("#error_operation").html()).html(error));
      }

      function success(success){
        $("div.alert-container").empty();
        $("div.alert-container").append($($("#success_operation").html()).html(success));
      }

      $('#new-password').on('input', function(event) {
        var password = $(this);
        var conf_passw = $('#conf-new-password');
        if (password.val() != $('#conf-new-password').val()) {
          inValidInput(password, _ERROR_PASSWORD_);
          return;
        }
        validInput(conf_passw);
        validInput(password);
      });

      $('#conf-new-password').on('input', function(event) {
        var conf_passw = $(this);
        var password = $("#new-password");
        if ($("#new-password").val() != conf_passw.val()) {
          inValidInput(conf_passw, _ERROR_PASSWORD_);
          return;
        }
        validInput(conf_passw);
        validInput(password);
      });

      $('button[name="change_password"]').on('click', {
        form: $("#change_password")
      }, validateForm);

     $("button[name='change_password']").on("click", function(){
        if($("input").hasClass("is-valid")){
            var hash_md5_password = md5($("input#new-password").val());

             post_ajax_json("../account/password_forgot.php", {username: $("input#username").val(), password: hash_md5_password }, function(data){
                console.log(data);
                  if(data["Error"]){
                    error(data["Error"]);
                    $("#change_password").removeClass('was-validated');
                    $("input").removeClass("is-valid");
                  }else{
                    success(JSON.stringify(data));
                    $("#change_password").remove();
                }
              });
        }else {
            $("#change_password").removeClass('was-validated');
        }

      });

    </script>
  </body>

</html>
