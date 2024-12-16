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