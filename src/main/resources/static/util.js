'use strict'
const baseURL = window.location;

// fetch utility function
function f(path, body=null, json=true, queryParams={}, method='post') {
  var url = new URL(baseURL);
  url.pathname = path;
  Object.entries(queryParams).forEach((e)=>{
    url.searchParams.append(e[0], e[1]);
  })
  var headers = {};
  if (body != null && typeof body == 'object') {
    headers['Content-Type'] = 'application/json';
    body = JSON.stringify(body);
  }
  return (body == null ? fetch(url) : fetch(url, { method: method, body: body, headers: headers }))
    .then((response) => {
      console.log(response);
      if (response.status === 401) { // not authorized
        $('#registerModal').modal('show');
        throw 'unauthorized';
      }
      if (response.status >= 400) {
        var err = `Error #${response.status}`;
        response.json()
        .then((m) => {
          notify(err, JSON.stringify(m, null, '  '));
        })
        .catch(() => {
          notify(err, response.statusText | response.status );
        })
;
        throw err;
      }
      return json ? response.json() : response;
    });
}

// safe escape html code
function htmlDecode(t){
   return t ? $('<div />').html(t).text() : '';
}
// notify utility function
function notify(title, message) {
  $('.toast')
    .find('.toast-header > strong').text(title).end()
    .find('.toast-body').text(htmlDecode(message)).end()
    .toast('show');
}

(function() {
  'use strict';
  window.addEventListener('load', function() {
    // Fetch all the forms we want to apply custom Bootstrap validation styles to
    var forms = document.getElementsByClassName('needs-validation');
    // Loop over them and prevent submission
    var validation = Array.prototype.filter.call(forms, function(form) {
      form.addEventListener('submit', function(event) {
        if (form.checkValidity() === false) {
          event.preventDefault();
          event.stopPropagation();
        }
        form.classList.add('was-validated');
      }, false);
    });
  }, false);
})();
