idInput = document.querySelector('#id-input');
passwordInput = document.querySelector('#password-input');
eraseButtonList = document.querySelectorAll('.erase-all')

idInput.addEventListener('input', showHideIdEraseButton);
passwordInput.addEventListener('input', showHidePasswordEraseButton);

idInput.onfocus = function() {
    showHideIdEraseButton();
}
passwordInput.onfocus = function() {
    showHidePasswordEraseButton();
};
idInput.onclick = eraseId();
passwordInput.onclick = erasePassword();
idInput.addEventListener('blur', hideEraseButton);
passwordInput.addEventListener('blur', hideEraseButton);

for (i = 0; i < eraseButtonList.length; i++) {
    eraseButtonList[i].onmousedown = function(event) {
        event.preventDefault(); // 기본 동작 중지
        event.stopPropagation(); // 이벤트 전파 중지
    }
}

idInput.focus();

function hideEraseButton() {
    eraseButtonList[0].style.display = 'none';
    eraseButtonList[1].style.display = 'none';
}

function showHideIdEraseButton() {
    eraseButtonList[1].style.display = 'none';
    if (idInput.value.length != 0) {
        eraseButtonList[0].style.display = 'block';
    } else {
        eraseButtonList[0].style.display = 'none';
    }
}

function showHidePasswordEraseButton() {
    eraseButtonList[0].style.display = 'none';
    if (passwordInput.value.length != 0) {
        eraseButtonList[1].style.display = 'block';
    } else {
        eraseButtonList[1].style.display = 'none';
    }
}

function eraseId() {
    idInput.value = '';
    showHideIdEraseButton()
}
function erasePassword() {
    passwordInput.value = '';
    showHidePasswordEraseButton()
}