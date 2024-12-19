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

function viewEpisode(episodeId) {
    console.log(episodeId);
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