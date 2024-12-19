function openPointPaymentModal() {
    document.getElementById('modalBackdrop').style.display = 'block';
    document.getElementById('pointPaymentModal').style.display = 'block';
}

function closePointPaymentModal() {
    document.getElementById('modalBackdrop').style.display = 'none';
    document.getElementById('pointPaymentModal').style.display = 'none';
}

function openSubscriptionModal() {
    document.getElementById('subscriptionModalBackdrop').style.display = 'block';
    document.getElementById('subscriptionModal').style.display = 'block';
}

function closeSubscriptionModal() {
    document.getElementById('subscriptionModalBackdrop').style.display = 'none';
    document.getElementById('subscriptionModal').style.display = 'none';
}

function updateFinalPrice(scope) {
    let selectedSubscription, finalPrice;
    let totalPrice = 0;

    if (scope === 'point') {
        // 포인트 모달 선택 요소
        selectedSubscription = document.querySelector('#pointPaymentModal input[name="point"]:checked');
        totalPrice = parseInt(selectedSubscription.value);

        let selectCoupon = document.querySelector('#pointPaymentModal .couponSelect');
        let selectedCoupon = selectCoupon.options[selectCoupon.selectedIndex];
        let couponMin = selectedCoupon.getAttribute('data-min');

        finalPrice = document.querySelector('#pointPaymentModal .finalPrice');
        if (totalPrice < couponMin) {
            selectCoupon.selectedIndex = 0;
            alert('최소 결제 금액을 충족하지 못했습니다.');
            updateFinalPrice('point');
            return;
        }
        totalPrice -= parseInt(selectedCoupon.value);
    } else if (scope === 'subscription') {
        // 구독 모달 선택 요소
        selectedSubscription = document.querySelector('#subscriptionModal input[name="subscription"]:checked');
        totalPrice = parseInt(selectedSubscription.value);
        finalPrice = document.querySelector('#subscriptionModal .finalPrice');
    }

    finalPrice.innerText = `${Math.max(0, totalPrice).toLocaleString()}원`;
}

function buyPoint() {
    const point = document.querySelector('#pointPaymentModal input[name="point"]:checked').value;

    let type;
    if (point === '1000') type = 'NORMAL_FIRST';
    else if (point === '3000') type = 'NORMAL_SECOND';
    else if (point === '5000') type = 'NORMAL_THIRD';
    else if (point === '10000') type = 'NORMAL_FOURTH';
    else if (point === '50000') type = 'NORMAL_FIFTH';

    let selectCoupon = document.querySelector('#pointPaymentModal .couponSelect');
    let selectedCoupon = selectCoupon.options[selectCoupon.selectedIndex];
    let couponId = selectedCoupon.getAttribute('data-coupon-id');

    if (couponId === null) couponId = 0;
    console.log(type, couponId);

    $.ajax({
        url: '/api/cash/establish',
        type: 'post',
        contentType: 'application/json',
        data: JSON.stringify({
            paymentType: type,
            couponId: couponId
        }),
        success: function (response) {
            if (response.next_redirect_pc_url != null)
                location.href = response.next_redirect_pc_url;
        },
        error: function (xhr, err, status) {
            alert(err + "이(가) 발생했습니다: " + status);
        }
    });
}

// 정산 요청
function requestSettlement() {
    fetch('/api/users/settlement', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'}
    }).then(response => {
        if (response.ok) {
            alert('정산 요청이 완료되었습니다.');
            location.reload();
        }
    }).catch(error => console.error('Error:', error));
}

// 계좌번호 수정
function updateAccount() {
    const accountNumber = document.getElementById('accountNumber').value;
    fetch('/api/users/account', {
        method: 'PUT',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({accountNumber})
    }).then(response => response.json())
        .then(data => {
            alert('계좌번호가 수정되었습니다.');
            location.reload();
        }).catch(error => console.error('Error:', error));
}

function autoPayment() {
    $.ajax({
        url: '/api/cash/establish',
        type: 'post',
        contentType: 'application/json',
        data: JSON.stringify({
            paymentType: 'AUTO',
            couponId: 0
        }),
        success: function (response) {
            console.log(response);
            if (response.next_redirect_pc_url != null)
                location.href = response.next_redirect_pc_url;
            else
                location.reload();
        },
        error: function (xhr, err, status) {
            alert(err + "이(가) 발생했습니다: " + status);
        }
    });
}

function subscribe() {
    const subscription = document.querySelector('#subscriptionModal input[name="subscription"]:checked').value;

    let type;
    if (subscription === '9900') type = 'SEQUENCE';
    else if (subscription === '14900') type = 'SUB_ONCE';

    $.ajax({
        url: '/api/cash/establish',
        type: 'post',
        contentType: 'application/json',
        data: JSON.stringify({
            paymentType: type,
            couponId: 0
        }),
        success: function (response) {
            console.log(response);
            if (response.next_redirect_pc_url != null)
                location.href = response.next_redirect_pc_url;
            else
                location.reload();
        },
        error: function (xhr, err, status) {
            alert(err + "이(가) 발생했습니다: " + status);
        }
    });
}

function changePayment() {
    $.ajax({
        url: '/api/cash/establish',
        type: 'post',
        contentType: 'application/json',
        data: JSON.stringify({
            paymentType: 'CHANGE_PAYMENT',
            couponId: 0
        }),
        success: function (response) {
            if (response.next_redirect_pc_url != null)
                location.href = response.next_redirect_pc_url;
        },
        error: function (xhr, err, status) {
            alert(err + "이(가) 발생했습니다: " + status);
        }
    });
}

function inactiveAutoPayment() {
    $.ajax({
        url: '/api/cash/inactive-auto-payment',
        type: 'post',
        contentType: 'application/json',
        success: function (response) {
            alert("자동 결제가 취소되었습니다.");
            location.reload();
        },
        error: function (xhr, err, status) {
            alert(err + "이(가) 발생했습니다: " + status);
        }
    });
}

function inactiveSubscription() {
    $.ajax({
        url: '/api/cash/inactive-subscription',
        type: 'post',
        contentType: 'application/json',
        success: function (response) {
            alert("정기 구독이 취소되었습니다.");
            location.reload();
        },
        error: function (xhr, err, status) {
            alert(err + "이(가) 발생했습니다: " + status);
        }
    });
}