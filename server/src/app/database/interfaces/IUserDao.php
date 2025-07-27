<?php

namespace app\database\interfaces;

interface IUserDao
{
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


