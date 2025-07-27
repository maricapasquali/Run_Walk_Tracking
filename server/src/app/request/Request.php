<?php
namespace app\request;

use app\exceptions\UrlException;

class Request
{
    public static function get()
    {
        return json_decode(file_get_contents('php://input'), true);
    }

    public static function getBody($of)
    {

        Request::checkMethod('POST');

        switch ($of) {
            case SIGN_UP:
                $required = [NAME, LAST_NAME, GENDER, BIRTH_DATE, EMAIL, CITY, PHONE, HEIGHT, TARGET, WEIGHT, USERNAME, PASSWORD];
                break;

            case PASSWORD_FORGOT:
            case SIGN_IN:
                $required = [USERNAME, PASSWORD];
                break;

            case FIRST_LOGIN:
                $required = [USERNAME, PASSWORD, TOKEN, DEVICE];
                break;

            case DOWNLOAD_IMAGE:
                $required = [TOKEN, IMG];
                break;

            case CHANGE_PASSWORD:
                $required = [USERNAME, OLD_PASSWORD, TOKEN, NEW_PASSWORD];
                break;

            case REQUEST_CHANGE_PASSWORD:
                $required = [EMAIL];
                break;

            case SYNC:
                $required = [LAST_UPDATE, DB_EXIST, TOKEN, DEVICE];
                break;

            case CONTINUE_SESSION:
                $required = [SESSION];
                break;

            case INSERT:
            case UPDATE:
                $required = [TOKEN, LAST_UPDATE, FILTER, DATA];
                break;

            case DELETE:
                $required = [TOKEN, LAST_UPDATE, FILTER];
                break;

            case UPDATE_ALL:
                $required = [TOKEN, LAST_UPDATE, DATA];
                break;

            default:
                $required = [];
                break;
        }

        $body = self::get();
        // print_r(json_encode($body));
        // print_r($required);

        $keys = array_keys($body);
        foreach ($required as $key) {
            if (!in_array($key, $keys) || $body[$key] === null)
                throw new UrlException(); //TODO: accumula key mancati e inviale come messaggio
        }
        return $body;
    }

    public static function checkMethod($method)
    {
        if ($_SERVER['REQUEST_METHOD'] !== $method) {
            throw new UrlException();
        }
    }

}


