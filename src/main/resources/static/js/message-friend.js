var keyboardOn = false;
var initialWindowHeight = 0;

window.onload = function() {
    initialWindowHeight = window.innerHeight;
    adjustWindowHeight();
    document.querySelector('.after-login-container').style.visibility = 'visible';
    var scrollContainer = document.querySelector('.message-container');
    scrollContainer.scrollTo({
        top: scrollContainer.scrollHeight,
        behavior: 'smooth'
    });
    if (tipPopup) {
        showTip();
    } else {
        document.getElementById('info-icon').style.opacity = 1;
    }
}

function adjustWindowHeight() {
    var vh = window.innerHeight * 0.01;
    document.documentElement.style.setProperty("--vh", `${vh}px`);
}

window.addEventListener("resize", () => {
    // 아래 끝까지 scroll 되어 있으면 resize 후에도 아래까지 scroll
    var scrollContainer = document.querySelector('.message-container');
    var scrolledDown = (scrollContainer.scrollHeight - scrollContainer.clientHeight <= scrollContainer.scrollTop + 1);
    // 높이 조정(vh)
    adjustWindowHeight();
    if (scrolledDown) {
        scrollContainer.scrollTo({
            top: scrollContainer.scrollHeight,
            behavior: 'smooth'
        });
    }
    // keyboard 나왔는지 감지
    if (window.innerHeight < 0.8 * initialWindowHeight) {
        keyboardOn = true;
    } else {
        keyboardOn = false;
    }
});

var initialHeight = document.getElementById('message-input').clientHeight;
var computedStyle = window.getComputedStyle(document.getElementById('message-input'));
var paddingTop = parseFloat(computedStyle.getPropertyValue('padding-top').replace('px', ''));
var paddingBottom = parseFloat(computedStyle.getPropertyValue('padding-bottom').replace('px', ''));
var textInnerHeight = initialHeight - Math.round(paddingTop + paddingBottom);

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
}

function hideTip() {
    if (keyboardOn) {
        document.querySelector('#message-input').focus();
    }
    document.querySelector('.overlay').style.display = 'none';

    $('#today-tip')[0].style.transform = ' scale(0, 0)';
    $('#info-icon').show().animate({
        opacity: '1',
    }, 500);
}

function showTip() {
    if (keyboardOn) {
        document.querySelector('#message-input').focus();
    }
    document.querySelector('.overlay').style.display = 'block';

    $('#today-tip')[0].style.transform = 'scale(1, 1)';
    $('#info-icon').animate({
        opacity: '0',
    }, 500, function() {
        $(this).hide();
    });
}