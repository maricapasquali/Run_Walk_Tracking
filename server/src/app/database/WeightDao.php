<?php

namespace app\database;

use app\database\interfaces\IWeightDao;
use Exception;
use function utility\formatDate;

class WeightDao implements IWeightDao
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

    public function create($weight)
    {
        return $this->daoFactory->transaction(function () use ($weight) {
            $date = formatDate($weight[DATE]);
            $stmt = $this->daoFactory->getConnection()->prepare("INSERT INTO weight(id_weight, id_user, date, value) VALUES (?, ?, ?, ?)");
            if (!$stmt)
                throw new Exception("Weight : Preparazione fallita. Errore: " . $this->daoFactory->getErrorConnection());
            $stmt->bind_param("iisd", $weight[ID_WEIGHT], $weight[ID_USER], $date, $weight[VALUE]);
            if (!$stmt->execute())
                throw new Exception("Weight : Inserimento fallito. Errore: " . $this->daoFactory->getErrorConnection());
            $stmt->close();
        });
    }

    public function getAllForUser($id_user)
    {
        $weights = [];
        if (
            $this->daoFactory->selection(function () use ($id_user, &$weights) {
                $stmt = $this->daoFactory->getConnection()->prepare("SELECT id_weight, date, value FROM weight WHERE id_user=? ORDER BY UNIX_TIMESTAMP(date)  DESC;");
                if (!$stmt)
                    throw new Exception("Weights : Preparazione fallita. Errore: " . $this->daoFactory->getErrorConnection());
                $stmt->bind_param("i", $id_user);
                if (!$stmt->execute())
                    throw new Exception("Weights : Selezione fallita. Errore: " . $this->daoFactory->getErrorConnection());
                $result = $stmt->get_result();
                while ($row = $result->fetch_assoc()) {
                    $weights[] = $row;
                }
                $stmt->close();
            })
        ) {
            return $weights;
        }

    }

    public function update($weight, $id_user)
    {
        return $this->daoFactory->transaction(function () use ($weight, $id_user) {
            $keys = [];
            $values = [];
            $typeParam = "";

            $allowedKeys = [DATE, VALUE];
            foreach ($weight as $key => $value) {
                if (in_array($key, $allowedKeys)) {
                    $keys[] = $key;
                    $values[] = $key == DATE ? formatDate($value) : $value;
                    switch ($key) {
                        case DATE:
                            $typeParam .= "s";
                            break;
                        case VALUE:
                            $typeParam .= "d";
                            break;
                    }
                }
            }
            $values[] = $weight[ID_WEIGHT];
            $values[] = $id_user;
            $typeParam .= "ii";

            if (count($values) > 2) {
                $stmt = $this->daoFactory->getConnection()->prepare("UPDATE weight SET " . join("=?,", $keys) . "=? WHERE id_weight=? and id_user=?");
                if (!$stmt)
                    throw new Exception("Weight update : Preparazione fallita. Errore: " . $this->daoFactory->getErrorConnection());
                $stmt->bind_param($typeParam, ...$values);
                if (!$stmt->execute())
                    throw new Exception("Weight : Update fallito. Errore: " . $this->daoFactory->getErrorConnection());
                $stmt->close();
            }
        });
    }

    public function updateAll($weights, $id_user)
    {
        return $this->daoFactory->transaction(function () use ($weights, $id_user) {
            $stmt = $this->daoFactory->getConnection()->prepare("DELETE FROM weight WHERE id_user =?;");
            if (!$stmt)
                throw new Exception("Delete All Weights : Preparazione fallita. Errore: " . $this->daoFactory->getErrorConnection());
            $stmt->bind_param("i", $id_user);
            if (!$stmt->execute())
                throw new Exception("All Weights : delete fallita. Errore: " . $this->daoFactory->getErrorConnection());
            $stmt->close();

            $allowedKeys = [ID_WEIGHT, DATE, VALUE];

            foreach ($weights as $weight) {
                $keys = [];
                $values = [];
                $typeParam = "";

                foreach ($weight as $key => $value) {
                    if (in_array($key, $allowedKeys)) {
                        $keys[] = $key;
                        $values[] = $key == DATE ? formatDate($value) : $value;
                        switch ($key) {
                            case DATE:
                                $typeParam .= "s";
                                break;
                            case VALUE:
                                $typeParam .= "d";
                                break;
                            default:
                                $typeParam .= "i";
                        }
                    }
                }
                $keys[] = ID_USER;
                $values[] = $id_user;
                $typeParam .= "i";

                if (count($values) > 2) {
                    $stmt = $this->daoFactory->getConnection()->prepare("INSERT INTO weight (" . join(",", $keys) . ") VALUES(" . join(",", str_split(str_repeat('?', count($keys)))) . ")");
                    if (!$stmt)
                        throw new Exception("Weight update : Preparazione fallita. Errore: " . $this->daoFactory->getErrorConnection());
                    $stmt->bind_param($typeParam, ...$values);
                    if (!$stmt->execute())
                        throw new Exception("Weight : Update fallito. Errore: " . $this->daoFactory->getErrorConnection());
                    $stmt->close();
                }
            }
        });

    }

    public function delete($id_weight)
    {
        return $this->daoFactory->transaction(function () use ($id_weight) {
            $stmt = $this->daoFactory->getConnection()->prepare("DELETE FROM weight WHERE id_weight=?");
            if (!$stmt)
                throw new Exception("Workout delete : Preparazione fallita. Errore: " . $this->daoFactory->getErrorConnection());
            $stmt->bind_param("i", $id_weight);
            if (!$stmt->execute())
                throw new Exception("Workout : Delete fallito. Errore: " . $this->daoFactory->getErrorConnection());
            $stmt->close();
        });

    }

}


