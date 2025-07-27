<?php

namespace app\database\interfaces;
interface IWorkoutDao
{
    public function create($workout);

    public function getAllForUser($id_user);

    public function update($workout, $id_user);

    public function updateAll($workouts, $id_user);

    public function delete($id_workout);
}

