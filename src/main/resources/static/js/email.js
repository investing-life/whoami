duplicateEmail = false;

emailInput = document.querySelector('#email-input');

emailInput.addEventListener('input', validateEmail);

function validateEmail(e) {
    duplicateEmail = false;
    checkDuplicateEmail();
    document.querySelector('#email-input-invalid-1').style.display='none';
    document.querySelector('#email-input-invalid-2').style.display='none';
    if (emailInput.value == "") {
        return true;
    } else if (!/^[a-zA-Z0-9+-\_.]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$/.test(emailInput.value)) {
        document.querySelector('#email-input-invalid-1').style.display='block';
        return false;
    } else if (duplicateEmail) {
        document.querySelector('#email-input-invalid-2').style.display='block';
        return false;
    }
    return true;
}

function validateForm() {
    if (validateEmail(null)) {
        return true;
    }
    return false;
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