const pageLinkList = document.querySelectorAll('.page-number');
const pageList = document.querySelectorAll('.page-chunk');
const pageItemList = document.querySelectorAll('.page-item');
currentPageLink = 0;
currentOpenedContent = -1;

handlePageLinkClick(0);


for (let i = 0; i < pageLinkList.length; i++) {
    pageLinkList[i].addEventListener('click', function() {
        handlePageLinkClick(i);
    });
}

// pagination 숫자 클릭 시
function handlePageLinkClick(i) {
    pagePrev = document.querySelector('.page-prev');
    pageNext = document.querySelector('.page-next');

    pagePrev.classList.remove("disabled")
    pageNext.classList.remove("disabled")
    if (i == 0) {
        pagePrev.classList.add("disabled");
    }
    if (i == pageLinkList.length - 1) {
        pageNext.classList.add("disabled");
    }

    pageItemList[currentPageLink + 1].classList.remove("active");
    pageList[currentPageLink].style.display = "none";
    currentPageLink = i;
    pageItemList[currentPageLink + 1].classList.add("active");
    pageList[currentPageLink].style.display = "block";

    // prev, next 버튼이 disable되면 해당 버튼을 눌러도 숫자를 누른 것과 같은 효과가 남
    // 예를 들면 1번 페이지가 열린 상태에서 disable된 prev를 누르면 1번 페이지가 눌린 것과 같은 효과
    if (currentOpenedContent != -1 && i != currentPageLink) {
        contentClose(currentOpenedContent);
        currentOpenedContent = 0
    }
}

// pagination << 클릭 시
function prevPage() {
    pagePrev = document.querySelector('.page-prev');
    pagePrev.classList.remove("disabled")
    if (currentPageLink == 1) {
        pagePrev.classList.add("disabled");
    }

    pageItemList[currentPageLink + 1].classList.remove("active");
    pageList[currentPageLink].style.display = "none";
    currentPageLink--;
    pageItemList[currentPageLink + 1].classList.add("active");
    pageList[currentPageLink].style.display = "block";

    if (currentOpenedContent != -1) {
        contentClose(currentOpenedContent);
        currentOpenedContent = 0
    }
}

// pagination >> 클릭 시
function nextPage() {
    pageNext = document.querySelector('.page-next');
    pageNext.classList.remove("disabled")
    if (currentPageLink == pageLinkList.length - 2) {
        pageNext.classList.add("disabled");
    }

    pageItemList[currentPageLink + 1].classList.remove("active");
    pageList[currentPageLink].style.display = "none";
    currentPageLink++;
    pageItemList[currentPageLink + 1].classList.add("active");
    pageList[currentPageLink].style.display = "block";

    if (currentOpenedContent != -1) {
        contentClose(currentOpenedContent);
        currentOpenedContent = 0
    }
}

// title 클릭 시 공지사항 내용 나옴
// 왜인지 모르지만 첫번째는 ease가 적용되지 않아서 강제로 열었다 닫음
const noticeTitleList = document.querySelectorAll('.notice-title');
const contentWrapperList = document.querySelectorAll('.content-wrapper');
const iconList = document.querySelectorAll('.col-1 > .fas');
for (let i = 0; i < noticeTitleList.length; i++) {
    contentOpen(i);
    contentClose(i);
}
currentOpenedContent = -1;

for (let i = 0; i < noticeTitleList.length; i++) {
    noticeTitleList[i].addEventListener('click', function() {
        // 열려있던 걸 누른 거면 ease 효과 주면서 닫기
        if (currentOpenedContent == i) {
            contentWrapperList[i].style.height = 0;
            iconList[i].classList.remove('fa-minus');
            iconList[i].classList.add('fa-plus');
            currentOpenedContent = -1;
        } else {
            if (currentOpenedContent != -1) {
                contentClose(currentOpenedContent);
            }
            currentOpenedContent = i;
            contentOpen(currentOpenedContent);
        }
    });
}

function contentClose(i) {
    // 기존에 열려있던 content 닫기
    contentWrapperList[i].style.transition = '';
    contentWrapperList[i].style.height = 0;
    iconList[i].classList.remove('fa-minus');
    iconList[i].classList.add('fa-plus');
    currentOpenedContent = -1;
}

function contentOpen(i) {
    // 클릭한 content 열기
    contentWrapperList[i].style.transition = 'height 0.7s ease';
    contentWrapperList[i].style.height = contentWrapperList[i].children[0].offsetHeight + 'px';
    iconList[i].classList.remove('fa-plus');
    iconList[i].classList.add('fa-minus');
}