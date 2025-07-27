<?php

use Dotenv\Dotenv;

require_once __DIR__ . "/../vendor/autoload.php";

$envFiles = [
    "database.env",
    "mailer.env",
    "server.env"
];
foreach ($envFiles as $file) {
    $envPath = __DIR__ . '/' . $file;
    if (file_exists($envPath)) {
        Dotenv::createImmutable(__DIR__, $file)->load();
    } else {
        error_log("The environment file '" . $envPath . "' not found.");
    }
}

// database.env
define("MYSQL_HOST", $_ENV["MYSQL_HOST"] ?: "localhost");
define("MYSQL_DATABASE", $_ENV["MYSQL_DATABASE"] ?: "runwalktracking");
define("MYSQL_USER", $_ENV["MYSQL_USER"]);
define("MYSQL_PASSWORD", $_ENV["MYSQL_PASSWORD"]);

// mailer.env
define("MAILER_HOST", $_ENV["MAILER_HOST"] ?: "smtp.gmail.com");
define("MAILER_PORT", $_ENV["MAILER_PORT"] ?: 587);
define("MAILER_USER", $_ENV["MAILER_USER"]);
define("MAILER_PASSWORD", $_ENV["MAILER_PASSWORD"]);

// server.env
define("_SERVER_", $_ENV["SERVER_URL"] ?: ("http://localhost:" . ($_ENV["SERVER_PORT"] ?: "80")));
