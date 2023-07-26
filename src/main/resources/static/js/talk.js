initialHeight = document.getElementById('message-input').clientHeight;
computedStyle = window.getComputedStyle(document.getElementById('message-input'));
paddingTop = parseFloat(computedStyle.getPropertyValue('padding-top').replace('px', ''));
paddingBottom = parseFloat(computedStyle.getPropertyValue('padding-bottom').replace('px', ''));
textInnerHeight = initialHeight - Math.round(paddingTop + paddingBottom);

window.onload = function() {
    init();

    var scrollContainer = document.getElementById('scroll-container');
    scrollContainer.scrollTo({
        top: scrollContainer.scrollHeight,
        behavior: 'smooth'
    });
}

window.addEventListener("resize", () => {
    init();
});

function init() {
    adjustContainerHeight();

    document.querySelector('.after-login-container').style.visibility = 'hidden';
    var timeQList = document.querySelectorAll('.time-Q');
    for (let i = 0; i < timeQList.length; i++) {
        timeQList[i].style.left = -(timeQList[i].offsetWidth + 10) + 'px'
    }
    var timeAList = document.querySelectorAll('.time-A');
    for (let i = 0; i < timeAList.length; i++) {
        timeAList[i].style.right = -(timeAList[i].offsetWidth + 10) + 'px'
    }

    document.getElementById('message-input').rows = 1;
    initialHeight = document.getElementById('message-input').clientHeight;
    computedStyle = window.getComputedStyle(document.getElementById('message-input'));
    paddingTop = parseFloat(computedStyle.getPropertyValue('padding-top').replace('px', ''));
    paddingBottom = parseFloat(computedStyle.getPropertyValue('padding-bottom').replace('px', ''));
    textInnerHeight = initialHeight - Math.round(paddingTop + paddingBottom);
    adjustTextareaRows(document.getElementById('message-input'));

    adjustSendIcon();

    document.querySelector('.after-login-container').style.visibility = 'visible';
}

function textInput(textarea) {
    // 버튼 enable / disable
    if (textarea.value == '') {
        document.getElementById('send-button').classList.add('disabled');
    } else {
        document.getElementById('send-button').classList.remove('disabled');
    }
    adjustTextareaRows(textarea);
}

// textarea 높이 조절
function adjustTextareaRows(textarea) {
    textarea.rows = 1;
    // 현재 textarea의 텍스트 높이를 구합니다.
    var textHeight = textarea.scrollHeight;
    // 텍스트 높이를 기준으로 행(row)의 개수를 계산합니다.
    var rows = (textHeight - initialHeight) / textInnerHeight + 1;
    // textarea의 행(row) 속성을 업데이트합니다.
    if (rows > 3) {
        textarea.rows = 3;
    } else {
        textarea.rows = rows;
        textarea.scrollTop = textarea.scrollHeight;
    }
    // 메세지 전송 버튼 높이
    document.getElementById('send-button').style.height = textarea.offsetHeight + 'px';

    adjustContainerHeight();

}

function adjustSendIcon() {
    buttonInnerWidth = parseFloat(window.getComputedStyle(document.getElementById('send-button')).getPropertyValue('width').replace('px',''))
    -parseFloat(window.getComputedStyle(document.getElementById('send-button')).getPropertyValue('padding-left').replace('px',''))
    -parseFloat(window.getComputedStyle(document.getElementById('send-button')).getPropertyValue('padding-right').replace('px',''));
    iconInnerWidth = parseFloat(window.getComputedStyle(document.getElementById('send-icon')).getPropertyValue('width').replace('px',''));

    document.getElementById('send-icon').style.marginLeft = (buttonInnerWidth - iconInnerWidth) / 2 + 'px';
}

function adjustContainerHeight() {
    // 아래 끝까지 scroll 되어 있으면 resize 후에도 아래까지 scroll

    var scrollContainer = document.getElementById('scroll-container');
    var scrolledDown = (scrollContainer.scrollHeight - scrollContainer.clientHeight <= scrollContainer.scrollTop + 1);

    scrollContainer.style.height = window.innerHeight
    - document.querySelector('h3.py-4').offsetHeight
    - document.querySelector('#input-div').offsetHeight - 10 + 'px';

    if (scrolledDown) {
        scrollContainer.scrollTo({
            top: scrollContainer.scrollHeight,
            behavior: 'smooth'
        });
    }
}

function send_message() {
    var message = document.getElementById("message-input").value;
    if (message == '') {
        return false;
    }

    // CSRF 토큰 가져오기
    var csrfToken = $("meta[name='_csrf']").attr("content");
    var csrfHeader = $("meta[name='_csrf_header']").attr("content");

    // AJAX 요청
    var xhr = new XMLHttpRequest();
    xhr.open("POST", window.location.pathname, true);
    xhr.setRequestHeader(csrfHeader, csrfToken);
    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    xhr.onreadystatechange = function() {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status === 200) {
                var container = document.getElementById('container');
                // 날짜가 바뀌었으면 추가
                var currentTime = new Date();
                var year = currentTime.getFullYear();
                var month = currentTime.getMonth() + 1;
                var date = currentTime.getDate();
                if (document.querySelectorAll('.date').length == 0 ||
                    year != parseInt(document.querySelectorAll('.date')[document.querySelectorAll('.date').length - 1].innerText.split(' ')[0].replace('년', '')) ||
                    month != parseInt(document.querySelectorAll('.date')[document.querySelectorAll('.date').length - 1].innerText.split(' ')[1].replace('월', '')) ||
                    date != parseInt(document.querySelectorAll('.date')[document.querySelectorAll('.date').length - 1].innerText.split(' ')[2].replace('일', ''))) {

                    dateDiv = document.createElement('div');
                    dateDiv.classList.add('date');

                    var dayOfWeek = currentTime.getDay();
                    // 요일 텍스트 매핑
                    var daysOfWeek = ["일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"];
                    var formattedDayOfWeek = daysOfWeek[dayOfWeek];
                    // 날짜 형식 포맷
                    var formattedDate = year + "년 " + month + "월 " + date + "일 " + formattedDayOfWeek;

                    dateDiv.innerText = formattedDate;

                    if (document.getElementById('no-message') != null) {
                        var scrollContainer = document.getElementById('scroll-container');
                        scrollContainer.removeChild(document.getElementById('no-message'));
                        dateDiv.style.marginTop = '0';
                    }

                    tempDiv = document.createElement('div');
                    tempDiv.style.clear = 'both';
                    container.appendChild(tempDiv);
                    container.appendChild(dateDiv);

                }

                var messageDiv = document.createElement('div');
                messageDiv.classList.add('message-Q');
                messageDiv.innerText = message;
                document.getElementById("message-input").value = '';
                document.getElementById('send-button').classList.add('disabled');
                adjustTextareaRows(document.getElementById("message-input"));
                var timeDiv = document.createElement('div');
                timeDiv.classList.add('time-Q');
                timeDiv.innerText = getCurrentTime();
                messageDiv.appendChild(timeDiv);
                container.appendChild(messageDiv);

                var scrollContainer = document.getElementById('scroll-container');
                scrollContainer.scrollTop = container.scrollHeight;
            } else {
                alert(xhr.responseText);
            }
        }
    };
    xhr.send("message=" + encodeURIComponent(message.replaceAll("\n", "\r\n")));
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