function updateAccount() {
    const accountNumber = document.getElementById('accountNumber').value;
    if (accountNumber) {
        $.ajax({
            url: '/api/users/account',
            type: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify({
                accountNumber: accountNumber,
            }),
            success: function (response) {
                alert("계좌번호가 변경되었습니다.");
            },
            error: function (xhr, err, status) {
                alert(err + "이(가) 발생했습니다: " + status);
            }
        });
    } else {
        alert("계좌번호를 입력하세요.");
    }
}

function calculateSettlement() {
    $.ajax({
        url: '/api/users/settlement',
        type: 'POST',
        success: function (response) {
            alert("정산이 완료되었습니다");
            location.reload();
        },
        error: function (xhr, err, status) {
            alert(err + "이(가) 발생했습니다: " + status);
        }
    });
}

function registerNewWork() {
    alert("신규 작품 등록 페이지로 이동합니다.");
    // 신규 작품 등록 페이지로 이동
    window.location.href = "/works/new";
}