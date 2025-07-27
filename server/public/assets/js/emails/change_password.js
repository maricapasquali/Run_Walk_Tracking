$(function() {

    const _ERROR_USERNAME_EMPTY_ ="Username non può essere vuoto";
    const _ERROR_PASSWORD_EMPTY_ ="Password non può essere vuoto";
    const _ERROR_PASSWORD_ = "Password inserita non è valida";
    const _SUCCESS_MESSAGE_ = "Modifica password avvenuta con successo!";
    const _INVALID_FORM_MESSAGE_ = "Compila tutti i campi correttamente!"
    
    function error(error) {
        $("div.alert-container").append(
          $($("#error_operation").html()).html("<p class='text-bold'>ERROR</p><p>" + error + "</p>")
        );
    }

    function success(success) {
        $("div.alert-container").append(
          $($("#success_operation").html()).html(success)
        );
    }

    var usernameInp = $("input[name=username]");
    var newPasswordInp = $('#new-password');
    var newPasswordConfInp = $('#conf-new-password');
    var submitBtn = $("button[name='change_password']");
    var resetBtn = $("button[name='reset_form']");


    usernameInp.on("input", function () {
      const target = $(this);
      const username = $(this).val();
      if (username.length > 0) 
        validInput(target);
      else
        inValidInput(target, _ERROR_USERNAME_EMPTY_);    
      
      toggleVisibilityOfSubmitBtn(submitBtn);
    })

    newPasswordInp.on('input', function () {
      var password = $(this);
      if (password.val().length == 0)
        inValidInput(password, _ERROR_PASSWORD_EMPTY_);
      else
        validInput(password);
      
      if (newPasswordConfInp.val().length > 0) {
        if (password.val() != newPasswordConfInp.val())
          inValidInput(newPasswordConfInp, _ERROR_PASSWORD_);
        else
          validInput(newPasswordConfInp);
      }
      
      toggleVisibilityOfSubmitBtn(submitBtn);
    });

    newPasswordConfInp.on('input', function () {
      var conf_passw = $(this);
      if (newPasswordInp.val() != conf_passw.val())
        inValidInput(conf_passw, _ERROR_PASSWORD_);
      else
        validInput(conf_passw);
      
      toggleVisibilityOfSubmitBtn(submitBtn);
    });    

    submitBtn.on('click', function () {
      $("div.alert-container").empty(); // clean prev alert

      if (isValidForm()) {
        submitBtn.prop("disabled", true);
        var hash_md5_password = md5($("input#new-password").val());

        var body = { username: $("input#username").val(), password: hash_md5_password }

        
        $_post("/account/password_forgot.php", body)
        .done(function(data) {
          console.debug(data);
          if(data.success) {
            success(_SUCCESS_MESSAGE_);            
            $("input#username").val("")
            $("input#new-password").val("");
            $("input#conf-new-password").val("");
            $("#change_password").remove();
          }
        })
        .fail(function(jqXHR) {
          const err = jqXHR.responseJSON?.Error
          const status = jqXHR.status
          const statusText = jqXHR.statusText
          error(status + " " + statusText + ": " + JSON.stringify(err, null, "\t"));
          $("#change_password").removeClass('was-validated');
        })
        .always(function() {
           submitBtn.prop("disabled", false);
        });
      } 
      else error(_INVALID_FORM_MESSAGE_);      
    });

    submitBtn.hide();

    resetBtn.on('click', function () {
      $("div.alert-container").empty();
      usernameInp.removeClass("is-valid is-invalid");
      newPasswordInp.removeClass("is-valid is-invalid");
      newPasswordConfInp.removeClass("is-valid is-invalid");
      toggleVisibilityOfSubmitBtn(submitBtn);
    });

});
