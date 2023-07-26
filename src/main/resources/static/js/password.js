passwordInput = document.querySelector('#password-input');
passwordCheckInput = document.querySelector('#passwordCheck-input');

passwordInput.addEventListener('input', validatePassword);
passwordCheckInput.addEventListener('input', validatePasswordCheck);

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

function validateForm() {
    if (validatePassword(null) && validatePasswordCheck(null)) {
        return true;
    }
    return false;
}
