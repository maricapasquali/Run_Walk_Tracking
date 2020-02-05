<?php require_once('../utility.php');?>
<!DOCTYPE html>
<html lang='it' dir='ltr'>

<head>
  <meta charset='utf-8'>
  <meta name='viewport' content='width=device-width, initial-scale=1.0'>
  <title>Email Forgot Password</title>
  <link rel='stylesheet' href='https://stackpath.bootstrapcdn.com/bootstrap/4.2.1/css/bootstrap.min.css' integrity='sha384-GJzZqFGwb1QTTN6wy59ffF1BuGJpLSa9DkKMp0DgiMDm4iYMj70gZWKYbI706tWS' crossorigin='anonymous'>
  <link rel='stylesheet' href='https://use.fontawesome.com/releases/v5.8.1/css/all.css' integrity='sha384-50oBUHEmvpQ+1lW4y57PTFmhCaXp0ML5d60M1M7uH2+nqUivzIebhndOJK28anvf' crossorigin='anonymous'>
</head>

<body>

  <div class='container-fluid'>

    <div class="col">
      <h1 class="mt-5 mb-5"> <?= APP_NAME ?> </h1>
      <h1 class="mt-5 mb-5"> Reset password?</h1>
      <p>Dear User, <br /> if you have requested a password reset, click on the button below. </p>
      <p>Otherwise, ignore this email.</p>
      <div class="mt-5 mb-5">
        <p><a id='link_change_password' class="btn btn-primary">Click here</a> </p>
        <strong>ATTENTION:<p> The link will be valid only for 24 hours after receiving this email. </p></strong>
      </div>
      <p>Team <strong><?= APP_NAME ?></strong></p>
    </div>
  </div>
</body>

</html>
