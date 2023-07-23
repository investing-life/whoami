window.onload = function() {
    init();
}

function adjustWindowHeight() {
    var vh = window.innerHeight * 0.01;
    document.documentElement.style.setProperty("--vh", `${vh}px`);
}

window.addEventListener("resize", () => {
    // 높이 조정(vh)
    adjustWindowHeight();
    set_room_container_height();
    set_overlay_width();
    set_alarm_position();
});

window.onscroll = function() {
    var sidebar = document.querySelector('.sidebar');
    sidebar.style.top = window.scrollY + 'px';
};

function init() {
    document.querySelector('.after-login-container').style.visibility = 'hidden';
    adjustWindowHeight();
    set_room_container_height();
    set_alarm_position();
    set_room_font_color();
    set_overlay_width();
    document.querySelector('.after-login-container').style.visibility = 'visible';
}

function shuffle(array) {
    array.sort(() => Math.random() - 0.5);
}

function preventScroll(e) {
    e.preventDefault();
}

function openSidebar() {
    // 본문 스크롤 금지
    document.querySelector('.after-login-container').classList.add('scrollDisabled');
    document.querySelector('.after-login-container').addEventListener('scroll', preventScroll);
    document.querySelector('.after-login-container').addEventListener('touchmove', preventScroll);
    document.querySelector('.after-login-container').addEventListener('mousewheel', preventScroll);

    var sidebar = document.querySelector('.sidebar');
    var overlay = document.querySelector('.overlay');
    sidebar.style.right = 0;
    sidebar.style.top = window.scrollY + 'px';
    overlay.style.display = 'block';
}

function closeSidebar(immediate) {
    // 본문 스크롤 가능
    document.querySelector('.after-login-container').classList.remove('scrollDisabled');
    document.querySelector('.after-login-container').removeEventListener('scroll', preventScroll);
    document.querySelector('.after-login-container').removeEventListener('touchmove', preventScroll);
    document.querySelector('.after-login-container').removeEventListener('mousewheel', preventScroll);

    var sidebar = document.querySelector('.sidebar');
    var overlay = document.querySelector('.overlay');
    if (immediate) {
        setTimeout(function() {
            sidebar.style.right = -sidebar.offsetWidth + 'px';
        }, 100);
    } else {
        sidebar.style.right = -sidebar.offsetWidth + 'px';
    }
    overlay.style.display = 'none';
}

function set_overlay_width() {
    var container = document.querySelector('.after-login-container');
    var overlay = document.querySelector('.overlay');
    overlay.style.width = container.offsetWidth + 'px';
}

function set_room_container_height() {
    // room-container div 높이 맞춰주기
    roomList = document.querySelectorAll('.room-container');
    roomGroupList = [];
    divNum = 0;
    maxHeight = 0;

    // div 높이 재설정 시 기존 div 높이 설정 없애기
    for (i = 0; i < roomList.length; i++) {
        roomList[i].style.height = '';
    }

    // 한 줄에 몇 개의 div 있는지 구하기
    for (i = 0; i < roomList.length; i++) {
        if (roomGroupList.length == 0) {
            roomGroupList.push(roomList[i]);
            divNum++;
        } else if (roomGroupList[0].offsetTop != roomList[i].offsetTop) {
            break;
        }
        else {
            roomGroupList.push(roomList[i]);
            divNum++;
        }
    }

    roomGroupList = []
    // divNum을 바탕으로 group 별 maxHeight 구한 후 일괄 적용
    for (i = 0; i < roomList.length; i++) {
        if (i % divNum == 0) {
            roomGroupList.forEach(function(div) {
                div.style.height = maxHeight + 'px';
            });
            roomGroupList = [];
            maxHeight = 0;
        }
        roomGroupList.push(roomList[i]);
        if (roomList[i].offsetHeight > maxHeight) { maxHeight = roomList[i].offsetHeight;  }
    }
    // for문 밖에서도 한 번 더
    roomGroupList.forEach(function(div) {
        div.style.height = maxHeight + 'px';
    });
}

function set_room_font_color() {
    backgroundList = document.getElementsByClassName('room-name-background');
    nameList = document.getElementsByClassName('room-name');

    for (let i = 0; i < backgroundList.length; i++) {
        var rgbString = window.getComputedStyle(backgroundList[i]).backgroundColor.match(/\d+/g);
        var r = parseInt(rgbString[0]);
        var g = parseInt(rgbString[1]);
        var b = parseInt(rgbString[2]);

        // 가중치 적용하여 밝기 계산
        var brightness = (r * 0.299) + (g * 0.587) + (b * 0.114);
        if (brightness < 128) {
            nameList[i].style.color = 'white';
            nameList[i].style.borderColor = 'black';
        }
    }
}

function set_alarm_position() {
    if (document.getElementsByClassName('sidebar-alarm').length != 0) {
        document.getElementsByClassName('sidebar-alarm')[0].style.top = parseInt(window.getComputedStyle(document.getElementsByClassName('sidebar-open-button')[0]).paddingTop) + Math.floor(parseInt(getComputedStyle(document.documentElement).fontSize) / 4) + 'px';
    }
}