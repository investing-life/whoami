duplicateID = false;
duplicateEmail = false;

idInput = document.querySelector('#id-input');
passwordInput = document.querySelector('#password-input');
passwordCheckInput = document.querySelector('#passwordCheck-input');
inlineRadio1 = document.querySelector('#inlineRadio1');
inlineRadio2 = document.querySelector('#inlineRadio2');
emailInput = document.querySelector('#email-input');

idInput.addEventListener('input', validateId);
passwordInput.addEventListener('input', validatePassword);
passwordCheckInput.addEventListener('input', validatePasswordCheck);
emailInput.addEventListener('input', validateEmail);

function validateId(e) {
    duplicateID = false;
    checkDuplicateID();
    document.querySelector('#id-input-valid').style.display='none';
    document.querySelector('#id-input-invalid-1').style.display='none';
    document.querySelector('#id-input-invalid-2').style.display='none';
    document.querySelector('#id-input-invalid-3').style.display='none';
    if (idInput.value.length < 4 || idInput.value.length > 20) {
        document.querySelector('#id-input-invalid-1').style.display='block';
        return false;
    } else if (!/^[0-9a-zA-Z]+$/.test(idInput.value)) {
        document.querySelector('#id-input-invalid-2').style.display='block';
        return false;
    } else if (duplicateID) {
        document.querySelector('#id-input-invalid-3').style.display='block';
        return false;
    } else {
        document.querySelector('#id-input-valid').style.display='block';
    }
    return true;
}

function validatePassword(e) {
    document.querySelector('#password-input-invalid-1').style.display='none';
    document.querySelector('#password-input-invalid-2').style.display='none';
    document.querySelector('#password-input-invalid-3').style.display='none';
    document.querySelector('#password-input-invalid-4').style.display='none';
    if (passwordInput.value.length < 10 || passwordInput.value.length > 20) {
        document.querySelector('#password-input-invalid-1').style.display='block';
        return false;
    } else if (passwordInput.value.search(/\s/) != -1) {
        document.querySelector('#password-input-invalid-2').style.display='block';
        return false;
    } else if (!/^[a-zA-Z0-9!@#$%^&*]+$/.test(passwordInput.value)) {
        document.querySelector('#password-input-invalid-3').style.display='block';
        return false;
    } else if (!/\d/.test(passwordInput.value) || !/[a-zA-Z]/.test(passwordInput.value)) {
        document.querySelector('#password-input-invalid-4').style.display='block';
        return false
    }
    validatePasswordCheck(e);
    return true;
}

function validatePasswordCheck(e) {
    document.querySelector('#passwordCheck-input-invalid').style.display='none';
    if (passwordInput.value != passwordCheckInput.value) {
        document.querySelector('#passwordCheck-input-invalid').style.display='block';
        return false;
    }
    return true;
}


function validateEmail(e) {
    duplicateEmail = false;
    checkDuplicateEmail();
    document.querySelector('#email-input-invalid-1').style.display='none';
    document.querySelector('#email-input-invalid-2').style.display='none';
    if (emailInput.value != "" && !/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z0-9-.]+$/.test(emailInput.value)) {
        document.querySelector('#email-input-invalid-1').style.display='block';
        return false;
    } else if (duplicateEmail) {
        document.querySelector('#email-input-invalid-2').style.display='block';
        return false;
    }
    return true;
}

function validateForm() {
    if (validateId(null) && validatePassword(null) && validatePasswordCheck(null) && validateEmail(null)) {
        return true;
    }
    return false;
}

// ajax로 duplicateID 확인
function checkDuplicateID() {

    // CSRF 토큰 가져오기
    var csrfToken = $("meta[name='_csrf']").attr("content");
    var csrfHeader = $("meta[name='_csrf_header']").attr("content");

    $.ajax({
        url: "/checkDuplicateID", // 중복 확인을 위한 서버의 엔드포인트 URL
        type: "POST",
        data: { ID: idInput.value },
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken); // CSRF 토큰을 요청 헤더에 포함
        },
        async: false,
        success: function (response) {
            // 서버로부터 받은 응답 처리
            if (response.duplicate) {
              // 중복된 ID인 경우 처리
              duplicateID = true;
            } else {
              // 중복되지 않은 ID인 경우 처리
              duplicateID = false;
            }
        },
        error: function () {
            // 요청 실패 시 처리
            alert("서버 요청에 실패하였습니다.");
        },
    })
}

// ajax로 duplicateEmail 확인
function checkDuplicateEmail() {

    // CSRF 토큰 가져오기
    var csrfToken = $("meta[name='_csrf']").attr("content");
    var csrfHeader = $("meta[name='_csrf_header']").attr("content");

    $.ajax({
        url: "/checkDuplicateEmail", // 중복 확인을 위한 서버의 엔드포인트 URL
        type: "POST",
        data: { email: emailInput.value },
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken); // CSRF 토큰을 요청 헤더에 포함
        },
        async: false,
        success: function (response) {
            // 서버로부터 받은 응답 처리
            if (response.duplicate) {
              // 중복된 ID인 경우 처리
              duplicateEmail = true;
            } else {
              // 중복되지 않은 ID인 경우 처리
              duplicateEmail = false;
            }
        },
        error: function () {
            // 요청 실패 시 처리
            alert("서버 요청에 실패하였습니다.");
        },
    })
}