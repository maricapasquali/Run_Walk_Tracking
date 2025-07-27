<?php
require __DIR__ . '/../vendor/autoload.php';
?>

<!DOCTYPE html>
<html lang='it' dir='ltr'>

    <head>
        <meta charset='utf-8'>
        <meta name='viewport' content='width=device-width, initial-scale=1.0'>
        <title> <?= APP_NAME ?> Server </title>
        <!-- Bootstrap CSS -->
        <link rel='stylesheet' href='https://stackpath.bootstrapcdn.com/bootstrap/4.2.1/css/bootstrap.min.css'
            integrity='sha384-GJzZqFGwb1QTTN6wy59ffF1BuGJpLSa9DkKMp0DgiMDm4iYMj70gZWKYbI706tWS' crossorigin='anonymous'>

        <!-- Custom CSS -->
        <link rel="stylesheet" href="/assets/css/index.css">
    </head>

    <body>
        <div class='container-fluid my-3'>
            <div class="row">
                <div class="col">
                    <h1> WELCOME to <strong> <?= APP_NAME ?> Server </strong></h1>
                </div>
            </div>
            <div class="row">
                <div class="col">
                    <h2> <a href="/docs" class="text-bold"> Documentation of ENDPOINTS </a></h2>
                </div>
            </div>
        </div>
    </body>
</html>