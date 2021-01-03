// --- setup search
const HOUR = 1000/*ms*/ * 60/*s*/ * 60/*m*/;
var timePeriod = HOUR * 1;
var POLLUTANTS = {};
var AQI = {};

var cityMetrics = $('#cityMetrics');
var periodButtons = $('#periodButtons')
  .find('button').click(function() {
    periodButtons.find('button').removeClass('active');
    timePeriod = HOUR * $(this).addClass('active').attr('data-target')
    $('#searchForm').submit();
  }).end();
var searchForm = $('#searchForm').submit(function(e) {
  e.preventDefault();
  // 4 hours ago
  var since = new Date(Date.now() - timePeriod);
  var params = {
    'city': this.elements.namedItem("city").value,
    'since': since.toISOString(),
    'size': 100
  };
  console.log('search-params', params)
  f('/api/v1/air-quality', undefined, true, params, 'get')
    .then((data) => {
      console.log(data.content);
      cityMetrics.empty();
      if (data.content && data.content.length > 0) {
        $('#cityName').text(data.content[0].stationName);
        var plot = {};
        data.content.forEach(e => {
          if (!plot.hasOwnProperty(e.pollutant)) {
            plot[e.pollutant] = {
              dangerLimit: e.dangerLimit,
              unit: e.unit,
              current: e.value,
//              style: e.isDangerous ? '' : 'text-danger',
              col: getAirQualityIndexColor(e.pollutant, e.value, e.isDangerous),
              data: []
            };
          }
          plot[e.pollutant].data.push({
            t: new Date(e.timestamp),
            v: e.value
          });
        });

        Object.entries(plot).forEach(e => {
          var k = e[0];
          var v = e[1];
          var tip = POLLUTANTS[k];
          var el = $(`
            <div class="row">
              <div class="col-md-4 col-lg-2 ${v.style} pollutant-current" style="color:var(${v.col})">
                <b>${k}</b><br />
                ${v.current} ${v.unit}
              </div>
              <div class="col-md-8 col-lg-10" id="${k}"></div>
            </div>
          `).appendTo(cityMetrics);
          if (tip) {
            el.find('.pollutant-current').tooltip({
              html: true,
              placement: 'right',
              title: `<h4>${tip.name}</h4>${tip.info.replace('\n', '<br/><br/>')}`
            });
          }
          MG.data_graphic({
            data: v.data,
            width: 800,
            height: 200,
            target: '#' + k,
            x_accessor: 't',
            y_accessor: 'v',
            baselines: [{'value': v.dangerLimit, "label": "danger level"}],
            color: ['var(--purple)']
          });
        })
      } else {
        $('#cityName').text('No data available');
      }
    });
});
$('#searchForm input').autoComplete({
  events: {
    search: function(qry, cb, el) {
      f('/api/v1/air-quality/cities', undefined, true, { 'city': qry }, 'get')
        .then((data) => {
          cb(data.map((val, i) => {
            return { 'value': i, 'text': val }
          }))
        });
    }
  }
}).on('autocomplete.select', function(evt, item) {
  window.location.hash = '#' + encodeURIComponent($('#searchForm input').val());
  $('#searchForm').submit();
});

var searchForm = $('#settingsChangeButton').click(function(e) {
  var data = {
    emailNotificationPeriod: $('#emailNotificationPeriod').val(),
    quietHoursStart: $('#quietHoursStart').val() || null,
    quietHoursEnd: $('#quietHoursEnd').val() || null,
    stationNames: $('#stationNames').val() || null
  }
  f('/api/v1/users/me/settings', data, true, {}, 'put')
    .then((data) => {
      $('#settingsDialog').modal('hide');
    });
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
function getCurrentUserSettings() {
  f(`/api/v1/users/me/settings`)
    .then((data) => {
      $('#emailNotificationPeriod').val(data.emailNotificationPeriod);
      $('#quietHoursStart').val(data.quietHoursStart);
      $('#quietHoursEnd').val(data.quietHoursEnd);
      var stationNames = $('#stationNames');
      stationNames.tagsinput('removeAll');
      (data.stationNames || ['1','2']).forEach(e => {
        stationNames.tagsinput('add', e);
      });
      console.log(data);
    });
}

function getAirQualityIndexColor(pollutant, value, isDangerous) {
  var aqi = AQI[pollutant];
  if (aqi) {
    if (value<aqi.very_good) {
      return '--green';
    } else if (value<aqi.good) {
      return '--teal';
    } else if (value<aqi.moderate) {
      return '--blue';
    } else if (value<aqi.poor) {
      return '--yellow';
    } else {
      return '--red';
    }
  } else {
    return isDangerous ? '--red' : '--dark';
  }
}

getCurrentUser();
getCurrentUserSettings();

// auto-select the id from the hash
if(window.location.hash) {
  var x = decodeURIComponent(window.location.hash.substring(1));
  $('#searchForm input')
    .autoComplete('set', { value: x, text: x })
  $('#searchForm').submit();
}

f('/pollutants.json').then((data) => POLLUTANTS = data );
f('/aqi.json').then((data) => AQI = data );

