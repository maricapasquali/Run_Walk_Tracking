<?php
require_once("utility.php");
require_once("dao/SessionDao.php");
require_once("dao/UserDao.php");
require_once("dao/SettingsDao.php");
require_once("dao/WorkoutDao.php");
require_once("dao/WeightDao.php");
try {
  $data = bodyRequest();

  if(!isset($data[SESSION][TOKEN]) || !isset($data[SESSION][LAST_UPDATE]) )
      throw new Exception(URL_NOT_VALID);

       // TODO: ULTERIORI CONTROLLI
  $session = SessionDao::checkForToken($data[SESSION][TOKEN]);
  if($session == NULL)  throw new Exception(SESSION_TOKEN_NOT_VALID);
  // UPDATE USER
  UserDao::update($data[USER]);
  // UPDATE SETTINGS
  $settings = $data[SETTINGS];
  SettingsDao::updateSportFor($settings[SPORT], $session[ID_USER]);
  SettingsDao::updateTargetFor($settings[TARGET], $session[ID_USER]);
  $unit_measure = $settings[UNIT_MEASURE];
  SettingsDao::updateUnitHeightFor($unit_measure[HEIGHT], $session[ID_USER]);
  SettingsDao::updateUnitWeightFor($unit_measure[WEIGHT], $session[ID_USER]);
  SettingsDao::updateUnitDistanceFor($unit_measure[DISTANCE], $session[ID_USER]);

 // UPDATE WORKOUTS
  foreach ($data[WORKOUTS] as $workout) {
    WorkoutDao::update($workout);
  }
  // UPDATE WEIGHTS
  foreach ($data[WEIGHTS] as $weight) {
    WeightDao::update($weight);
  }
  //print json_encode(array(UPDATE =>true));
  print json_encode(array(UPDATE => SessionDao::updateLastUpdate($data[SESSION][LAST_UPDATE],$session[ID_USER])));
} catch (Exception $e) {
  print json_errors($e->getMessage());
}

/*
{
	"session": {
		"token": "PVBXx1zTPmFIj9LHmtQy8eL0IceEg4XCc29lzF6rnuTI6pJfEhgo4aLE1UP7YoK51Gsnxgf6Ph6obviUGU6Gl9opYQ42ELLhebSO",
		"last_update": 3
	},
	"user": {
          "id_user": 5,
          "name": "Maria",
          "last_name": "Bianchi",
          "gender": "FEMALE",
          "birth_date": "1995-02-11",
          "email": "mariabianchi@gmail.com",
          "phone": "3333333333",
          "city": "Milano",
          "height": 1.55,
          "img_encode": null
      },
	"weights": [
          {
            "id_weight": 5,
            "date": "2019-12-24",
            "value": 70.5
          }
        ],
  "workouts": [],
  "settings": {
          "sport": "WALK",
          "target": "LOSE_WEIGHT",
          "unit_measure": {
            "energy": "KILO_CALORIES",
            "distance": "KILOMETER",
            "weight": "KILOGRAM",
            "height": "METER"
        }
}


  */

?>
