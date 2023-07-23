window.onload = function() {
    var currentDomain = window.location.protocol + '//' + window.location.hostname;

    // <div> 요소를 생성합니다.
    const domainDiv = document.getElementById('domain');
    domainDiv.innerText = currentDomain + '/home/rooms/' + link;

    // <i> 요소를 생성하여 <div> 안에 추가합니다.
    const shareIcon = document.createElement('i');
    shareIcon.id = 'share-button';
    shareIcon.className = 'fas fa-share-from-square';
    shareIcon.style.cursor = 'pointer';
    domainDiv.appendChild(shareIcon);

    if (domainDiv.offsetLeft != shareIcon.offsetLeft) {
        shareIcon.style.marginLeft = '0.8rem';
    }

    shareIcon.addEventListener('click', async () => {
        try {
            await navigator.share({
                title: 'Who Am I?',
                text: '나에 대해 알아갈 수 있는 웹페이지\n',
                url: currentDomain + '/home/rooms/' + link,
            });
            console.log('공유 성공');
        } catch (error) {
            console.error('공유 실패', error);
        }
    });

    document.querySelector('.after-login-container').style.visibility = 'visible';
}
