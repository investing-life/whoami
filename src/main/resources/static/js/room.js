colorInput = document.querySelector('#color-input');

colorInput.addEventListener('input', validateColor);

var currX = -1;
var currY = -1;

// 색상 팔레트
var pallet = [["#FF0000", "#FF5E00", "#FFBB00", "#FFE400", "#ABF200", "#1DDB16", "#00D8FF", "#0054FF", "#0100FF", "#5F00FF", "#FF00DD", "#FF007F", "#000000", "#FFFFFF"],
              ["#FFD8D8", "#FAE0D4", "#FAECC5", "#FAF4C0", "#E4F7BA", "#CEFBC9", "#D4F4FA", "#D9E5FF", "#DAD9FF", "#E8D9FF", "#FFD9FA", "#FFD9EC", "#F6F6F6", "#EAEAEA"],
              ["#FFA7A7", "#FFC19E", "#FFE08C", "#FAED7D", "#CEF279", "#B7F0B1", "#B2EBF4", "#B2CCFF", "#B5B2FF", "#D1B2FF", "#FFB2F5", "#FFB2D9", "#D5D5D5", "#BDBDBD"],
              ["#F15F5F", "#F29661", "#F2CB61", "#E5D85C", "#BCE55C", "#86E57F", "#5CD1E5", "#6799FF", "#6B66FF", "#A566FF", "#F361DC", "#F361A6", "#A6A6A6", "#8C8C8C"],
              ["#CC3D3D", "#CC723D", "#CCA63D", "#C4B73B", "#9FC93C", "#47C83E", "#3DB7CC", "#4374D9", "#4641D9", "#8041D9", "#D941C5", "#D9418C", "#747474", "#5D5D5D"],
              ["#980000", "#993800", "#997000", "#998A00", "#6B9900", "#2F9D27", "#008299", "#003399", "#050099", "#3F0099", "#990085", "#99004C", "#4C4C4C", "#353535"],
              ["#670000", "#662500", "#664B00", "#665C00", "#476600", "#22741C", "#005766", "#002266", "#030066", "#2A0066", "#660058", "#660033", "#212121", "#191919"]];


window.onload = function() {
    init();
}

function init() {
    // 테이블을 생성할 위치 선택
    var table = document.getElementById('color-table');

    // 10x10 테이블 생성
    for (var i = 0; i < 7; i++) {
        var row = table.insertRow();

        for (var j = 0; j < 14; j++) {
            var cell = row.insertCell();
            cell.style.backgroundColor = pallet[i][j];
            cell.classList.add('cell');
            cell.addEventListener("click", handleCellClick)
        }
    }

    // 초기 색상 지정
    colorInput.value = roomColor;
    document.querySelector('#picked-color').style.backgroundColor = colorInput.value;
}

// 방 색상 변경 버튼 클릭
$(document).ready(function() {
  const changeColorMenu = $('#change-color-menu');
  const colorForm = $('#color-form');

  changeColorMenu.click(function() {
    colorForm.slideToggle();
    document.getElementById('picked-color').style.width = document.getElementById('picked-color-label').clientHeight + 'px';
    document.getElementById('picked-color').style.height = document.getElementById('picked-color-label').clientHeight + 'px';

    if (colorForm.is(':visible')) {
      // 스크롤 다운
      $('html, body').animate({ scrollTop: colorForm.offset().top }, 'smooth');
    }
  });
});

// 클릭 이벤트 핸들러 함수
function handleCellClick(event) {
    var cell = event.target; // 클릭한 셀 가져오기
    var rowIndex = cell.parentElement.rowIndex;
    var cellIndex = cell.cellIndex;
    colorPicked(rowIndex, cellIndex);
    // 클릭한 셀의 행 인덱스와 열 인덱스를 출력 (디버깅용)
}

// x번째 row, y번째 column에 접근하는 함수
function accessCell(x, y) {
    var table = document.getElementById("color-table");
    var row = table.rows[x];
    var cell = row.cells[y];
    return cell;
}

function colorPicked(x, y) {
    if (currX >= 0 && currY >= 0) {
        accessCell(currX, currY).classList.remove('active');
    }
    accessCell(x, y).classList.add('active');
    document.querySelector('#color-input').value = pallet[x][y];
    document.querySelector('#picked-color').style.backgroundColor = pallet[x][y];
    currX = x;
    currY = y;
}


function validateColor(e) {
    document.querySelector('#color-input-invalid-1').style.display='none';
    if (!/^#[0-9A-Fa-f]{6}$/.test(colorInput.value)) {
        document.querySelector('#color-input-invalid-1').style.display='block';
        return false;
    }
    document.querySelector('#picked-color').style.backgroundColor = colorInput.value;
    return true;
}

function validateForm() {
    if (validateColor(null)) {
        return true;
    }
    return false;
}


// Go! 넣을지 말지
for (let i = 0; i < document.querySelectorAll('.test-friend').length; i++) {
    temp = document.querySelectorAll('.test-friend')[i];
    if (!testList[i]) {
        temp.innerText = 'Go!';
    } else {
        temp.innerText = '(완료)';
        temp.style.fontStyle = 'italic';
        temp.style.color = '#D5D5D5';
        temp.onclick = '';
        temp.style.cursor = 'none';

    }
}

// 방 제목 수정 부분
duplicateRoomName = false;
originalTitle = '';

function showEditTitleForm() {
    // 이름을 바꾸고 있었다면 submit 하고 실패하면 return
    if (document.getElementById("edit-name-form") != null && !submitEditNameForm()) {
        return false;
    }

    var titleText = document.getElementById("title-text");
    var editTitleForm = document.createElement("form");
    editTitleForm.setAttribute("id", "edit-title-form");
    editTitleForm.style.width = "90%"
    editTitleForm.style.margin = "0 auto";
    // 입력 필드를 생성하고 폼에 추가
    var nameInput = document.createElement("input");
    nameInput.setAttribute("type", "text");
    nameInput.setAttribute("class", "form-control");
    nameInput.setAttribute("id", "name-input");
    nameInput.setAttribute("name", "title");
    nameInput.setAttribute("onblur", "submitEditTitleForm()");
    originalTitle = titleText.innerText;
    nameInput.value = titleText.innerText;
    editTitleForm.appendChild(nameInput);

    // validate 부분
    nameInput.addEventListener('input', validateName);

    titleText.parentNode.replaceChild(editTitleForm, titleText);
    nameInput.select();

    var modifyTitle = document.getElementById("modify-title");
    var submitTitle = document.getElementById("submit-title");
    modifyTitle.style.display = "none";
    submitTitle.style.display = "block";
}

function submitEditTitleForm() {
    var editTitleForm = document.getElementById("edit-title-form");

    // 폼 데이터 수집
    var formData = new FormData(editTitleForm);
    if (!validateName(null)) {
        return false;
    }

    // CSRF 토큰 가져오기
    var csrfToken = $("meta[name='_csrf']").attr("content");
    var csrfHeader = $("meta[name='_csrf_header']").attr("content");

    // AJAX 요청
    var xhr = new XMLHttpRequest();
    xhr.open("POST", window.location.pathname + "/title", true);
    xhr.setRequestHeader(csrfHeader, csrfToken);
    xhr.send(formData);

    var titleText = document.createElement("span");
    titleText.setAttribute("id", "title-text");
    var nameInput = document.getElementById("name-input");
    titleText.innerText = nameInput.value;

    // 폼 제출 후 원래의 이름 텍스트로 다시 변경
    editTitleForm.parentNode.replaceChild(titleText, editTitleForm);
    var modifyTitle = document.getElementById("modify-title");
    var submitTitle = document.getElementById("submit-title");
    modifyTitle.style.display = "inline";
    submitTitle.style.display = "none";

    document.querySelector('#name-input-valid').style.display='none';

    // onclick 수정 기능이 있으면 onblur에서 저장되자마자 다시 click 돼서 수정 창이 켜짐
    modifyTitle.onclick = null;
    setTimeout(function() {
        modifyTitle.onclick = showEditTitleForm;
    }, 500);

    return true;
}

function validateName(e) {
    duplicateRoomName = false;
    var nameInput = document.querySelector('#name-input')
    checkDuplicateName();
    document.querySelector('#name-input-valid').style.display='none';
    document.querySelector('#name-input-invalid-1').style.display='none';
    document.querySelector('#name-input-invalid-2').style.display='none';
    document.querySelector('#name-input-invalid-3').style.display='none';
    if (nameInput.value.length < 1 || nameInput.value.length > 15) {
        document.querySelector('#name-input-invalid-1').style.display='block';
        return false;
    } else if (/^\s/.test(nameInput.value) || /\s$/.test(nameInput.value)) {
        document.querySelector('#name-input-invalid-2').style.display='block';
        return false;
    } else if (duplicateRoomName) {
        document.querySelector('#name-input-invalid-3').style.display='block';
        return false;
    } else {
        document.querySelector('#name-input-valid').style.display='block';
    }
    return true;
}

// ajax로 duplicateName 확인
function checkDuplicateName() {
    var nameInput = document.querySelector('#name-input')
    if (originalTitle == nameInput.value) { duplicateRoomName = false; return; }

    // CSRF 토큰 가져오기
    var csrfToken = $("meta[name='_csrf']").attr("content");
    var csrfHeader = $("meta[name='_csrf_header']").attr("content");

    $.ajax({
        url: "/checkDuplicateRoomName", // 중복 확인을 위한 서버의 엔드포인트 URL
        type: "POST",
        data: { name: nameInput.value },
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken); // CSRF 토큰을 요청 헤더에 포함
        },
        async: false,
        success: function (response) {
            // 서버로부터 받은 응답 처리
            if (response.duplicate) {
              // 중복된 Name인 경우 처리
              duplicateRoomName = true;
            } else {
              // 중복되지 않은 Name인 경우 처리
              duplicateRoomName = false;
            }
        },
        error: function () {
            // 요청 실패 시 처리
            alert("서버 요청에 실패하였습니다.");
        },
    })
}

// 이름 수정 부분
function showEditNameForm() {
    // 방 제목을 바꾸고 있었다면 submit하고 실패하면 return
    if (document.getElementById("edit-title-form") != null && !submitEditTitleForm()) {
        return false;
    }

    var nameText = document.getElementById("name-text");
    var editNameForm = document.createElement("form");
    editNameForm.setAttribute("id", "edit-name-form");
    // 입력 필드를 생성하고 폼에 추가
    var nicknameInput = document.createElement("input");
    nicknameInput.setAttribute("type", "text");
    nicknameInput.setAttribute("class", "form-control");
    nicknameInput.setAttribute("id", "nickname-input");
    nicknameInput.setAttribute("name", "nickname");
    nicknameInput.setAttribute("onblur", "submitEditNameForm()");
    nicknameInput.value = nameText.innerText;
    editNameForm.appendChild(nicknameInput);

    // validate 부분
    nicknameInput.addEventListener('input', validateNickname);

    nameText.parentNode.replaceChild(editNameForm, nameText);
    nicknameInput.select();

    var modifyName = document.getElementById("modify-name");
    modifyName.innerText = "확인";
    modifyName.onclick = submitEditNameForm;
}

function submitEditNameForm() {
    var editNameForm = document.getElementById("edit-name-form");

    // 폼 데이터 수집
    var formData = new FormData(editNameForm);
    if (!validateNickname(null)) {
        return false;
    }

    // CSRF 토큰 가져오기
    var csrfToken = $("meta[name='_csrf']").attr("content");
    var csrfHeader = $("meta[name='_csrf_header']").attr("content");

    // AJAX 요청
    var xhr = new XMLHttpRequest();
    xhr.open("POST", window.location.pathname + "/name", true);
    xhr.setRequestHeader(csrfHeader, csrfToken);
    xhr.send(formData);

    var nameText = document.createElement("span");
    nameText.setAttribute("id", "name-text");
    var nicknameInput = document.getElementById("nickname-input");
    nameText.innerText = nicknameInput.value;

    // 폼 제출 후 원래의 이름 텍스트로 다시 변경
    editNameForm.parentNode.replaceChild(nameText, editNameForm);
    var modifyName = document.getElementById("modify-name");
    modifyName.innerText = "수정";

    setTimeout(function() {
        modifyName.onclick = showEditNameForm;
    }, 500);

    return true
}

function validateNickname(e) {
    nicknameInput = document.querySelector('#nickname-input');
    document.querySelector('#nickname-input-invalid-1').style.display='none';
    document.querySelector('#nickname-input-invalid-2').style.display='none';
    if (nicknameInput.value.length < 1 || nicknameInput.value.length > 15) {
        document.querySelector('#nickname-input-invalid-1').style.display='block';
        return false;
    } else if (/^\s/.test(nicknameInput.value) || /\s$/.test(nicknameInput.value)) {
        document.querySelector('#nickname-input-invalid-2').style.display='block';
        return false;
    }
    return true;
}

// 메세지 더보기
function moreMessage() {
    messageList = document.querySelectorAll(".message-content");
    moreMessage = document.querySelector("#more-message");
    showAll = document.querySelector("#show-all");
    for (i = 3; i < 6 && i < messageList.length; i++) {
        messageList[i].style.display = "";
    }
    moreMessage.style.display = "none";
    if (messageList.length > 6) {
        showAll.style.display = "";
    }
}

function leaveRoom() {
  var result = confirm("방에서 나가도 보낸 메세지는 삭제되지 않으며, 받은 메세지는 복구할 수 없습니다.\n정말 방을 나가시겠습니까?");
  if (!result) { return; }

  // CSRF 토큰 가져오기
  var csrfToken = $("meta[name='_csrf']").attr("content");
  var csrfHeader = $("meta[name='_csrf_header']").attr("content");

  var xhr = new XMLHttpRequest();
  xhr.open("POST", window.location.pathname + "/leave", true);
  xhr.setRequestHeader("Content-Type", "application/json");
  xhr.setRequestHeader(csrfHeader, csrfToken);
  xhr.onreadystatechange = function () {
    if (xhr.readyState === XMLHttpRequest.DONE) {
      if (xhr.status === 200) {
        // 성공적으로 요청을 보냈을 때 처리할 코드
        window.location.href = "/home";
      } else {
        // 요청이 실패했을 때 처리할 코드
        console("방 탈출 실패");
      }
    }
  };
  xhr.send();
}


// chart.js
const ctx = document.getElementById('myChart');

const COLORS = [
  '#4dc9f6',
  '#f67019',
  '#f53794',
  '#537bc4',
  '#acc236',
  '#166a8f',
  '#00a950',
  '#58595b',
  '#8549ba'
];

function color(index) {
    return COLORS[index % COLORS.length];
}

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

responsiveFontSize = 20 / 550 * ctx.offsetWidth;
responsiveBorderWidth = 6 / 550 * ctx.offsetWidth;

Chart.defaults.plugins.legend.display = false
Chart.defaults.elements.point.pointStyle = false
Chart.defaults.font.size = responsiveFontSize;
new Chart(ctx, {
  type: 'radar',
  data: {
    labels: ['개방성', '성실성', '외향성', '우호성', '신경증'],
    datasets: [{
      label: '나의 평가',
      data: [openness, conscientiousness, extraversion, agreeableness, neuroticism],
      borderColor: CHART_COLORS.red,
      backgroundColor: transparentize(CHART_COLORS.red, 0.5),
      borderWidth: responsiveBorderWidth,
      fill: true
    }, ...(openness_ !== 0 ? [{
      label: '친구의 평가',
      data: [openness_, conscientiousness_, extraversion_, agreeableness_, neuroticism_],
      borderColor: CHART_COLORS.green,
      backgroundColor: transparentize(CHART_COLORS.green, 0.5),
      borderWidth: responsiveBorderWidth,
      fill: true
    }] : []), {
      label: '한국인 평균',
      data: [2.9277, 2.9506, 3.0734, 3.0231, 3.0474],
      borderColor: CHART_COLORS.blue,
      backgroundColor: transparentize(CHART_COLORS.blue, 0.5),
      borderWidth: responsiveBorderWidth,
      fill: true
    }]
  },
  options: {
    responsive: false,
    scales: {
      r: {
        beginAtZero: true,
        min: 0,
        max: 5,
        ticks: {
          stepSize: 1,
        },
        pointLabels: {
          display: false,
          font: {
            family: 'Noto Sans KR',
            size: responsiveFontSize+2,
            weight: 'bold'
          }
        }
      }
    },
  },
});