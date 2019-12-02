<?php
require_once("utility.php");
//require_once("dao/UserDao.php");
//require_once("dao/WorkoutDao.php");
//require_once("dao/WeightDao.php");
//require_once("dao/StatisticsDao.php");
//require_once("dao/SettingsDao.php");

try{
// USER
/*
print json_encode(UserDao::create($user));
print json_encode(UserDao::changePassword(39, bodyRequest()()[PASSWORD]));

$email ="marica@gmail.com";
$key = rand();
$expiry_date = date_end_validity_link();

print json_encode(array("email"=>$email, "key"=>$key, "expiry_date" =>$expiry_date) +
  array("request_sended" =>UserDao::requestForgotPassword($email, $key, $expiry_date)));
*/
/*
if(!isset($_GET['username']) || !isset($_GET['password']))
    throw new Exception(json_errors("username o password non settao!"));
print json_encode(UserDao::checkCredential($_GET['username'], $_GET['password']));
*/
/*
if(!isset($_GET['id']))
    throw new Exception(json_errors("id non settao!"));
print json_encode(UserDao::getUserForId($_GET['id']));
*/
//print json_encode(UserDao::update($_GET));

// WORKOUT
//print json_encode(WorkoutDao::create(array("id_user"=>27, "date"=>date("Y-m-d H:i:s"), "id_sport"=> 2, "duration"=> 3600)));
//print json_encode(WorkoutDao::getAllForUser(27));
//print json_encode(WorkoutDao::update(array( "map_route" => "1212121212", "id_sport"=> 1, "id_workout"=>13)));
//print json_encode(array("delete"=> WorkoutDao::delete(13)));

// WEIGHT
//print json_encode(WeightDao::create(array(ID_USER=>27, DATE=>date("Y-m-d"), VALUE=> 76.5)));
//print json_encode(WeightDao::update(array( DATE => "2019-10-22" , VALUE=> 72.0, ID_WEIGHT=>14)));
//print json_encode(array("delete"=> WeightDao::delete(22)));

// STATISTICS
//print json_encode(StatisticsDao::getAllMiddleSpeedFor(27));
//print json_encode(StatisticsDao::getAllCaloriesFor(27));
//print json_encode(StatisticsDao::getAllDistanceFor(27));
//print json_encode(StatisticsDao::getAllWeightFor(27));

// SETTINGS
//print json_encode(SettingsDao::getSettingsFor(27));
//print json_encode(array("update sport"=>SettingsDao::updateSportFor(2, 27)));
//print json_encode(array("update target"=>SettingsDao::updateTargetFor(2, 27)));
//print json_encode(array("update language"=>SettingsDao::updateLanguageFor("Inglese", 27)));
//print json_encode(array("update distance"=>SettingsDao::updateUnitDistanceFor(1,27)));
//print json_encode(array("update weight"=>SettingsDao::updateUnitWeightFor(1,27)));
//print json_encode(array("update height"=>SettingsDao::updateUnitHeightFor(1,27)));

print "BENVENUTO";

}catch(Exception $e){
  print json_errors($e->getMessage());
}


?>
