<?php

interface IUserDao {
  public function create($user);
  public function checkSignUp($userCredentials);
  public function checkSignIn($username, $password);
  public function getUserForId($id);
  public function getUserForUsername($username);
  public function allData($session_token, $_device);
  public function getImageProfileForIdUserAndName($id, $name);
  public function requestForgotPassword($email, $c_key, $end_validity);
  public function update($user, $id_user);
  public function changePassword($password, $id_user);
  public function delete($id_user);
}

interface ISettingsDao {
  public function getAllForUser($id_user);
  public function updateSportFor($sport, $id_user);
  public function updateTargetFor($target, $id_user);
  public function updateUnitHeightFor($height, $id_user);
  public function updateUnitWeightFor($weight, $id_user);
  public function updateUnitDistanceFor($distance, $id_user);
  public function updateUnits($unit_measure, $id_user);
}

interface IWeightDao {
  public function create($weight);
  public function getAllForUser($id_user);
  public function update($weight, $id_user);
  public function updateAll($weights, $id_user);
  public function delete($id_weight);
}

interface ISessionDao {
  public function create($id_user, $device);
  public function checkForIdUser($id_user);
  public function checkForToken($session_token);
  public function update($id_user);
  public function setLastUpdate($last_update, $id_user);
  public function setNewDevice($device, $id_user);
}

interface IWorkoutDao {
  public function create($workout);
  public function getAllForUser($id_user);
  public function update($workout, $id_user);
  public function updateAll($workouts, $id_user);
  public function delete($id_workout);
}

 ?>
