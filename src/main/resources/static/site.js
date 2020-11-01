// --- setup search
var searchForm = $('#searchForm').submit(function(e) {
  e.preventDefault();
  console.log('search clicked:', this.elements.namedItem("city").value);
});
$('#searchForm input').autoComplete({
  events: {
    searchPost: function(data) {
      // data == the data returned by the API
      var ret = {};
      ret =  data.map(function(val, index) {
        return { 'value': index, 'text': val }
      });
      console.log(ret);
      return ret;
    }
  }
}).on('autocomplete.select', function(evt, item) {
  searchForm.submit();
});

// --- setup password change dialog
$('#passwordChangeButton').click(function() {
  var data = {
    currentPassword: $('#currentPassword').val(),
    newPassword: $('#newPassword').val(),
    newPasswordAgain: $('#newPasswordAgain').val()
  }
  f('/api/v1/users/me', data, true, {}, 'put')
    .then((data) => {
      $('#changePasswordForm')[0].reset();
      $('#passwordDialog').modal('hide');
    });
})

// -- other, small functions
function getCurrentUser() {
  f(`/api/v1/users/me`)
    .then((data) => {
      $('#userName').text(data.username);
    });
}

getCurrentUser();
