<?php
  require_once('../utility.php');
  if(!isset($_GET[ID])) die(json_errors(new UrlExeception()));
  require_once('../dao/UserDao.php');
  try{
    $user = UserDao::instance()->getUserForId($_GET[ID]);

    $date = new DateTime($user[BIRTH_DATE]);
  }catch(Exception $e){
    die(json_errors($e));
  }
?>
<!DOCTYPE html>
<html lang='it' dir='ltr'>

<head>
  <meta charset='utf-8'>
  <meta name='viewport' content='width=device-width, initial-scale=1.0'>
  <title>SignUp</title>
  <link rel='stylesheet' href='https://stackpath.bootstrapcdn.com/bootstrap/4.2.1/css/bootstrap.min.css' integrity='sha384-GJzZqFGwb1QTTN6wy59ffF1BuGJpLSa9DkKMp0DgiMDm4iYMj70gZWKYbI706tWS' crossorigin='anonymous'>
  <link rel='stylesheet' href='https://use.fontawesome.com/releases/v5.8.1/css/all.css' integrity='sha384-50oBUHEmvpQ+1lW4y57PTFmhCaXp0ML5d60M1M7uH2+nqUivzIebhndOJK28anvf' crossorigin='anonymous'>
  <script src="https://code.jquery.com/jquery-3.3.1.min.js" integrity="sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8=" crossorigin="anonymous"></script>

  <style media="screen">
    td,
    th {
      padding: 10px 20px 10px 10px;
    }
  </style>
</head>

<body>

  <div class='container-fluid'>
    <div class="col">
      <h1 class="mt-5 mb-5"> <?=APP_NAME?> </h1>
      <p> Dear <strong id="username"><?=$user[USERNAME]; ?></strong>, your account has been created.</p>
        <table id='table-info'>
          <tbody>
            <tr>
              <th>Name</th>
              <td id='name'><?=$user[NAME]; ?></td>
            </tr>
            <tr>
              <th>Last Name</th>
              <td id='lastName'><?=$user[LAST_NAME];?></td>
            </tr>
            <tr>
              <th>Gender</th>
              <td id='gender'><?=strtolower($user[GENDER]);?></td>
            </tr>
            <tr>
              <th>Birth Date</th>
              <td id='birth_date'><?=$date->format('m/d/Y');?></td>
            </tr>
            <tr>
              <th>Email</th>
              <td id='email'><?=$user[EMAIL];?></td>
            </tr>
            <tr>
              <th>City</th>
              <td id='city'><?=$user[CITY];?></td>
            </tr>
            <tr>
              <th>Phone</th>
              <td id='tel'><?=$user[PHONE];?></td>
            </tr>

          </tbody>
        </table>

      <div class="mt-4 mb-4">
        <p>To complete the registration you will have to use the Activation Code below when requested:</p>
        <strong id="token"></strong>
      </div>

      <p>Team <strong> <?=APP_NAME?></strong></p>
    </div>
  </div>
</body>

</html>
