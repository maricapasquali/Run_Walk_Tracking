<?php

namespace app\email;

use DateTime;
use DOMDocument;
use Exception;
use PHPMailer\PHPMailer\PHPMailer;

class Mailer
{

    private PHPMailer $mail;

    private static $instance;

    private static string $MAIL_TEMPLATES_PATH = __DIR__ . "/templates";

    public static function instance(): Mailer
    {
        if (self::$instance == null)
            self::$instance = new self();
        return self::$instance;
    }

    public function __construct()
    {
        $this->mail = new PHPMailer(true); // Enable exceptions
        //Server settings
        $this->mail->isSMTP(); // Use SMTP
        $this->mail->Host = MAILER_HOST;
        $this->mail->Port = MAILER_PORT;
        $this->mail->SMTPAuth = true; // Enable authentication SMTP
        $this->mail->Username = MAILER_USER;
        $this->mail->Password = MAILER_PASSWORD;
        $this->mail->SMTPSecure = PHPMailer::ENCRYPTION_STARTTLS;
    }

    public function sendSignup($to_email, $user, $token): bool
    {
        // Send email with signup token
        $doc = $this->docHtml($this->_absolutePathOf("sign_up"));
        $findMe = $doc->getElementById("token");
        if (is_object($findMe))
            $findMe->nodeValue = $token;
        foreach ($user as $key => $value) {
            $findMe = $doc->getElementById($key);
            if (is_object($findMe)) {
                $findMe->nodeValue = $key == BIRTH_DATE ? (new DateTime($value))->format('m/d/Y') : $value;
            }
        }
        $emailContent = $doc->saveHTML();
        $completeUsername = "{$user[NAME]} {$user[LAST_NAME]}";
        return $this->sendEmail($to_email, "Sign Up", $emailContent, $completeUsername);
    }

    public function sendRequestResetPassword($to_email, $c_key): bool
    {
        // Send email with reset password link
        $doc = $this->docHtml($this->_absolutePathOf("support_password"));
        $findMe = $doc->getElementById("link_change_password");
        if (is_object($findMe))
            $findMe->setAttribute('href', _SERVER_ . '/emails/change_password.php?c_key=' . $c_key);
        return $this->sendEmail($to_email, "Reset Password", $doc->saveHTML());
    }

    private function sendEmail($to_email, $subject, $body, $completeUsername = null): bool
    {
        try {
            $mailDomainSender = "mail." . strtolower(preg_replace("[\s|/]", "", APP_NAME)) . ".com";
            // Set email sender and recipient
            $this->mail->setFrom("no-reply@$mailDomainSender", APP_NAME);
            $this->mail->addAddress($to_email, $completeUsername != null ? $completeUsername : '');

            $this->mail->isHTML(true); // Set email format to HTML
            $this->mail->Subject = APP_NAME . " - " . $subject; // Email subject
            $this->mail->Body = $body;

            return $this->mail->send();
        } catch (Exception $e) {
            // Log the error message
            error_log("Mailer Error: {$e->getMessage()}");
            error_log("Error send email to $to_email: {$this->mail->ErrorInfo}");
            return false;
        }
    }

    private function docHtml($url)
    {
        $doc = new DOMDocument();
        $doc->validateOnParse = true;
        libxml_use_internal_errors(true);
        $success = $doc->loadHTMLFile($url);
        if (!$success)
            die("<div class='mx-5 mt-5 px-5 pt-5'><strong>Documento $url non è stato caricato.</strong></div>");
        $apps = $doc->getElementsByTagName("app-name");
        foreach ($apps as $app) {
            $app->textContent = APP_NAME;
        }
        return $doc;
    }

    private function _absolutePathOf($template): string
    {
        $directory = Mailer::$MAIL_TEMPLATES_PATH;
        return "$directory/$template.html";
    }
}


