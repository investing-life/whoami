window.onload = function() {
    adjustWindowHeight();
    document.querySelector('.after-login-container').style.visibility = 'visible';
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
});