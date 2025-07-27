function inValidInput(input, msg) {
  input.removeClass("is-valid").addClass("is-invalid");
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

function isValidForm() {
  return $('input:not(.is-valid)').length === 0;
}

function toggleVisibilityOfSubmitBtn(btn) {
  if (isValidForm()) btn.show();
  else btn.hide();
}

function $_post(url, body) {
  return $.ajax({
    type: "POST",
    url: url,
    data: JSON.stringify(body),
    dataType: 'json'
  })  
}