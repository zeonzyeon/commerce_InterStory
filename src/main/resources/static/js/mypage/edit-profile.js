const checkNickname = () => {
    const nicknameInput = document.querySelector('.profile-input');
    const nicknameError = document.getElementById('nicknameError');
    const submitButton = document.querySelector('.profile-button');
    const nickname = nicknameInput.value;

    if (!nickname) {
        nicknameError.textContent = '닉네임을 입력해주세요.';
        nicknameError.classList.remove('hidden');
        return;
    }

    fetch(`/api/users/check-duplication-nickname?nickname=${nickname}`)
        .then(response => response.json())
        .then(data => {
            console.log(data);
            if (data) {
                submitButton.classList.remove('hidden');
                nicknameInput.readOnly = true;
                nicknameCheck = true;
                nicknameError.classList.add('hidden');
            } else {
                nicknameError.textContent = '이미 사용중인 닉네임입니다.';
                nicknameError.classList.remove('hidden');
                nicknameCheck = false;
            }
        })
        .catch(error => {
            console.error('Error:', error);
            nicknameError.textContent = '닉네임 중복 확인 중 오류가 발생했습니다.';
            nicknameError.classList.remove('hidden');
        });
};

// 이미지 변경 처리 함수
const handleImageChange = () => {
    const fileInput = document.getElementById('profileImage');
    const spinner = document.getElementById('imageUploadSpinner');

    if (fileInput.files && fileInput.files[0]) {
        const formData = new FormData();
        formData.append('profileImage', fileInput.files[0]);

        // 이미지 미리보기
        const reader = new FileReader();
        reader.onload = (e) => {
            document.getElementById('profilePreview').src = e.target.result;
        }
        reader.readAsDataURL(fileInput.files[0]);

        // 스피너 표시
        spinner.classList.remove('hidden');

        // 이미지 업로드 요청
        fetch('/api/users/update-profile', {
            method: 'POST',
            body: formData
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert("이미지 변경 완료");
                } else {
                    alert(data.message);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showToast('이미지 업로드 중 오류가 발생했습니다.', 'error');
            })
            .finally(() => {
                spinner.classList.add('hidden');
            });
    }
}

// 이미지 클릭 이벤트 처리
document.querySelector('#profileImage').addEventListener('click', function (e) {
    document.getElementById('profileImageInput').click();
});

// 닉네임 변경 폼 제출
document.querySelector('.profile-form').addEventListener('submit', function (e) {
    e.preventDefault();

    if (!nicknameCheck) {
        alert('닉네임 중복 확인을 해주세요.');
        return;
    }

    const nickname = this.querySelector('input').value;

    fetch('/api/users/update-nickname', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({nickname})
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert(data.message);
                //location.reload();
            } else {
                alert(data.message || '닉네임 변경에 실패했습니다.');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('닉네임 변경 중 오류가 발생했습니다.');
        });
});

// 구독하기 ** 해야함
document.querySelector('.banner-button')?.addEventListener('click', function () {
    location.href = '/auth/withdrawal';
});