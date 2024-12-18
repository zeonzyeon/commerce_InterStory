function novelFavorite() {
    const novelId = document.getElementById('novel-profile').getAttribute('data-novel-id');

    $.ajax({
        url: '/api/novels/' + novelId + '/favorite',
        type: 'POST',
        contentType: 'application/json',
        success: function () {
            location.reload();
            console.log("상태 변경에 성공했습니다.");
        },
        error: function () {
            alert("상태 변경에 실패했습니다.");
        }
    });
}

document.getElementById('btn-novel-share').addEventListener('click', () => {
    const currentUrl = window.location.href;
    navigator.clipboard.writeText(currentUrl)
        .then(() => {
            alert('URL이 복사되었습니다.');
        })
        .catch(err => {
            console.error('복사에 실패했습니다.', err);
        });
});

document.getElementById('episode-sort-recent').addEventListener('click', () => {
    const currentUrl = new URL(window.location.href);
    currentUrl.searchParams.set('sort', 'NEW_TO_OLD');
    window.history.pushState({}, '', currentUrl);
    location.reload();
});

document.getElementById('episode-sort-old').addEventListener('click', () => {
    const currentUrl = new URL(window.location.href);
    currentUrl.searchParams.set('sort', 'OLD_TO_NEW');
    window.history.pushState({}, '', currentUrl);
    location.reload();
});

function viewEpisode(episodeId, episodeTitle, isFree, isPurchased) {
    console.log(episodeId, episodeTitle, isFree, isPurchased);
    if (isFree === 'true' || isPurchased === 'true') {
        window.location.href = '/episodes/' + episodeId;
    } else {
        openPurchaseModal(episodeId, episodeTitle);
    }
}

function showAllEpisodes() {
    const currentUrl = new URL(window.location.href);
    currentUrl.searchParams.set('showAll', 'true');
    window.history.pushState({}, '', currentUrl);
    location.reload();
}

function showMinEpisodes() {
    const currentUrl = new URL(window.location.href);
    currentUrl.searchParams.set('showAll', 'false');
    window.history.pushState({}, '', currentUrl);
    location.reload();
}


document.getElementById('btn-comment-sort-recent').addEventListener('click', () => {
    const currentUrl = new URL(window.location.href);
    currentUrl.searchParams.set('commentSort', 'NEW_TO_OLD');
    window.history.pushState({}, '', currentUrl);
    location.reload();
});

document.getElementById('btn-comment-sort-popular').addEventListener('click', () => {
    const currentUrl = new URL(window.location.href);
    currentUrl.searchParams.set('commentSort', 'RECOMMENDATION');
    window.history.pushState({}, '', currentUrl);
    location.reload();
});

function openPurchaseModal(episodeId, episodeTitle) {
    document.getElementById('modal-backdrop').style.display = 'block';
    document.getElementById('episode-purchase-modal').style.display = 'block';

    document.getElementById('purchase-episode-title').innerText = episodeTitle;

    basket = document.getElementById('episode-basket');
    purchase = document.getElementById('episode-purchase');

    basket.addEventListener('click', () => {
        $.ajax({
            url: '/api/novels/episodes/' + episodeId + '/cart',
            type: 'POST',
            success: function (response) {
                alert(response);
                location.reload();
            },
            error: function () {
            }
        });
    });
    purchase.addEventListener('click', () => {
        $.ajax({
            url: '/api/novels/episodes/' + episodeId + '/purchase',
            type: 'POST',
            success: function (response) {
                alert(response);
                location.href = '/episodes/' + episodeId;
            },
            error: function () {
            }
        });
    });
}

function closePurchaseModal() {
    document.getElementById('modal-backdrop').style.display = 'none';
    document.getElementById('episode-purchase-modal').style.display = 'none';
}