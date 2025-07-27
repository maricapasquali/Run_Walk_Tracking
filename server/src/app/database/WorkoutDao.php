<?php

namespace app\database;

use app\database\interfaces\IWorkoutDao;
use Exception;

class WorkoutDao implements IWorkoutDao
{

    private $daoFactory;

    public function __construct()
    {
        $this->daoFactory = DaoFactory::instance();
    }

    private static $instance;

    public static function instance()
    {
        if (self::$instance == null)
            self::$instance = new self();
        return self::$instance;
    }

    public function create($workout)
    {
        return $this->daoFactory->transaction(function () use ($workout) {
            $keys = [];
            $values = [];
            $typeParam = "";

            foreach ($workout as $key => $value) {
                $keys[] = $key;
                $values[] = $value;
                if ($key == ID_USER || $key == DURATION || $key == ID_WORKOUT)
                    $typeParam .= "i";
                else if ($key == DISTANCE || $key == CALORIES)
                    $typeParam .= "d";
                else
                    $typeParam .= "s";
            }

            $stmt = $this->daoFactory->getConnection()->prepare("INSERT INTO workout" . "(" . join(",", $keys) . ")" . " VALUES (" . join(",", str_split(str_repeat('?', count($keys)))) . ")");
            if (!$stmt)
                throw new Exception("User : Preparazione fallita. Errore: " . $this->daoFactory->getErrorConnection());

            $stmt->bind_param($typeParam, ...$values);
            if (!$stmt->execute())
                throw new Exception("User : Inserimento fallito. Errore: " . $this->daoFactory->getErrorConnection());
            $stmt->close();
        });
    }

    public function getAllForUser($id_user)
    {
        $workouts = [];
        if (
            $this->daoFactory->selection(function () use ($id_user, &$workouts) {
                $stmt = $this->daoFactory->getConnection()->prepare("SELECT id_workout, map_route, date, duration, distance, calories, sport FROM workout  WHERE id_user =?;");
                if (!$stmt)
                    throw new Exception("Workouts : Preparazione fallita. Errore: " . $this->daoFactory->getErrorConnection());
                $stmt->bind_param("i", $id_user);
                if (!$stmt->execute())
                    throw new Exception("Workouts : Selezione fallita. Errore: " . $this->daoFactory->getErrorConnection());
                $result = $stmt->get_result();
                while ($row = $result->fetch_assoc()) {
                    $workouts[] = $row;
                }
                $stmt->close();
            })
        ) {
            return $workouts;
        }
    }

    public function update($workout, $id_user)
    {
        return $this->daoFactory->transaction(function () use ($workout, $id_user) {
            $keys = [];
            $values = [];
            $typeParam = "";
            $allowedKeys = [MAP_ROUTE, DATE, DURATION, DISTANCE, CALORIES, SPORT];

            foreach ($workout as $key => $value) {
                if (in_array($key, $allowedKeys)) {
                    $keys[] = $key;
                    $values[] = $value;
                    if ($key == DURATION)
                        $typeParam .= "i";
                    else if ($key == DISTANCE || $key == CALORIES)
                        $typeParam .= "d";
                    else
                        $typeParam .= "s";
                }
            }
            $values[] = $workout[ID_WORKOUT];
            $values[] = $id_user;
            $typeParam .= "ii";

            if (count($values) <= 2)
                return;

            $stmt = $this->daoFactory->getConnection()->prepare("UPDATE workout SET " . join("=?,", $keys) . "=? WHERE id_workout=? and id_user=?");
            if (!$stmt)
                throw new Exception("Workout update : Preparazione fallita. Errore: " . $this->daoFactory->getErrorConnection());
            $stmt->bind_param($typeParam, ...$values);
            if (!$stmt->execute())
                throw new Exception("Workout : Update fallito. Errore: " . $this->daoFactory->getErrorConnection());
            $stmt->close();
        });
    }

    public function updateAll($workouts, $id_user)
    {
        return $this->daoFactory->transaction(function () use ($workouts, $id_user) {
            $stmt = $this->daoFactory->getConnection()->prepare("DELETE FROM workout WHERE id_user =?;");
            if (!$stmt)
                throw new Exception("Delete Workouts : Preparazione fallita. Errore: " . $this->daoFactory->getErrorConnection());
            $stmt->bind_param("i", $id_user);
            if (!$stmt->execute())
                throw new Exception("All Workouts : Delete fallita. Errore: " . $this->daoFactory->getErrorConnection());
            $stmt->close();

            foreach ($workouts as $workout) {
                $keys = [];
                $values = [];
                $typeParam = "";

                $allowedKeys = [ID_WORKOUT, MAP_ROUTE, DATE, DURATION, DISTANCE, CALORIES, SPORT];

                foreach ($workout as $key => $value) {
                    if (in_array($key, $allowedKeys)) {
                        $keys[] = $key;
                        $values[] = $value;
                        if ($key == DURATION || $key == ID_WORKOUT)
                            $typeParam .= "i";
                        else if ($key == DISTANCE || $key == CALORIES)
                            $typeParam .= "d";
                        else
                            $typeParam .= "s";
                    }
                }
                $typeParam .= "i";
                $keys[] = ID_USER;
                $values[] = $id_user;

                if (count($values) > 2) {
                    $stmt = $this->daoFactory->getConnection()->prepare("INSERT INTO workout (" . join(",", $keys) . ") VALUES(" . join(",", str_split(str_repeat('?', count($keys)))) . ")");
                    if (!$stmt)
                        throw new Exception("Workout update : Preparazione fallita. Errore: " . $this->daoFactory->getErrorConnection());
                    $stmt->bind_param($typeParam, ...$values);
                    if (!$stmt->execute())
                        throw new Exception("Workout : Update fallito. Errore: " . $this->daoFactory->getErrorConnection());
                    $stmt->close();
                }
            }
        });
    }

    public function delete($id_workout)
    {
        return $this->daoFactory->transaction(function () use ($id_workout) {
            $stmt = $this->daoFactory->getConnection()->prepare("DELETE FROM workout WHERE id_workout=?");
            if (!$stmt)
                throw new Exception("Workout delete : Preparazione fallita. Errore: " . $this->daoFactory->getErrorConnection());
            $stmt->bind_param("i", $id_workout);
            if (!$stmt->execute())
                throw new Exception("Workout : Delete fallito. Errore: " . $this->daoFactory->getErrorConnection());
            $stmt->close();
        });
    }

}


