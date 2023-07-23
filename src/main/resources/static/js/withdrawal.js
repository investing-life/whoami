function validateForm() {
    if (document.querySelector('#flexCheckDefault').checked) {
        document.querySelector('#check-invalid').style.display = 'none';
        return true;
    } else {
        document.querySelector('#check-invalid').style.display = 'block';
        return false;
    }
}
