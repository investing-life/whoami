// 공지사항

// 대화 클릭 시 내용 띄우기
$(document).ready(function() {
  $('.notice-title').click(function() {
    var index = $(this).data('index');
    $('#notice-num')[0].innerText = noticeList[index].indexNumber;
    $('#notice-title-input')[0].value = noticeList[index].title;
    $('#notice-content-input')[0].value = noticeList[index].content;
  });
});

function newNotice() {
  $('#notice-num')[0].innerText = '새 글';
  $('#notice-title-input')[0].value = '';
  $('#notice-content-input')[0].value = '';
}

function saveNotice() {
  // CSRF 토큰 가져오기
  var csrfToken = $("meta[name='_csrf']").attr("content");
  var csrfHeader = $("meta[name='_csrf_header']").attr("content");

  var title = $('#notice-title-input')[0].value;
  var content = $('#notice-content-input')[0].value;

  if (title == '' || content == '') { return; }

  if ($('#notice-num')[0].innerText == '새 글') {
    $.ajax({
      url: window.location.pathname + '/notice', // 요청을 보낼 URL 주소
      method: 'POST', // HTTP 메서드 (POST)
      headers: {
        [csrfHeader]: csrfToken // CSRF 토큰을 헤더에 추가
      },
      data: { title: title, content: content.replaceAll("\n", "\r\n") }, // 전송할 데이터 (객체 형태)
      success: function(response) {
        $('#notice-title-input')[0].value = '';
        $('#notice-content-input')[0].value = '';
        // 요청이 성공했을 때 실행할 콜백 함수
        alert('요청이 성공했습니다.');
      },
      error: function(xhr, status, error) {
        // 요청이 실패했을 때 실행할 콜백 함수
        alert('요청이 실패했습니다.');
      }
    });
  } else {
    $.ajax({
      url: window.location.pathname + '/notice', // 요청을 보낼 URL 주소
      method: 'PATCH', // HTTP 메서드 (PATCH)
      headers: {
        [csrfHeader]: csrfToken // CSRF 토큰을 헤더에 추가
      },
      data: { indexNumber: $('#notice-num')[0].innerText, title: title, content: content.replaceAll("\n", "\r\n") }, // 전송할 데이터 (객체 형태)
      success: function(response) {
        $('#notice-num')[0].innerText = '새 글';
        $('#notice-title-input')[0].value = '';
        $('#notice-content-input')[0].value = '';
        // 요청이 성공했을 때 실행할 콜백 함수
        alert('요청이 성공했습니다.');
      },
      error: function(xhr, status, error) {
        // 요청이 실패했을 때 실행할 콜백 함수
        alert('요청이 실패했습니다.');
      }
    });
  }
}

function deleteNotice() {
  var result = confirm("정말 삭제하시겠습니까?");
  if (!result) { return; }

  // CSRF 토큰 가져오기
  var csrfToken = $("meta[name='_csrf']").attr("content");
  var csrfHeader = $("meta[name='_csrf_header']").attr("content");

  var title = $('#notice-title-input')[0].value;
  var content = $('#notice-content-input')[0].value;

  if ($('#notice-num')[0].innerText == '새 글') {
    $('#notice-title-input')[0].value = '';
    $('#notice-content-input')[0].value = '';
  } else {
    $.ajax({
      url: window.location.pathname + '/notice', // 요청을 보낼 URL 주소
      method: 'DELETE', // HTTP 메서드 (DELETE)
      headers: {
        [csrfHeader]: csrfToken // CSRF 토큰을 헤더에 추가
      },
      data: { indexNumber: $('#notice-num')[0].innerText }, // 전송할 데이터 (객체 형태)
      success: function(response) {
        $('#notice-num')[0].innerText = '새 글';
        $('#notice-title-input')[0].value = '';
        $('#notice-content-input')[0].value = '';
        // 요청이 성공했을 때 실행할 콜백 함수
        alert('요청이 성공했습니다.');
      },
      error: function(xhr, status, error) {
        // 요청이 실패했을 때 실행할 콜백 함수
        alert('요청이 실패했습니다.');
      }
    });
  }
}

// 개발자와의 대화 메세지 내용 width 설정 후 높이 맞추기
function adjustTalk() {
    // 너비 설정
    contentList = document.getElementsByClassName('message-content');
    contentWidth = document.getElementsByClassName('message-preview')[0].clientWidth
        - document.getElementsByClassName('member-id')[0].offsetWidth - parseInt(window.getComputedStyle(document.getElementsByClassName('member-id')[0]).marginLeft) - parseInt(window.getComputedStyle(document.getElementsByClassName('member-id')[0]).marginRight)
        - document.getElementsByClassName('message-type')[0].offsetWidth - parseInt(window.getComputedStyle(document.getElementsByClassName('message-type')[0]).borderLeft)
        - document.getElementsByClassName('message-time')[0].offsetWidth - parseInt(window.getComputedStyle(document.getElementsByClassName('message-time')[0]).borderLeft)
        // 자기 자신의 borderLeft와 padding
        - parseInt(window.getComputedStyle(contentList[0]).borderLeft) - 1;
    for (const content of contentList) {
        content.style.width = contentWidth + 'px';
    }

    // 나머지 요소들 높이 맞추기
    idList = document.getElementsByClassName('member-id');
    typeList = document.getElementsByClassName('message-type');
    timeList = document.getElementsByClassName('message-time');

    for (let i = 0; i < document.getElementsByClassName('message-preview').length; i++) {
        idList[i].style.height = (contentList[i].offsetHeight - parseInt(window.getComputedStyle(idList[i]).marginTop) - parseInt(window.getComputedStyle(idList[i]).marginBottom)) + 'px';
        typeList[i].style.height = (contentList[i].offsetHeight) + 'px';
        timeList[i].style.height = (contentList[i].offsetHeight) + 'px';
    }
}

window.onload = function() {
    adjustTalk();
}

// 옆에 scroll bar 생겨서 너비 바뀌는 것 감지
const target = document.querySelector('.talk');

const observer = new ResizeObserver((entries) => {
  // 관찰 중인 배열 형식의 객체 리스트
  entries.forEach((entry) => {
    adjustTalk();
  });
});

// 크기변화를 관찰할 요소지정
observer.observe(target);


// 메세지 전송 ajax
function send_message(i) {
    var message = document.getElementsByClassName("answer-text")[i].value;
    if (message == '') {
        return false;
    }

    // CSRF 토큰 가져오기
    var csrfToken = $("meta[name='_csrf']").attr("content");
    var csrfHeader = $("meta[name='_csrf_header']").attr("content");

    // AJAX 요청
    var xhr = new XMLHttpRequest();
    xhr.open("POST", window.location.pathname + "/answer", true);
    xhr.setRequestHeader(csrfHeader, csrfToken);
    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    xhr.onreadystatechange = function() {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status === 200) {
                var container = document.getElementsByClassName('qna-content')[i];
                // 날짜가 바뀌었으면 추가
                var currentTime = new Date();
                var year = currentTime.getFullYear();
                var month = currentTime.getMonth() + 1;
                var date = currentTime.getDate();
                if (container.querySelectorAll('.date').length == 0 ||
                    year != parseInt(container.querySelectorAll('.date')[container.querySelectorAll('.date').length - 1].innerText.split(' ')[0].replace('년', '')) ||
                    month != parseInt(container.querySelectorAll('.date')[container.querySelectorAll('.date').length - 1].innerText.split(' ')[1].replace('월', '')) ||
                    date != parseInt(container.querySelectorAll('.date')[container.querySelectorAll('.date').length - 1].innerText.split(' ')[2].replace('일', ''))) {

                    dateDiv = document.createElement('div');
                    dateDiv.classList.add('date');

                    var dayOfWeek = currentTime.getDay();
                    // 요일 텍스트 매핑
                    var daysOfWeek = ["일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"];
                    var formattedDayOfWeek = daysOfWeek[dayOfWeek];
                    // 날짜 형식 포맷
                    var formattedDate = year + "년 " + month + "월 " + date + "일 " + formattedDayOfWeek;

                    dateDiv.innerText = formattedDate;

                    tempDiv = document.createElement('div');
                    tempDiv.style.clear = 'both';
                    container.appendChild(tempDiv);
                    container.appendChild(dateDiv);
                }

                var messageDiv = document.createElement('div');
                messageDiv.classList.add('message-A');
                messageDiv.innerText = message;
                document.getElementsByClassName("answer-text")[i].value = '';
                var timeDiv = document.createElement('div');
                timeDiv.classList.add('time-A');
                timeDiv.innerText = getCurrentTime();
                messageDiv.appendChild(timeDiv);
                container.appendChild(messageDiv);

                document.getElementsByClassName('message-content')[i].innerText = message;
            } else {
                alert("Failed to send a message!");
            }
        }
    };
    xhr.send("message=" + encodeURIComponent(message.replaceAll("\n", "\r\n")) + "&memberId=" + talkObjects[i].memberId);
}

function getCurrentTime() {
    var currentTime = new Date();
    var hours = currentTime.getHours();
    var minutes = currentTime.getMinutes();

    // 오전/오후 구분 및 시간 조정
    var meridiem = '오전';
    if (hours >= 12) {
        meridiem = '오후';
        if (hours > 12) {
            hours -= 12;
        }
    }
    if (hours == 0) {
        hours += 12;
    }

    // 시간과 분을 2자리로 맞추기
    minutes = minutes < 10 ? '0' + minutes : minutes;

    var currentTimeString = meridiem + ' ' + hours + ':' + minutes;
    return currentTimeString;
}


// 대화 클릭 시 토글
$(document).ready(function() {
  $('.message-preview').click(function() {
    var index = $(this).data('index');
    $('.qna[data-index="' + index + '"]').slideToggle();
    // 만약 새 질문이었다면 질문을 읽었다고 서버에 get 요청 보냄
    if (talkObjects[index].newQuestion) {
      $.ajax(window.location.pathname + "/read/question/" + talkObjects[index].memberId);
      talkObjects[index].newQuestion = false;
      document.getElementsByClassName('message-preview')[index].getElementsByClassName('new-question')[0].style.display = 'none';
    }
  });
});

// chart.js
const historyContext = document.getElementById('historyChart');
const pyramidContext = document.getElementById('pyramidChart')

function transparentize(value, opacity) {
  var alpha = 1 - opacity;
  let r = parseInt(value.slice(1, 3), 16),
    g = parseInt(value.slice(3, 5), 16),
    b = parseInt(value.slice(5, 7), 16);

    return 'rgba(' + r + ', ' + g + ', ' + b + ', ' + alpha + ')'
}

const CHART_COLORS = {
  red: '#FF6384',
  orange: '#FF9F40',
  yellow: '#FFCD56',
  green: '#4BC0C0',
  blue: '#36A2EB',
  purple: '#9966FF',
  grey: '#C9CBCF'
};

const historyData = {
  labels: historyList.map(function(obj) { return obj.date; }),
  datasets: [
    {
      label: '가입자',
      data: historyList.map(function(obj) { return obj.memberNum; }),
      borderColor: CHART_COLORS.red,
      backgroundColor: transparentize(CHART_COLORS.red, 0.5),
    },
    {
      label: '접속자',
      data: historyList.map(function(obj) { return obj.visitorNum; }),
      borderColor: CHART_COLORS.blue,
      backgroundColor: transparentize(CHART_COLORS.blue, 0.5),
    },
  ]
};

new Chart(historyContext, {
  type: 'line',
  data: historyData,
  options: {
    responsive: true,
    maintainAspectRatio: true,
    aspectRatio: 2,
    plugins: {
      legend: {
        position: 'top',
      },
    }
  },
});

const pyramidData = {
  labels: ['0-19', '20-29', '30-39', '40-49', '50-59', '60+'].reverse(),
  datasets: [
    {
      label: '남성',
      borderColor: CHART_COLORS.blue,
      borderWidth: 1,
      backgroundColor: transparentize(CHART_COLORS.blue, 0.5),
      borderRadius: 10,
      barThickness: 20,
      data: array.slice(0, 6).reverse(),
    },
    {
      label: '여성',
      borderColor: CHART_COLORS.red,
      borderWidth: 1,
      backgroundColor: transparentize(CHART_COLORS.red, 0.5),
      borderRadius: 10,
      barThickness: 20,
      data: array.slice(7, 12).reverse().map((k) => -k),
    },
  ]
}

new Chart(pyramidContext, {
  type: 'bar',
  data: pyramidData,
  options: {
    indexAxis: 'y',
    plugins: {
      legend: {
        display: false,
      },
      tooltip: {
        yAlign: 'bottom',
        titleAlign: 'center',
        callbacks: {
          label: function(context) {
            return `${context.dataset.label} ${Math.abs(context.raw)}`;
          }
        }
      }
    },
    responsive: true,
    maintainAspectRatio: true,
    aspectRatio: 3,
    scales: {
      x: {
        suggestedMin: -1 * Math.max(...array),
        suggestedMax: Math.max(...array),
        stacked: true,
        ticks: {
          callback: function(value) {
            return Math.abs(value);
          }
        },
        grid: {
//          display: false,
        },
      },
      y: {
        stacked: true,
        beginAtZero: true,
        grid: {
//          display: false,
        },
      }
    },
  }
});
