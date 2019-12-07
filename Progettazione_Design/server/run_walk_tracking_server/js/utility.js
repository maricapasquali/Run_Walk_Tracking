function inValidInput(input, msg) {
  input.addClass("is-invalid");
  var feedback = input.next();
  feedback.html(msg);
  feedback.addClass("invalid-feedback");
}

function validInput(input) {
  input.removeClass("is-invalid").addClass("is-valid");
  var feedback = input.next();
  feedback.html("");
  feedback.removeClass("invalid-feedback");
}

function validateForm(event) {
  var form = event.data.form;
  if (form[0].checkValidity() === false) {
    event.preventDefault();
    event.stopPropagation();
    form.find('input').not('.is-valid').addClass('is-invalid');
  }
  form.addClass('was-validated');
}

function post_ajax_json(url, data, action) {
  if (action && typeof action === "function") {
    $.ajax({
      type: "POST",
      url: url,
      data: data,
      dataType: 'json',
      success: action,
      error: function(jqXHR, textStatus) {
        alert(textStatus + ' : ' + JSON.stringify(jqXHR));
      }
    });
  }
}