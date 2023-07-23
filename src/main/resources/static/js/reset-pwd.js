// 폼 제출 이벤트 핸들러
document.getElementById("reset-pwd-form").onsubmit = function(event) {
  event.preventDefault(); // 폼의 기본 동작 중지

  // 버튼 비활성화
  var submitButton = document.getElementById("submit-button");
  submitButton.disabled = true;
  submitButton.textContent = "검색 중."
  var intervalId = setInterval(repeatAction, 1000);

  // 폼 데이터 가져오기
  var formData = new FormData(this);

  // CSRF 토큰 가져오기
  var csrfToken = $("meta[name='_csrf']").attr("content");
  var csrfHeader = $("meta[name='_csrf_header']").attr("content");

  formData.append("_csrf", csrfToken);

  // AJAX 요청 보내기
  var xhr = new XMLHttpRequest();
  xhr.open("POST", "/reset-pwd");
  xhr.onreadystatechange = function() {
    if (xhr.readyState === XMLHttpRequest.DONE) {
      if (xhr.status === 200) {
        // 응답 처리
        var response = JSON.parse(xhr.responseText);
        if (response.succeed == "true") {
          alert(response.message);
          window.location.href = "/login";
        } else {
          alert(response.message);
          submitButton.disabled = false;
          submitButton.textContent = "비밀번호 초기화";
        }
      } else {
        // 에러 처리
        alert("요청이 실패했습니다.");
        submitButton.disabled = false;
        submitButton.textContent = "비밀번호 초기화";
      }
      clearInterval(intervalId);
    }
  };
  xhr.send(formData);
};

function repeatAction() {
  var submitButton = document.getElementById("submit-button");
  if (submitButton.textContent == "검색 중.") {
    submitButton.textContent = "검색 중..";
  } else if (submitButton.textContent == "검색 중..") {
    submitButton.textContent = "검색 중...";
  } else if (submitButton.textContent == "검색 중...") {
    submitButton.textContent = "검색 중.";
  }
}
