duplicateRoomName = false;

nameInput = document.querySelector('#name-input');
nicknameInput = document.querySelector('#nickname-input');
colorInput = document.querySelector('#color-input');

nameInput.addEventListener('input', validateName);
nicknameInput.addEventListener('input', validateNickname);
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
    colorPicked(parseInt(Math.random() * 7), parseInt(Math.random() * 14));

    document.getElementById('picked-color').style.width = document.getElementById('picked-color-label').clientHeight + 'px';
    document.getElementById('picked-color').style.height = document.getElementById('picked-color-label').clientHeight + 'px';
}

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

function validateName(e) {
    duplicateRoomName = false;
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

function validateNickname(e) {
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
    if (validateName(null) && validateNickname(null) && validateColor(null)) {
        return true;
    }
    return false;
}

// ajax로 duplicateName 확인
function checkDuplicateName() {

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

